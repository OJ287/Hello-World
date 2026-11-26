package jp.co.aquaclara.accis.web.domain.service.c0501;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S01M004TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M003TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M005TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M006TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M007TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M013TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M014TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M016TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M017TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M019TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M020TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M027TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M029TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M031TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M046TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M050TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M204TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M302TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02V001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S03T002TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S06T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S09T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S10T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S14T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S01M004TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S01M004TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M003TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M003TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M007TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M007TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M014TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M014TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M019TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M019TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M020TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M020TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M027TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M027TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M031TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M031TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M050TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M050TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M302TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M302TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M005TC0501W02Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M006TC0501W02Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M013TC0501W02Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M016TC0501W02Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M204TC0501W02Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02V001TC0501W02Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S03T002TC0501W02Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S04T001TC0501W02Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S06T001TC0501W02Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TC0501W02Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S09T001TC0501W02Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S10T001TC0501W02Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S14T001TC0501W02Entity;
import jp.co.aquaclara.accis.web.domain.dto.c0501.C0501W02ClaimInfoDto;
import jp.co.aquaclara.accis.web.domain.dto.c0501.C0501W02JissekiInfoDto;
import jp.co.aquaclara.accis.web.domain.dto.c0501.C0501W02JissekisumInfoDto;
import jp.co.aquaclara.accis.web.domain.dto.c0501.C0501W02SearchConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.c0501.C0501W02ServerInfoDto;
import jp.co.aquaclara.accis.web.domain.dto.c0501.C0501W02SkysakiInfoDto;
import jp.co.aquaclara.accis.web.domain.dto.c0501.C0501W02TokuisakiInfoConfirmDto;
import jp.co.aquaclara.accis.web.domain.dto.c0501.C0501W02TokuisakiPointRunkDto;
import jp.co.aquaclara.accis.web.domain.dto.c0800.LatestDlvryDayDto;
import jp.co.aquaclara.accis.web.domain.service.business.SagyoyoyakuService;
import jp.co.aquaclara.accis.web.domain.service.c0800.LatestDlvryDayService;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;

/**
 * C0501w02_得意先情報確認(得意先情報確認)Service
 * 
 * @author accisdev041
 *
 */
@Service
public class C0501W02Service {

    @Autowired
    private final MessageSource msg = null;

    /** 直近配達可能日取得Service **/
    @Autowired
    private LatestDlvryDayService getNearestDlvryDayService;

    /** 担当者マスタDAO **/
    @Autowired
    private S02M003TDao s02m003tDao;

    /** コンディションマスタDAO **/
    @Autowired
    private S01M004TDao s01m004tDao;

    /** 営業日マスタDAO **/
    @Autowired
    private S02M005TDao s02m005tDao;

    /** システム管理マスタDAO **/
    @Autowired
    private S02M006TDao s02m006tDao;

    /** 基本商品マスタDAO **/
    @Autowired
    private S02M007TDao s02m007tDao;

    /** 得意先マスタDAO **/
    @Autowired
    private S02M013TDao s02m013tDao;

    /** エージェントマスタDAO **/
    @Autowired
    private S02M014TDao s02m014tDao;

    /** 定例売上仕入マスタDAO **/
    @Autowired
    private S02M016TDao s02m016tDao;

    /** 請求先マスタDAO **/
    @Autowired
    private S02M017TDao s02m017tDao;

    /** 配達会社マスタDAO **/
    @Autowired
    private S02M019TDao s02m019tDao;

    /** メニューマスタDAO **/
    @Autowired
    private S02M029TDao s02m029tDao;

    /** 営業エリアマスタDAO **/
    @Autowired
    private S02M020TDao s02m020tDao;

    /** 得意先獲得販促詳細マスタ **/
    @Autowired
    private S02M027TDao s02m027tDao;

    /** 配達拠点マスタDAO **/
    @Autowired
    private S02M031TDao s02m031tDao;

    /** ショップマスタDAO **/
    @Autowired
    private S02M046TDao s02m046tDao;

    /** 得意先実績テーブルDAO **/
    @Autowired
    private S02M050TDao s02m050tDao;

    /** 得意先ポイントランクDAO **/
    @Autowired
    private S02M204TDao s02m204tDao;

    /** 商品マスタビューDAO **/
    @Autowired
    private S02V001TDao s02v001tDao;

    /** 月別得意先配達実績DAO **/
    @Autowired
    private S03T002TDao s03t002tDao;

    /** 受注配達テーブルDAO **/
    @Autowired
    private S04T001TDao s04t001tDao;

    /** 売上テーブルDAO **/
    @Autowired
    private S06T001TDao S06T001TDao;

    /** 請求残高テーブルDAO **/
    @Autowired
    private S07T001TDao S07T001TDao;

    /** 決済履歴テーブルDAO **/
    @Autowired
    private S09T001TDao s09T001tDao;

    /** 入金取込得意先ワークDAO **/
    @Autowired
    private S10T001TDao s10T001TDao;

    /** 問合せクレームテーブルDAO **/
    @Autowired
    private S14T001TDao s14t001tDao;

    /** 配達エリアグループマスタDAO **/
    @Autowired
    private S02M302TDao s02m302tDao;

    /** 作業予約Service **/
    @Autowired
    private SagyoyoyakuService sagyoyoyakuservice;

    /** 明細最大件数情報取得 **/
    private String detailmamxlength = "0";

    /** ステータス：締時間オーバーによる次回可能日 */
    public static int STATUS_ADDED = 2;

    /**
     * 固定値定義
     */
    // 未締請求日表示文字列
    public static final String MIJIMESKDATE_DISPLAY_STRING = "現在";
    // マスキング文字
    public static final String MASKING_STRING = "**********";

    /**
     * 1.得意先情報確認初期表示
     * 
     * @param conditionDto 検索条件
     * @return
     */
    public C0501W02TokuisakiInfoConfirmDto search(C0501W02SearchConditionDto conditionDto) {

        // 得意先情報確認生成
        C0501W02TokuisakiInfoConfirmDto tokuisakiInfoConfirmDto =
            new C0501W02TokuisakiInfoConfirmDto();

        // ボタン制御
        controlButton(tokuisakiInfoConfirmDto, conditionDto);

        // 得意先マスタ検索：単独TBL検索
        S02M013TEntityExample S02M013TExample = new S02M013TEntityExample();
        jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntityExample.Criteria S02M013TCriteria =
            S02M013TExample.createCriteria();
        S02M013TCriteria.andApkihoncdEqualTo(conditionDto.getApkihoncd())
            .andShopcdEqualTo(conditionDto.getShopcd())
            .andTokuisakicdEqualTo(conditionDto.getTokuisakicd());
        S02M013TEntity s02m013tOut = s02m013tDao.selectByExample(S02M013TExample).get(0);

        // 請求先マスタ検索：単独TBL検索
        S02M017TEntity s02m017tOut = null;
        if (!StringUtils.isEmpty(s02m013tOut.getSkysakicd())) {
            S02M017TEntityExample S02M017TExample = new S02M017TEntityExample();
            jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntityExample.Criteria S02M017TCriteria =
                S02M017TExample.createCriteria();
            S02M017TCriteria.andApkihoncdEqualTo(conditionDto.getApkihoncd())
                .andShopcdEqualTo(conditionDto.getShopcd())
                .andSkysakicdEqualTo(s02m013tOut.getSkysakicd());
            s02m017tOut = s02m017tDao.selectByExample(S02M017TExample).get(0);

            // 得意先情報確認セット
            tokuisakiInfoConfirmDto.setSkysakicd(s02m013tOut.getSkysakicd());
            tokuisakiInfoConfirmDto.setSkysakikanrikbn(s02m017tOut.getSkysakikanrikbn());

        }

        // 得意先詳細情報取得
        getTokuisakiDetailInfo(tokuisakiInfoConfirmDto, s02m013tOut, s02m017tOut, conditionDto);

        // サーバー情報データ取得
        getServerlInfo(tokuisakiInfoConfirmDto, conditionDto);

        // 請求情報データ取得
        getSkysakiInfo(tokuisakiInfoConfirmDto, conditionDto);

        // 受注／配達実績データ(上)取得
        getJissekiInfoUp(tokuisakiInfoConfirmDto);

        // 受注／配達実績データ(下)取得
        getJissekiInfoDown(tokuisakiInfoConfirmDto);

        // 受注／配達実績データ(受注本数)取得
        getJissekiInfoCount(tokuisakiInfoConfirmDto, conditionDto);

        // 問合せ・クレーム履歴データ取得
        getClaimInfo(tokuisakiInfoConfirmDto);

        // 受注存在チェック
        // checkAcptData(tokuisakiInfoConfirmDto, s02m013tOut);

        // 登録済受注件数取得
        S04T001TEntityExample acptCondition = new S04T001TEntityExample();
        acptCondition.createCriteria()
            // AP基本コード
            .andApkihoncdEqualTo(conditionDto.getApkihoncd())
            // ショップコード
            .andShopcdEqualTo(conditionDto.getShopcd())
            // 得意先コード
            .andTokuisakicdEqualTo(conditionDto.getTokuisakicd())
            // 伝票ステータス…受注、受注（再配達）、受注（配達中）のいずれか
            .andDnpystsIn(List.of(CommonConstants.DNPYSTS_02, CommonConstants.DNPYSTS_03,
                CommonConstants.DNPYSTS_11))
            // 有効区分
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        tokuisakiInfoConfirmDto.setAcptcnt(s04t001tDao.countByExample(acptCondition));

        // 受注日（開始、終了）取得
        calcAcptdd(tokuisakiInfoConfirmDto, conditionDto);

        // ポイントランク取得
        getPointRunk(tokuisakiInfoConfirmDto);

        // 作業グループ取得
        getSagyogroup(tokuisakiInfoConfirmDto);

        // 返却用DTOセット
        return tokuisakiInfoConfirmDto;

    }

    /**
     * 2.ボタン制御
     * 
     * @param conditionDto
     * @return
     */
    private void controlButton(C0501W02TokuisakiInfoConfirmDto tokuisakiInfoConfirmDto,
        C0501W02SearchConditionDto conditionDto) {

        // 受注登録ボタンの表示制御情報取得
        tokuisakiInfoConfirmDto.setJuchubtnhyoji(getAuthorityInfo(conditionDto, "C0801W00"));

        // 問合せクレーム一時受付ボタンの表示制御情報取得
        tokuisakiInfoConfirmDto.setClaimbtnhyoji(getAuthorityInfo(conditionDto, "C0901W00"));

        // 作業予約確認ボタンの表示制御情報取得
        boolean sagyoyoyakuRiyo = sagyoyoyakuservice.isSagyoyoyakuRiyo();
        tokuisakiInfoConfirmDto.setSagyoyoyakubtnhyoji(
            sagyoyoyakuRiyo ? getAuthorityInfo(conditionDto, "B1702W00") : "0");

    }

