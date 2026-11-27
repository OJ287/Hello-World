package jp.co.aquaclara.accis.web.controller.b0501;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.web.controller.b0501.bean.B0501W02AdditionalBean;
import jp.co.aquaclara.accis.web.controller.b0501.bean.B0501W02BaseBean;
import jp.co.aquaclara.accis.web.controller.b0501.bean.B0501W02DeliveryOrderBean;
import jp.co.aquaclara.accis.web.controller.b0501.bean.B0501W02RentalBean;
import jp.co.aquaclara.accis.web.controller.b0501.bean.B0501W02ReservationBean;
import jp.co.aquaclara.accis.web.controller.b0501.bean.B0501W02SaleBean;
import jp.co.aquaclara.accis.web.controller.b0501.form.B0501W02Form;
import jp.co.aquaclara.accis.web.controller.commons.AccisWebBaseController;
import jp.co.aquaclara.accis.web.controller.commons.DbManagementSession;
import jp.co.aquaclara.accis.web.controller.commons.MenuAuthSession;
import jp.co.aquaclara.accis.web.controller.commons.TaxInfo;
import jp.co.aquaclara.accis.web.controller.commons.TaxSession;
import jp.co.aquaclara.accis.web.controller.commons.UserSessionForm;
import jp.co.aquaclara.accis.web.domain.dto.b0501.B0501W02DisplayDto;
import jp.co.aquaclara.accis.web.domain.service.b0501.B0501W02Service;

/**
 * B0501W02_得意先マスタ(登録画面)-コントローラクラス.
 */
@Controller
@RequestMapping("/b0501w02")
public class B0501W02Controller extends AccisWebBaseController {

    private static final String VIEW_NAME = "b0501/b0501w02_form";

    // 処理ステータス
    private final String SYORI_SHINKI = "1"; // 新規登録
    private final String SYORI_KOSHIN = "2"; // 更新

    // 請求設定区分ラジオボタンの選択値
    private final String SKYSETTEIKBN_0 = "0"; // 指定なし
    private final String SKYSETTEIKBN_1 = "1"; // コード指定

    // 会員コード区分ラジオボタンの選択値
    private final String KAIINCDKBN_0 = "0"; // 指定なし
    private final String KAIINCDKBN_1 = "1"; // コード指定

    // チェックボックス設定値
    private final String CHK_ON = "1"; // チェックON

    @Autowired
    private B0501W02Service b0501w02Service;

    @Autowired
    private UserSessionForm userSession;

    @Autowired
    private TaxSession taxSession;

    /* 担当メニューマスタ(S02M045T)の使用権限レベル と DB管理項目を追加 */
    @ModelAttribute("menuAuthSession_B0501W02")
    public MenuAuthSession setMenuAuthSession() {
        return new MenuAuthSession();
    }

    @ModelAttribute("dbManagementSession_B0501W02")
    public DbManagementSession setDbManagementSession() {
        return new DbManagementSession();
    }

    /**
     * 初期表示(新規登録時).
     * 
     * @param form B0501W02Form
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param menuAuthSession メニュー権限
     * @param dbManagementSession DB権限
     * @param mv ModelAndView
     * @return ModelAndView
     */
    @GetMapping("/{apkihoncd}/{shopcd}")
    public ModelAndView initRegist(@ModelAttribute("b0501W02Form") B0501W02Form form,
        @PathVariable("apkihoncd") String apkihoncd, @PathVariable("shopcd") String shopcd,
        MenuAuthSession menuAuthSession, @ModelAttribute(name = "dbManagementSession_B0501W02",
            binding = false) DbManagementSession dbManagementSession,
        ModelAndView mv) {

        initForm(form, apkihoncd, shopcd, menuAuthSession, dbManagementSession, "", "");

        // 表示画面設定
        mv.setViewName(VIEW_NAME);

        return mv;
    }

    /**
     * 初期表示(更新時).
     * 
     * @param form B0501W02Form
     * @param menuAuthSession メニュー権限
     * @param dbManagementSession DB権限
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @param apshopfixedflag APショップ固定フラグ
     * @param kinoid 機能ID
     * @param jigyoshokanri 事業所管理
     * @param origintokuisakicd 遷移元得意先コード
     * @param mv ModelAndView
     * @return ModelAndView
     */
    @GetMapping("/{apkihoncd}/{shopcd}/{tokuisakicd}")
    public ModelAndView init(@ModelAttribute("b0501W02Form") B0501W02Form form,
        @ModelAttribute(name = "menuAuthSession_B0501W02",
            binding = false) MenuAuthSession menuAuthSession,
        @ModelAttribute(name = "dbManagementSession_B0501W02",
            binding = false) DbManagementSession dbManagementSession,
        @PathVariable("apkihoncd") String apkihoncd, @PathVariable("shopcd") String shopcd,
        @PathVariable("tokuisakicd") String tokuisakicd,
        @RequestParam(name = "apshopfixedflag", required = false) String apshopfixedflag,
        @RequestParam(name = "kinoid", required = false) String kinoid,
        @RequestParam(name = "jigyoshokanri", required = false) String jigyoshokanri,
        @RequestParam(name = "origintokuisakicd", required = false) String origintokuisakicd,
        ModelAndView mv) {

        initForm(form, apkihoncd, shopcd, tokuisakicd, origintokuisakicd, menuAuthSession,
            dbManagementSession, kinoid, jigyoshokanri, apshopfixedflag);

        // 表示画面設定
        mv.setViewName(VIEW_NAME);
        return mv;
    }

    /**
     * formの初期化.(新規登録時)
     * 
     * @param form B0501W02Form
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param menuAuthSession メニュー権限
     * @param dbManagementSession DB権限
     * @param kinoid 機能ID
     * @param apshopfixedflag APショップ固定フラグ
     */
    private void initForm(B0501W02Form form, String apkihoncd, String shopcd,
        MenuAuthSession menuAuthSession, DbManagementSession dbManagementSession, String kinoid,
        String apshopfixedflag) {

        // 初期表示情報取得
        B0501W02DisplayDto dto = b0501w02Service.init(apkihoncd, shopcd, dbManagementSession.getUnyoDate());

        // form内のオブジェクトを初期化
        form.setBaseBean(getBaseBean(dto, menuAuthSession.getMenuAuth(), kinoid, apshopfixedflag,
            dbManagementSession.getUnyoDate(), ""));
        form.setDeliveryOrderBean(getDeliveryOrderBean(dto));
        form.setSaleBean(getSaleBean(dto));
        form.setAdditionalBean(getAdditionalBean(dto));
        form.setRentalBean(getRentalBean(dto));
        form.setReservationBean(getReservationBean(dto));
    }

