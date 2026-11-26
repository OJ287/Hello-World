package jp.co.aquaclara.accis.web.controller.c0501;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.web.controller.c0501.bean.C0501W02BaseBean;
import jp.co.aquaclara.accis.web.controller.c0501.bean.C0501W02Meisai1Bean;
import jp.co.aquaclara.accis.web.controller.c0501.bean.C0501W02Meisai2Bean;
import jp.co.aquaclara.accis.web.controller.c0501.bean.C0501W02Meisai3Bean;
import jp.co.aquaclara.accis.web.controller.c0501.bean.C0501W02Meisai4Bean;
import jp.co.aquaclara.accis.web.controller.c0501.bean.C0501W02Meisai5Bean;
import jp.co.aquaclara.accis.web.controller.c0501.bean.C0501W02Meisai6Bean;
import jp.co.aquaclara.accis.web.controller.c0501.bean.C0501W02Meisai7Bean;
import jp.co.aquaclara.accis.web.controller.c0501.bean.C0501W02OrderAndDeliveryBean;
import jp.co.aquaclara.accis.web.controller.c0501.bean.C0501W02SalesBean;
import jp.co.aquaclara.accis.web.controller.c0501.form.C0501W02Form;
import jp.co.aquaclara.accis.web.controller.commons.AccisWebBaseController;
import jp.co.aquaclara.accis.web.controller.commons.LoginSession;
import jp.co.aquaclara.accis.web.controller.commons.UserSessionForm;
import jp.co.aquaclara.accis.web.domain.dto.c0501.C0501W02ClaimInfoDto;
import jp.co.aquaclara.accis.web.domain.dto.c0501.C0501W02JissekiInfoDto;
import jp.co.aquaclara.accis.web.domain.dto.c0501.C0501W02SearchConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.c0501.C0501W02ServerInfoDto;
import jp.co.aquaclara.accis.web.domain.dto.c0501.C0501W02SkysakiInfoDto;
import jp.co.aquaclara.accis.web.domain.dto.c0501.C0501W02TokuisakiInfoConfirmDto;
import jp.co.aquaclara.accis.web.domain.dto.c0501.C0501W02TokuisakiPointRunkDto;
import jp.co.aquaclara.accis.web.domain.service.c0501.C0501W02Service;

/**
 * C0501W02_得意先情報確認(得意先情報確認)コントローラクラス.
 */
@Controller
@RequestMapping("/c0501w02")
public class C0501W02Controller extends AccisWebBaseController {

    private static final String VIEW_NAME = "c0501/c0501w02_form";

    /**
     * ログインセッション情報.
     */
    @Autowired
    private LoginSession loginSession;

    /**
     * ユーザセッション情報.
     */
    @Autowired
    private UserSessionForm userSessionForm;

    /**
     * 得意先情報確認(得意先情報確認)サービスクラス
     */
    @Autowired
    private C0501W02Service service;


    /**
     * 固定値定義
     */
    // 未締請求日表示文字列
    public static final String MIJIMESKDATE_DISPLAY_STRING = "現在";
    // マスキング文字
    public static final String MASKING_STRING = "**********";

    /**
     * 初期表示.
     * 
     * @param form C0501W02Form
     * @param apkihoncd
     * @param shopcd
     * @param tokuisakicd
     * @param mv ModelAndView
     * @return ModelAndView
     */
    @GetMapping("/{apkihoncd}/{shopcd}/{tokuisakicd}")
    public ModelAndView Init(@ModelAttribute("c0501W02Form") C0501W02Form form,
        @PathVariable("apkihoncd") String apkihoncd, @PathVariable("shopcd") String shopcd,
        @PathVariable("tokuisakicd") String tokuisakicd, ModelAndView mv) {

        // 画面の各オブジェクトへ値の初期設定.
        mv.addObject("sessionScope", userSessionForm);

        mv.addObject("apkihoncd", apkihoncd);
        mv.addObject("shopcd", shopcd);
        mv.addObject("cdkensakuflg", "1");

        // サービス呼出
        C0501W02SearchConditionDto conditionDto = new C0501W02SearchConditionDto();
        conditionDto.setApkihoncd(apkihoncd);
        conditionDto.setShopcd(shopcd);
        conditionDto.setTokuisakicd(tokuisakicd);
        conditionDto.setTnshacd(userSessionForm.getTnshacd());
        conditionDto.setUnyoDate(loginSession.getUnyoDate());
        conditionDto.setTnshamenuctgrycd(userSessionForm.getTnshamenuctgrycd());

        C0501W02TokuisakiInfoConfirmDto tokuisakiInfoConfirmDto = service.search(conditionDto);

        init(mv);

        initForm(form, tokuisakiInfoConfirmDto);

        // 表示画面設定
        mv.setViewName(VIEW_NAME);
        return mv;
    }

    /**
     * 基本情報の初期化.
     * 
     * @param mv ModelAndView
     */
    private void init(ModelAndView mv) {}