    /**
     * 3.得意先詳細情報取得
     * 
     * @param tokuisakiInfoConfirmDto
     * @param s02M013TEntity
     * @param s02M017TEntity
     * @param conditionDto
     * @return
     */
    private void getTokuisakiDetailInfo(C0501W02TokuisakiInfoConfirmDto tokuisakiInfoConfirmDto,
        S02M013TEntity s02M013TEntity, S02M017TEntity s02M017TEntity,
        C0501W02SearchConditionDto conditionDto) {

        // 得意先情報Entity生成
        C0501W02TokuisakiInfoConfirmDto tmpTokuisakiInfoConfirmDto =
            new C0501W02TokuisakiInfoConfirmDto();

        // 得意先Entityのコピー
        // ＡＰ基本コード
        tmpTokuisakiInfoConfirmDto.setApkihoncd(s02M013TEntity.getApkihoncd());
        // ショップコード
        tmpTokuisakiInfoConfirmDto.setShopcd(s02M013TEntity.getShopcd());
        // 請求先コード
        tmpTokuisakiInfoConfirmDto.setSkysakicd(s02M013TEntity.getSkysakicd());
        // 得意先コード
        tmpTokuisakiInfoConfirmDto.setTokuisakicd(s02M013TEntity.getTokuisakicd());
        // 申込受付日
        tmpTokuisakiInfoConfirmDto.setMoshikomircptdd(s02M013TEntity.getMoshikomircptdd());
        // 入会日
        tmpTokuisakiInfoConfirmDto.setNyukaidd(s02M013TEntity.getNyukaidd());
        // 退会日
        tmpTokuisakiInfoConfirmDto.setTaikaidd(s02M013TEntity.getTaikaidd());
        // 連絡事項公開
        tmpTokuisakiInfoConfirmDto.setRenrakujikokokai(s02M013TEntity.getRenrakujikokokai());
        // 地図番号
        tmpTokuisakiInfoConfirmDto.setChizuno(s02M013TEntity.getChizuno());
        // 配達順
        tmpTokuisakiInfoConfirmDto.setDlvryjun(s02M013TEntity.getDlvryjun());
        // 配達方法区分
        tmpTokuisakiInfoConfirmDto.setDlvryhohokbn(s02M013TEntity.getDlvrykbn());
        // 印刷メモ１
        tmpTokuisakiInfoConfirmDto.setPrntmemon1(s02M013TEntity.getPrntmemon1());
        // 印刷メモ２
        tmpTokuisakiInfoConfirmDto.setPrntmemon2(s02M013TEntity.getPrntmemon2());
        // 印刷メモ３
        tmpTokuisakiInfoConfirmDto.setPrntmemon3(s02M013TEntity.getPrntmemon3());
        // 受注時間指定時刻
        tmpTokuisakiInfoConfirmDto
            .setAcptjikanshiteijikoku(s02M013TEntity.getAcptjikanshiteijikoku());
        // 獲得詳細メモ
        tmpTokuisakiInfoConfirmDto.setKakutokushsmemo(s02M013TEntity.getKakutokushsmemo());
        // 本部エージェントコード
        tmpTokuisakiInfoConfirmDto.setHonbuejntcd(s02M013TEntity.getHonbuejntcd());
        // 獲得エージェントコード
        tmpTokuisakiInfoConfirmDto.setKakutokuejntcd(s02M013TEntity.getKakutokuejntcd());
        // まとめ買いサービス区分
        tmpTokuisakiInfoConfirmDto.setMtmkisvkbn(s02M013TEntity.getMtmkisvkbn());
        // 紹介得意先コード
        tmpTokuisakiInfoConfirmDto.setShoukaitokuisakicd(s02M013TEntity.getShoukaitokuisakicd());
        // 保証金預かり日
        tmpTokuisakiInfoConfirmDto.setHoshokinazukaridd(s02M013TEntity.getHoshokinazukaridd());
        // 保証金返却日
        tmpTokuisakiInfoConfirmDto.setHoshokinhenkyakudd(s02M013TEntity.getHoshokinhenkyakudd());

        // 保証金預かり金額
        tmpTokuisakiInfoConfirmDto.setHoshokinazukarikin("");
        if (!Objects.isNull(s02M013TEntity.getHoshokinazukarikin())) {
            if (s02M013TEntity.getHoshokinazukarikin() == 0) {
                tmpTokuisakiInfoConfirmDto.setHoshokinazukarikin("");
            } else {
                tmpTokuisakiInfoConfirmDto
                    .setHoshokinazukarikin(String.valueOf(s02M013TEntity.getHoshokinazukarikin()));
            }
        }

        // 保証金返却金額
        tmpTokuisakiInfoConfirmDto.setHoshokinhenkyakukin("");
        if (!Objects.isNull(s02M013TEntity.getHoshokinhenkyakukin())) {
            if (s02M013TEntity.getHoshokinhenkyakukin() == 0) {
                tmpTokuisakiInfoConfirmDto.setHoshokinhenkyakukin("");
            } else {
                tmpTokuisakiInfoConfirmDto.setHoshokinhenkyakukin(
                    String.valueOf(s02M013TEntity.getHoshokinhenkyakukin()));
            }
        }

        // 連絡先番号区分名称１
        tmpTokuisakiInfoConfirmDto.setRenrakusakinokbnnm1("");
        if (!StringUtils.isEmpty(s02M013TEntity.getRenrakusakinokbnn1())) {
            tmpTokuisakiInfoConfirmDto.setRenrakusakinokbnnm1(getSystemMasterValue(
                CommonConstants.SYS_SETTEINO_011, s02M013TEntity.getRenrakusakinokbnn1()));
        }

        // 連絡先番号区分名称２
        tmpTokuisakiInfoConfirmDto.setRenrakusakinokbnnm2("");
        if (!StringUtils.isEmpty(s02M013TEntity.getRenrakusakinokbnn2())) {
            tmpTokuisakiInfoConfirmDto.setRenrakusakinokbnnm2(getSystemMasterValue(
                CommonConstants.SYS_SETTEINO_011, s02M013TEntity.getRenrakusakinokbnn2()));
        }

        // 連絡先番号区分名称３
        tmpTokuisakiInfoConfirmDto.setRenrakusakinokbnnm3("");
        if (!StringUtils.isEmpty(s02M013TEntity.getRenrakusakinokbnn3())) {
            tmpTokuisakiInfoConfirmDto.setRenrakusakinokbnnm3(getSystemMasterValue(
                CommonConstants.SYS_SETTEINO_011, s02M013TEntity.getRenrakusakinokbnn3()));
        }

        // 連絡先番号区分名称４
        tmpTokuisakiInfoConfirmDto.setRenrakusakinokbnnm4("");
        if (!StringUtils.isEmpty(s02M013TEntity.getRenrakusakinokbnn4())) {
            tmpTokuisakiInfoConfirmDto.setRenrakusakinokbnnm4(getSystemMasterValue(
                CommonConstants.SYS_SETTEINO_011, s02M013TEntity.getRenrakusakinokbnn4()));
        }

        // 種別
        tmpTokuisakiInfoConfirmDto.setKind("");
        if (!StringUtils.isEmpty(s02M013TEntity.getCdkindkbn())) {
            tmpTokuisakiInfoConfirmDto.setKind(getSystemMasterValue(
                CommonConstants.SYS_SETTEINO_016, s02M013TEntity.getCdkindkbn()));
        }

        // 配達方法
        tmpTokuisakiInfoConfirmDto.setDlvryhoho("");
        if (!StringUtils.isEmpty(s02M013TEntity.getDlvrykbn())) {
            tmpTokuisakiInfoConfirmDto.setDlvryhoho(getConditionMasterValue(
                CommonConstants.CONDITION_ID_CN042, s02M013TEntity.getDlvrykbn()));
        }

        // 受注受付区分
        tmpTokuisakiInfoConfirmDto.setAcptrcptkbn(s02M013TEntity.getAcptrcptkbn());

        // 受注受付区分名称
        tmpTokuisakiInfoConfirmDto.setAcptrcptkbnnm("");
        if (!StringUtils.isEmpty(s02M013TEntity.getAcptrcptkbn())) {
            tmpTokuisakiInfoConfirmDto.setAcptrcptkbnnm(getConditionMasterValue(
                CommonConstants.CONDITION_ID_CN041, s02M013TEntity.getAcptrcptkbn()));
        }

        // 担当者マスタ検索：単独TBL検索
        tmpTokuisakiInfoConfirmDto.setDlvrytnshanm("");
        if (!Strings.isEmpty(s02M013TEntity.getDlvrytnshacd())) {
            S02M003TEntityExample S02M003TExample = new S02M003TEntityExample();
            jp.co.aquaclara.accis.common.domain.repository.entity.S02M003TEntityExample.Criteria S02M003TCriteria =
                S02M003TExample.createCriteria();
            S02M003TCriteria.andApkihoncdEqualTo(s02M013TEntity.getApkihoncd())
                .andShopcdEqualTo(s02M013TEntity.getShopcd())
                .andTnshacdEqualTo(s02M013TEntity.getDlvrytnshacd());
            List<S02M003TEntity> s02m003tOutList = s02m003tDao.selectByExample(S02M003TExample);

            // 担当者名称
            if (s02m003tOutList.size() > 0) {
                tmpTokuisakiInfoConfirmDto.setDlvrytnshanm(s02m003tOutList.get(0).getTnshamei());
            }
        }


        // 配達エリアコード
        tmpTokuisakiInfoConfirmDto.setDlvryareacd(s02M013TEntity.getDlvryareacd());

        // 納品先情報１
        tmpTokuisakiInfoConfirmDto.setNohinsakijoho1("");
        if (!StringUtils.isEmpty(s02M013TEntity.getNohinsakijohon1())) {
            tmpTokuisakiInfoConfirmDto.setNohinsakijoho1(getSystemMasterValue(
                CommonConstants.SYS_SETTEINO_056, s02M013TEntity.getNohinsakijohon1()));
        }

        // 納品先情報２
        tmpTokuisakiInfoConfirmDto.setNohinsakijoho2("");
        if (!StringUtils.isEmpty(s02M013TEntity.getNohinsakijohon2())) {
            tmpTokuisakiInfoConfirmDto.setNohinsakijoho2(getSystemMasterValue(
                CommonConstants.SYS_SETTEINO_057, s02M013TEntity.getNohinsakijohon2()));
        }

        // 不在置きフラグ
        tmpTokuisakiInfoConfirmDto.setFzaiokikanoflg(s02M013TEntity.getFzaiokikanoflg());

        // 初回設置日
        tmpTokuisakiInfoConfirmDto.setShokaistupdd(s02M013TEntity.getShokaiwsvstupdd());

        // 保証金区分
        tmpTokuisakiInfoConfirmDto.setHoshokinkbn(s02M013TEntity.getHoshokinkbn());

        // 保証金区分区分名称
        tmpTokuisakiInfoConfirmDto.setHoshokinkbnnm("");
        if (!StringUtils.isEmpty(s02M013TEntity.getHoshokinkbn())) {
            tmpTokuisakiInfoConfirmDto.setHoshokinkbnnm(getConditionMasterValue(
                CommonConstants.CONDITION_ID_CN101, s02M013TEntity.getHoshokinkbn()));
        }

        // 受注時間指定区分名称
        tmpTokuisakiInfoConfirmDto.setAcptjikanshiteikbnnm("");
        if (!StringUtils.isEmpty(s02M013TEntity.getAcptjikanshiteikbn())) {
            tmpTokuisakiInfoConfirmDto.setAcptjikanshiteikbnnm(getSystemMasterValue(
                CommonConstants.SYS_SETTEINO_012, s02M013TEntity.getAcptjikanshiteikbn()));
        }

        // 受付区分
        tmpTokuisakiInfoConfirmDto.setRcptkbnnm("");
        if (!StringUtils.isEmpty(s02M013TEntity.getRcptkbn())) {
            tmpTokuisakiInfoConfirmDto.setRcptkbnnm(getSystemMasterValue(
                CommonConstants.SYS_SETTEINO_018, s02M013TEntity.getRcptkbn()));
        }

        // 獲得区分
        tmpTokuisakiInfoConfirmDto.setKakutokukbnnm("");
        if (!StringUtils.isEmpty(s02M013TEntity.getKakutokukbn())) {
            tmpTokuisakiInfoConfirmDto.setKakutokukbnnm(getSystemMasterValue(
                CommonConstants.SYS_SETTEINO_019, s02M013TEntity.getKakutokukbn()));
        }

        // 媒体区分
        tmpTokuisakiInfoConfirmDto.setBaitaikbnnm("");
        if (!StringUtils.isEmpty(s02M013TEntity.getBaitaikbn())) {
            tmpTokuisakiInfoConfirmDto.setBaitaikbnnm(getSystemMasterValue(
                CommonConstants.SYS_SETTEINO_020, s02M013TEntity.getBaitaikbn()));
        }

        // 獲得詳細コード
        tmpTokuisakiInfoConfirmDto.setKakutokushscd(s02M013TEntity.getKakutokushsmemo());

        // 料金プランリンク
        tmpTokuisakiInfoConfirmDto
            .setRknkindtokubetsuuntflg(s02M013TEntity.getRknkindtokubetsuuntflg());

        // 特記事項

        // 特別単価区分
        tmpTokuisakiInfoConfirmDto.setTokubetsuuntkbnnm("");
        if (!StringUtils.isEmpty(s02M013TEntity.getTokubetsuuntkbn())) {
            tmpTokuisakiInfoConfirmDto.setTokubetsuuntkbnnm(getSystemMasterValue(
                CommonConstants.SYS_SETTEINO_140, s02M013TEntity.getTokubetsuuntkbn()));
        }

        // 回収方法
        tmpTokuisakiInfoConfirmDto.setKsihoho("");
        if (!Objects.isNull(s02M017TEntity)) {
            if (!StringUtils.isEmpty(s02M017TEntity.getKaishuhohokbn())) {
                tmpTokuisakiInfoConfirmDto.setKsihoho(getSystemMasterValue(
                    CommonConstants.SYS_SETTEINO_051, s02M017TEntity.getKaishuhohokbn()));
            }
        }

        // 入金予定日
        tmpTokuisakiInfoConfirmDto.setNyknytidd("");
        if (!Objects.isNull(s02M017TEntity)) {
            if (!StringUtils.isEmpty(s02M017TEntity.getShrsaito())) {
                tmpTokuisakiInfoConfirmDto.setNyknytiddkbn(s02M017TEntity.getShrsaito());
                tmpTokuisakiInfoConfirmDto.setNyknytidd(getConditionMasterValue(
                    CommonConstants.CONDITION_ID_CN086, s02M017TEntity.getShrsaito()));
            }
        }

        // 締日
        tmpTokuisakiInfoConfirmDto.setShimedd("");
        if (!Objects.isNull(s02M017TEntity)) {

            tmpTokuisakiInfoConfirmDto.setShimedd(s02M017TEntity.getSkyshimedd() + '日');
            tmpTokuisakiInfoConfirmDto.setShimedd(s02M017TEntity.getShrdd() + '日');
        }

        // 配達拠点マスタ検索：単独TBL検索
        tmpTokuisakiInfoConfirmDto.setDlvrykyotencd("");
        tmpTokuisakiInfoConfirmDto.setDlvrykyotennm("");
        if (!Strings.isEmpty(s02M013TEntity.getDlvrykankatsuapkihoncd())
            && !Strings.isEmpty(s02M013TEntity.getDlvrykankatsushopcd())
            && !Strings.isEmpty(s02M013TEntity.getDlvrykankatsukyotencd())) {

            S02M031TEntityExample S02M031TExample = new S02M031TEntityExample();
            jp.co.aquaclara.accis.common.domain.repository.entity.S02M031TEntityExample.Criteria S02M031TCriteria =
                S02M031TExample.createCriteria();
            S02M031TCriteria.andApkihoncdEqualTo(s02M013TEntity.getDlvrykankatsuapkihoncd())
                .andShopcdEqualTo(s02M013TEntity.getDlvrykankatsushopcd())
                .andDlvrykyotencdEqualTo(s02M013TEntity.getDlvrykankatsukyotencd());
            List<S02M031TEntity> s02m031tOutList = s02m031tDao.selectByExample(S02M031TExample);

            if (s02m031tOutList.size() > 0) {
                // 配達拠点コード
                tmpTokuisakiInfoConfirmDto
                    .setDlvrykyotencd(s02m031tOutList.get(0).getDlvrykyotencd());
                // 配達拠点名称
                tmpTokuisakiInfoConfirmDto
                    .setDlvrykyotennm(s02m031tOutList.get(0).getDlvrykyotennm());
            }
        }

        // 得意先実績テーブル検索：単独TBL検索
        S02M050TEntityExample jissekiCondition = new S02M050TEntityExample();
        jissekiCondition.createCriteria().andApkihoncdEqualTo(s02M013TEntity.getApkihoncd())
            .andShopcdEqualTo(s02M013TEntity.getShopcd())
            .andTokuisakicdEqualTo(s02M013TEntity.getTokuisakicd());
        Optional<S02M050TEntity> jissekiSearchResult =
            s02m050tDao.selectByExample(jissekiCondition).stream().findFirst();

        tmpTokuisakiInfoConfirmDto.setAzukaribtln1n2rittoruhns("");
        tmpTokuisakiInfoConfirmDto.setAzukaribtln8rittoruhns("");
        jissekiSearchResult.ifPresent(jissekiEntity -> {
            // 預けボトル１２Ｌ
            tmpTokuisakiInfoConfirmDto.setAzukaribtln1n2rittoruhns(
                upperLimmitEdit(jissekiEntity.getAzukaribtln1n2rittoruhns()));
            // 預けボトル７Ｌ
            tmpTokuisakiInfoConfirmDto.setAzukaribtln8rittoruhns(
                upperLimmitEdit(jissekiEntity.getAzukaribtln8rittoruhns()));
        });

        // 営業エリアマスタ検索：単独TBL検索
        tmpTokuisakiInfoConfirmDto.setEigyoareanm("");
        if (!Strings.isEmpty(s02M013TEntity.getEigyoareacd())) {
            S02M020TEntityExample S02M020TExample = new S02M020TEntityExample();
            jp.co.aquaclara.accis.common.domain.repository.entity.S02M020TEntityExample.Criteria S02M020TCriteria =
                S02M020TExample.createCriteria();
            S02M020TCriteria.andApkihoncdEqualTo(s02M013TEntity.getApkihoncd())
                .andShopcdEqualTo(s02M013TEntity.getShopcd())
                .andEigyoareacdEqualTo(s02M013TEntity.getEigyoareacd());
            List<S02M020TEntity> s02m020tOutList = s02m020tDao.selectByExample(S02M020TExample);

            // 営業エリア名称
            if (s02m020tOutList.size() > 0) {
                tmpTokuisakiInfoConfirmDto.setEigyoareanm(s02m020tOutList.get(0).getEigyoareanm());
            }
        }

        // 担当者マスタ検索：単独TBL検索
        tmpTokuisakiInfoConfirmDto.setEigyotnshanm("");
        if (!Strings.isEmpty(s02M013TEntity.getEigyotnshacd())) {
            S02M003TEntityExample S02M003TExample = new S02M003TEntityExample();
            jp.co.aquaclara.accis.common.domain.repository.entity.S02M003TEntityExample.Criteria S02M003TCriteria =
                S02M003TExample.createCriteria();
            S02M003TCriteria.andApkihoncdEqualTo(s02M013TEntity.getApkihoncd())
                .andShopcdEqualTo(s02M013TEntity.getShopcd())
                .andTnshacdEqualTo(s02M013TEntity.getEigyotnshacd());
            List<S02M003TEntity> s02m003tOutList = s02m003tDao.selectByExample(S02M003TExample);

            // 担当者名称
            if (s02m003tOutList.size() > 0) {
                tmpTokuisakiInfoConfirmDto.setEigyotnshanm(s02m003tOutList.get(0).getTnshamei());
            }
        }

        // 得意先獲得販促詳細マスタ検索：単独TBL検索
        tmpTokuisakiInfoConfirmDto.setKakutokushs("");
        tmpTokuisakiInfoConfirmDto.setKakutokushscd("");
        if (!Strings.isEmpty(s02M013TEntity.getHansokushscd())) {
            S02M027TEntityExample S02M027TExample = new S02M027TEntityExample();
            jp.co.aquaclara.accis.common.domain.repository.entity.S02M027TEntityExample.Criteria S02M027TCriteria =
                S02M027TExample.createCriteria();
            S02M027TCriteria.andApkihoncdEqualTo(s02M013TEntity.getApkihoncd())
                .andShopcdEqualTo(s02M013TEntity.getShopcd())
                .andHansokushscdEqualTo(s02M013TEntity.getHansokushscd());
            List<S02M027TEntity> s02m027tOutList = s02m027tDao.selectByExample(S02M027TExample);

            // 獲得詳細名
            if (s02m027tOutList.size() > 0) {
                tmpTokuisakiInfoConfirmDto.setKakutokushscd(s02M013TEntity.getHansokushscd());
                tmpTokuisakiInfoConfirmDto.setKakutokushs(s02m027tOutList.get(0).getHansokunm());
            }
        }

        // 配達エリアマスタ結合データ取得
        S02M013TC0501W02Entity s02m013tEntityIn = new S02M013TC0501W02Entity();
        s02m013tEntityIn.setApkihoncd(s02M013TEntity.getApkihoncd());
        s02m013tEntityIn.setShopcd(s02M013TEntity.getShopcd());
        s02m013tEntityIn.setTokuisakicd(s02M013TEntity.getTokuisakicd());
        s02m013tEntityIn.setDlvrykyotencd(tmpTokuisakiInfoConfirmDto.getDlvrykyotencd());
        s02m013tEntityIn.setDlvryareacd(tmpTokuisakiInfoConfirmDto.getDlvryareacd());
        s02m013tEntityIn.setUnyodate(conditionDto.getUnyoDate());
        S02M013TC0501W02Entity s02m013tEntityOut = s02m013tDao.selectByC0501W02(s02m013tEntityIn);

        tmpTokuisakiInfoConfirmDto.setDlvryareanm("");
        tmpTokuisakiInfoConfirmDto.setDlvryshua("");
        tmpTokuisakiInfoConfirmDto.setDlvryshub("");
        tmpTokuisakiInfoConfirmDto.setDlvryshuc("");
        tmpTokuisakiInfoConfirmDto.setDlvryshud("");
        tmpTokuisakiInfoConfirmDto.setSunddlvrykanodd("");
        tmpTokuisakiInfoConfirmDto.setMonddlvrykanodd("");
        tmpTokuisakiInfoConfirmDto.setTueddlvrykanodd("");
        tmpTokuisakiInfoConfirmDto.setWedddlvrykanodd("");
        tmpTokuisakiInfoConfirmDto.setTheddlvrykanodd("");
        tmpTokuisakiInfoConfirmDto.setFriddlvrykanodd("");
        tmpTokuisakiInfoConfirmDto.setSatddlvrykanodd("");
        if (!Objects.isNull(s02m013tEntityOut)) {
            // 配達エリア名称
            tmpTokuisakiInfoConfirmDto.setDlvryareanm(s02m013tEntityOut.getDlvryareanm());
            // 配達ＷＥＥＫＡ
            tmpTokuisakiInfoConfirmDto.setDlvryshua(s02m013tEntityOut.getDlvryshua());
            // 配達ＷＥＥＫＢ
            tmpTokuisakiInfoConfirmDto.setDlvryshub(s02m013tEntityOut.getDlvryshub());
            // 配達ＷＥＥＫＣ
            tmpTokuisakiInfoConfirmDto.setDlvryshuc(s02m013tEntityOut.getDlvryshuc());
            // 配達ＷＥＥＫＤ
            tmpTokuisakiInfoConfirmDto.setDlvryshud(s02m013tEntityOut.getDlvryshud());
            // 日曜配達可能日
            tmpTokuisakiInfoConfirmDto.setSunddlvrykanodd(s02m013tEntityOut.getSunddlvrykanodd());
            // 月曜配達可能日
            tmpTokuisakiInfoConfirmDto.setMonddlvrykanodd(s02m013tEntityOut.getMonddlvrykanodd());
            // 火曜配達可能日
            tmpTokuisakiInfoConfirmDto.setTueddlvrykanodd(s02m013tEntityOut.getTueddlvrykanodd());
            // 水曜配達可能日
            tmpTokuisakiInfoConfirmDto.setWedddlvrykanodd(s02m013tEntityOut.getWedddlvrykanodd());
            // 木曜配達可能日
            tmpTokuisakiInfoConfirmDto.setTheddlvrykanodd(s02m013tEntityOut.getTheddlvrykanodd());
            // 金曜配達可能日
            tmpTokuisakiInfoConfirmDto.setFriddlvrykanodd(s02m013tEntityOut.getFriddlvrykanodd());
            // 土曜配達可能日
            tmpTokuisakiInfoConfirmDto.setSatddlvrykanodd(s02m013tEntityOut.getSatddlvrykanodd());
        }

        // 最新決済結果情報取得
        S09T001TC0501W02Entity s09T001tEntityIn = new S09T001TC0501W02Entity();
        s09T001tEntityIn.setApkihoncd(s02M013TEntity.getApkihoncd());
        s09T001tEntityIn.setShopcd(s02M013TEntity.getShopcd());
        s09T001tEntityIn.setSkysakicd(tokuisakiInfoConfirmDto.getSkysakicd());
        S09T001TC0501W02Entity s09T001tEntityOut = s09T001tDao.selectByC0501W02(s09T001tEntityIn);

        tmpTokuisakiInfoConfirmDto.setKsishimedydmdd("");
        tmpTokuisakiInfoConfirmDto.setKsikekkanm("");
        tmpTokuisakiInfoConfirmDto.setKsikin("");
        tmpTokuisakiInfoConfirmDto.setShimedd2("");
        tmpTokuisakiInfoConfirmDto.setKsihohokbn("");
        if (!Objects.isNull(s09T001tEntityOut)) {
            // 決済締年月日 */
            tmpTokuisakiInfoConfirmDto.setKsishimedydmdd(s09T001tEntityOut.getFurikaedydmdd());
            // 決済結果名称
            tmpTokuisakiInfoConfirmDto.setKsikekkanm(s09T001tEntityOut.getKsikekka());
            // 決済金額
            tmpTokuisakiInfoConfirmDto.setKsikin(s09T001tEntityOut.getKsikin());
            // 締日２
            tmpTokuisakiInfoConfirmDto.setShimedd2(s09T001tEntityOut.getShimedd());
            // 決済方法区分
            tmpTokuisakiInfoConfirmDto.setKsihohokbn(s09T001tEntityOut.getKsihohokbn());
        }

        // 紹介者数取得：単独TBL検索
        S02M013TEntityExample S02M013TExample = new S02M013TEntityExample();
        jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntityExample.Criteria S02M013TCriteria =
            S02M013TExample.createCriteria();
        S02M013TCriteria.andApkihoncdEqualTo(s02M013TEntity.getApkihoncd())
            .andShopcdEqualTo(s02M013TEntity.getShopcd())
            .andShoukaitokuisakicdEqualTo(s02M013TEntity.getTokuisakicd());
        long shoshakazu = s02m013tDao.countByExample(S02M013TExample);

        // 紹介者数
        tmpTokuisakiInfoConfirmDto.setShoshakazu("");
        if (!Objects.isNull(shoshakazu)) {
            tmpTokuisakiInfoConfirmDto.setShoshakazu(String.valueOf(shoshakazu));
        }

        // 配達会社マスタ検索：単独TBL検索
        tmpTokuisakiInfoConfirmDto.setDlvrycmpnynm("");
        if (!Strings.isEmpty(s02M013TEntity.getDlvrycmpnycd())) {

            S02M019TEntityExample S02M019TExample = new S02M019TEntityExample();
            jp.co.aquaclara.accis.common.domain.repository.entity.S02M019TEntityExample.Criteria S02M019TCriteria =
                S02M019TExample.createCriteria();
            S02M019TCriteria.andApkihoncdEqualTo(s02M013TEntity.getApkihoncd())
                .andShopcdEqualTo(s02M013TEntity.getShopcd())
                .andDlvrycmpnycdEqualTo(s02M013TEntity.getDlvrycmpnycd());
            List<S02M019TEntity> s02m019tOutList = s02m019tDao.selectByExample(S02M019TExample);

            // 配達会社名称
            if (s02m019tOutList.size() > 0) {
                tmpTokuisakiInfoConfirmDto
                    .setDlvrycmpnynm(s02m019tOutList.get(0).getDlvrycmpnynm());
            }
        }

        // 受付区分取得
        S02M006TC0501W02Entity s02m006tEntityIn = new S02M006TC0501W02Entity();
        s02m006tEntityIn.setApkihoncd(s02M013TEntity.getApkihoncd());
        s02m006tEntityIn.setShopcd(s02M013TEntity.getShopcd());
        s02m006tEntityIn.setTokuisakicd(s02M013TEntity.getTokuisakicd());
        String rcptkbn = s02m006tDao.selectByC0501W02(s02m006tEntityIn);

        // 受付区分
        tmpTokuisakiInfoConfirmDto.setRcptkbn("");
        if (!Strings.isEmpty(rcptkbn)) {
            tmpTokuisakiInfoConfirmDto.setRcptkbn(rcptkbn);
        }

        tmpTokuisakiInfoConfirmDto.setHonbuejntcdnm("");
        if (!Strings.isEmpty(s02M013TEntity.getHonbuejntcd())) {
            // エージェントマスタ検索：単独TBL検索
            S02M014TEntityExample S02M014TExample = new S02M014TEntityExample();
            jp.co.aquaclara.accis.common.domain.repository.entity.S02M014TEntityExample.Criteria S02M014TCriteria =
                S02M014TExample.createCriteria();
            S02M014TCriteria.andApkihoncdEqualTo("001").andShopcdEqualTo("000")
                .andEjntcdEqualTo(s02M013TEntity.getHonbuejntcd());
            List<S02M014TEntity> s02m014tOutList = s02m014tDao.selectByExample(S02M014TExample);

            // 本部エージェントコード名称
            if (s02m014tOutList.size() > 0) {
                tmpTokuisakiInfoConfirmDto.setHonbuejntcdnm(s02m014tOutList.get(0).getEjntmein1());
            }
        }

        tmpTokuisakiInfoConfirmDto.setKakutokuejntcdnm("");
        if (!Strings.isEmpty(s02M013TEntity.getKakutokuejntcd())) {
            // エージェントマスタ検索：単独TBL検索
            S02M014TEntityExample S02M014TExample = new S02M014TEntityExample();
            jp.co.aquaclara.accis.common.domain.repository.entity.S02M014TEntityExample.Criteria S02M014TCriteria =
                S02M014TExample.createCriteria();
            S02M014TCriteria.andApkihoncdEqualTo(s02M013TEntity.getApkihoncd())
                .andShopcdEqualTo(s02M013TEntity.getShopcd())
                .andEjntcdEqualTo(s02M013TEntity.getKakutokuejntcd());
            List<S02M014TEntity> s02m014tOutList = s02m014tDao.selectByExample(S02M014TExample);

            // 獲得エージェントコード名称
            if (s02m014tOutList.size() > 0) {
                tmpTokuisakiInfoConfirmDto
                    .setKakutokuejntcdnm(s02m014tOutList.get(0).getEjntmein1());
            }
        }
        // 獲得担当者コード
        tmpTokuisakiInfoConfirmDto.setKakutokutnshacd(s02M013TEntity.getKakutokutnshacd());

        tmpTokuisakiInfoConfirmDto.setKakutokutnshamei("");
        if (!Strings.isEmpty(s02M013TEntity.getKakutokutnshacd())) {

            // 担当者マスタ検索：単独TBL検索
            S02M003TEntityExample S02M003TExample = new S02M003TEntityExample();
            jp.co.aquaclara.accis.common.domain.repository.entity.S02M003TEntityExample.Criteria S02M003TCriteria =
                S02M003TExample.createCriteria();
            S02M003TCriteria.andApkihoncdEqualTo(s02M013TEntity.getApkihoncd())
                .andShopcdEqualTo(s02M013TEntity.getShopcd())
                .andTnshacdEqualTo(s02M013TEntity.getKakutokutnshacd());
            List<S02M003TEntity> s02m003tOutList = s02m003tDao.selectByExample(S02M003TExample);

            // 獲得担当者名称
            if (s02m003tOutList.size() > 0) {
                tmpTokuisakiInfoConfirmDto
                    .setKakutokutnshamei(s02m003tOutList.get(0).getTnshamei());
            }
        }

        // 得意先マスタ検索：単独TBL検索
        tmpTokuisakiInfoConfirmDto.setTokuisakinm("");
        if (!Strings.isEmpty(s02M013TEntity.getShoukaitokuisakicd())) {
            S02M013TExample = new S02M013TEntityExample();
            S02M013TCriteria = S02M013TExample.createCriteria();
            S02M013TCriteria.andApkihoncdEqualTo(s02M013TEntity.getApkihoncd())
                .andShopcdEqualTo(s02M013TEntity.getShopcd())
                .andTokuisakicdEqualTo(s02M013TEntity.getShoukaitokuisakicd());
            List<S02M013TEntity> s02m013tOutList = s02m013tDao.selectByExample(S02M013TExample);

            // 得意先名称
            tmpTokuisakiInfoConfirmDto.setTokuisakinm("");
            if (s02m013tOutList.size() > 0) {
                tmpTokuisakiInfoConfirmDto
                    .setTokuisakinm(s02m013tOutList.get(0).getTokuisakinmn1());
            }
        }

        // ショップマスタ検索：単独TBL検索
        S02M046TEntityExample S02M046TExample = new S02M046TEntityExample();
        jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityExample.Criteria S02M046TCriteria =
            S02M046TExample.createCriteria();
        S02M046TCriteria.andApkihoncdEqualTo(s02M013TEntity.getApkihoncd())
            .andShopcdEqualTo(s02M013TEntity.getShopcd());
        List<S02M046TEntity> s02m046tOutList = s02m046tDao.selectByExample(S02M046TExample);

        tmpTokuisakiInfoConfirmDto.setTokuisakinmn1(s02M013TEntity.getTokuisakinmn1());
        tmpTokuisakiInfoConfirmDto.setTokuisakinmn2(s02M013TEntity.getTokuisakinmn2());
        tmpTokuisakiInfoConfirmDto.setRenrakusakinon1(s02M013TEntity.getRenrakusakinon1());
        tmpTokuisakiInfoConfirmDto.setRenrakusakinon2(s02M013TEntity.getRenrakusakinon2());
        tmpTokuisakiInfoConfirmDto.setRenrakusakinon3(s02M013TEntity.getRenrakusakinon3());
        tmpTokuisakiInfoConfirmDto.setRenrakusakinon4(s02M013TEntity.getRenrakusakinon4());
        if (s02m046tOutList.size() > 0
            && "2".equals(s02m046tOutList.get(0).getHonbuclcrenkeikbn())) {
            // 得意先名称１
            tmpTokuisakiInfoConfirmDto
                .setTokuisakinmn1(asteriskChangeNm(s02M013TEntity.getTokuisakinmn1()));
            // 得意先名称２
            tmpTokuisakiInfoConfirmDto
                .setTokuisakinmn2(asteriskChangeNm(s02M013TEntity.getTokuisakinmn2()));
            // 連絡先番号１
            tmpTokuisakiInfoConfirmDto
                .setRenrakusakinon1(asteriskChangeDn(s02M013TEntity.getRenrakusakinon1()));
            // 連絡先番号２
            tmpTokuisakiInfoConfirmDto
                .setRenrakusakinon2(asteriskChangeDn(s02M013TEntity.getRenrakusakinon2()));
            // 連絡先番号３
            tmpTokuisakiInfoConfirmDto
                .setRenrakusakinon3(asteriskChangeDn(s02M013TEntity.getRenrakusakinon3()));
            // 連絡先番号４
            tmpTokuisakiInfoConfirmDto
                .setRenrakusakinon4(asteriskChangeDn(s02M013TEntity.getRenrakusakinon4()));

        }

        // 売上月次年月
        tmpTokuisakiInfoConfirmDto.setUrigetsujidydm(conditionDto.getUnyoDate().substring(0, 6));
        if (s02m046tOutList.size() > 0
            && !Strings.isEmpty(s02m046tOutList.get(0).getUrigetsujidydmdd())) {
            tmpTokuisakiInfoConfirmDto
                .setUrigetsujidydm(s02m046tOutList.get(0).getUrigetsujidydmdd().substring(0, 6));
        }

        // レンタル料金取得（集計無し）
        S02M016TC0501W02Entity s02m016tEntityIn = new S02M016TC0501W02Entity();
        s02m016tEntityIn.setApkihoncd(s02M013TEntity.getApkihoncd());
        s02m016tEntityIn.setShopcd(s02M013TEntity.getShopcd());
        s02m016tEntityIn.setTokuisakicd(s02M013TEntity.getTokuisakicd());
        s02m016tEntityIn.setUnyodate(conditionDto.getUnyoDate().substring(0, 6));
        String rtlrkn = s02m016tDao.selectByC0501W02NoSumMoney(s02m016tEntityIn);

        // サーバーレンタル料金
        tmpTokuisakiInfoConfirmDto.setRtlrkn("0");
        if (!Strings.isEmpty(rtlrkn)) {
            tmpTokuisakiInfoConfirmDto.setRtlrkn(String.valueOf(rtlrkn));
        } else {
            // レンタル料金取得（集計有り）
            s02m016tEntityIn = new S02M016TC0501W02Entity();
            s02m016tEntityIn.setApkihoncd(s02M013TEntity.getApkihoncd());
            s02m016tEntityIn.setShopcd(s02M013TEntity.getShopcd());
            s02m016tEntityIn.setTokuisakicd(s02M013TEntity.getTokuisakicd());
            s02m016tEntityIn.setUnyodate(conditionDto.getUnyoDate().substring(0, 6));
            rtlrkn = s02m016tDao.selectByC0501W02SumMoney(s02m016tEntityIn);
            if (!Strings.isEmpty(rtlrkn)) {
                tmpTokuisakiInfoConfirmDto.setRtlrkn(String.valueOf(rtlrkn));
            }
        }

        // 事業所管理
        tmpTokuisakiInfoConfirmDto.setJigyoshokanri(s02m046tOutList.get(0).getJigyoshokanri());

        // 配達可能日の取得 共通処理作成待ち
        LatestDlvryDayDto latestDlvryDayDto = null;
        if (!StringUtils.isEmpty(s02M013TEntity.getDlvrykankatsuapkihoncd())
            && !StringUtils.isEmpty(s02M013TEntity.getDlvrykankatsushopcd())
            && !StringUtils.isEmpty(s02M013TEntity.getDlvrykankatsukyotencd())
            && !StringUtils.isEmpty(s02M013TEntity.getDlvryareacd())) {
            latestDlvryDayDto = getNearestDlvryDayService.getNearestDlvryDay(
                s02M013TEntity.getDlvrykankatsuapkihoncd(), s02M013TEntity.getDlvrykankatsushopcd(),
                s02M013TEntity.getDlvrykankatsukyotencd(), s02M013TEntity.getDlvryareacd(),
                conditionDto.getUnyoDate());
        }

        if (!Objects.isNull(latestDlvryDayDto)) {
            if (Strings.isEmpty(tokuisakiInfoConfirmDto.getMsg2())
                && latestDlvryDayDto.getStatus() == STATUS_ADDED) {
                tokuisakiInfoConfirmDto
                    .setMsg2(msg.getMessage(MessageID.W180.toString(), null, Locale.JAPAN));
            }
        }

        // まとめ買い有料本数
        tmpTokuisakiInfoConfirmDto.setMtmkiyuryohns("");
        if (!Objects.isNull(s02M013TEntity.getMtmkiyuryohns())) {
            tmpTokuisakiInfoConfirmDto
                .setMtmkiyuryohns(String.valueOf(s02M013TEntity.getMtmkiyuryohns()));
        }
        // まとめ買い無料本数
        tmpTokuisakiInfoConfirmDto.setMtmkimuryohns("");
        if (!Objects.isNull(s02M013TEntity.getMtmkimuryohns())) {
            tmpTokuisakiInfoConfirmDto
                .setMtmkimuryohns(String.valueOf(s02M013TEntity.getMtmkimuryohns()));
        }

        // 得意先情報Entityの値を得意先情報確認Entityへセット
        // ＡＰ基本コード
        tokuisakiInfoConfirmDto.setApkihoncd(tmpTokuisakiInfoConfirmDto.getApkihoncd());
        // ショップコード
        tokuisakiInfoConfirmDto.setShopcd(tmpTokuisakiInfoConfirmDto.getShopcd());
        // 得意先コード
        tokuisakiInfoConfirmDto.setTokuisakicd(tmpTokuisakiInfoConfirmDto.getTokuisakicd());
        // 請求先コード
        tokuisakiInfoConfirmDto.setSkysakicd(tmpTokuisakiInfoConfirmDto.getSkysakicd());
        // 得意先名称１
        tokuisakiInfoConfirmDto.setTokuisakinmn1(tmpTokuisakiInfoConfirmDto.getTokuisakinmn1());
        // 連絡先番号区分名称１
        tokuisakiInfoConfirmDto
            .setRenrakusakinokbnnm1(tmpTokuisakiInfoConfirmDto.getRenrakusakinokbnnm1());
        // 連絡先番号１
        tokuisakiInfoConfirmDto.setRenrakusakinon1(tmpTokuisakiInfoConfirmDto.getRenrakusakinon1());
        // 連絡先番号区分名称２
        tokuisakiInfoConfirmDto
            .setRenrakusakinokbnnm2(tmpTokuisakiInfoConfirmDto.getRenrakusakinokbnnm2());
        // 連絡先番号２
        tokuisakiInfoConfirmDto.setRenrakusakinon2(tmpTokuisakiInfoConfirmDto.getRenrakusakinon2());
        // 得意先名称２
        tokuisakiInfoConfirmDto.setTokuisakinmn2(tmpTokuisakiInfoConfirmDto.getTokuisakinmn2());
        // 連絡先番号区分名称３
        tokuisakiInfoConfirmDto
            .setRenrakusakinokbnnm3(tmpTokuisakiInfoConfirmDto.getRenrakusakinokbnnm3());
        // 連絡先番号３
        tokuisakiInfoConfirmDto.setRenrakusakinon3(tmpTokuisakiInfoConfirmDto.getRenrakusakinon3());
        // 連絡先番号区分名称４
        tokuisakiInfoConfirmDto
            .setRenrakusakinokbnnm4(tmpTokuisakiInfoConfirmDto.getRenrakusakinokbnnm4());
        // 連絡先番号４
        tokuisakiInfoConfirmDto.setRenrakusakinon4(tmpTokuisakiInfoConfirmDto.getRenrakusakinon4());
        // 申込受付日
        tokuisakiInfoConfirmDto.setMoshikomircptdd(tmpTokuisakiInfoConfirmDto.getMoshikomircptdd());
        // 入会日
        tokuisakiInfoConfirmDto.setNyukaidd(tmpTokuisakiInfoConfirmDto.getNyukaidd());
        // 退会日
        tokuisakiInfoConfirmDto.setTaikaidd(tmpTokuisakiInfoConfirmDto.getTaikaidd());
        // 種別
        tokuisakiInfoConfirmDto.setKind(tmpTokuisakiInfoConfirmDto.getKind());
        // 回収方法
        tokuisakiInfoConfirmDto.setKsihoho(tmpTokuisakiInfoConfirmDto.getKsihoho());
        // 入金予定区分
        tokuisakiInfoConfirmDto.setNyknytiddkbn(tmpTokuisakiInfoConfirmDto.getNyknytiddkbn());
        // 入金予定日
        tokuisakiInfoConfirmDto.setNyknytidd(tmpTokuisakiInfoConfirmDto.getNyknytidd());
        // 締日
        tokuisakiInfoConfirmDto.setShimedd(tmpTokuisakiInfoConfirmDto.getShimedd());
        // 決済締年月日 */
        tokuisakiInfoConfirmDto.setKsishimedydmdd(tmpTokuisakiInfoConfirmDto.getKsishimedydmdd());
        // 決済結果名称
        tokuisakiInfoConfirmDto.setKsikekkanm(tmpTokuisakiInfoConfirmDto.getKsikekkanm());
        // 決済金額
        tokuisakiInfoConfirmDto.setKsikin(tmpTokuisakiInfoConfirmDto.getKsikin());
        // ★レンタル料金
        tokuisakiInfoConfirmDto.setRtlrkn(tmpTokuisakiInfoConfirmDto.getRtlrkn());
        // 連絡事項公開
        tokuisakiInfoConfirmDto
            .setRenrakujikokokai(tmpTokuisakiInfoConfirmDto.getRenrakujikokokai());
        // 配達拠点名称
        tokuisakiInfoConfirmDto.setDlvrykyotennm(tmpTokuisakiInfoConfirmDto.getDlvrykyotennm());
        // 配達エリア名称
        tokuisakiInfoConfirmDto.setDlvryareanm(tmpTokuisakiInfoConfirmDto.getDlvryareanm());
        // 地図番号
        tokuisakiInfoConfirmDto.setChizuno(tmpTokuisakiInfoConfirmDto.getChizuno());
        // 配達順
        tokuisakiInfoConfirmDto.setDlvryjun(tmpTokuisakiInfoConfirmDto.getDlvryjun());
        // 配達方法区分
        tokuisakiInfoConfirmDto.setDlvryhohokbn(tmpTokuisakiInfoConfirmDto.getDlvryhohokbn());
        // 配達方法
        tokuisakiInfoConfirmDto.setDlvryhoho(tmpTokuisakiInfoConfirmDto.getDlvryhoho());
        // 配達会社名称
        tokuisakiInfoConfirmDto.setDlvrycmpnynm(tmpTokuisakiInfoConfirmDto.getDlvrycmpnynm());
        // 受注受付区分
        tokuisakiInfoConfirmDto.setAcptrcptkbn(tmpTokuisakiInfoConfirmDto.getAcptrcptkbn());
        // 受注受付区分名称
        tokuisakiInfoConfirmDto.setAcptrcptkbnnm(tmpTokuisakiInfoConfirmDto.getAcptrcptkbnnm());
        // 営業担当者名
        tokuisakiInfoConfirmDto.setDlvrytnshanm(tmpTokuisakiInfoConfirmDto.getDlvrytnshanm());
        // 担当者名
        tokuisakiInfoConfirmDto.setTnshamei(tmpTokuisakiInfoConfirmDto.getTnshamei());
        // 預かりボトル１２Ｌ本数
        tokuisakiInfoConfirmDto
            .setAzukaribtln1n2rittoruhns(tmpTokuisakiInfoConfirmDto.getAzukaribtln1n2rittoruhns());
        // 預かりボトル７Ｌ本数
        tokuisakiInfoConfirmDto
            .setAzukaribtln8rittoruhns(tmpTokuisakiInfoConfirmDto.getAzukaribtln8rittoruhns());
        // 配達ＷＥＥＫＡ
        tokuisakiInfoConfirmDto.setDlvryshua(tmpTokuisakiInfoConfirmDto.getDlvryshua());
        // 配達ＷＥＥＫＢ
        tokuisakiInfoConfirmDto.setDlvryshub(tmpTokuisakiInfoConfirmDto.getDlvryshub());
        // 配達ＷＥＥＫＣ
        tokuisakiInfoConfirmDto.setDlvryshuc(tmpTokuisakiInfoConfirmDto.getDlvryshuc());
        // 配達ＷＥＥＫＤ
        tokuisakiInfoConfirmDto.setDlvryshud(tmpTokuisakiInfoConfirmDto.getDlvryshud());
        // 日曜配達可能日
        tokuisakiInfoConfirmDto.setSunddlvrykanodd(tmpTokuisakiInfoConfirmDto.getSunddlvrykanodd());
        // 月曜配達可能日
        tokuisakiInfoConfirmDto.setMonddlvrykanodd(tmpTokuisakiInfoConfirmDto.getMonddlvrykanodd());
        // 火曜配達可能日
        tokuisakiInfoConfirmDto.setTueddlvrykanodd(tmpTokuisakiInfoConfirmDto.getTueddlvrykanodd());
        // 水曜配達可能日
        tokuisakiInfoConfirmDto.setWedddlvrykanodd(tmpTokuisakiInfoConfirmDto.getWedddlvrykanodd());
        // 木曜配達可能日
        tokuisakiInfoConfirmDto.setTheddlvrykanodd(tmpTokuisakiInfoConfirmDto.getTheddlvrykanodd());
        // 金曜配達可能日
        tokuisakiInfoConfirmDto.setFriddlvrykanodd(tmpTokuisakiInfoConfirmDto.getFriddlvrykanodd());
        // 土曜配達可能日
        tokuisakiInfoConfirmDto.setSatddlvrykanodd(tmpTokuisakiInfoConfirmDto.getSatddlvrykanodd());
        // 受注時間指定時刻
        tokuisakiInfoConfirmDto
            .setAcptjikanshiteijikoku(tmpTokuisakiInfoConfirmDto.getAcptjikanshiteijikoku());
        // 受注時間指定区分名称
        tokuisakiInfoConfirmDto
            .setAcptjikanshiteikbnnm(tmpTokuisakiInfoConfirmDto.getAcptjikanshiteikbnnm());
        // 納品先情報１
        tokuisakiInfoConfirmDto.setNohinsakijoho1(tmpTokuisakiInfoConfirmDto.getNohinsakijoho1());
        // 納品先情報２
        tokuisakiInfoConfirmDto.setNohinsakijoho2(tmpTokuisakiInfoConfirmDto.getNohinsakijoho2());
        // 不在置き可能フラグ
        tokuisakiInfoConfirmDto.setFzaiokikanoflg(tmpTokuisakiInfoConfirmDto.getFzaiokikanoflg());
        // 印刷メモ１
        tokuisakiInfoConfirmDto.setPrntmemon1(tmpTokuisakiInfoConfirmDto.getPrntmemon1());
        // 印刷メモ２
        tokuisakiInfoConfirmDto.setPrntmemon2(tmpTokuisakiInfoConfirmDto.getPrntmemon2());
        // 印刷メモ３
        tokuisakiInfoConfirmDto.setPrntmemon3(tmpTokuisakiInfoConfirmDto.getPrntmemon3());
        // 営業エリア名称
        tokuisakiInfoConfirmDto.setEigyoareanm(tmpTokuisakiInfoConfirmDto.getEigyoareanm());
        // 営業担当者名称
        tokuisakiInfoConfirmDto.setEigyotnshanm(tmpTokuisakiInfoConfirmDto.getEigyotnshanm());
        // 受付区分名称
        tokuisakiInfoConfirmDto.setRcptkbnnm(tmpTokuisakiInfoConfirmDto.getRcptkbnnm());
        // 獲得区分名称
        tokuisakiInfoConfirmDto.setKakutokukbnnm(tmpTokuisakiInfoConfirmDto.getKakutokukbnnm());
        // 媒体区分名称
        tokuisakiInfoConfirmDto.setBaitaikbnnm(tmpTokuisakiInfoConfirmDto.getBaitaikbnnm());
        // 獲得詳細コード
        tokuisakiInfoConfirmDto.setKakutokushscd(tmpTokuisakiInfoConfirmDto.getKakutokushscd());
        // 獲得詳細
        tokuisakiInfoConfirmDto.setKakutokushs(tmpTokuisakiInfoConfirmDto.getKakutokushs());
        // 獲得詳細メモ
        tokuisakiInfoConfirmDto.setKakutokushsmemo(tmpTokuisakiInfoConfirmDto.getKakutokushsmemo());
        // 本部エージェントコード
        tokuisakiInfoConfirmDto.setHonbuejntcd(tmpTokuisakiInfoConfirmDto.getHonbuejntcd());
        // 本部エージェントコード名称
        tokuisakiInfoConfirmDto.setHonbuejntcdnm(tmpTokuisakiInfoConfirmDto.getHonbuejntcdnm());
        // 獲得エージェントコード
        tokuisakiInfoConfirmDto.setKakutokuejntcd(tmpTokuisakiInfoConfirmDto.getKakutokuejntcd());
        // 獲得エージェントコード名称
        tokuisakiInfoConfirmDto
            .setKakutokuejntcdnm(tmpTokuisakiInfoConfirmDto.getKakutokuejntcdnm());
        // 獲得担当者コード
        tokuisakiInfoConfirmDto.setKakutokutnshacd(tmpTokuisakiInfoConfirmDto.getKakutokutnshacd());
        // 獲得担当者名
        tokuisakiInfoConfirmDto
            .setKakutokutnshamei(tmpTokuisakiInfoConfirmDto.getKakutokutnshamei());
        // 特別単価区分名称
        tokuisakiInfoConfirmDto
            .setTokubetsuuntkbnnm(tmpTokuisakiInfoConfirmDto.getTokubetsuuntkbnnm());
        // 料金種別特別単価フラグ
        tokuisakiInfoConfirmDto
            .setRknkindtokubetsuuntflg(tmpTokuisakiInfoConfirmDto.getRknkindtokubetsuuntflg());
        // まとめ買いサービス区分
        tokuisakiInfoConfirmDto.setMtmkisvkbn(tmpTokuisakiInfoConfirmDto.getMtmkisvkbn());
        // まとめ買い有料本数
        tokuisakiInfoConfirmDto.setMtmkiyuryohns(tmpTokuisakiInfoConfirmDto.getMtmkiyuryohns());
        // まとめ買い無料本数
        tokuisakiInfoConfirmDto.setMtmkimuryohns(tmpTokuisakiInfoConfirmDto.getMtmkimuryohns());
        // 初回設置日
        tokuisakiInfoConfirmDto.setShokaistupdd(tmpTokuisakiInfoConfirmDto.getShokaistupdd());
        // 紹介得意先コード
        tokuisakiInfoConfirmDto
            .setShoukaitokuisakicd(tmpTokuisakiInfoConfirmDto.getShoukaitokuisakicd());
        // 得意先名称
        tokuisakiInfoConfirmDto.setTokuisakinm(tmpTokuisakiInfoConfirmDto.getTokuisakinm());
        // 紹介者数
        tokuisakiInfoConfirmDto.setShoshakazu(tmpTokuisakiInfoConfirmDto.getShoshakazu());
        // 保証金区分
        tokuisakiInfoConfirmDto.setHoshokinkbn(tmpTokuisakiInfoConfirmDto.getHoshokinkbn());
        // 保証金区分名称
        tokuisakiInfoConfirmDto.setHoshokinkbnnm(tmpTokuisakiInfoConfirmDto.getHoshokinkbnnm());
        // 保証金預かり日
        tokuisakiInfoConfirmDto
            .setHoshokinazukaridd(tmpTokuisakiInfoConfirmDto.getHoshokinazukaridd());
        // 保証金預かり金額
        tokuisakiInfoConfirmDto
            .setHoshokinazukarikin(tmpTokuisakiInfoConfirmDto.getHoshokinazukarikin());
        // 保証金返却日
        tokuisakiInfoConfirmDto
            .setHoshokinhenkyakudd(tmpTokuisakiInfoConfirmDto.getHoshokinhenkyakudd());
        // 保証金返却金額
        tokuisakiInfoConfirmDto
            .setHoshokinhenkyakukin(tmpTokuisakiInfoConfirmDto.getHoshokinhenkyakukin());
        // 締日2
        tokuisakiInfoConfirmDto.setShimedd2(tmpTokuisakiInfoConfirmDto.getShimedd());
        // 決済方法区分
        tokuisakiInfoConfirmDto.setKsihohokbn(tmpTokuisakiInfoConfirmDto.getKsihohokbn());
        // 売上月次年月
        tokuisakiInfoConfirmDto.setUrigetsujidydm(tmpTokuisakiInfoConfirmDto.getUrigetsujidydm());
        // リンク表示を設定する。
        // 得意先詳細情報
        tokuisakiInfoConfirmDto
            .setKengenpoptokusyosaijoho(getAuthorityInfo(conditionDto, "Z0504W01"));
        // パターン
        tokuisakiInfoConfirmDto.setKengenpopptn(getAuthorityInfo(conditionDto, "B0801W00"));
        // 最終決済結果
        tokuisakiInfoConfirmDto
            .setKengenpopsaishinksikekka(getAuthorityInfo(conditionDto, "B1601W00"));
        // サーバー情報
        tokuisakiInfoConfirmDto.setKengenpopwsvjoho(getAuthorityInfo(conditionDto, "B0504W00"));
        // 請求締日
        tokuisakiInfoConfirmDto
            .setKengenpoptokuisakitotsukiumjh(getAuthorityInfo(conditionDto, "B1501W00"));
        // 受注/配達実績
        tokuisakiInfoConfirmDto
            .setKengenpopacptdlvryjisseki(getAuthorityInfo(conditionDto, "C0807W00"));
        // 問合せクレーム履歴
        tokuisakiInfoConfirmDto
            .setKengenpoptoiawaseclmrireki(getAuthorityInfo(conditionDto, "C0901W00"));
        // 決済方法区分
        tokuisakiInfoConfirmDto.setKsihohokbn(tmpTokuisakiInfoConfirmDto.getKsihohokbn());
        // 事業所管理
        tokuisakiInfoConfirmDto.setJigyoshokanri(tmpTokuisakiInfoConfirmDto.getJigyoshokanri());
        // 消費税（ラベル）
        String shohitaxkinlabel = "";
        if (!Objects.isNull(s02M017TEntity)
            && CommonConstants.SKYSAKIKANRIKBN_2.equals(s02M017TEntity.getSkysakikanrikbn())) {
            shohitaxkinlabel = CommonConstants.SKYSAKIKANRIKBN_2;
        } else {
            shohitaxkinlabel = getCurrentValue(s02m046tOutList.get(0).getGenztaxkeisankbnuri(),
                s02m046tOutList.get(0).getShintaxkeisankbnuri(),
                s02m046tOutList.get(0).getTaxkeisankbnurikirikaedd(), conditionDto.getUnyoDate());
        }
        tokuisakiInfoConfirmDto.setShohitaxkinlabel(shohitaxkinlabel);
        // 配達拠点コード
        tokuisakiInfoConfirmDto.setDlvrykyotencd(tmpTokuisakiInfoConfirmDto.getDlvrykyotencd());
        // 配達エリアコード
        tokuisakiInfoConfirmDto.setDlvryareacd(tmpTokuisakiInfoConfirmDto.getDlvryareacd());
    }