    /**
     * formの初期化.(更新時)
     * 
     * @param form B0501W02Form
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @param origintokuisakicd 遷移元得意先コード
     * @param menuAuthSession メニュー権限
     * @param dbManagementSession DB権限
     * @param kinoid 機能ID
     * @param jigyoshokanri 事業所管理
     * @param apshopfixedflag APショップ固定フラグ
     */
    private void initForm(B0501W02Form form, String apkihoncd, String shopcd, String tokuisakicd,
        String origintokuisakicd, MenuAuthSession menuAuthSession,
        DbManagementSession dbManagementSession, String kinoid, String jigyoshokanri,
        String apshopfixedflag) {

        // 初期表示情報取得
        B0501W02DisplayDto dto =
            b0501w02Service.init(apkihoncd, shopcd, tokuisakicd, dbManagementSession.getUnyoDate());

        // 排他制御用項目をセッションにセット
        dbManagementSession.setUpdateDateForLock(dto.getKshnsystmymd());
        dbManagementSession.setUpdateTimeForLock(dto.getKshnsystmjikan());

        // form内のオブジェクトを初期化
        form.setBaseBean(getBaseBean(dto, menuAuthSession.getMenuAuth(), kinoid, apshopfixedflag,
            dbManagementSession.getUnyoDate(), origintokuisakicd));
        form.setDeliveryOrderBean(getDeliveryOrderBean(dto));
        form.setSaleBean(getSaleBean(dto));
        form.setAdditionalBean(getAdditionalBean(dto));
        form.setRentalBean(getRentalBean(dto));
        form.setReservationBean(getReservationBean(dto));
    }