    /**
     * formの初期化.
     * 
     * @param form C0501W02Form
     * @param tokuisakiInfoConfirmDto C0501W02TokuisakiInfoConfirmDto
     */
    private void initForm(C0501W02Form form,
        C0501W02TokuisakiInfoConfirmDto tokuisakiInfoConfirmDto) {

        // form内のオブジェクトを初期化
        form.setKttcd(tokuisakiInfoConfirmDto.getTokuisakicd());
        form.setBaseBean(getInitBaseBean(tokuisakiInfoConfirmDto));
        form.setOrderAndDeliveryBean(getInitDeliveryBean(tokuisakiInfoConfirmDto));
        form.setSalesBean(getInitSalesBean(tokuisakiInfoConfirmDto));
        form.setMeisai1BeanList(getInitMeisai1BeanList(tokuisakiInfoConfirmDto));
        form.setMeisai2BeanList(getInitMeisai2BeanList(tokuisakiInfoConfirmDto));
        form.setMeisai3BeanList(getInitMeisai3BeanList(tokuisakiInfoConfirmDto));
        form.setMeisai4BeanList(getInitMeisai4BeanList(tokuisakiInfoConfirmDto));
        form.setMeisai5BeanList(getInitMeisai5BeanList(tokuisakiInfoConfirmDto));
        form.setMeisai6BeanList(getInitMeisai6BeanList(tokuisakiInfoConfirmDto));
        form.setMeisai7BeanList(getInitMeisai7BeanList(tokuisakiInfoConfirmDto));
    }

    /**
     * C0501W02BaseBean設定
     * 
     * @param tokuisakiInfoConfirmDto C0501W02TokuisakiInfoConfirmDto
     * @return C0501W02BaseBean
     */
    private C0501W02BaseBean getInitBaseBean(
        C0501W02TokuisakiInfoConfirmDto tokuisakiInfoConfirmDto) {

        C0501W02BaseBean baseBean = new C0501W02BaseBean();

        // AP基本コード
        baseBean.setApkihoncd(tokuisakiInfoConfirmDto.getApkihoncd());
        // ショップコード
        baseBean.setShopcd(tokuisakiInfoConfirmDto.getShopcd());
        // 得意先コード
        baseBean.setTokuisakicd(tokuisakiInfoConfirmDto.getTokuisakicd());
        // 請求先コード
        baseBean.setSkysakicd(tokuisakiInfoConfirmDto.getSkysakicd());
        // 照会得意先当月売上明細情報権限
        baseBean.setKengentokuisakitotsukiumjh(
            tokuisakiInfoConfirmDto.getKengenpoptokuisakitotsukiumjh());
        // メッセージ2
        baseBean.setMsg2(tokuisakiInfoConfirmDto.getMsg2());
        // 得意先名称１
        baseBean.setTokuisakinmn1(tokuisakiInfoConfirmDto.getTokuisakinmn1());
        // 連絡先番号区分名称１
        baseBean.setRenrakusakinokbnnm1(tokuisakiInfoConfirmDto.getRenrakusakinokbnnm1());
        // 連絡先番号１
        baseBean.setRenrakusakinon1(tokuisakiInfoConfirmDto.getRenrakusakinon1());
        // 連絡先番号区分名称２
        baseBean.setRenrakusakinokbnnm2(tokuisakiInfoConfirmDto.getRenrakusakinokbnnm2());
        // 連絡先番号２
        baseBean.setRenrakusakinon2(tokuisakiInfoConfirmDto.getRenrakusakinon2());
        // 得意先名称２
        baseBean.setTokuisakinmn2(tokuisakiInfoConfirmDto.getTokuisakinmn2());
        // 連絡先番号区分名称３
        baseBean.setRenrakusakinokbnnm3(tokuisakiInfoConfirmDto.getRenrakusakinokbnnm3());
        // 連絡先番号３
        baseBean.setRenrakusakinon3(tokuisakiInfoConfirmDto.getRenrakusakinon3());
        // 連絡先番号区分名称４
        baseBean.setRenrakusakinokbnnm4(tokuisakiInfoConfirmDto.getRenrakusakinokbnnm4());
        // 連絡先番号４
        baseBean.setRenrakusakinon4(tokuisakiInfoConfirmDto.getRenrakusakinon4());
        // 申込受付日
        baseBean.setMoshikomircptdd(
            DateTimeUtil.toSlashedDate(tokuisakiInfoConfirmDto.getMoshikomircptdd()));
        // 入会日
        baseBean.setNyukaidd(DateTimeUtil.toSlashedDate(tokuisakiInfoConfirmDto.getNyukaidd()));
        // 退会日
        baseBean.setTaikaidd(DateTimeUtil.toSlashedDate(tokuisakiInfoConfirmDto.getTaikaidd()));
        // 種別
        baseBean.setKind(tokuisakiInfoConfirmDto.getKind());
        // 回収方法
        baseBean.setKsihoho(tokuisakiInfoConfirmDto.getKsihoho());
        // 入金予定日区分
        baseBean.setNyknytiddkbn(tokuisakiInfoConfirmDto.getNyknytiddkbn());
        // 入金予定日
        baseBean.setNyknytidd(tokuisakiInfoConfirmDto.getNyknytidd());
        // 締日
        baseBean.setShimedd(tokuisakiInfoConfirmDto.getShimedd());
        // 照会最新決済結果表示権限
        baseBean.setKengenpopsaishinksikekka(tokuisakiInfoConfirmDto.getKengenpopsaishinksikekka());
        // 決済締年月日
        baseBean.setKsishimedydmdd(
            DateTimeUtil.toSlashedDate(tokuisakiInfoConfirmDto.getKsishimedydmdd()));
        // 決済結果名称
        baseBean.setKsikekkanm(tokuisakiInfoConfirmDto.getKsikekkanm());
        // 決済金額
        baseBean.setKsikin(ItemUtil.editComma(tokuisakiInfoConfirmDto.getKsikin()));
        // 照会サーバー情報権限
        baseBean.setKengenpopwsvjoho(tokuisakiInfoConfirmDto.getKengenpopwsvjoho());
        // レンタル料金
        baseBean.setRtlrkn(ItemUtil.editComma(tokuisakiInfoConfirmDto.getRtlrkn()));
        // 連絡事項（公開）
        baseBean.setRenrakujikokokai(replaceBrTag(tokuisakiInfoConfirmDto.getRenrakujikokokai()));
        // 印刷メモ１
        baseBean.setPrntmemon1(tokuisakiInfoConfirmDto.getPrntmemon1());
        // 印刷メモ２
        baseBean.setPrntmemon2(tokuisakiInfoConfirmDto.getPrntmemon2());
        // 印刷メモ３
        baseBean.setPrntmemon3(tokuisakiInfoConfirmDto.getPrntmemon3());
        // 受注登録ボタン表示可否フラグ
        baseBean.setJuchubtnhyoji(tokuisakiInfoConfirmDto.getJuchubtnhyoji());
        // 問合せクレームボタン表示可否フラグ
        baseBean.setClaimbtnhyoji(tokuisakiInfoConfirmDto.getClaimbtnhyoji());
        // 作業予約確認ボタン表示可否フラグ
        baseBean.setSagyoyoyakubtnhyoji(tokuisakiInfoConfirmDto.getSagyoyoyakubtnhyoji());
        // ＣＴＩ連携フラグ
        baseBean.setCtiservletflg("0");
        if (loginSession.getLoginKind() == 1 || loginSession.getLoginKind() == 2
            || loginSession.getLoginKind() == 3) {
            baseBean.setCtiservletflg("1");
        }
        // 照会問合せクレーム履歴権限
        baseBean
            .setKengenpoptoiawaseclmrireki(tokuisakiInfoConfirmDto.getKengenpoptoiawaseclmrireki());
        // 事業所管理
        baseBean.setJigyoshokanri(tokuisakiInfoConfirmDto.getJigyoshokanri());
        // 受注日（開始）
        baseBean.setAcptstrtdd(tokuisakiInfoConfirmDto.getAcptddFrom());
        // 受注日（終了）
        baseBean.setAcptshrydd(tokuisakiInfoConfirmDto.getAcptddTo());
        // 消費税（ラベル）
        baseBean.setShohitaxkinlabel(tokuisakiInfoConfirmDto.getShohitaxkinlabel());

        // 登録済受注件数
        baseBean.setAcptcnt(tokuisakiInfoConfirmDto.getAcptcnt());

        // 作業グループコード
        baseBean.setSagyogroupcd(tokuisakiInfoConfirmDto.getSagyogroupcd());

        return baseBean;
    }