    /**
     * 4.システム管理マスタ設定値取得
     * 
     * @param searchResultList
     * @return
     */
    private String getSystemMasterValue(String setteino, String setteicd) {

        return s02m006tDao.findConfig(setteino, setteicd);

    }

    /**
     * コンディションマスタ設定値取得
     * 
     * @param conditionid
     * @param setteiatai
     * @return
     */
    private String getConditionMasterValue(String conditionid, String setteiatai) {

        String retVal = "";

        S01M004TEntityExample S01M004TExample = new S01M004TEntityExample();
        jp.co.aquaclara.accis.common.domain.repository.entity.S01M004TEntityExample.Criteria S01M004TCriteria =
            S01M004TExample.createCriteria();
        S01M004TCriteria.andConditionidEqualTo(conditionid).andSetteiataiEqualTo(setteiatai);

        List<S01M004TEntity> s01m004tOutList = s01m004tDao.selectByExample(S01M004TExample);

        if (s01m004tOutList.size() > 0) {
            retVal = s01m004tOutList.get(0).getItemnm();
        }

        return retVal;
    }

    /**
     * 5.権限情報取得
     * 
     * @param conditionDto
     * @param funcId
     * @return
     */
    private String getAuthorityInfo(C0501W02SearchConditionDto conditionDto, String funcId) {

        String retStr = "0";
        if (!Strings.isEmpty(s02m029tDao.getPgAuth(conditionDto.getTnshamenuctgrycd(), funcId))) {
            retStr = "1";
        }

        return retStr;
    }