    /**
     * B0501W02BaseBean設定
     * 
     * @param dto 画面表示用DTO
     * @param menuAuth メニュー権限
     * @param kinoid 機能ID
     * @param apshopfixedflag APショップ固定フラグ
     * @param unyodd 運用日付
     * @param origintokuisakicd 遷移元得意先コード
     * @return B0501W02BaseBean
     */
    public B0501W02BaseBean getBaseBean(B0501W02DisplayDto dto, String menuAuth, String kinoid,
        String apshopfixedflag, String unyodd, String origintokuisakicd) {
        // 初回サーバー設置日(得意先マスタ.初回サーバ設置日に「0」が設定されたレコードが存在するため)
        String shokaiwsvstupdd = getFormatedDateString(dto.getShokaiwsvstupdd(), "");
        B0501W02BaseBean baseBean = new B0501W02BaseBean(setAuthflg(menuAuth), // 権限フラグ
            StringUtils.isEmpty(dto.getTokuisakicd()) ? "f" : "t", // 更新モード判定フラグ
            dto.getApkihoncd(), // ＡＰ基本コード
            dto.getShopcd(), // ショップコード
            "", // ＡＰ名称
            "", // ショップ名称
            "", // 請求先自動区分
            dto.getShopkbn(), // ショップ区分
            "", // 呼び出し画面パラメータ領域
            "", // 呼び出し画面パラメータ領域1
            dto.getSkysakicd(), // 請求先コード変更前
            "", // 会員コード変更前
            userSession.getApkbn(), // 基本区分
            "", // 更新区分
            "", // ブラックフラグ
            "", // 作成区分
            dto.getOyatokuisakicd(), // 親得意先コード
            "", // 親得意先コードチェック
            "", // 親得意先コード画面制御
            "", // ベビアクアプランコード
            "", // 日付
            "", // 初期表示の標準価格
            dto.getTokuisakijotai(), // 得意先状態
            dto.getJotaists(), // 状態ステータス
            "", // 運用AP基本コード
            "", // 運用ショップ基本コード
            dto.getHonbuejntapkihoncd(), // 本部AP基本コード
            dto.getHonbuejntshopcd(), // 本部ショップコード
            "", // 発行日
            "", // プランコード
            dto.getKainsakuseiumukbn(), // 会員作成有無区分
            "", // 子得意先価格別データ有無フラグ
            "", // 郵便番号1コード参照フラグ
            "", // 郵便番号2コード参照フラグ
            "", // 郵便番号3コード参照フラグ
            "", // 郵便番号4コード参照フラグ
            dto.getChihoupubdanticdn1(), // 地方公共団体コード1
            dto.getChihoupubdanticdn2(), // 地方公共団体コード2
            "", // 委託相手先コード参照エラーフラグ
            "", // 会員コード参照エラーフラグ
            "", // 差出人イメージ
            "", // エントリーID
            StringUtils.isEmpty(dto.getTokuisakicd()) ? "（新規登録）" : dto.getTokuisakicd(), // 得意先コード1
            dto.getTokuisakicd(), // hidden得意先コード
            dto.getTokuisakinmn1(), // 得意先名称
            dto.getTokuisakijotainm(), // 得意先状態名称
            dto.getJotaistsnm(), // 状態ステータス名称
            dto.getHaneimotottcd(), // 反映元得意先コード
            dto.getHaneimototokuisakinm(), // 反映元得意先名称
            dto.getFttbnyukaiflg(), // 再入会フラグ
            dto.getTokuisakikbn(), // 得意先区分
            dto.getJutakudlvrycstkbn(), // 受託配達顧客区分
            dto.getCdkindkbn(), // コード種別区分
            dto.getGyoshukbn(), // 業種区分
            dto.getGyoshushskbn(), // 業種詳細区分
            dto.getSkysakicd(), // 請求先コード
            dto.getSkysakinmn1(), // 請求先名称1
            (StringUtils.isEmpty(dto.getSkysakicd()) ? SKYSETTEIKBN_0 : SKYSETTEIKBN_1), // 請求設定区分
            (StringUtils.isEmpty(dto.getSkysakicd()) ? SKYSETTEIKBN_0 : SKYSETTEIKBN_1), // 請求設定区分変更前
            dto.getTokuisakishopcd(), // 得意先ショップコード
            dto.getTokuisakishopnm(), // 得意先ショップ名称
            dto.getItakuaiteapkihoncd(), // 委託相手AP基本コード
            dto.getItakuaiteshopcd(), // 委託相手ショップコード
            dto.getItakuaitetokuisakicd(), // 委託相手得意先コード
            dto.getItakuaitetokuisakinm(), // 委託相手得意先名称
            dto.getKaiincd(), // 会員コード
            (StringUtils.isEmpty(dto.getKainsakuseiumukbn()) ? KAIINCDKBN_0
                : StringUtils.isEmpty(dto.getKaiincd()) ? KAIINCDKBN_0 : KAIINCDKBN_1), // 会員コード区分
            "", // 仕入先コード
            "", // 仕入先名称
            dto.getRiyowsvkbn(), // 利用サーバー区分
            dto.getJigyoshocd(), // 事業所コード
            dto.getTokuisakinmn1(), // 得意先名称1
            dto.getTokuisakinmn2(), // 得意先名称2
            dto.getTokuisakikeishokbn(), // 得意先敬称区分
            dto.getTokuisakinmkanan1(), // 得意先名称カナ1
            dto.getTokuisakinmkanan2(), // 得意先名称カナ2
            dto.getYubinno(), // 郵便番号
            dto.getAdrsn1(), // 住所1
            dto.getAdrsn2(), // 住所2
            dto.getAdrsn3(), // 住所3
            dto.getRenrakusakinokbnn1(), // 連絡先番号区分1
            dto.getRenrakusakinon1(), // 連絡先番号1
            dto.getRenrakusakimemon1(), // 連絡先メモ1
            dto.getRenrakusakinokbnn2(), // 連絡先番号区分2
            dto.getRenrakusakinon2(), // 連絡先番号2
            dto.getRenrakusakimemon2(), // 連絡先メモ2
            dto.getRenrakusakinokbnn3(), // 連絡先番号区分3
            dto.getRenrakusakinon3(), // 連絡先番号3
            dto.getRenrakusakimemon3(), // 連絡先メモ3
            dto.getRenrakusakinokbnn4(), // 連絡先番号区分4
            dto.getRenrakusakinon4(), // 連絡先番号4
            dto.getRenrakusakimemon4(), // 連絡先メモ4
            dto.getMerumagakbn(), // メルマガ区分
            dto.getMailadrse(), // メールアドレス
            dto.getAccesskahiskysaki(), // 請求先リンク表示
            dto.getPlncd(), // プラン
            DateTimeUtil.toSlashedDate(dto.getRknplnsetteidd()), // 料金プラン設定日
            "", // プラン初回違約金フラグ
            DateTimeUtil.toSlashedDate(dto.getRknplnkeizokukshndd()), // 料金プラン継続更新日
            "", // プラン更新違約金フラグ
            DateTimeUtil.toSlashedDateYYYYMM(dto.getPlnkgndydm()), // プラン期限年月
            dto.getJishasvcd(), // 自社サービス区分
            dto.getRknkindtokubetsuuntflg(), // 特別単価区分
            "", // 親子反映機能
            dto.getRknkindtokubetsunebikiflg(), // 特別値引
            StringUtils.isEmpty(dto.getIyakukinkin()) ? "0" : dto.getIyakukinkin(), // 違約金金額
            CommonConstants.IYAKUKINKANRIFLG_0.equals(dto.getIyakukinkanriflg()) ? CHK_ON : "", // 違約金管理対象フラグ
            "", // 違約金
            dto.getKodomomein1(), // 子供1
            DateTimeUtil.toSlashedDate(dto.getKodomotanjoddn1()), // 子供誕生日1
            dto.getKodomomein2(), // 子供2
            DateTimeUtil.toSlashedDate(dto.getKodomotanjoddn2()), // 子供誕生日2
            dto.getKodomomein3(), // 子供3
            DateTimeUtil.toSlashedDate(dto.getKodomotanjoddn3()), // 子供誕生日3
            DateTimeUtil.toSlashedDate(dto.getMoshikomircptdd()), // 申込受付日
            DateTimeUtil.toSlashedDate(dto.getMoshikomircptdd()), // 申込受付日変更前
            dto.getStartup(), // はじめてｾｯﾄ
            DateTimeUtil.toSlashedDate(dto.getNyukaidd()), // 入会日
            DateTimeUtil.toSlashedDate(dto.getNyukaidd()), // 入会日変更前
            DateTimeUtil.toSlashedDate(dto.getTaikaidd()), // 退会日
            DateTimeUtil.toSlashedDate(dto.getTaikaidd()), // 退会日変更前
            DateTimeUtil.toSlashedDate(dto.getMoshikomicncldd()), // 申込キャンセル日
            DateTimeUtil.toSlashedDate(dto.getMoshikomicncldd()), // 申込キャンセル日変更前
            dto.getNyukaimokoshono(), // 入会申込書番号
            dto.getMoshikomircptkbn(), // 申込書受付区分
            dto.getKaiinmokoshono(), // 会員申込書番号
            dto.getTaikairiyukbn(), // 退会理由区分
            dto.getTaikairiyushs(), // 退会理由詳細
            dto.getMoshikomitoshacd(), // 申込登録者コード
            dto.getMoshikomitoshamei(), // 登録者名5
            dto.getNyukaiddtoshacd(), // 入会日登録者コード
            dto.getNyukaiddtoshamei(), // 登録者名6
            dto.getTaikaiddtoshacd(), // 退会日登録者コード
            dto.getTaikaiddtoshamei(), // 登録者名7
            dto.getRtlskykanrikbn(), // レンタル請求管理区分
            dto.getSvrtlrkn(), // サーバーレンタル料金
            dto.getSaburtlsokocd(), // サブレンタル倉庫コード
            dto.getSaburtlsokonm(), // 倉庫名称1
            shokaiwsvstupdd, // 初回サーバー設置日（先に書式設定済）
            dto.getStupddtoshaapkihoncd(), // 設置日登録者AP基本コード
            dto.getStupddtoshashopcd(), // 設置日登録者ショップコード
            dto.getStupddtoshacd(), // 設置日登録者コード
            dto.getStupddtoshamei(), // 登録者名1
            DateTimeUtil.toSlashedDate(dto.getWsvhikiagedd()), // サーバー引上日
            dto.getHikiageddtoshaapkihoncd(), // 引上日登録者AP基本コード
            dto.getHikiageddtoshashopcd(), // 引上日登録者ショップコード
            dto.getHikiageddtoshacd(), // 引上登録者コード
            dto.getHikiageddtoshamei(), // 登録者名3
            dto.getYukokbn(), // 有効区分
            dto.getYukokbn(), // 有効区分変更前
            DateTimeUtil.toSlashedDate(dto.getInptsystmymd()), // 登録年月日
            DateTimeUtil.toColonedDateHHMMSS(dto.getInptsystmjikan()), // 登録時間
            dto.getToshaapkihoncd(), // 登録者AP基本コード
            dto.getToshashopcd(), // 登録者ショップコード
            dto.getToshacd(), // 登録者コード
            dto.getToshamei(), // 登録者名称
            DateTimeUtil.toSlashedDate(dto.getKshnsystmymd()), // 更新年月日
            DateTimeUtil.toColonedDateHHMMSS(dto.getKshnsystmjikan()), // 更新時間
            dto.getKoshaapkihoncd(), // 更新者AP基本コード
            dto.getKoshashopcd(), // 更新者ショップコード
            dto.getKoshacd(), // 更新者コード
            dto.getKoshamei(), // 更新者名称
            "", // 更新確認メッセージ
            StringUtils.isEmpty(dto.getTokuisakicd()) ? SYORI_SHINKI : SYORI_KOSHIN, // 処理ステータス
            CHK_ON, // 更新ボタン表示フラグ
            userSession.getAkshanikbn(), // アクセス範囲区分
            dto.getAccesskahiteireiuriage(), // 定例売上マスタリンク表示
            dto.getAccesskahitokuisakisettisv(), // 得意先設置サーバーマスタリンク表示
            dto.getAccesskahikain(), // 会員マスタリンク表示
            dto.getAccesskahiiyakukin(), // 違約金リンク表示
            StringUtils.isEmpty(dto.getInvoicshikoymd())
                ? DateTimeUtil.toNoSlashedDate(CommonConstants.DATE_MAX)
                : dto.getInvoicshikoymd(), // インボイス施工日
            unyodd, // 現在日(インボイス施工日との比較用項目)
            kinoid, // 遷移元機能ID
            dto.getJigyoshokanri(), // 事業所管理
            apshopfixedflag, // APショップ固定フラグ
            origintokuisakicd // 遷移元得意先コード
        );

        return baseBean;
    }