    /**
     * C0501W02OrderAndDeliveryBean設定
     * 
     * @param tokuisakiInfoConfirmDto C0501W02TokuisakiInfoConfirmDto
     * @return C0501W02OrderAndDeliveryBean
     */
    private C0501W02OrderAndDeliveryBean getInitDeliveryBean(
        C0501W02TokuisakiInfoConfirmDto tokuisakiInfoConfirmDto) {

        C0501W02OrderAndDeliveryBean orderAndDeliveryBean = new C0501W02OrderAndDeliveryBean();

        // 配達拠点名称
        orderAndDeliveryBean.setDlvrykyotennm(tokuisakiInfoConfirmDto.getDlvrykyotennm());
        // 配達エリア名称
        orderAndDeliveryBean.setDlvryareanm(tokuisakiInfoConfirmDto.getDlvryareanm());
        // 地図番号
        orderAndDeliveryBean.setChizuno(tokuisakiInfoConfirmDto.getChizuno());
        // 配達順
        orderAndDeliveryBean.setDlvryjun(tokuisakiInfoConfirmDto.getDlvryjun());
        // 配達方法区分
        orderAndDeliveryBean.setDlvryhohokbn(tokuisakiInfoConfirmDto.getDlvryhohokbn());
        // 配達方法
        orderAndDeliveryBean.setDlvryhoho(tokuisakiInfoConfirmDto.getDlvryhoho());
        // 配達会社名称
        orderAndDeliveryBean.setDlvrycmpnynm(tokuisakiInfoConfirmDto.getDlvrycmpnynm());
        // 受注受付区分
        orderAndDeliveryBean.setAcptrcptkbn(tokuisakiInfoConfirmDto.getAcptrcptkbn());
        // 受注受付区分名称
        orderAndDeliveryBean.setAcptrcptkbnnm(tokuisakiInfoConfirmDto.getAcptrcptkbnnm());
        // 担当者名
        orderAndDeliveryBean.setTnshamei(tokuisakiInfoConfirmDto.getDlvrytnshanm());
        // 預かりボトル１２ｌ本数
        orderAndDeliveryBean
            .setAzukaribtln1n2rittoruhns(tokuisakiInfoConfirmDto.getAzukaribtln1n2rittoruhns());
        // 預かりボトル８ｌ本数
        orderAndDeliveryBean
            .setAzukaribtln8rittoruhns(tokuisakiInfoConfirmDto.getAzukaribtln8rittoruhns());
        // パターン権限
        orderAndDeliveryBean.setKengenpopptn(tokuisakiInfoConfirmDto.getKengenpopptn());
        // 配達WEEK A
        orderAndDeliveryBean.setDlvryshua(tokuisakiInfoConfirmDto.getDlvryshua());
        // 配達WEEK B
        orderAndDeliveryBean.setDlvryshub(tokuisakiInfoConfirmDto.getDlvryshub());
        // 配達WEEK C
        orderAndDeliveryBean.setDlvryshuc(tokuisakiInfoConfirmDto.getDlvryshuc());
        // 配達WEEK D
        orderAndDeliveryBean.setDlvryshud(tokuisakiInfoConfirmDto.getDlvryshud());
        // 日曜配達可能日
        orderAndDeliveryBean.setSunddlvrykanodd(tokuisakiInfoConfirmDto.getSunddlvrykanodd());
        // 月曜配達可能日
        orderAndDeliveryBean.setMonddlvrykanodd(tokuisakiInfoConfirmDto.getMonddlvrykanodd());
        // 火曜配達可能日
        orderAndDeliveryBean.setTueddlvrykanodd(tokuisakiInfoConfirmDto.getTueddlvrykanodd());
        // 水曜配達可能日
        orderAndDeliveryBean.setWedddlvrykanodd(tokuisakiInfoConfirmDto.getWedddlvrykanodd());
        // 木曜配達可能日
        orderAndDeliveryBean.setTheddlvrykanodd(tokuisakiInfoConfirmDto.getTheddlvrykanodd());
        // 金曜配達可能日/
        orderAndDeliveryBean.setFriddlvrykanodd(tokuisakiInfoConfirmDto.getFriddlvrykanodd());
        // 土曜配達可能日
        orderAndDeliveryBean.setSatddlvrykanodd(tokuisakiInfoConfirmDto.getSatddlvrykanodd());
        // 受注時間指定時刻
        orderAndDeliveryBean.setAcptjikanshiteijikoku(
            DateTimeUtil.toColonedDateHHMM(tokuisakiInfoConfirmDto.getAcptjikanshiteijikoku()));
        // 受注時間指定区分名称
        orderAndDeliveryBean
            .setAcptjikanshiteikbnnm(tokuisakiInfoConfirmDto.getAcptjikanshiteikbnnm());
        // 納品先情報１
        orderAndDeliveryBean.setNohinsakijoho1(tokuisakiInfoConfirmDto.getNohinsakijoho1());
        // 納品先情報２
        orderAndDeliveryBean.setNohinsakijoho2(tokuisakiInfoConfirmDto.getNohinsakijoho2());
        // 不在置き可能フラグ
        orderAndDeliveryBean.setFzaiokikanoflg(tokuisakiInfoConfirmDto.getFzaiokikanoflg());
        // 照会受注配達実績権限
        orderAndDeliveryBean
            .setKengenpopacptdlvryjisseki(tokuisakiInfoConfirmDto.getKengenpopacptdlvryjisseki());
        // １月色表示フラグ
        orderAndDeliveryBean
            .setJanuaryFlg(tokuisakiInfoConfirmDto.getJissekisumInfo().getMonthColorFlg()[0]);
        // ２月色表示フラグ
        orderAndDeliveryBean
            .setFebruaryFlg(tokuisakiInfoConfirmDto.getJissekisumInfo().getMonthColorFlg()[1]);
        // ３月色表示フラグ
        orderAndDeliveryBean
            .setMarchFlg(tokuisakiInfoConfirmDto.getJissekisumInfo().getMonthColorFlg()[2]);
        // ４月色表示フラグ
        orderAndDeliveryBean
            .setAprilFlg(tokuisakiInfoConfirmDto.getJissekisumInfo().getMonthColorFlg()[3]);
        // ５月色表示フラグ
        orderAndDeliveryBean
            .setMayFlg(tokuisakiInfoConfirmDto.getJissekisumInfo().getMonthColorFlg()[4]);
        // ６月色表示フラグ
        orderAndDeliveryBean
            .setJuneFlg(tokuisakiInfoConfirmDto.getJissekisumInfo().getMonthColorFlg()[5]);
        // ７月色表示フラグ
        orderAndDeliveryBean
            .setJulyFlg(tokuisakiInfoConfirmDto.getJissekisumInfo().getMonthColorFlg()[6]);
        // ８月色表示フラグ
        orderAndDeliveryBean
            .setAugustFlg(tokuisakiInfoConfirmDto.getJissekisumInfo().getMonthColorFlg()[7]);
        // ９月色表示フラグ
        orderAndDeliveryBean
            .setSeptemberFlg(tokuisakiInfoConfirmDto.getJissekisumInfo().getMonthColorFlg()[8]);
        // １０月色表示フラグ
        orderAndDeliveryBean
            .setOctoberFlg(tokuisakiInfoConfirmDto.getJissekisumInfo().getMonthColorFlg()[9]);
        // １１月色表示フラグ
        orderAndDeliveryBean
            .setNovemberFlg(tokuisakiInfoConfirmDto.getJissekisumInfo().getMonthColorFlg()[10]);
        // １２月色表示フラグ
        orderAndDeliveryBean
            .setDecemberFlg(tokuisakiInfoConfirmDto.getJissekisumInfo().getMonthColorFlg()[11]);
        // レギュラー本数合計
        orderAndDeliveryBean.setRgrahnssm(ItemUtil
            .editComma(tokuisakiInfoConfirmDto.getJissekisumInfo().getSuryotableRegular()[12]));
        // レギュラー本数平均
        orderAndDeliveryBean.setRgrahnsheikin(ItemUtil
            .editComma(tokuisakiInfoConfirmDto.getJissekisumInfo().getSuryotableRegular()[13]));
        // スリム本数合計
        orderAndDeliveryBean.setSrmhnssm(ItemUtil
            .editComma(tokuisakiInfoConfirmDto.getJissekisumInfo().getSuryotableSlim()[12]));
        // スリム本数平均
        orderAndDeliveryBean.setSrmhnsheikin(ItemUtil
            .editComma(tokuisakiInfoConfirmDto.getJissekisumInfo().getSuryotableSlim()[13]));
        // その他本数合計
        orderAndDeliveryBean.setSonotahnssm(
            ItemUtil.editComma(tokuisakiInfoConfirmDto.getJissekisumInfo().getSuryotableEtc()[12]));
        // その他本数平均
        orderAndDeliveryBean.setSonotahnsheikin(
            ItemUtil.editComma(tokuisakiInfoConfirmDto.getJissekisumInfo().getSuryotableEtc()[13]));

        return orderAndDeliveryBean;

    }