    /**
     * 6.サーバー情報データ取得
     * 
     * @param tokuisakiInfoConfirmDto
     * @param conditionDto
     */
    private void getServerlInfo(C0501W02TokuisakiInfoConfirmDto tokuisakiInfoConfirmDto,
        C0501W02SearchConditionDto conditionDto) {

        // 明細最大件数情報取得
        detailmamxlength = getSystemMasterValue(CommonConstants.SYS_SETTEINO_035, "1");

        // サーバー情報取得
        S02V001TC0501W02Entity s02v001tEntityIn = new S02V001TC0501W02Entity();
        s02v001tEntityIn.setApkihoncd(conditionDto.getApkihoncd());
        s02v001tEntityIn.setShopcd(conditionDto.getShopcd());
        s02v001tEntityIn.setTokuisakicd(conditionDto.getTokuisakicd());
        List<S02V001TC0501W02Entity> s02v001tEntityOutList =
            s02v001tDao.selectByC0501W02(s02v001tEntityIn);

        int counter = 0;
        List<C0501W02ServerInfoDto> serverInfoList = new ArrayList<>();
        for (S02V001TC0501W02Entity e : s02v001tEntityOutList) {
            counter++;

            if (Integer.valueOf(detailmamxlength) < counter) {
                break;
            }

            C0501W02ServerInfoDto serverInfo = new C0501W02ServerInfoDto();
            // サーバー種類明細
            serverInfo.setWsvshuruimei(getCurrentValue(e.getGenzshnnmryakusho(),
                e.getShinshnnmryakusho(), e.getShnnmkirikaedd(), conditionDto.getUnyoDate()));
            // フロントパネル明細
            serverInfo.setFrontpanelmei(
                getCurrentValue(e.getFrontgenzshnnmryakusho(), e.getFrontshinshnnmryakusho(),
                    e.getFrontshnnmkirikaedd(), conditionDto.getUnyoDate()));
            // 台数明細
            serverInfo.setDaisumei(e.getDaisu());

            serverInfoList.add(serverInfo);
        }
        tokuisakiInfoConfirmDto.setServerInfoList(serverInfoList);
    }