    /**
     * B0501W02DeliveryOrderBean設定.
     * 
     * @param dto 画面表示用DTO
     * @return B0501W02DeliveryOrderBean
     */
    public B0501W02DeliveryOrderBean getDeliveryOrderBean(B0501W02DisplayDto dto) {

        B0501W02DeliveryOrderBean deliveryOrderBean = new B0501W02DeliveryOrderBean(
            // 営業管理情報
            dto.getEigyoareacd(), // 営業エリアコード
            dto.getEigyoareanm(), // 営業エリア名称
            dto.getEigyotnshacd(), // 営業担当者コード
            dto.getEigyotnshamei(), // 営業担当者名称
            dto.getUrikeijotnshakbn(), // 売上計上担当者区分
            dto.getRcptkbn(), // 受付区分
            dto.getKakutokukbn(), // 獲得区分
            dto.getBaitaikbn(), // 媒体区分
            dto.getKakutokutnshacd(), // 獲得担当者コード
            dto.getKakutokutnshamei(), // 担当者名3
            dto.getTashakirikae(), // 他社切替
            dto.getHonbuejntcd(), // 本部エージェントコード
            dto.getHonbuejntcd(), // 本部エージェントコード(本部エージェントコード(初期表示時の値保管用))
            dto.getHonbuejntmei(), // エージェント名1
            dto.getHonshokaino(), // 本部紹介番号
            dto.getKakutokuhonbukynpncd(), // 獲得本部キャンペーンコード
            dto.getKakutokuhonbukynpnnm(), // 獲得本部キャンペーン名称
            dto.getHonbukynpnkeihinkbn(), // 獲得本部キャンペーン景品区分
            dto.getKakutokuejntcd(), // 獲得エージェントコード
            dto.getKakutokuejntmei(), // エージェント名2
            dto.getShanaitnshacd(), // エージェントマージン1
            dto.getShanaitnshamei(), // エージェントマージン2
            dto.getKakutokukynpncd(), // 獲得キャンペーンコード
            dto.getKakutokukynpnnm(), // 獲得キャンペーン名称
            dto.getKynpnkeihinkbn(), // 獲得景品
            dto.getShoukaitokuisakicd(), // 紹介元得意先コード
            dto.getShoukaitokuisakicd(), // 紹介元得意先コード変更前
            dto.getShoukaitokuisakinm(), // 得意先名
            dto.getHansokushscd(), // 販促詳細コード
            dto.getHansokushsnm(), // 販促名称
            dto.getKakutokushsmemo(), // 獲得詳細メモ
            dto.getToritugitenkaiinid(), // 取次店会員id
            // 配達情報
            dto.getShokaitokutenmemo(), // 初回特典メモ
            dto.getDlvrykankatsuapkihoncd(), // 配達管轄AP基本コード
            dto.getDlvrykankatsushopcd(), // 配達管轄ショップ基本コード
            dto.getDlvrykankatsuapkihonnm(), // AP名称2
            dto.getDlvrykankatsushopnm(), // ショップ名称2
            dto.getDlvrykbn(), // 配達区分
            dto.getFzaiokikanoflg(), // 不在置き可能フラグ
            dto.getDlvrykankatsukyotencd(), // 配達管轄拠点コード
            dto.getDlvrykankatsukyotennm(), // 拠点名称
            dto.getDlvrycmpnycd(), // 配達会社コード
            dto.getDlvrycmpnynm(), // 配達会社名称
            dto.getDlvryareacd(), // 配達エリアコード
            dto.getDlvryareanm(), // 配達エリア名称
            dto.getDlvrytnshacd(), // 配達担当者コード
            dto.getDlvrytnshamei(), // 配達担当者名
            dto.getDlvryjun(), // 配達順
            dto.getNohinsakijohon1(), // 納品先情報1
            dto.getNohinsakijohon2(), // 納品先情報2
            dto.getChizuno(), // 地図番号
            dto.getGaiburenkeidlvrycd(), // 外部連携配達コード
            // 受注情報
            dto.getAcptrcptkbnnm(), // 受注受付区分
            dto.getNestlehachusakikbn(), // ネスレ発注先
            dto.getAcptteishiflg(), // 受注停止フラグ
            dto.getAcptjikanshiteikbn(), // 受注時間指定区分
            DateTimeUtil.toColonedDateHHMM(dto.getAcptjikanshiteijikoku()), // 受注時間指定時刻
            DateTimeUtil.toColonedDateHHMM(dto.getAcptjikanshiteijikokushry()), // 受注時間指定時刻終了
            DateTimeUtil.toSlashedDate(dto.getSaishuteikiacptdlvryytidd()), // 受注前回定期受注日
            dto.getRegularbottlenm(), // 商品名称6
            ItemUtil.editComma(dto.getAzukaribtln1n2rittoruhns()), // 預かりボトル本数1
            dto.getKinotptnashiflg(), // 金額出力無フラグ
            dto.getSlimbottlenm(), // 商品名称7
            ItemUtil.editComma(dto.getAzukaribtln8rittoruhns()), // 預かりボトル本数2
            dto.getChuitokuisakikbnaci(), // 注意得意先区分ACI
            dto.getChuinaiyoaci(), // 注意内容ACI
            dto.getChuitokuisakikbnap(), // 注意得意先区分AP
            dto.getChuinaiyoap(), // 注意内容AP
            dto.getRenrakujikokokai(), // 連絡事項公開
            dto.getTokkijikohikokai(), // 特記事項非公開
            dto.getPrntmemon1(), // 印刷メモ1
            dto.getPrntmemon2(), // 印刷メモ2
            dto.getPrntmemon3(), // 印刷メモ3
            dto.getAccesskahiteikijuchu() // 定期受注リンク表示
        );

        return deliveryOrderBean;
    }