    /**
     * C0501W02SalesBean設定
     * 
     * @param tokuisakiInfoConfirmDto C0501W02TokuisakiInfoConfirmDto
     * @return C0501W02SalesBean
     */
    private C0501W02SalesBean getInitSalesBean(
        C0501W02TokuisakiInfoConfirmDto tokuisakiInfoConfirmDto) {

        C0501W02SalesBean salesBean = new C0501W02SalesBean();

        // 営業エリア名称
        salesBean.setEigyoareanm(tokuisakiInfoConfirmDto.getEigyoareanm());
        // 営業担当者名称
        salesBean.setEigyotnshanm(tokuisakiInfoConfirmDto.getEigyotnshanm());
        // 受付区分名称
        salesBean.setRcptkbnnm(tokuisakiInfoConfirmDto.getRcptkbnnm());
        // 獲得区分名称
        salesBean.setKakutokukbnnm(tokuisakiInfoConfirmDto.getKakutokukbnnm());
        // 媒体区分名称
        salesBean.setBaitaikbnnm(tokuisakiInfoConfirmDto.getBaitaikbnnm());
        // 獲得詳細コード
        salesBean.setKakutokushscd(tokuisakiInfoConfirmDto.getKakutokushscd());
        // 獲得詳細
        salesBean.setKakutokushs(tokuisakiInfoConfirmDto.getKakutokushs());
        // 獲得詳細メモ
        salesBean.setKakutokushsmemo(tokuisakiInfoConfirmDto.getKakutokushsmemo());
        // 本部エージェントコード
        salesBean.setHonbuejntcd(tokuisakiInfoConfirmDto.getHonbuejntcd());
        // 本部エージェントコード名称
        salesBean.setHonbuejntcdnm(tokuisakiInfoConfirmDto.getHonbuejntcdnm());
        // 獲得エージェントコード
        salesBean.setKakutokuejntcd(tokuisakiInfoConfirmDto.getKakutokuejntcd());
        // 獲得エージェントコード名称
        salesBean.setKakutokuejntcdnm(tokuisakiInfoConfirmDto.getKakutokuejntcdnm());
        // 獲得担当者コード
        salesBean.setKakutokutnshacd(tokuisakiInfoConfirmDto.getKakutokutnshacd());
        // 獲得担当者名
        salesBean.setKakutokutnshamei(tokuisakiInfoConfirmDto.getKakutokutnshamei());
        // 料金種別特別単価フラグ
        salesBean.setRknkindtokubetsuuntflg(tokuisakiInfoConfirmDto.getRknkindtokubetsuuntflg());
        // 特別単価区分名称
        salesBean.setTokubetsuuntkbnnm(tokuisakiInfoConfirmDto.getTokubetsuuntkbnnm());
        // まとめ買いサービス区分
        salesBean.setMtmkisvkbn(tokuisakiInfoConfirmDto.getMtmkisvkbn());
        // まとめ買い有料本数
        salesBean.setMtmkiyuryohns(ItemUtil.editComma(tokuisakiInfoConfirmDto.getMtmkiyuryohns()));
        // まとめ買い無料本数
        salesBean.setMtmkimuryohns(ItemUtil.editComma(tokuisakiInfoConfirmDto.getMtmkimuryohns()));
        // 初回設置日
        salesBean
            .setShokaistupdd(DateTimeUtil.toSlashedDate(tokuisakiInfoConfirmDto.getShokaistupdd()));
        // 紹介得意先コード
        salesBean.setShoukaitokuisakicd(tokuisakiInfoConfirmDto.getShoukaitokuisakicd());
        // 得意先名称
        salesBean.setTokuisakinm(tokuisakiInfoConfirmDto.getTokuisakinm());
        // 紹介者数
        salesBean.setShoshakazu(tokuisakiInfoConfirmDto.getShoshakazu());
        // 保証金区分
        salesBean.setHoshokinkbn(tokuisakiInfoConfirmDto.getHoshokinkbn());
        // 保証金区分名称
        salesBean.setHoshokinkbnnm(tokuisakiInfoConfirmDto.getHoshokinkbnnm());
        // 保証金預かり日
        salesBean.setHoshokinazukaridd(
            DateTimeUtil.toSlashedDate(tokuisakiInfoConfirmDto.getHoshokinazukaridd()));
        // 保証金預かり金額
        salesBean.setHoshokinazukarikin(
            ItemUtil.editComma(tokuisakiInfoConfirmDto.getHoshokinazukarikin()));
        // 保証金返却日
        salesBean.setHoshokinhenkyakudd(
            DateTimeUtil.toSlashedDate(tokuisakiInfoConfirmDto.getHoshokinhenkyakudd()));
        // 保証金返却金額
        salesBean.setHoshokinhenkyakukin(
            ItemUtil.editComma(tokuisakiInfoConfirmDto.getHoshokinhenkyakukin()));

        return salesBean;

    }