    /**
     * 7.請求情報データ取得
     * 
     * @param tokuisakiInfoConfirmDto
     * @param conditionDto
     */
    private void getSkysakiInfo(C0501W02TokuisakiInfoConfirmDto tokuisakiInfoConfirmDto,
        C0501W02SearchConditionDto conditionDto) {

        List<C0501W02SkysakiInfoDto> skysakiInfoList = new ArrayList<>();

        // ショップマスタ検索：単独TBL検索
        S02M046TEntityExample S02M046TExample = new S02M046TEntityExample();
        jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityExample.Criteria S02M046TCriteria =
            S02M046TExample.createCriteria();
        S02M046TCriteria.andApkihoncdEqualTo(tokuisakiInfoConfirmDto.getApkihoncd())
            .andShopcdEqualTo(tokuisakiInfoConfirmDto.getShopcd());
        List<S02M046TEntity> s02m046tOutList = s02m046tDao.selectByExample(S02M046TExample);

        // 得意先マスタ検索：単独TBL検索
        long tokuisakiCount = 0L;
        if (!StringUtils.isEmpty(tokuisakiInfoConfirmDto.getSkysakicd())) {
            S02M013TEntityExample S02M013TExample = new S02M013TEntityExample();
            jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntityExample.Criteria S02M013TCriteria =
                S02M013TExample.createCriteria();
            S02M013TCriteria.andApkihoncdEqualTo(tokuisakiInfoConfirmDto.getApkihoncd())
                .andShopcdEqualTo(tokuisakiInfoConfirmDto.getShopcd())
                .andSkysakicdEqualTo(tokuisakiInfoConfirmDto.getSkysakicd())
                .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
            tokuisakiCount = s02m013tDao.countByExample(S02M013TExample);
        }

        if (tokuisakiCount != 0 && tokuisakiCount <= 10) {
            // 未締め請求データの取得
            // 請求未締め入金金額情報取得
            S10T001TC0501W02Entity S10T001TEntityIn = new S10T001TC0501W02Entity();
            S10T001TEntityIn.setApkihoncd(tokuisakiInfoConfirmDto.getApkihoncd());
            S10T001TEntityIn.setShopcd(tokuisakiInfoConfirmDto.getShopcd());
            S10T001TEntityIn.setTokuisakicd(tokuisakiInfoConfirmDto.getTokuisakicd());
            S10T001TEntityIn.setSkysakicd(tokuisakiInfoConfirmDto.getSkysakicd());
            List<S10T001TC0501W02Entity> S10T001TEntityOut =
                s10T001TDao.selectByC0501W02(S10T001TEntityIn);

            // 請求未締め売上金額情報取得
            S06T001TC0501W02Entity S06T001TEntityIn = new S06T001TC0501W02Entity();
            S06T001TEntityIn.setApkihoncd(tokuisakiInfoConfirmDto.getApkihoncd());
            S06T001TEntityIn.setShopcd(tokuisakiInfoConfirmDto.getShopcd());
            S06T001TEntityIn.setTokuisakicd(tokuisakiInfoConfirmDto.getTokuisakicd());
            S06T001TEntityIn.setSkysakicd(tokuisakiInfoConfirmDto.getSkysakicd());
            List<S06T001TC0501W02Entity> S06T001TEntityOut =
                S06T001TDao.selectByC0501W02(S06T001TEntityIn);

            // 請求残高テーブル検索：単独TBL検索
            S07T001TEntityExample S07T001TExample = new S07T001TEntityExample();
            jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntityExample.Criteria S07T001TCriteria =
                S07T001TExample.createCriteria();
            S07T001TCriteria.andApkihoncdEqualTo(tokuisakiInfoConfirmDto.getApkihoncd())
                .andShopcdEqualTo(tokuisakiInfoConfirmDto.getShopcd())
                .andSkysakicdEqualTo(tokuisakiInfoConfirmDto.getSkysakicd())
                .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
            S07T001TExample.setOrderByClause("SHIMEDYDM DESC, SHIMEDD DESC, RENBAN DESC");
            List<S07T001TEntity> s07T001tOutList = S07T001TDao.selectByExample(S07T001TExample);

            // 前回請求金額取得
            String znkskykinmei = "0";
            if (s07T001tOutList.size() > 0) {
                S07T001TEntity S07T001TEntityZenIn = new S07T001TEntity();
                S07T001TEntityZenIn.setApkihoncd(tokuisakiInfoConfirmDto.getApkihoncd());
                S07T001TEntityZenIn.setShopcd(tokuisakiInfoConfirmDto.getShopcd());
                S07T001TEntityZenIn.setSkysakicd(tokuisakiInfoConfirmDto.getSkysakicd());
                S07T001TEntityZenIn.setShimedydm(s07T001tOutList.get(0).getShimedydm());
                S07T001TEntityZenIn.setShimedd(s07T001tOutList.get(0).getShimedd());
                S07T001TEntityZenIn.setRenban(s07T001tOutList.get(0).getRenban());
                List<S07T001TEntity> S07T001TEntityZenkaiOut =
                    S07T001TDao.selectByC0501W02ZenkaiKin(S07T001TEntityZenIn);
                if (S07T001TEntityZenkaiOut.size() > 0) {
                    znkskykinmei = String.valueOf(S07T001TEntityZenkaiOut.get(0).getKurikoshikin());
                }
            }

            String taxKbn = "";
            Long uriKin = (long) 0;
            if (CommonConstants.SKYSAKIKANRIKBN_2
                .equals(tokuisakiInfoConfirmDto.getSkysakikanrikbn())) {
                taxKbn = CommonConstants.SKYSAKIKANRIKBN_2;
                if (S06T001TEntityOut.size() > 0 && S06T001TEntityOut.get(0) != null) {
                    uriKin = S06T001TEntityOut.get(0).getUrikin();
                }
            } else {
                taxKbn = getCurrentValue(s02m046tOutList.get(0).getGenztaxkeisankbnuri(),
                    s02m046tOutList.get(0).getShintaxkeisankbnuri(),
                    s02m046tOutList.get(0).getTaxkeisankbnurikirikaedd(),
                    conditionDto.getUnyoDate());
                if (CommonConstants.KBN_TAXKEISAN_IN.equals(taxKbn)) {
                    if (S06T001TEntityOut.size() > 0 && S06T001TEntityOut.get(0) != null) {
                        uriKin = S06T001TEntityOut.get(0).getUrikin()
                            + S06T001TEntityOut.get(0).getTaxkin();
                    }
                }
                if (CommonConstants.KBN_TAXKEISAN_EX.equals(taxKbn)) {
                    if (S06T001TEntityOut.size() > 0 && S06T001TEntityOut.get(0) != null) {
                        uriKin = S06T001TEntityOut.get(0).getUrikin();
                    }
                }
            }

            C0501W02SkysakiInfoDto skysakiInfo = new C0501W02SkysakiInfoDto();
            // 請求締日
            skysakiInfo.setSkyshimeddmei(MIJIMESKDATE_DISPLAY_STRING);
            // 前回請求金額
            skysakiInfo.setZnkskykinmei(znkskykinmei);
            // 入金金額
            if (Objects.isNull(S10T001TEntityOut.get(0))) {
                skysakiInfo.setNyknkinmei("0");
            } else {
                skysakiInfo.setNyknkinmei(S10T001TEntityOut.get(0).getNyknkin());
            }
            // 売上金額
            skysakiInfo.setUrikinmei(String.valueOf(uriKin));
            // 消費税金額
            skysakiInfo.setUchishohitaxkinmei(MASKING_STRING);
            // ８％税込売上
            skysakiInfo.setN8persiirekinmei(MASKING_STRING);
            // １０％税込売上
            skysakiInfo.setN10persiirekinmei(MASKING_STRING);
            // 請求金額
            skysakiInfo.setSkykinmei(MASKING_STRING);
            skysakiInfoList.add(skysakiInfo);

            // 過去請求情報取得
            S07T001TC0501W02Entity S07T001TEntityIn = new S07T001TC0501W02Entity();
            S07T001TEntityIn.setApkihoncd(tokuisakiInfoConfirmDto.getApkihoncd());
            S07T001TEntityIn.setShopcd(tokuisakiInfoConfirmDto.getShopcd());
            S07T001TEntityIn.setSkysakicd(tokuisakiInfoConfirmDto.getSkysakicd());
            List<S07T001TC0501W02Entity> s07t001tEntityKakoOutList =
                S07T001TDao.selectByC0501W02(S07T001TEntityIn);

            for (S07T001TC0501W02Entity e : s07t001tEntityKakoOutList) {

                taxKbn = "";
                uriKin = (long) 0;
                if (CommonConstants.SKYSAKIKANRIKBN_2
                    .equals(tokuisakiInfoConfirmDto.getSkysakikanrikbn())) {
                    taxKbn = CommonConstants.SKYSAKIKANRIKBN_2;
                    uriKin = e.getKonkaitaxnukiurikin();
                } else {
                    taxKbn = getCurrentValue(s02m046tOutList.get(0).getGenztaxkeisankbnuri(),
                        s02m046tOutList.get(0).getShintaxkeisankbnuri(),
                        s02m046tOutList.get(0).getTaxkeisankbnurikirikaedd(),
                        conditionDto.getUnyoDate());
                    if (CommonConstants.KBN_TAXKEISAN_IN.equals(taxKbn)) {
                        uriKin = e.getKonkaishohitaxkin() + e.getKonkaitaxnukiurikin();
                    }
                    if (CommonConstants.KBN_TAXKEISAN_EX.equals(taxKbn)) {
                        uriKin = e.getKonkaitaxnukiurikin();
                    }
                }
                skysakiInfo = new C0501W02SkysakiInfoDto();
                // 請求締日
                skysakiInfo.setSkyshimeddmei(e.getShimedydm() + e.getShimedd());
                // 前回請求金額
                skysakiInfo.setZnkskykinmei(String.valueOf(e.getZnkkurikoshikin()));
                // 入金金額
                skysakiInfo.setNyknkinmei(String.valueOf(e.getKonkainyknkin()));
                // 売上金額
                skysakiInfo.setUrikinmei(String.valueOf(uriKin));
                // 消費税金額
                skysakiInfo.setUchishohitaxkinmei(String.valueOf(e.getKonkaishohitaxkin()));
                // ８％税込売上
                skysakiInfo.setN8persiirekinmei(e.getN8perzeikomiuri());
                // １０％税込売上
                skysakiInfo.setN10persiirekinmei(e.getN10perzeikomiuri());
                // 請求金額
                skysakiInfo
                    .setSkykinmei(String.valueOf(e.getZnkkurikoshikin() - e.getKonkainyknkin()
                        + e.getKonkaitaxnukiurikin() + e.getKonkaishohitaxkin()));
                skysakiInfoList.add(skysakiInfo);

            }
        }
        tokuisakiInfoConfirmDto.setSkysakiInfoList(skysakiInfoList);
    }