    /**
     * B0501W02SaleBean設定.
     * 
     * @param dto 画面表示用DTO
     * @return B0501W02SaleBean
     */
    public B0501W02SaleBean getSaleBean(B0501W02DisplayDto dto) {

        B0501W02SaleBean saleBean = new B0501W02SaleBean(
            // マイアクア連係情報
            dto.getMtmkisvkbn(), // まとめ買いサービス区分
            dto.getMtmkiyuryohns(), // まとめ買い有料本数
            dto.getMtmkimuryohns(), // まとめ買い無料本数
            dto.getMuryobtlsvkbn(), // 無料ボトルサービス区分
            dto.getMyaqauntjohodsplyumuflg(), // マイアクア単価情報表示有無フラグ
            dto.getMyaqaskyjohodsplyumuflg(), // マイアクア請求情報表示有無フラグ
            dto.getKyuminkanri(), // 休眠管理
            dto.getKyuminkbnnm(), // 休眠区分名称
            dto.getApbetsutokuisakirnk(), // AP別得意先ランク
            ItemUtil.editComma(dto.getYoshingendokin()), // 与信限度金額
            ItemUtil.editComma(dto.getGenzaizd()), // 現在残高
            dto.getGenzaizd(), // 現在残高hidden
            ItemUtil.editComma(dto.getKeijomaeacptkin()), // 計上前受注金額
            dto.getTokuisakigaibuskbtscd(), // 得意先外部識別コード1
            dto.getTokuisakigaibuskbtscd2(), // 得意先外部識別コード2
            dto.getTokuisakigaibuskbtscd3(), // 得意先外部識別コード3
            dto.getTokuisakishukeicd(), // 得意先集計コード
            dto.getTokuisakishoshakazu(), // 紹介者数
            dto.getCollabokbn(), // コラボ区分
            // 保証金情報
            dto.getHoshokinkbn(), // 保証金区分
            dto.getAzushono(), // 預かり書番号
            DateTimeUtil.toSlashedDate(dto.getHoshokinazukaridd()), // 保証金預かり日
            ItemUtil.editComma(dto.getHoshokinazukarikin()), // 保証金預かり金額
            DateTimeUtil.toSlashedDate(dto.getHoshokinhenkyakudd()), // 保証金返却日
            ItemUtil.editComma(dto.getHoshokinhenkyakukin()), // 保証金返却金額
            // マージン個別設定
            dto.getMgntrgtkbn(), // マージン対象区分
            dto.getWanshottocd(), // ワンショットコード
            dto.getWanshottonm(), // マージン名称
            dto.getBonasumgncnt(), // ボーナスマージン件数
            dto.getBonasumgnkin(), // ボーナスマージン金額
            dto.getBtlmgntrgtdkikan(), // ボトルマージン対象期間
            dto.getMgnyoshncdn1(), // マージン用商品コード1
            dto.getMgnyoshnnm1(), // 商品名称1
            dto.getMgncdn1(), // マージンコード1
            dto.getMgnnm1(), // マージン名称1
            dto.getMgnyoshncdn2(), // マージン用商品コード2
            dto.getMgnyoshnnm2(), // 商品名称2
            dto.getMgncdn2(), // マージンコード2
            dto.getMgnnm2(), // マージン名称2
            dto.getMgnyoshncdn3(), // マージン用商品コード3
            dto.getMgnyoshnnm3(), // 商品名称3
            dto.getMgncdn3(), // マージンコード3
            dto.getMgnnm3(), // マージン名称3
            dto.getMgnyoshncdn4(), // マージン用商品コード4
            dto.getMgnyoshnnm4(), // 商品名称4
            dto.getMgncdn4(), // マージンコード4
            dto.getMgnnm4(), // マージン名称4
            // 値引き設定
            dto.getJidonebikicdn1(), // 自動値引コード1
            dto.getJidonebikinm1(), // 自動値引名称1
            dto.getJidonebishncd1(), // 商品コード1
            dto.getJidonebishnnm1(), // 商品名称1
            dto.getGekkanhanbaihns1(), // 月間販売本数1
            dto.getJidonebikikindnm1(), // 自動値引種別1
            dto.getNebikibtlhns1(), // 値引本数1
            dto.getNebikikin1(), // 値引金額1
            DateTimeUtil.toSlashedDateYYYYMM(dto.getNebikistrtdydmn1()), // 値引開始年月1
            DateTimeUtil.toSlashedDateYYYYMM(dto.getNebikishrydydmn1()), // 値引終了年月1
            dto.getJidonebikicdn2(), // 自動値引コード2
            dto.getJidonebikinm2(), // 自動値引名称2
            dto.getJidonebishncd2(), // 商品コード2
            dto.getJidonebishnnm2(), // 商品名称2
            dto.getGekkanhanbaihns2(), // 月間販売本数2
            dto.getJidonebikikindnm2(), // 自動値引種別2
            dto.getNebikibtlhns2(), // 値引本数2
            dto.getNebikikin2(), // 値引金額2
            DateTimeUtil.toSlashedDateYYYYMM(dto.getNebikistrtdydmn2()), // 値引開始年月2
            DateTimeUtil.toSlashedDateYYYYMM(dto.getNebikishrydydmn2()), // 値引終了年月2
            dto.getJidonebikicdn3(), // 自動値引コード3
            dto.getJidonebikinm3(), // 自動値引名称3
            dto.getJidonebishncd3(), // 商品コード3
            dto.getJidonebishnnm3(), // 商品名称3
            dto.getGekkanhanbaihns3(), // 月間販売本数3
            dto.getJidonebikikindnm3(), // 自動値引種別3
            dto.getNebikibtlhns3(), // 値引本数3
            dto.getNebikikin3(), // 値引金額3
            DateTimeUtil.toSlashedDateYYYYMM(dto.getNebikistrtdydmn3()), // 値引開始年月3
            DateTimeUtil.toSlashedDateYYYYMM(dto.getNebikishrydydmn3()), // 値引終了年月3
            dto.getPointkbn(), // ポイント付与管理
            dto.getMyaquakaiininptumu(), // マイアクア会員登録
            dto.getMtmkipointkbn(), // まとめ買いポイント区分
            dto.getMtmkisuryo(), // まとめ買い数量
            dto.getMtmkipointsu(), // まとめ買いポイント数
            dto.getNyukaitokutenumu(), // 入会特典区分
            dto.getNyukaitokutenpointsu(), // 入会特典ポイント数
            dto.getPointrnkshin(), // ステージ修正（新）
            DateTimeUtil.toSlashedDate(dto.getTekiyoudd()), // ステージ切替日
            dto.getPointrnkgen(), //適用ステージ（現）
            dto.getPointrnkcdgen() // ポイントランクコード（現）
        );

        return saleBean;
    }