    /**
     * List<C0501W02Meisai1Bean>設定
     * 
     * @param tokuisakiInfoConfirmDto C0501W02TokuisakiInfoConfirmDto
     * @return C0501W02SalesBean
     */
    private List<C0501W02Meisai1Bean> getInitMeisai1BeanList(
        C0501W02TokuisakiInfoConfirmDto tokuisakiInfoConfirmDto) {

        List<C0501W02Meisai1Bean> meisai1List = new ArrayList<>();
        for (C0501W02ServerInfoDto e : tokuisakiInfoConfirmDto.getServerInfoList()) {

            C0501W02Meisai1Bean meisai1Bean = new C0501W02Meisai1Bean();
            // サーバー種類明細
            meisai1Bean.setWsvshuruimei(e.getWsvshuruimei());
            // フロントパネル明細
            meisai1Bean.setFrontpanelmei(e.getFrontpanelmei());
            // 台数明細
            meisai1Bean.setDaisumei(e.getDaisumei());

            meisai1List.add(meisai1Bean);
        }

        return meisai1List;
    }

    /**
     * List<C0501W02Meisai2Bean>設定
     * 
     * @param tokuisakiInfoConfirmDto C0501W02TokuisakiInfoConfirmDto
     * @return C0501W02SalesBean
     */
    private List<C0501W02Meisai2Bean> getInitMeisai2BeanList(
        C0501W02TokuisakiInfoConfirmDto tokuisakiInfoConfirmDto) {

        List<C0501W02Meisai2Bean> meisai2List = new ArrayList<>();
        for (C0501W02SkysakiInfoDto e : tokuisakiInfoConfirmDto.getSkysakiInfoList()) {
            C0501W02Meisai2Bean meisai2Bean = new C0501W02Meisai2Bean();

            // 照会請求締日明細
            if (!MIJIMESKDATE_DISPLAY_STRING.equals(e.getSkyshimeddmei())) {
                meisai2Bean.setSkyshimeddmei(DateTimeUtil.toSlashedDate(e.getSkyshimeddmei()));
            }
            // 前回請求金額明細
            meisai2Bean.setZnkskykinmei(ItemUtil.editComma(e.getZnkskykinmei()));
            // 入金金額明細
            meisai2Bean.setNyknkinmei(ItemUtil.editComma(e.getNyknkinmei()));
            // 売上金額明細
            meisai2Bean.setUrikinmei(ItemUtil.editComma(e.getUrikinmei()));
            // 内消費税金額明細
            if (MASKING_STRING.equals(e.getUchishohitaxkinmei())) {
                meisai2Bean.setUchishohitaxkinmei(e.getUchishohitaxkinmei());
            } else {
                meisai2Bean.setUchishohitaxkinmei(ItemUtil.editComma(e.getUchishohitaxkinmei()));
            }
            // ８％税込売上額計明細
            if (MASKING_STRING.equals(e.getN8persiirekinmei())) {
                meisai2Bean.setN8persiirekinmei(e.getN8persiirekinmei());
            } else {
                meisai2Bean.setN8persiirekinmei(ItemUtil.editComma(e.getN8persiirekinmei()));
            }
            // １０％税込売上額計明細
            if (MASKING_STRING.equals(e.getUchishohitaxkinmei())) {
                meisai2Bean.setN10persiirekinmei(e.getN10persiirekinmei());
            } else {
                meisai2Bean.setN10persiirekinmei(ItemUtil.editComma(e.getN10persiirekinmei()));
            }
            // 請求金額明細
            if (MASKING_STRING.equals(e.getSkykinmei())) {
                meisai2Bean.setSkykinmei(e.getSkykinmei());
            } else {
                meisai2Bean.setSkykinmei(ItemUtil.editComma(e.getSkykinmei()));
            }

            meisai2List.add(meisai2Bean);

        }

        return meisai2List;
    }