    /**
     * 9.受注／配達実績データ(上)取得
     * 
     * @param tokuisakiInfoConfirmDto
     */
    private void getJissekiInfoUp(C0501W02TokuisakiInfoConfirmDto tokuisakiInfoConfirmDto) {

        S04T001TC0501W02Entity S04T001TEntityIn = new S04T001TC0501W02Entity();
        S04T001TEntityIn.setApkihoncd(tokuisakiInfoConfirmDto.getApkihoncd());
        S04T001TEntityIn.setShopcd(tokuisakiInfoConfirmDto.getShopcd());
        S04T001TEntityIn.setTokuisakicd(tokuisakiInfoConfirmDto.getTokuisakicd());
        S04T001TEntityIn.setDetailmamxlength(detailmamxlength);

        List<S04T001TC0501W02Entity> S04T001TEntityOutList =
            s04t001tDao.selectByC0501W02MiDlvry(S04T001TEntityIn);

        List<C0501W02JissekiInfoDto> jissekiInfoList = new ArrayList<>();
        for (S04T001TC0501W02Entity e : S04T001TEntityOutList) {
            C0501W02JissekiInfoDto jissekiInfo = new C0501W02JissekiInfoDto();
            jissekiInfo.setAcptdnpyno(e.getAcptdnpyno());
            jissekiInfo.setEdano(e.getEdano());
            jissekiInfo.setAcptdd1mei(e.getAcptdd());
            jissekiInfo.setDlvrydd1mei(e.getDlvrydd());
            jissekiInfo.setRgrayuryo1mei(e.getDlvryytirgrayusyohns());
            jissekiInfo.setRgramuryo1mei(e.getDlvryytirgramushohns());
            jissekiInfo.setSrmyuryo1mei(e.getDlvryytisrmyusyohns());
            jissekiInfo.setSrmmuryo1mei(e.getDlvryytisrmmushohns());
            jissekiInfo.setSonotayuryo1mei(e.getDlvryytisonotayusyohns());
            jissekiInfo.setSonotamuryo1mei(e.getDlvryytisonotamushohns());
            jissekiInfo.setSeihinmizuuri1mei(e.getDlvryytiseihinmizuurikin());
            jissekiInfo.setSonotaurikin1mei(e.getDlvryytisonotaurikin());
            jissekiInfo.setDnpystsnm1mei(e.getAcptkbn());
            jissekiInfo.setDnpysts(e.getDnpysts());

            jissekiInfoList.add(jissekiInfo);

        }
        tokuisakiInfoConfirmDto.setJissekiInfoUpList(jissekiInfoList);
    }