    /**
     * B0501W02AdditionalBean設定.
     * 
     * @param dto 画面表示用DTO
     * @return B0501W02AdditionalBean
     */
    public B0501W02AdditionalBean getAdditionalBean(B0501W02DisplayDto dto) {

        B0501W02AdditionalBean additionalBean = new B0501W02AdditionalBean(
            // 入会者情報
            DateTimeUtil.toSlashedDate(dto.getNyukaiinptdd()), // 入会者情報登録日
            dto.getRiyomokutekikbn(), // 利用目的区分
            DateTimeUtil.toSlashedDateYYYYMM(dto.getNyushadydm()), // 入社年月
            dto.getSeibetsukbn(), // 性別区分
            dto.getKinmusakin1(), // 勤務先1
            DateTimeUtil.toSlashedDate(dto.getNyushatanjodd()), // 誕生日
            dto.getKinmusakin2(), // 勤務先2
            dto.getSykugyokbn(), // 職業区分
            dto.getKinmusakiyubinno(), // 勤務先郵便番号
            dto.getKinmusakichihoupubdanticdn1(), // 勤務先地方公共団体コード1
            dto.getKinmusakichihoupubdanticdn2(), // 勤務先地方公共団体コード2
            dto.getHaiguumuflg(), // 配偶者区分
            dto.getKinmusakiadrsn1(), // 勤務先住所1
            dto.getSonotadokyoninumuflg(), // その他同居人有無区分
            dto.getKinmusakiadrsn2(), // 勤務先住所2
            dto.getShihasyazokukbn(), // 支払者続柄区分
            dto.getKinmusakiadrsn3(), // 勤務先住所3
            dto.getKojinsyoumeikindkbn(), // 個人証明種別区分
            dto.getKinmusakitelno(), // 勤務先電話番号
            dto.getKojinsyoumeino(), // 個人証明番号
            dto.getKinmusakifaxno(), // 勤務先FAX番号
            DateTimeUtil.toSlashedDate(dto.getSetaijohoinptdd()), // 世帯情報登録日
            dto.getSetainushi(), // 世帯主
            dto.getJugyoinkibkbn(), // 従業員規模区分
            dto.getKazokukoseikazudansei(), // 世帯人数
            dto.getKazokukoseikazujosei(), // 内子供数
            dto.getKojudykazukbn(), // 居住年数区分
            dto.getPetjohokbn(), // ペット情報区分
            ItemUtil.editComma(dto.getSetaisyunyu()), // 世帯収入
            dto.getDoitsusekeninzukbn(), // 同一生計人数区分
            dto.getGaikokujinninzu(), // 外国人人数
            dto.getGaikokujinbiko() // 外国人備考
        );

        return additionalBean;
    }