    /**
     * List<C0501W02Meisai3Bean>設定
     * 
     * @param tokuisakiInfoConfirmDto C0501W02TokuisakiInfoConfirmDto
     * @return C0501W02SalesBean
     */
    private List<C0501W02Meisai3Bean> getInitMeisai3BeanList(
        C0501W02TokuisakiInfoConfirmDto tokuisakiInfoConfirmDto) {

        List<C0501W02Meisai3Bean> meisai3List = new ArrayList<>();
        for (C0501W02JissekiInfoDto e : tokuisakiInfoConfirmDto.getJissekiInfoUpList()) {
            C0501W02Meisai3Bean meisai3Bean = new C0501W02Meisai3Bean();

            // 伝票ステータス
            meisai3Bean.setDnpysts(e.getDnpysts());
            // 受注日１明細
            meisai3Bean.setAcptdd1mei(DateTimeUtil.toSlashedDate(e.getAcptdd1mei()));
            // 配達日１明細
            meisai3Bean.setDlvrydd1mei(DateTimeUtil.toSlashedDate(e.getDlvrydd1mei()));
            // レギュラー有料１明細
            meisai3Bean.setRgrayuryo1mei(e.getRgrayuryo1mei());
            // レギュラー無料１明細
            meisai3Bean.setRgramuryo1mei(e.getRgramuryo1mei());
            // スリム有料１明細
            meisai3Bean.setSrmyuryo1mei(e.getSrmyuryo1mei());
            // スリム無料１明細
            meisai3Bean.setSrmmuryo1mei(e.getSrmmuryo1mei());
            // その他有料１明細
            meisai3Bean.setSonotayuryo1mei(e.getSonotayuryo1mei());
            // その他無料１明細
            meisai3Bean.setSonotamuryo1mei(e.getSonotamuryo1mei());
            // 製品水売上１明細
            meisai3Bean.setSeihinmizuuri1mei(ItemUtil.editComma(e.getSeihinmizuuri1mei()));
            // その他売上金額１明細
            meisai3Bean.setSonotaurikin1mei(ItemUtil.editComma(e.getSonotaurikin1mei()));
            // 伝票ステータス名称１明細
            meisai3Bean.setDnpystsnm1mei(e.getDnpystsnm1mei());
            // 受注伝票番号
            meisai3Bean.setAcptdnpyno(e.getAcptdnpyno());
            // 枝番号
            meisai3Bean.setEdano(String.valueOf(e.getEdano()));

            meisai3List.add(meisai3Bean);
        }

        return meisai3List;

    }