    /**
     * 10.受注／配達実績データ(下)取得
     * 
     * @param tokuisakiInfoConfirmDto
     */
    private void getJissekiInfoDown(C0501W02TokuisakiInfoConfirmDto tokuisakiInfoConfirmDto) {

        // 配達中の受注配達情報を取得
        S04T001TC0501W02Entity S04T001TEntityIn = new S04T001TC0501W02Entity();
        S04T001TEntityIn.setApkihoncd(tokuisakiInfoConfirmDto.getApkihoncd());
        S04T001TEntityIn.setShopcd(tokuisakiInfoConfirmDto.getShopcd());
        S04T001TEntityIn.setTokuisakicd(tokuisakiInfoConfirmDto.getTokuisakicd());

        List<S04T001TC0501W02Entity> S04T001TEntityOutList =
            s04t001tDao.selectByC0501W02GenDlvry(S04T001TEntityIn);

        List<C0501W02JissekiInfoDto> jissekiInfoList = new ArrayList<>();
        for (S04T001TC0501W02Entity e : S04T001TEntityOutList) {
            C0501W02JissekiInfoDto jissekiInfo = new C0501W02JissekiInfoDto();
            jissekiInfo.setAcptdnpyno(e.getAcptdnpyno());
            jissekiInfo.setEdano(e.getEdano());
            jissekiInfo.setAcptdd1mei(e.getAcptdd());
            jissekiInfo.setDlvrydd1mei(e.getDlvrydd());
            jissekiInfo.setRgrayuryo1mei(e.getDlvryytirgrayusyohns());
            jissekiInfo.setRgramuryo1mei(e.getDlvryytirgramushohns());
            jissekiInfo.setSrmyuryo1mei(e.getDlvryytisrmyusyohns());
            jissekiInfo.setSrmmuryo1mei(e.getDlvryytisrmmushohns());
            jissekiInfo.setSonotayuryo1mei(e.getDlvryytisonotayusyohns());
            jissekiInfo.setSonotamuryo1mei(e.getDlvryytisonotamushohns());
            jissekiInfo.setSeihinmizuuri1mei(e.getDlvryytiseihinmizuurikin());
            jissekiInfo.setSonotaurikin1mei(e.getDlvryytisonotaurikin());
            jissekiInfo.setDnpystsnm1mei(e.getAcptkbn());
            jissekiInfo.setDnpysts(e.getDnpysts());

            jissekiInfoList.add(jissekiInfo);

        }

        // 過去分配達の受注配達情報を取得
        S02M005TC0501W02Entity S02M005TEntityIn = new S02M005TC0501W02Entity();
        S02M005TEntityIn.setApkihoncd(tokuisakiInfoConfirmDto.getApkihoncd());
        S02M005TEntityIn.setShopcd(tokuisakiInfoConfirmDto.getShopcd());
        S02M005TEntityIn.setTokuisakicd(tokuisakiInfoConfirmDto.getTokuisakicd());

        // 対応するDAOメソッドのクエリはUNIONを使用しており、条件を満たす結果がない場合でも、検索結果が0件にならない
        s02m005tDao.selectByC0501W02KakoDlvry(S02M005TEntityIn).stream()
            // 得意先実績テーブルの前回配達伝票番号などがNULLだとListにはNullが入る
            // また配達実績テーブルと受注配達テーブルが結合できなかった場合は結果に含めない（設計書に記載あり）
            .filter(
                entity -> Objects.nonNull(entity) && !StringUtils.isEmpty(entity.getAcptdnpyno2())
                    && !StringUtils.isEmpty(entity.getEdano2()))
            // Entity→DTO
            .map(e -> {
                C0501W02JissekiInfoDto jissekiInfo = new C0501W02JissekiInfoDto();
                jissekiInfo.setAcptdnpyno(e.getAcptdnpyno2());
                jissekiInfo.setEdano(e.getEdano2());
                jissekiInfo.setAcptdd1mei(e.getAcptdd());
                jissekiInfo.setDlvrydd1mei(e.getDlvrydd());
                jissekiInfo.setRgrayuryo1mei(e.getRgrayuryo());
                jissekiInfo.setRgramuryo1mei(e.getRgramuryo());
                jissekiInfo.setSrmyuryo1mei(e.getSrmyuryo());
                jissekiInfo.setSrmmuryo1mei(e.getSrmmuryo());
                jissekiInfo.setSonotayuryo1mei(e.getSonotayuryo());
                jissekiInfo.setSonotamuryo1mei(e.getSonotamuryo());
                jissekiInfo.setSeihinmizuuri1mei(e.getSeihinmizuuri());
                jissekiInfo.setSonotaurikin1mei(e.getSonotaurikin());
                jissekiInfo.setDnpystsnm1mei(e.getAcptkbn());
                jissekiInfo.setDnpysts(e.getDnpysys());
                return jissekiInfo;
            })
            // Listに追加
            .forEach(dto -> jissekiInfoList.add(dto));

        tokuisakiInfoConfirmDto.setJissekiInfoDownList(jissekiInfoList);
    }