    /**
     * B0501W02RentalBean設定.
     * 
     * @param dto 画面表示用DTO
     * @return B0501W02RentalBean
     */
    public B0501W02RentalBean getRentalBean(B0501W02DisplayDto dto) {

        B0501W02RentalBean rentalBean = new B0501W02RentalBean(
            // 定例売上設定
            dto.getTeireicount() > 1 ? "複数設定されています" : "", // 複数行用メッセージ
            dto.getTeireihasseino(), // 定例発生番号
            dto.getTteireikbn(), // 定例設定区分
            dto.getKeijodd(), // 計上日
            DateTimeUtil.toSlashedDateYYYYMM(dto.getShokaikeijodydm()), // 開始年月
            DateTimeUtil.toSlashedDateYYYYMM(dto.getJikaikeijodydm()), // 次回計上年月
            DateTimeUtil.toSlashedDateYYYYMM(dto.getShrydydm()), // 終了年月
            DateTimeUtil.toSlashedDate(dto.getZnkhasseikeijodd()), // 前回発生計上日
            dto.getJissekitnshacd(), // 実績担当者コード
            dto.getJissekitnshamei(), // 実績担当者名
            dto.getShncd(), // 商品名
            "", // 次回計上日
            "", // 初回計上日
            dto.getTaxkbn(), // 商品税区分
            dto.getKeijokbnnm(), // 売上計上区分
            ItemUtil.editCommaDecimal(dto.getSuryo()), // 数量
            dto.getHyojunprice(), // 標準価格
            ItemUtil.editCommaDecimal(dto.getUnt()), // 単価
            dto.getKin(), // 合計金額
            dto.getShohitaxkin(), // 消費税
            ItemUtil.editCommaDecimal(dto.getShokaiunt()), // 初回単価
            dto.getShokaikin(), // 初回金額
            dto.getShokaishohitaxkin(), // 消費税1
            dto.getMeisaibiko(), // 明細備考
            dto.getRtlrknshukeikbn(), // 売上設定
            dto.getHasseikankakukeijoptn(), // 発生間隔計上パターン
            dto.getDnpyhasseiarinashiflg(), // 売上発生有無フラグ
            dto.getYukokbn1(), // 有効区分
            DateTimeUtil.toSlashedDate(dto.getInptsystmymd1()), // 登録年月日
            DateTimeUtil.toColonedDateHHMMSS(dto.getInptsystmjikan1()), // 登録時間
            dto.getToshaapkihoncd1(), // 登録者AP基本コード
            dto.getToshashopcd1(), // 登録者ショップコード
            dto.getToshacd1(), // 登録者コード
            dto.getToshamei1(), // 登録者名称
            DateTimeUtil.toSlashedDate(dto.getKshnsystmymd1()), // 更新年月日
            DateTimeUtil.toColonedDateHHMMSS(dto.getKshnsystmjikan1()), // 更新時間
            dto.getKoshaapkihoncd1(), // 更新者AP基本コード
            dto.getKoshashopcd1(), // 更新者ショップコード
            dto.getKoshacd1(), // 更新者コード
            dto.getKoshamei1(), // 更新者名称
            dto.getPriceinfo(), // 価格情報
            getJsshohitaxvo() // 消費税情報
        );

        return rentalBean;
    }