    /**
     * List<C0501W02Meisai4Bean>設定
     * 
     * @param tokuisakiInfoConfirmDto C0501W02TokuisakiInfoConfirmDto
     * @return C0501W02SalesBean
     */
    private List<C0501W02Meisai4Bean> getInitMeisai4BeanList(
        C0501W02TokuisakiInfoConfirmDto tokuisakiInfoConfirmDto) {

        List<C0501W02Meisai4Bean> meisai4List = new ArrayList<>();

        int counter = 0;
        for (C0501W02JissekiInfoDto e : tokuisakiInfoConfirmDto.getJissekiInfoDownList()) {
            C0501W02Meisai4Bean meisai4Bean = new C0501W02Meisai4Bean();
            counter++;
            if (counter > 3) {
                break;
            }
            // 伝票ステータス
            meisai4Bean.setDnpysts(e.getDnpysts());
            // 受注日２明細
            meisai4Bean.setAcptdd2mei(DateTimeUtil.toSlashedDate(e.getAcptdd1mei()));
            // 配達日２明細
            meisai4Bean.setDlvrydd2mei(DateTimeUtil.toSlashedDate(e.getDlvrydd1mei()));
            // レギュラー有料２明細
            meisai4Bean.setRgrayuryo2mei(e.getRgrayuryo1mei());
            // レギュラー無料２明細
            meisai4Bean.setRgramuryo2mei(e.getRgramuryo1mei());
            // スリム有料２明細
            meisai4Bean.setSrmyuryo2mei(e.getSrmyuryo1mei());
            // スリム無料２明細
            meisai4Bean.setSrmmuryo2mei(e.getSrmmuryo1mei());
            // その他有料２明細
            meisai4Bean.setSonotayuryo2mei(e.getSonotayuryo1mei());
            // その他無料２明細
            meisai4Bean.setSonotamuryo2mei(e.getSonotamuryo1mei());
            // 製品水売上２明細
            meisai4Bean.setSeihinmizuuri2mei(ItemUtil.editComma(e.getSeihinmizuuri1mei()));
            // その他売上金額２明細
            meisai4Bean.setSonotaurikin2mei(ItemUtil.editComma(e.getSonotaurikin1mei()));
            // 伝票ステータス名称２明細
            meisai4Bean.setDnpystsnm2mei(e.getDnpystsnm1mei());
            // 受注伝票番号
            meisai4Bean.setAcptdnpyno2(e.getAcptdnpyno());
            // 枝番号2
            meisai4Bean.setEdano2(String.valueOf(e.getEdano()));

            meisai4List.add(meisai4Bean);
        }

        return meisai4List;
    }