    /**
     * 11.受注本数集計データ取得
     * 
     * @param tokuisakiInfoConfirmDto
     * @param conditionDto
     */
    private void getJissekiInfoCount(C0501W02TokuisakiInfoConfirmDto tokuisakiInfoConfirmDto,
        C0501W02SearchConditionDto conditionDto) {

        // 基本商品マスタ検索：単独TBL検索：レギュラーボトルの商品コードを検索する。
        S02M007TEntityExample S02M007TExample = new S02M007TEntityExample();
        jp.co.aquaclara.accis.common.domain.repository.entity.S02M007TEntityExample.Criteria S02M007TCriteria =
            S02M007TExample.createCriteria();
        S02M007TCriteria.andApkihoncdEqualTo(CommonConstants.APCD_001)
            .andStkbnEqualTo(CommonConstants.STKBN_0)
            .andLctgrycdEqualTo(CommonConstants.LCTGRYCD_01)
            .andMctgrycdEqualTo(CommonConstants.MCTGRYCD_001)
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        List<S02M007TEntity> s02m007tOutList = s02m007tDao.selectByExample(S02M007TExample);

        // レギュラー商品リスト
        List<String> rgraCdList = new ArrayList<String>();
        // スリム商品リスト
        List<String> srmCdList = new ArrayList<String>();
        // その他商品リスト
        List<String> sonotCdList = new ArrayList<String>();
        rgraCdList.add(" ");
        srmCdList.add(" ");
        sonotCdList.add(" ");
        for (S02M007TEntity e : s02m007tOutList) {
            if (CommonConstants.SCTGRYCD_001.equals(e.getSctgrycd())) {
                rgraCdList.add(e.getShncd());
            } else if (CommonConstants.SCTGRYCD_002.equals(e.getSctgrycd())) {
                srmCdList.add(e.getShncd());
            } else {
                sonotCdList.add(e.getShncd());
            }
        }

        // 経過月数
        int monthCount = 1;

        String uriYm = null;
        if (!Strings.isEmpty(tokuisakiInfoConfirmDto.getUrigetsujidydm())) {
            // 売上月次年月日の年月
            uriYm = tokuisakiInfoConfirmDto.getUrigetsujidydm().substring(0, 6);
        } else {
            // 運用日付の1ヶ月前の年月（売上締が月末となるため）
            uriYm = calcDate(conditionDto.getUnyoDate(), -1).substring(0, 6);
        }

        // 初回設置日が存在する場合
        String setUrigetsujidydmddFrom = "";
        String setUrigetsujidydmddTo = "";
        if (!Strings.isEmpty(tokuisakiInfoConfirmDto.getShokaistupdd())) {
            // 初回年月
            String firstYm = tokuisakiInfoConfirmDto.getShokaistupdd().substring(0, 6);
            // 売上月次年月日開始（終了年月に当月を含めるため、12ヶ月間のデータを取得するには開始年月は11ヶ月前）
            setUrigetsujidydmddFrom = calcDate(uriYm + "01", -11).substring(0, 6);
            // 売上月次年月日終了
            setUrigetsujidydmddTo = uriYm;

            for (int i = 1; i <= 12; i++) {
                monthCount = i;
                if (firstYm.equals(uriYm)) {
                    break;
                }
                firstYm = calcDate(firstYm + "01", 1).substring(0, 6);
            }
        }

        // 小カテゴリー別配達実績取得
        S03T002TC0501W02Entity S03T002TEntityIn = new S03T002TC0501W02Entity();
        S03T002TEntityIn.setApkihoncd(tokuisakiInfoConfirmDto.getApkihoncd());
        S03T002TEntityIn.setShopcd(tokuisakiInfoConfirmDto.getShopcd());
        S03T002TEntityIn.setTokuisakicd(tokuisakiInfoConfirmDto.getTokuisakicd());
        S03T002TEntityIn.setDydmfrom(setUrigetsujidydmddFrom);
        S03T002TEntityIn.setDydmto(setUrigetsujidydmddTo);
        S03T002TEntityIn.setRgraCdList(rgraCdList);
        S03T002TEntityIn.setSrmCdList(srmCdList);
        S03T002TEntityIn.setSonotCdList(sonotCdList);

        List<S03T002TC0501W02Entity> S03T002TEntityOutList =
            s03t002tDao.selectByC0501W02(S03T002TEntityIn);

        String suryotableRegular[] =
            {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"};
        String suryotableSlim[] =
            {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"};
        String suryotableEtc[] =
            {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"};
        for (S03T002TC0501W02Entity e : S03T002TEntityOutList) {
            int idx = Integer.parseInt(e.getDydm().substring(4, 6)) - 1;

            // レギュラー
            if ("0".equals(e.getShn())) {
                // 数量
                int suryo = Integer.parseInt(suryotableRegular[idx]);
                suryo += Integer.parseInt(e.getSuryo());
                suryotableRegular[idx] = String.valueOf(suryo);

                // 合計
                int sum = Integer.parseInt(suryotableRegular[12]);
                sum += Integer.parseInt(e.getSuryo());
                suryotableRegular[12] = String.valueOf(sum);

                // 平均
                BigDecimal bdSum = new BigDecimal(sum);
                BigDecimal bdMonthCount = new BigDecimal(monthCount);
                BigDecimal bdAvg = bdSum.divide(bdMonthCount, 0, RoundingMode.HALF_UP);
                suryotableRegular[13] = String.valueOf(bdAvg);
            }

            // スリム
            if ("1".equals(e.getShn())) {
                // 数量
                int suryo = Integer.parseInt(suryotableSlim[idx]);
                suryo += Integer.parseInt(e.getSuryo());
                suryotableSlim[idx] = String.valueOf(suryo);

                // 合計
                int sum = Integer.parseInt(suryotableSlim[12]);
                sum += Integer.parseInt(e.getSuryo());
                suryotableSlim[12] = String.valueOf(sum);

                // 平均
                BigDecimal bdSum = new BigDecimal(sum);
                BigDecimal bdMonthCount = new BigDecimal(monthCount);
                BigDecimal bdAvg = bdSum.divide(bdMonthCount, 0, RoundingMode.HALF_UP);
                suryotableSlim[13] = String.valueOf(bdAvg);
            }

            // その他
            if ("2".equals(e.getShn())) {
                // 数量
                int suryo = Integer.parseInt(suryotableEtc[idx]);
                suryo += Integer.parseInt(e.getSuryo());
                suryotableEtc[idx] = String.valueOf(suryo);

                // 合計
                int sum = Integer.parseInt(suryotableEtc[12]);
                sum += Integer.parseInt(e.getSuryo());
                suryotableEtc[12] = String.valueOf(sum);

                // 平均
                BigDecimal bdSum = new BigDecimal(sum);
                BigDecimal bdMonthCount = new BigDecimal(monthCount);
                BigDecimal bdAvg = bdSum.divide(bdMonthCount, 0, RoundingMode.HALF_UP);
                suryotableEtc[13] = String.valueOf(bdAvg);
            }
        }

        String monthColorFlg[] = {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"};

        int unyoMonth = Integer.parseInt(conditionDto.getUnyoDate().substring(4, 6));
        for (int i = 0; i < unyoMonth - 1; i++) {
            monthColorFlg[i] = "1";
        }

        C0501W02JissekisumInfoDto jissekisumInfo = new C0501W02JissekisumInfoDto();
        jissekisumInfo.setSuryotableRegular(suryotableRegular);
        jissekisumInfo.setSuryotableSlim(suryotableSlim);
        jissekisumInfo.setSuryotableEtc(suryotableEtc);
        jissekisumInfo.setMonthColorFlg(monthColorFlg);
        tokuisakiInfoConfirmDto.setJissekisumInfo(jissekisumInfo);
    }

    /**
     * 12.問合せ・クレーム履歴データ取得
     * 
     * @param tokuisakiInfoConfirmDto
     */
    private void getClaimInfo(C0501W02TokuisakiInfoConfirmDto tokuisakiInfoConfirmDto) {

        // 得意先問合せクレーム履歴リスト取得
        S14T001TC0501W02Entity s14T001tEntityIn = new S14T001TC0501W02Entity();
        s14T001tEntityIn.setApkihoncd(tokuisakiInfoConfirmDto.getApkihoncd());
        s14T001tEntityIn.setShopcd(tokuisakiInfoConfirmDto.getShopcd());
        s14T001tEntityIn.setTokuisakicd(tokuisakiInfoConfirmDto.getTokuisakicd());
        s14T001tEntityIn.setDetailmamxlength("4");

        List<S14T001TC0501W02Entity> s14t001TEntityOutList =
            s14t001tDao.selectByC0501W02(s14T001tEntityIn);

        List<C0501W02ClaimInfoDto> claimInfoList = new ArrayList<>();
        for (S14T001TC0501W02Entity e : s14t001TEntityOutList) {
            C0501W02ClaimInfoDto claimInfo = new C0501W02ClaimInfoDto();
            claimInfo.setYmdmei(e.getRcptdd());
            claimInfo.setToiawasekbnnmmei(e.getToiawasekbn());
            claimInfo.setToiawasenaiyokbnnmmei(e.getNaiyokbn());
            claimInfo.setToiawasestskbn(e.getSts());
            claimInfo.setToiawasestsnmmei("");
            if (!StringUtils.isEmpty(e.getSts())) {
                claimInfo.setToiawasestsnmmei(
                    getConditionMasterValue(CommonConstants.CONDITION_ID_CN186, e.getSts()));
            }
            claimInfo.setKenmeimei(e.getKenmei());
            claimInfo.setTaishameimei(e.getTnshamei());
            claimInfo.setRcptkanrino(e.getRcptkanrino());
            claimInfoList.add(claimInfo);
        }
        tokuisakiInfoConfirmDto.setClaimInfoList(claimInfoList);
    }

    /**
     * 13.受注存在チェック
     * 
     * @param retmessages
     * @param tokuisakiInfoConfirmDto
     * @param s02M013TEntity
     */
    public void checkAcptData(String apkihoncd, String shopcd, String tokuisakicd,
        List<String> retmessages) {

        // 得意先マスタ検索：単独TBL検索
        S02M013TEntityExample S02M013TExample = new S02M013TEntityExample();
        jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntityExample.Criteria S02M013TCriteria =
            S02M013TExample.createCriteria();
        S02M013TCriteria.andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd)
            .andTokuisakicdEqualTo(tokuisakicd).andYukokbnEqualTo(CommonConstants.FLG_ON);
        List<S02M013TEntity> s02m013tOut = s02m013tDao.selectByExample(S02M013TExample);

        // 有効区分、状態区分チェック
        if (s02m013tOut.size() == 0
            || !CommonConstants.YUKOKBN_1.equals(s02m013tOut.get(0).getYukokbn())
            || !CommonConstants.INIT_TOKUISAKIJOTAI
                .equals(s02m013tOut.get(0).getTokuisakijotai())) {
            return;
        }

        // 受注配達テーブル検索：単独TBL検索
        S04T001TEntityExample S04T001TExample = new S04T001TEntityExample();
        jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntityExample.Criteria S04T001TCriteria =
            S04T001TExample.createCriteria();
        S04T001TCriteria.andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd)
            .andTokuisakicdEqualTo(tokuisakicd).andAcptkbnEqualTo(CommonConstants.KBN_ACPT_SETTI)
            .andAcptkbnshsEqualTo(CommonConstants.KBN_ACPTSYS_SHOKAISETTI)
            .andDlvryjissekiddIsNotNull();
        List<S04T001TEntity> s04t001tOutList = s04t001tDao.selectByExample(S04T001TExample);
        // 受注データチェック
        if (s04t001tOutList.size() != 1) {
            return;
        }

        String dlvryjissekidd = "";
        if (s04t001tOutList.size() > 0
            && !StringUtils.isEmpty(s04t001tOutList.get(0).getDlvryjissekidd())) {
            dlvryjissekidd = s04t001tOutList.get(0).getDlvryjissekidd();
        }
        // 受注配達テーブル検索：単独TBL検索
        S04T001TExample = new S04T001TEntityExample();
        S04T001TCriteria = S04T001TExample.createCriteria();
        S04T001TCriteria.andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd)
            .andTokuisakicdEqualTo(tokuisakicd).andAcptstsNotEqualTo(CommonConstants.DNPYSTS_09)
            .andAcptkbnNotEqualTo(CommonConstants.KBN_ACPT_SETTI)
            .andAcptkbnshsNotEqualTo(CommonConstants.KBN_ACPTSYS_SHOKAISETTI)
            .andDlvryjissekiddGreaterThanOrEqualTo(dlvryjissekidd)
            .andYukokbnEqualTo(CommonConstants.FLG_ON);
        s04t001tOutList = s04t001tDao.selectByExample(S04T001TExample);
        // 受注データチェック
        if (s04t001tOutList.size() > 0) {
            return;
        }

        BusinessExceptionFactory.setMessageList(retmessages, MessageID.W218);

    }

    /**
     * 14.受注日（開始、終了）取得
     * 
     * @param tokuisakiInfoConfirmDto
     * @param s02M013TEntity
     */
    void calcAcptdd(C0501W02TokuisakiInfoConfirmDto tokuisakiInfoConfirmDto,
        C0501W02SearchConditionDto conditionDto) {

        String targetDate = "";

        // 受注日（開始）
        targetDate = calcDate(conditionDto.getUnyoDate(), -4);
        tokuisakiInfoConfirmDto.setAcptddFrom(targetDate.substring(0, 6) + "01");

        // 受注日（終了）
        targetDate = conditionDto.getUnyoDate();
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Integer.parseInt(targetDate.substring(0, 4)),
            Integer.parseInt(targetDate.substring(4, 6)) - 1,
            Integer.parseInt(targetDate.substring(6, 8)));
        String lastDate = String.valueOf(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        tokuisakiInfoConfirmDto.setAcptddTo(targetDate.substring(0, 6) + lastDate);

    }

    /**
     * 15. ポイントランク取得
     *
     *
     * @param tokuisakiInfoConfirmDto
     */
    // ポイントランク取得
    public void getPointRunk(C0501W02TokuisakiInfoConfirmDto tokuisakiInfoConfirmDto) {
        S02M204TC0501W02Entity s02M204tEntityIn = new S02M204TC0501W02Entity();
        s02M204tEntityIn.setApkihoncd(tokuisakiInfoConfirmDto.getApkihoncd());
        s02M204tEntityIn.setShopcd(tokuisakiInfoConfirmDto.getShopcd());
        s02M204tEntityIn.setTokuisakicd(tokuisakiInfoConfirmDto.getTokuisakicd());

        List<S02M204TC0501W02Entity> s02M204TEntityOutList =
            s02m204tDao.selectByC0501(s02M204tEntityIn);

        List<C0501W02TokuisakiPointRunkDto> pointRunkList = new ArrayList<>();
        for (S02M204TC0501W02Entity e : s02M204TEntityOutList) {
            C0501W02TokuisakiPointRunkDto pointRunk = new C0501W02TokuisakiPointRunkDto();
            pointRunk.setTekiyoudd(e.getTekiyoudd());
            pointRunk.setPointrnk(e.getPointrnk());
            pointRunk.setPointbairitu(e.getPointbairitu());
            pointRunkList.add(pointRunk);
        }
        tokuisakiInfoConfirmDto.setPointRunkList(pointRunkList);
    }

    /**
     * 16. 作業グループコード取得
     *
     *
     * @param tokuisakiInfoConfirmDto
     */
    private void getSagyogroup(C0501W02TokuisakiInfoConfirmDto tokuisakiInfoConfirmDto) {
        // 拠点、またはエリアがnullの場合は空を設定
        if (StringUtils.isEmpty(tokuisakiInfoConfirmDto.getDlvrykyotencd())
            && StringUtils.isEmpty(tokuisakiInfoConfirmDto.getDlvryareacd())) {
            tokuisakiInfoConfirmDto.setSagyogroupcd("");
            return;
        }
        // 配達エリアグループマスタから作業グループを取得
        S02M302TEntityExample example = new S02M302TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(tokuisakiInfoConfirmDto.getApkihoncd())
            .andShopcdEqualTo(tokuisakiInfoConfirmDto.getShopcd())
            .andDlvrykyotencdEqualTo(tokuisakiInfoConfirmDto.getDlvrykyotencd())
            .andDlvryareacdEqualTo(tokuisakiInfoConfirmDto.getDlvryareacd())
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        List<S02M302TEntity> entities = s02m302tDao.selectByExample(example);
        if (entities.size() == 0) {
            tokuisakiInfoConfirmDto.setSagyogroupcd("");
        } else {
            tokuisakiInfoConfirmDto.setSagyogroupcd(entities.get(0).getSagyogroupcd());
        }
    }

    /**
     * アスタリスク付名称編集
     * 
     * @param itemNm
     * @return
     */
    private String asteriskChangeNm(String itemNm) {
        String val = itemNm;
        if (Strings.isEmpty(val)) {
            val = "";
        } else if (!Strings.isEmpty(val) && val.length() >= 2) {
            val = val.substring(0, 2) + "**********************************************";
        } else if (!Strings.isEmpty(val) && val.length() == 1) {
            val = val + "************************************************";
        }
        return val;
    }

    /**
     * アスタリスク付電話番号編集
     * 
     * @param itemNm
     * @return
     */
    private String asteriskChangeDn(String itemNm) {
        String val = itemNm;
        int num = 0;
        if (Strings.isEmpty(val)) {
            val = "";
        } else if (!Strings.isEmpty(val) && val.length() >= 7) {
            val = val.substring(0, 7) + "********";
        } else if (!Strings.isEmpty(val) && val.length() < 7) {
            num = 15 - val.length();
            for (int i = 0; i < num; i++) {
                val += "*";
            }
        }
        return val;
    }

    /**
     * 本数超過分編集
     * 
     * @param itemNm 本数
     * @return 本数（上限を超える場合は上限値）
     */
    private String upperLimmitEdit(Integer itemNm) {
        final int maxValue = 99999;
        // 引数がNULLの場合は空文字を返す
        if (Objects.isNull(itemNm)) {
            return "";
        }
        // 引数が最大値を超える場合は最大値を適用
        int ret = itemNm.intValue() > maxValue ? maxValue : itemNm.intValue();
        // 文字列にして返す
        return String.valueOf(ret);
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
     * 日付計算
     * 
     * @param targetDate 対象日付
     * @param calcMonth 加減月
     * @return
     */
    private String calcDate(String targetDate, int calcMonth) {
        final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate target = LocalDate.parse(targetDate, format);
        LocalDate calculated = target.plusMonths(calcMonth);
        return calculated.format(format);
    }

}