    /**
     * B0501W02ReservationBean設定.
     * 
     * @param dto 画面表示用DTO
     * @return B0501W02ReservationBean
     */
    public B0501W02ReservationBean getReservationBean(B0501W02DisplayDto dto) {

        B0501W02ReservationBean reservationBean = new B0501W02ReservationBean(
            // 得意先予約情報
            StringUtils.isEmpty(dto.getYoyakuinptdd()) ? "" : CHK_ON, // 得意先マスタ変更フラグ
            dto.getYoyakuinptdd(), // 得意先マスタ予約登録日hidden
            DateTimeUtil.toSlashedDate(dto.getTtmasutachgdd()), // 得意先マスタ変更日
            DateTimeUtil.toSlashedDate(dto.getInptsystmymd2()), // 得意先マスタ登録年月日
            DateTimeUtil.toColonedDateHHMMSS(dto.getInptsystmjikan2()), // 得意先マスタ登録時間
            dto.getToshaapkihoncd2(), // 得意先マスタ登録者AP基本コード
            dto.getToshashopcd2(), // 得意先マスタ登録者ショップコード
            dto.getToshacd2(), // 得意先マスタ登録者コード
            dto.getToshamei2(), // 得意先マスタ登録者名称
            DateTimeUtil.toSlashedDate(dto.getKshnsystmymd2()), // 得意先マスタ更新年月日
            DateTimeUtil.toColonedDateHHMMSS(dto.getKshnsystmjikan2()), // 得意先マスタ更新時間
            dto.getKoshaapkihoncd2(), // 得意先マスタ更新者AP基本コード
            dto.getKoshashopcd2(), // 得意先マスタ更新者ショップコード
            dto.getKoshacd2(), // 得意先マスタ更新者コード
            dto.getKoshamei2(), // 得意先マスタ更新者名称
            DateTimeUtil.toSlashedDate(dto.getTtmasutachgdd()), // 日付3
            dto.getTokuisakinmn11(), // 得意先名称11
            dto.getTokuisakikeishokbn1(), // 得意先敬称区分1
            dto.getTokuisakinmkanan11(), // 得意先名称カナ11
            dto.getTokuisakinmn21(), // 得意先名称21
            dto.getTokuisakinmkanan21(), // 得意先名称カナ21
            dto.getYubinno1(), // 郵便番号1
            dto.getChihoupubdanticdn11(), // 得意先地方公共団体コード1
            dto.getChihoupubdanticdn21(), // 得意先地方公共団体コード2
            dto.getRenrakusakinokbnn11(), // 連絡先番号区分11
            dto.getRenrakusakinon11(), // 連絡先番号11
            dto.getRenrakusakimemon11(), // 連絡先メモ11
            dto.getAdrsn11(), // 住所11
            dto.getRenrakusakinokbnn21(), // 連絡先番号区分21
            dto.getRenrakusakinon21(), // 連絡先番号21
            dto.getRenrakusakimemon21(), // 連絡先メモ21
            dto.getAdrsn21(), // 住所21
            dto.getRenrakusakinokbnn31(), // 連絡先番号区分31
            dto.getRenrakusakinon31(), // 連絡先番号31
            dto.getRenrakusakimemon31(), // 連絡先メモ31
            dto.getAdrsn31(), // 住所31
            dto.getRenrakusakinokbnn41(), // 連絡先番号区分41
            dto.getRenrakusakinon41(), // 連絡先番号41
            dto.getRenrakusakimemon41(), // 連絡先メモ41
            dto.getJigyoshocd1(), // 事業所コード1
            dto.getEigyoareacd1(), // 営業エリアコード1
            dto.getEigyoareanm1(), // 営業エリア名称1
            dto.getEigyotnshacd1(), // 営業担当者コード1
            dto.getEigyotnshanm1(), // 営業担当者名称1
            dto.getDlvrykankatsuapkihoncd1(), // 配達管轄ＡＰ基本コード1
            dto.getDlvrykankatsushopcd1(), // 配達管轄ショップコード1
            dto.getApnm(), // ＡＰ名称1
            dto.getShopnm(), // ショップ名称1
            dto.getDlvrykbn1(), // 配達区分1
            dto.getDlvrycmpnycd1(), // 配達会社コード1
            dto.getDlvrycmpnynm1(), // 配達会社名称1
            dto.getDlvrykankatsukyotencd1(), // 配達管轄拠点コード1
            dto.getDlvrykyotennm(), // 拠点名称1
            dto.getDlvryareacd1(), // 配達エリアコード1
            dto.getDlvryareanm1(), // 配達エリア名称1
            dto.getDlvrytnshacd1(), // 配達担当者コード1
            dto.getDlvrytnshamei1(), // 配達担当者名1
            dto.getPrntmemon11(), // 印刷メモ11
            dto.getChizuno1(), // 地図番号1
            dto.getDlvryjun1(), // 配達順1
            dto.getPrntmemon21(), // 印刷メモ21
            dto.getNohinsakijohon11(), // 納品先情報11
            dto.getNohinsakijohon21(), // 納品先情報21
            dto.getPrntmemon31(), // 印刷メモ31
            dto.getAcptjikanshiteikbn1(), // 受注時間指定区分1
            DateTimeUtil.toColonedDateHHMM(dto.getAcptjikanshiteijikoku1()), // 受注時間指定時刻開始
            DateTimeUtil.toColonedDateHHMM(dto.getAcptjikanshiteijikokushry1()), // 受注時間指定時刻終了1

            // 請求先予約情報
            StringUtils.isEmpty(dto.getYoyakuinptdd1()) ? "" : CHK_ON, // 請求先マスタ変更フラグ
            dto.getYoyakuinptdd1(), // 請求先マスタ予約登録日hidden
            DateTimeUtil.toSlashedDate(dto.getSksmasutachgdd()), // 請求先マスタ変更日
            DateTimeUtil.toSlashedDate(dto.getInptsystmymd3()), // 登録システム日付2
            DateTimeUtil.toColonedDateHHMMSS(dto.getInptsystmjikan3()), // 登録システム時間2
            dto.getToshaapkihoncd3(), // 登録者AP基本コード2
            dto.getToshashopcd3(), // 登録者ショップコード2
            dto.getToshacd3(), // 登録者コード2
            dto.getToshamei3(), // 登録者名称4
            DateTimeUtil.toSlashedDate(dto.getKshnsystmymd3()), // 更新システム日付2
            DateTimeUtil.toColonedDateHHMMSS(dto.getKshnsystmjikan3()), // 更新システム時間2
            dto.getKoshaapkihoncd3(), // 更新者AP基本コード2
            dto.getKoshashopcd3(), // 更新者ショップコード2
            dto.getKoshacd3(), // 更新者コード2
            dto.getKoshamei3(), // 更新者名称4
            DateTimeUtil.toSlashedDate(dto.getSksmasutachgdd()), // 日付2
            dto.getSkysakinmn11(), // 請求先名称11
            dto.getSkysakikeishokbn(), // 請求先敬称
            dto.getSkysakinmkana(), // 請求先名称カナ
            dto.getSkysakinmn21(), // 請求先名称2
            dto.getSkysakinmryakusho(), // 請求先名称略称
            dto.getYubinno2(), // 郵便番号2
            dto.getRenrakusakinokbnn12(), // 連絡先番号区分12
            dto.getRenrakusakinon12(), // 連絡先番号12
            dto.getAdrsn12(), // 住所12
            dto.getRenrakusakinokbnn22(), // 連絡先番号区分22
            dto.getRenrakusakinon22(), // 連絡先番号22
            dto.getAdrsn22(), // 住所22
            dto.getRenrakusakinokbnn32(), // 連絡先番号区分32
            dto.getRenrakusakinon32(), // 連絡先番号32
            dto.getAdrsn32(), // 住所32
            dto.getRenrakusakinokbnn42(), // 連絡先番号区分42
            dto.getRenrakusakinon42(), // 連絡先番号42
            dto.getEigyotnshacd2(), // 営業担当者コード2
            dto.getEigyotnshamei2(), // 営業担当者名称2
            dto.getKaishutnshacd(), // 回収担当者コード
            dto.getKaishutnshamei(), // 回収担当者名称
            dto.getJigyoshocd2() // 事業所コード2
        );

        return reservationBean;
    }

    /**
     * 権限フラグ設定.
     * 
     * @param menuAuth メニュー権限
     * @return 権限フラグ
     */
    private String setAuthflg(String menuAuth) {

        // 権限フラグの設定
        String authflg = "t";
        if (menuAuth != null && menuAuth.equals("ROLE_MENU_AUTH_UPDATE")) {
            authflg = "f";
        }

        return authflg;
    }

    /**
     * 消費税情報取得
     * 
     * @return 消費税リスト
     */
    private List<String> getJsshohitaxvo() {
        List<String> taxinfoList = new ArrayList<>();
        for (TaxInfo taxinfo : taxSession.getTaxInfo()) {
            StringBuilder buf = new StringBuilder();
            buf.append("{taxkbn:").append(taxinfo.getTaxkbn());
            buf.append(",tekiyostrtdd:").append(taxinfo.getTekiyostrtdd());
            buf.append(",tekiyoshrydd:").append(taxinfo.getTekiyoshrydd());
            buf.append(",shohitax:").append(taxinfo.getShohitax());
            buf.append("}");
            taxinfoList.add(buf.toString());
        }
        return taxinfoList;
    }

    /**
     * 書式設定された日付文字列を取得する
     * 
     * @param dateString yyyyMMdd形式の日付
     * @param defaultString デフォルト値
     * @return yyyy/MM/dd形式の日付文字列（空や日付として妥当ではない場合はデフォルト値）
     */
    private String getFormatedDateString(String dateString, String defaultString) {
        if (StringUtils.isEmpty(dateString)) {
            return defaultString;
        }
        if (DateTimeUtil.check(dateString, "yyyyMMdd")) {
            return DateTimeUtil.toSlashedDate(dateString);
        }
        return defaultString;
    }
}