    /**
     * List<C0501W02Meisai5Bean>設定
     * 
     * @param tokuisakiInfoConfirmDto C0501W02TokuisakiInfoConfirmDto
     * @return List<C0501W02Meisai5Bean>
     */
    private List<C0501W02Meisai5Bean> getInitMeisai5BeanList(
        C0501W02TokuisakiInfoConfirmDto tokuisakiInfoConfirmDto) {

        List<C0501W02Meisai5Bean> meisai5BeanList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            C0501W02Meisai5Bean meisai5Bean = new C0501W02Meisai5Bean();
            meisai5Bean.setRgrahnsmei(ItemUtil
                .editComma(tokuisakiInfoConfirmDto.getJissekisumInfo().getSuryotableRegular()[i]));
            meisai5Bean.setSrmhnsmei(ItemUtil
                .editComma(tokuisakiInfoConfirmDto.getJissekisumInfo().getSuryotableSlim()[i]));
            meisai5Bean.setSonotahnsmei(ItemUtil
                .editComma(tokuisakiInfoConfirmDto.getJissekisumInfo().getSuryotableEtc()[i]));
            meisai5BeanList.add(meisai5Bean);
        }
        return meisai5BeanList;
    }

    /**
     * List<C0501W02Meisai6Bean>設定
     * 
     * @param tokuisakiInfoConfirmDto C0501W02TokuisakiInfoConfirmDto
     * @return List<C0501W02Meisai6Bean>
     */
    private List<C0501W02Meisai6Bean> getInitMeisai6BeanList(
        C0501W02TokuisakiInfoConfirmDto tokuisakiInfoConfirmDto) {

        List<C0501W02Meisai6Bean> meisai6List = new ArrayList<>();

        for (C0501W02ClaimInfoDto e : tokuisakiInfoConfirmDto.getClaimInfoList()) {
            C0501W02Meisai6Bean meisai6bean = new C0501W02Meisai6Bean();

            // 日付明細
            meisai6bean.setYmdmei(DateTimeUtil.toSlashedDate(e.getYmdmei()));
            // 問合せ区分名称明細
            meisai6bean.setToiawasekbnnmmei(e.getToiawasekbnnmmei());
            // 問合せ内容区分名称明細
            meisai6bean.setToiawasenaiyokbnnmmei(e.getToiawasenaiyokbnnmmei());
            // 問合せステータス名称明細
            meisai6bean.setToiawasestskbn(e.getToiawasestskbn());
            // 問合せステータス名称明細
            meisai6bean.setToiawasestsnmmei(e.getToiawasestsnmmei());
            // 件名明細
            meisai6bean.setKenmeimei(ItemUtil.substringBytes(e.getKenmeimei(), 30, "Windows-31J"));
            // 対応者名明細
            meisai6bean.setTaishameimei(e.getTaishameimei());
            // 受付管理番号
            meisai6bean.setRcptkanrino(e.getRcptkanrino());

            meisai6List.add(meisai6bean);
        }

        return meisai6List;

    }

    /**
     * List<C0501W02Meisai7Bean>設定
     * 
     * @param tokuisakiInfoConfirmDto C0501W02TokuisakiInfoConfirmDto
     * @return List<C0501W02Meisai7Bean>
     */
    private List<C0501W02Meisai7Bean> getInitMeisai7BeanList(
        C0501W02TokuisakiInfoConfirmDto tokuisakiInfoConfirmDto) {

        List<C0501W02Meisai7Bean> meisai7List = new ArrayList<>();

        for (C0501W02TokuisakiPointRunkDto e : tokuisakiInfoConfirmDto.getPointRunkList()) {
            C0501W02Meisai7Bean meisai7bean = new C0501W02Meisai7Bean();

            // 日付
            meisai7bean.setTekiyoudd(DateTimeUtil.toSlashedDate(e.getTekiyoudd()));
            // 履歴(ランク名称)
            meisai7bean.setPointrnk(e.getPointrnk());
            // 履歴(ランク倍率)
            meisai7bean.setPointbairitu(e.getPointbairitu());

            meisai7List.add(meisai7bean);
        }

        return meisai7List;

    }

    /**
     * 改行コード<BR>
     * タグ置換
     * 
     * @param targetString
     * @return
     */
    private String replaceBrTag(String targetString) {
        String ret = "";
        if (!Strings.isEmpty(targetString)) {
            ret = targetString.replaceAll("<br>", "\n").replaceAll("\r\n", "\n");
        }
        return ret;
    }

}

