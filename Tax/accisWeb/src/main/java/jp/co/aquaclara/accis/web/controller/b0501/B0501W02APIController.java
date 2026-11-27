package jp.co.aquaclara.accis.web.controller.b0501;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.constants.SystemConstants;
import jp.co.aquaclara.accis.common.constants.ValidationConstants;
import jp.co.aquaclara.accis.common.exception.BusinessException;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.web.controller.b0501.bean.B0501W02AdditionalBean;
import jp.co.aquaclara.accis.web.controller.b0501.bean.B0501W02AddressBean;
import jp.co.aquaclara.accis.web.controller.b0501.bean.B0501W02BaseBean;
import jp.co.aquaclara.accis.web.controller.b0501.bean.B0501W02DeliveryOrderBean;
import jp.co.aquaclara.accis.web.controller.b0501.bean.B0501W02RentalBean;
import jp.co.aquaclara.accis.web.controller.b0501.bean.B0501W02ReservationBean;
import jp.co.aquaclara.accis.web.controller.b0501.bean.B0501W02SaleBean;
import jp.co.aquaclara.accis.web.controller.b0501.form.B0501W02Form;
import jp.co.aquaclara.accis.web.controller.b0501.session.B0501W00Session;
import jp.co.aquaclara.accis.web.controller.commons.AccisWebBaseController;
import jp.co.aquaclara.accis.web.controller.commons.AciRestResponse;
import jp.co.aquaclara.accis.web.controller.commons.DbManagementSession;
import jp.co.aquaclara.accis.web.controller.commons.UserSessionForm;
import jp.co.aquaclara.accis.web.controller.parts.master.bean.CommonPartsListBean;
import jp.co.aquaclara.accis.web.controller.z9999.bean.Z9999W01JobStateBean;
import jp.co.aquaclara.accis.web.domain.dto.b0501.B0501W02AddressDto;
import jp.co.aquaclara.accis.web.domain.dto.b0501.B0501W02DisplayDto;
import jp.co.aquaclara.accis.web.domain.dto.b0501.B0501W02ShnDto;
import jp.co.aquaclara.accis.web.domain.dto.b0501.B0501W02UpdateDto;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M001TDto;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M003TDto;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M006TDto;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M013TDto;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M014TDto;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M015TDto;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M017TDto;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M018TDto;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M019TDto;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M020TDto;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M027TDto;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M031TDto;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M041TDto;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M046TDto;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M059TDto;
import jp.co.aquaclara.accis.web.domain.service.b0501.B0501R02Service;
import jp.co.aquaclara.accis.web.domain.service.b0501.B0501W02Service;
import jp.co.aquaclara.accis.web.domain.service.c0800.UtilityService;
import jp.co.aquaclara.accis.web.domain.service.common.async.AsyncManagementService;
import jp.co.aquaclara.accis.web.domain.service.common.async.AsyncManagementServiceModel;
import jp.co.aquaclara.accis.web.domain.service.master.S02M001TService;
import jp.co.aquaclara.accis.web.domain.service.master.S02M003TService;
import jp.co.aquaclara.accis.web.domain.service.master.S02M006TService;
import jp.co.aquaclara.accis.web.domain.service.master.S02M013TService;
import jp.co.aquaclara.accis.web.domain.service.master.S02M014TService;
import jp.co.aquaclara.accis.web.domain.service.master.S02M019TService;
import jp.co.aquaclara.accis.web.domain.service.master.S02M020TService;
import jp.co.aquaclara.accis.web.domain.service.master.S02M027TService;
import jp.co.aquaclara.accis.web.domain.service.master.S02M031TService;
import jp.co.aquaclara.accis.web.domain.service.master.S02M041TService;
import jp.co.aquaclara.accis.web.domain.service.master.S02M046TService;
import jp.co.aquaclara.accis.web.domain.service.master.S02M059TService;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;
import jp.co.aquaclara.accis.web.exception.dto.FormField;
import lombok.extern.slf4j.Slf4j;

/**
 * B0501W02_得意先マスタ(登録画面)APIコントローラクラス.
 */
@RestController
@SessionAttributes(types = {DbManagementSession.class})
@RequestMapping("/api/b0501w02")
@Slf4j
public class B0501W02APIController extends AccisWebBaseController {

    @Autowired
    private S02M001TService s02m001tService;

    @Autowired
    private S02M003TService s02m003tService;

    @Autowired
    private S02M013TService s02m013tService;

    @Autowired
    private S02M014TService s02m014tService;

    @Autowired
    private S02M019TService s02m019tService;

    @Autowired
    private S02M020TService s02m020tService;

    @Autowired
    private S02M027TService s02m027tService;

    @Autowired
    private S02M031TService s02m031tService;

    @Autowired
    private S02M041TService s02m041tService;

    @Autowired
    private S02M046TService s02m046tService;

    @Autowired
    private S02M059TService s02m059tService;

    @Autowired
    private B0501W02Service b0501w02Service;

    @Autowired
    private S02M006TService s02m006tService;

    @Autowired
    private B0501W00Session b0501w00session;

    @Autowired
    private B0501W02Controller controller;

    @Autowired
    private UserSessionForm userSessionForm;

    /**
     * 非同期処理（帳票出力）OnlineBatchサービス
     */
    @Autowired
    private B0501R02Service b0501r02Service;

    /**
     * ユーティリティサービス
     */
    @Autowired
    private UtilityService util;

    /**
     * 非同期処理管理（受付）サービス
     */
    @Autowired
    private AsyncManagementService<B0501W02DisplayDto> asyncManagementService;

    // 請求設定区分ラジオボタンの選択値
    private final String SKYSETTEIKBN_0 = "0"; // 指定なし
    private final String SKYSETTEIKBN_1 = "1"; // コード指定
    private final String SKYSETTEIKBN_2 = "2"; // 自動生成

    // 会員コード区分ラジオボタンの選択値
    private final String KAIINCDKBN_0 = "0"; // 指定なし
    private final String KAIINCDKBN_1 = "1"; // コード指定

    // 売上設定ラジオボタンの選択値
    private final String URISETTEI_0 = "0"; // 定例売上マスタ

    // チェックボックス設定値
    private final String CHK_ON = "1"; // チェックON

    // --- 空アイテム生成まわり（クラススコープに配置） ---
    private static final int CDKIND_EMPTY_VALUE_LEN = 1;
    private static final int GYOSHU_EMPTY_VALUE_LEN = 2;
    private static final int EMPTY_LABEL_LEN = 10;
    private static final String NBSP = "\u00A0";
    private static final String CDKIND_EMPTY_VALUE =
        SystemConstants.WORD_HALF_WIDTH_SPACE.repeat(CDKIND_EMPTY_VALUE_LEN);
    private static final String GYOSHU_EMPTY_VALUE =
        SystemConstants.WORD_HALF_WIDTH_SPACE.repeat(GYOSHU_EMPTY_VALUE_LEN);
    private static final String EMPTY_LABEL = NBSP.repeat(EMPTY_LABEL_LEN);
    private static final List<CommonPartsListBean> CDKIND_EMPTY_ITEM_LIST =
        List.of(new CommonPartsListBean(CDKIND_EMPTY_VALUE, EMPTY_LABEL));
    private static final List<CommonPartsListBean> GYOSHU_EMPTY_ITEM_LIST =
        List.of(new CommonPartsListBean(GYOSHU_EMPTY_VALUE, EMPTY_LABEL));

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage,
        MethodParameter parameter, Type targetType,
        Class<? extends HttpMessageConverter<?>> converterType) {

        // インスタンスがB0501W02Formでない場合、処理を抜ける
        if (!(body instanceof B0501W02Form)) {
            return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
        }

        // RequestBody(マッピング後)のformの値を取得
        B0501W02Form form = (B0501W02Form) body;

        // 基本情報Bean
        // 必須入力以外でバリデーション対象の項目値が空白値の場合nullに置換
        B0501W02BaseBean base = form.getBaseBean();
        base.setHaneimototokuisakicd(ItemUtil.emptyToNull(base.getHaneimototokuisakicd())); // 反映元得意先コード
        base.setSkysakicd(ItemUtil.emptyToNull(base.getSkysakicd())); // 請求先コード
        base.setTokuisakishopcd(ItemUtil.emptyToNull(base.getTokuisakishopcd())); // 得意先ショップコード
        base.setItakuaiteapkihoncd(ItemUtil.emptyToNull(base.getItakuaiteapkihoncd())); // 委託相手AP基本コード
        base.setItakuaiteshopcd(ItemUtil.emptyToNull(base.getItakuaiteshopcd())); // 委託相手ショップコード
        base.setItakuaitetokuisakicd(ItemUtil.emptyToNull(base.getItakuaitetokuisakicd())); // 委託相手得意先コード
        base.setKaiincd(ItemUtil.emptyToNull(base.getKaiincd())); // 会員コード
        base.setTokuisakinmn1(ItemUtil.emptyToNull(base.getTokuisakinmn1())); // 得意先名称1
        base.setTokuisakinmn2(ItemUtil.emptyToNull(base.getTokuisakinmn2())); // 得意先名称2
        base.setTokuisakinmkanan1(ItemUtil.emptyToNull(base.getTokuisakinmkanan1())); // 得意先名称カナ1
        base.setTokuisakinmkanan2(ItemUtil.emptyToNull(base.getTokuisakinmkanan2())); // 得意先名称カナ2
        base.setYubinno(ItemUtil.emptyToNull(base.getYubinno())); // 郵便番号
        base.setAdrsn1(ItemUtil.emptyToNull(base.getAdrsn1())); // 住所1
        base.setAdrsn2(ItemUtil.emptyToNull(base.getAdrsn2())); // 住所2
        base.setAdrsn3(ItemUtil.emptyToNull(base.getAdrsn3())); // 住所3
        base.setMailadrse(ItemUtil.emptyToNull(base.getMailadrse())); // メールアドレス
        base.setRenrakusakinon1(ItemUtil.emptyToNull(base.getRenrakusakinon1())); // 連絡先番号1
        base.setRenrakusakimemon1(ItemUtil.emptyToNull(base.getRenrakusakimemon1())); // 連絡先メモ1
        base.setRenrakusakinon2(ItemUtil.emptyToNull(base.getRenrakusakinon2())); // 連絡先番号2
        base.setRenrakusakimemon2(ItemUtil.emptyToNull(base.getRenrakusakimemon2())); // 連絡先メモ2
        base.setRenrakusakinon3(ItemUtil.emptyToNull(base.getRenrakusakinon3())); // 連絡先番号3
        base.setRenrakusakimemon3(ItemUtil.emptyToNull(base.getRenrakusakimemon3())); // 連絡先メモ3
        base.setRenrakusakinon4(ItemUtil.emptyToNull(base.getRenrakusakinon4())); // 連絡先番号4
        base.setRenrakusakimemon4(ItemUtil.emptyToNull(base.getRenrakusakimemon4())); // 連絡先メモ4
        base.setRknplnsetteidd(ItemUtil.emptyToNull(base.getRknplnsetteidd())); // 料金プラン設定日
        base.setRknplnkeizokukshndd(ItemUtil.emptyToNull(base.getRknplnkeizokukshndd())); // 料金プラン継続更新日
        base.setPlnkgndydm(ItemUtil.emptyToNull(base.getPlnkgndydm())); // プラン期限年月
        base.setKodomomein1(ItemUtil.emptyToNull(base.getKodomomein1())); // 子供1
        base.setKodomotanjoddn1(ItemUtil.emptyToNull(base.getKodomotanjoddn1())); // 子供誕生日1
        base.setKodomomein2(ItemUtil.emptyToNull(base.getKodomomein2())); // 子供2
        base.setKodomotanjoddn2(ItemUtil.emptyToNull(base.getKodomotanjoddn2())); // 子供誕生日2
        base.setKodomomein3(ItemUtil.emptyToNull(base.getKodomomein3())); // 子供3
        base.setKodomotanjoddn3(ItemUtil.emptyToNull(base.getKodomotanjoddn3())); // 子供誕生日3
        base.setMoshikomircptdd(ItemUtil.emptyToNull(base.getMoshikomircptdd())); // 申込受付日
        base.setNyukaidd(ItemUtil.emptyToNull(base.getNyukaidd())); // 入会日
        base.setTaikaidd(ItemUtil.emptyToNull(base.getTaikaidd())); // 退会日
        base.setMoshikomicncldd(ItemUtil.emptyToNull(base.getMoshikomicncldd())); // 申込キャンセル日
        base.setNyukaimokoshono(ItemUtil.emptyToNull(base.getNyukaimokoshono())); // 入会申込書番号
        base.setKaiinmokoshono(ItemUtil.emptyToNull(base.getKaiinmokoshono())); // 会員申込書番号
        base.setTaikairiyushs(ItemUtil.emptyToNull(base.getTaikairiyushs())); // 退会理由詳細
        base.setSaburtlsokocd(ItemUtil.emptyToNull(base.getSaburtlsokocd())); // サブレンタル倉庫コード
        base.setShokaiwsvstupdd(ItemUtil.emptyToNull(base.getShokaiwsvstupdd())); // 初回サーバー設置日
        base.setStupddtoshacd(ItemUtil.emptyToNull(base.getStupddtoshacd())); // 設置日登録者コード
        base.setWsvhikiagedd(ItemUtil.emptyToNull(base.getWsvhikiagedd())); // サーバー引上日
        base.setHikiagetoshacd(ItemUtil.emptyToNull(base.getHikiagetoshacd())); // 引上登録者コード

        // 営業・配達・受注情報Bean
        B0501W02DeliveryOrderBean deli = form.getDeliveryOrderBean();
        deli.setEigyoareacd(ItemUtil.emptyToNull(deli.getEigyoareacd())); // 営業エリアコード
        deli.setEigyotnshacd(ItemUtil.emptyToNull(deli.getEigyotnshacd())); // 営業担当者コード
        deli.setRcptkbn(ItemUtil.emptyToNull(deli.getRcptkbn())); // 受付区分
        deli.setKakutokukbn(ItemUtil.emptyToNull(deli.getKakutokukbn())); // 獲得区分
        deli.setKakutokutnshacd(ItemUtil.emptyToNull(deli.getKakutokutnshacd())); // 獲得担当者コード
        deli.setHonbuejntcd(ItemUtil.emptyToNull(deli.getHonbuejntcd())); // 本部エージェントコード
        deli.setHonbuejntcdpre(deli.getHonbuejntcdpre()); // 本部エージェントコード(本部エージェントコード(初期表示時の値保管用))
        deli.setKakutokuhonbukynpncd(ItemUtil.emptyToNull(deli.getKakutokuhonbukynpncd())); // 獲得本部キャンペーンコード
        deli.setKakutokuejntcd(ItemUtil.emptyToNull(deli.getKakutokuejntcd())); // 獲得エージェントコード
        deli.setKakutokukynpncd(ItemUtil.emptyToNull(deli.getKakutokukynpncd())); // 獲得キャンペーンコード
        deli.setShoukaitokuisakicd(ItemUtil.emptyToNull(deli.getShoukaitokuisakicd())); // 紹介元得意先コード
        deli.setHansokushscd(ItemUtil.emptyToNull(deli.getHansokushscd())); // 販促詳細コード
        deli.setKakutokushsmemo(ItemUtil.emptyToNull(deli.getKakutokushsmemo())); // 獲得詳細メモ
        deli.setToritugitenkaiinid(ItemUtil.emptyToNull(deli.getToritugitenkaiinid())); // 取次店会員id
        deli.setShokaitokutenmemo(ItemUtil.emptyToNull(deli.getShokaitokutenmemo())); // 初回特典メモ
        deli.setDlvrykankatsuapkihoncd(ItemUtil.emptyToNull(deli.getDlvrykankatsuapkihoncd())); // 配達管轄AP基本コード
        deli.setDlvrykankatsushopcd(ItemUtil.emptyToNull(deli.getDlvrykankatsushopcd())); // 配達管轄ショップ基本コード
        deli.setDlvrykankatsukyotencd(ItemUtil.emptyToNull(deli.getDlvrykankatsukyotencd())); // 配達管轄拠点コード
        deli.setDlvrycmpnycd(ItemUtil.emptyToNull(deli.getDlvrycmpnycd())); // 配達会社コード
        deli.setDlvryareacd(ItemUtil.emptyToNull(deli.getDlvryareacd())); // 配達エリアコード
        deli.setDlvrytnshacd(ItemUtil.emptyToNull(deli.getDlvrytnshacd())); // 配達担当者コード
        deli.setDlvryjun(ItemUtil.emptyToNull(deli.getDlvryjun())); // 配達順
        deli.setChizuno(ItemUtil.emptyToNull(deli.getChizuno())); // 地図番号
        deli.setGaiburenkeidlvrycd(ItemUtil.emptyToNull(deli.getGaiburenkeidlvrycd())); // 外部連携配達コード
        deli.setAcptjikanshiteijikoku(ItemUtil.emptyToNull(deli.getAcptjikanshiteijikoku())); // 受注時間指定時刻
        deli.setAcptjikanshiteijikokushry(
            ItemUtil.emptyToNull(deli.getAcptjikanshiteijikokushry())); // 受注時間指定時刻終了
        deli.setAzukaribtlhnsn1(ItemUtil.emptyToNull(deli.getAzukaribtlhnsn1())); // 預かりボトル本数1
        deli.setAzukaribtlhnsn2(ItemUtil.emptyToNull(deli.getAzukaribtlhnsn2())); // 預かりボトル本数2
        deli.setChuinaiyoaci(ItemUtil.emptyToNull(deli.getChuinaiyoaci())); // 注意内容ACI
        deli.setChuinaiyoap(ItemUtil.emptyToNull(deli.getChuinaiyoap())); // 注意内容AP
        deli.setRenrakujikokokai(ItemUtil.emptyToNull(deli.getRenrakujikokokai())); // 連絡事項公開
        deli.setTokkijikohikokai(ItemUtil.emptyToNull(deli.getTokkijikohikokai())); // 特記事項非公開
        deli.setPrntmemon1(ItemUtil.emptyToNull(deli.getPrntmemon1())); // 印刷メモ1
        deli.setPrntmemon2(ItemUtil.emptyToNull(deli.getPrntmemon2())); // 印刷メモ2
        deli.setPrntmemon3(ItemUtil.emptyToNull(deli.getPrntmemon3())); // 印刷メモ3

        // 営業２情報Bean
        B0501W02SaleBean sale = form.getSaleBean();
        sale.setMtmkiyuryohns(ItemUtil.emptyToNull(sale.getMtmkiyuryohns())); // まとめ買い有料本数
        sale.setMtmkimuryohns(ItemUtil.emptyToNull(sale.getMtmkimuryohns())); // まとめ買い無料本数
        sale.setYoshingendokin(ItemUtil.emptyToNull(sale.getYoshingendokin())); // 与信限度金額
        sale.setGenzaizd(StringUtils.isEmpty(sale.getGenzaizdhid()) ? "0" : sale.getGenzaizdhid()); // 現在残高
        sale.setTokuisakigaibuskbtscd1(ItemUtil.emptyToNull(sale.getTokuisakigaibuskbtscd1())); // 得意先外部識別コード1
        sale.setTokuisakigaibuskbtscd2(ItemUtil.emptyToNull(sale.getTokuisakigaibuskbtscd2())); // 得意先外部識別コード2
        sale.setTokuisakigaibuskbtscd3(ItemUtil.emptyToNull(sale.getTokuisakigaibuskbtscd3())); // 得意先外部識別コード3
        sale.setTokuisakishukeicd(ItemUtil.emptyToNull(sale.getTokuisakishukeicd())); // 得意先集計コード
        sale.setAzushono(ItemUtil.emptyToNull(sale.getAzushono())); // 預かり書番号
        sale.setHoshokinazukaridd(ItemUtil.emptyToNull(sale.getHoshokinazukaridd())); // 保証金預かり日
        sale.setHoshokinazukarikin(ItemUtil.emptyToNull(sale.getHoshokinazukarikin())); // 保証金預かり金額
        sale.setHoshokinhenkyakudd(ItemUtil.emptyToNull(sale.getHoshokinhenkyakudd())); // 保証金返却日
        sale.setHoshokinhenkyakukin(ItemUtil.emptyToNull(sale.getHoshokinhenkyakukin())); // 保証金返却金額
        sale.setWanshottocd(ItemUtil.emptyToNull(sale.getWanshottocd())); // ワンショットコード
        sale.setBtlmgntrgtdkikan(ItemUtil.emptyToNull(sale.getBtlmgntrgtdkikan())); // ボトルマージン対象期間
        sale.setNebikistrtdydmn1(ItemUtil.emptyToNull(sale.getNebikistrtdydmn1())); // 値引開始年月1
        sale.setNebikishrydydmn1(ItemUtil.emptyToNull(sale.getNebikishrydydmn1())); // 値引終了年月1
        sale.setNebikistrtdydmn2(ItemUtil.emptyToNull(sale.getNebikistrtdydmn2())); // 値引開始年月2
        sale.setNebikishrydydmn2(ItemUtil.emptyToNull(sale.getNebikishrydydmn2())); // 値引終了年月2
        sale.setNebikistrtdydmn3(ItemUtil.emptyToNull(sale.getNebikistrtdydmn3())); // 値引開始年月3
        sale.setNebikishrydydmn3(ItemUtil.emptyToNull(sale.getNebikishrydydmn3())); // 値引終了年月3

        // 営業付帯情報Bean
        B0501W02AdditionalBean add = form.getAdditionalBean();
        add.setKinmusakin1(ItemUtil.emptyToNull(add.getKinmusakin1())); // 勤務先1
        add.setKinmusakin2(ItemUtil.emptyToNull(add.getKinmusakin2())); // 勤務先2
        add.setKinmusakiyubinno(ItemUtil.emptyToNull(add.getKinmusakiyubinno())); // 勤務先郵便番号
        add.setKinmusakiadrsn1(ItemUtil.emptyToNull(add.getKinmusakiadrsn1())); // 勤務先住所1
        add.setKinmusakiadrsn2(ItemUtil.emptyToNull(add.getKinmusakiadrsn2())); // 勤務先住所2
        add.setKinmusakiadrsn3(ItemUtil.emptyToNull(add.getKinmusakiadrsn3())); // 勤務先住所3
        add.setKojinsyoumeino(ItemUtil.emptyToNull(add.getKojinsyoumeino())); // 個人証明番号
        add.setKinmusakitelno(ItemUtil.emptyToNull(add.getKinmusakitelno())); // 勤務先電話番号
        add.setKinmusakifaxno(ItemUtil.emptyToNull(add.getKinmusakifaxno())); // 勤務先FAX番号
        add.setSetaijyouhouinptdd(ItemUtil.emptyToNull(add.getSetaijyouhouinptdd())); // 世帯情報登録日
        add.setSetainushi(ItemUtil.emptyToNull(add.getSetainushi())); // 世帯主
        add.setSetaininzu(ItemUtil.emptyToNull(add.getSetaininzu())); // 世帯人数
        add.setUchikodomokazu(ItemUtil.emptyToNull(add.getUchikodomokazu())); // 内子供数
        add.setSetaisyunyu(ItemUtil.emptyToNull(add.getSetaisyunyu())); // 世帯収入
        add.setGaikokujinninzu(ItemUtil.emptyToNull(add.getGaikokujinninzu())); // 外国人人数
        add.setGaikokujinbiko(ItemUtil.emptyToNull(add.getGaikokujinbiko())); // 外国人備考

        // サーバーレンタル料情報Bean
        B0501W02RentalBean rent = form.getRentalBean();
        rent.setKeijodd(ItemUtil.emptyToNull(rent.getKeijodd())); // 計上日
        rent.setStrtdydm(ItemUtil.emptyToNull(rent.getStrtdydm())); // 開始年月
        rent.setJikaikeijodydm(ItemUtil.emptyToNull(rent.getJikaikeijodydm())); // 次回計上年月
        rent.setShrydydm(ItemUtil.emptyToNull(rent.getShrydydm())); // 終了年月
        rent.setJissekitnshacd(ItemUtil.emptyToNull(rent.getJissekitnshacd())); // 実績担当者コード
        rent.setSuryo(ItemUtil.emptyToNull(rent.getSuryo())); // 数量
        rent.setUnt(ItemUtil.emptyToNull(rent.getUnt())); // 単価
        rent.setShokaiunt(ItemUtil.emptyToNull(rent.getShokaiunt())); // 初回単価
        rent.setMeisaibiko(ItemUtil.emptyToNull(rent.getMeisaibiko())); // 明細備考

        // 名称・住所変更予約情報Bean
        B0501W02ReservationBean resv =
            ObjectUtils.isEmpty(form.getReservationBean()) ? new B0501W02ReservationBean()
                : form.getReservationBean();
        resv.setTtmasutachgdd(ItemUtil.emptyToNull(resv.getTtmasutachgdd())); // 得意先マスタ変更日
        resv.setTokuisakinmn11(ItemUtil.emptyToNull(resv.getTokuisakinmn11())); // 得意先名称11
        resv.setTokuisakinmkanan11(ItemUtil.emptyToNull(resv.getTokuisakinmkanan11())); // 得意先名称カナ11
        resv.setTokuisakinmn21(ItemUtil.emptyToNull(resv.getTokuisakinmn21())); // 得意先名称21
        resv.setTokuisakinmkanan21(ItemUtil.emptyToNull(resv.getTokuisakinmkanan21())); // 得意先名称カナ21
        resv.setYubinno1(ItemUtil.emptyToNull(resv.getYubinno1())); // 郵便番号1
        resv.setRenrakusakinon11(ItemUtil.emptyToNull(resv.getRenrakusakinon11())); // 連絡先番号11
        resv.setRenrakusakimemon11(ItemUtil.emptyToNull(resv.getRenrakusakimemon11())); // 連絡先メモ11
        resv.setAdrsn11(ItemUtil.emptyToNull(resv.getAdrsn11())); // 住所11
        resv.setRenrakusakinon21(ItemUtil.emptyToNull(resv.getRenrakusakinon21())); // 連絡先番号21
        resv.setRenrakusakimemon21(ItemUtil.emptyToNull(resv.getRenrakusakimemon21())); // 連絡先メモ21
        resv.setAdrsn21(ItemUtil.emptyToNull(resv.getAdrsn21())); // 住所21
        resv.setRenrakusakinon31(ItemUtil.emptyToNull(resv.getRenrakusakinon31())); // 連絡先番号31
        resv.setRenrakusakimemon31(ItemUtil.emptyToNull(resv.getRenrakusakimemon31())); // 連絡先メモ31
        resv.setAdrsn31(ItemUtil.emptyToNull(resv.getAdrsn31())); // 住所31
        resv.setRenrakusakinon41(ItemUtil.emptyToNull(resv.getRenrakusakinon41())); // 連絡先番号41
        resv.setRenrakusakimemon41(ItemUtil.emptyToNull(resv.getRenrakusakimemon41())); // 連絡先メモ41
        resv.setEigyoareacd1(ItemUtil.emptyToNull(resv.getEigyoareacd1())); // 営業エリアコード1
        resv.setEigyotnshacd1(ItemUtil.emptyToNull(resv.getEigyotnshacd1())); // 営業担当者コード1
        resv.setDlvrykankatsuapkihoncd1(ItemUtil.emptyToNull(resv.getDlvrykankatsuapkihoncd1())); // 配達管轄ＡＰ基本コード1
        resv.setDlvrykankatsushopcd1(ItemUtil.emptyToNull(resv.getDlvrykankatsushopcd1())); // 配達管轄ショップコード1
        resv.setDlvrycmpnycd1(ItemUtil.emptyToNull(resv.getDlvrycmpnycd1())); // 配達会社コード1
        resv.setDlvrykankatsukyotencd1(ItemUtil.emptyToNull(resv.getDlvrykankatsukyotencd1())); // 配達管轄拠点コード1
        resv.setDlvryareacd1(ItemUtil.emptyToNull(resv.getDlvryareacd1())); // 配達エリアコード1
        resv.setDlvrytnshacd1(ItemUtil.emptyToNull(resv.getDlvrytnshacd1())); // 配達担当者コード1
        resv.setPrntmemon11(ItemUtil.emptyToNull(resv.getPrntmemon11())); // 印刷メモ11
        resv.setChizuno1(ItemUtil.emptyToNull(resv.getChizuno1())); // 地図番号1
        resv.setDlvryjun1(ItemUtil.emptyToNull(resv.getDlvryjun1())); // 配達順1
        resv.setPrntmemon21(ItemUtil.emptyToNull(resv.getPrntmemon21())); // 印刷メモ21
        resv.setPrntmemon31(ItemUtil.emptyToNull(resv.getPrntmemon31())); // 印刷メモ31
        resv.setAcptjikanshiteijikokustr(ItemUtil.emptyToNull(resv.getAcptjikanshiteijikokustr())); // 受注時間指定時刻開始
        resv.setAcptjikanshiteijikokushry1(
            ItemUtil.emptyToNull(resv.getAcptjikanshiteijikokushry1())); // 受注時間指定時刻終了1
        resv.setSkmhnkymd(ItemUtil.emptyToNull(resv.getSkmhnkymd())); // 請求先マスタ変更日
        resv.setSkysakinmn11(ItemUtil.emptyToNull(resv.getSkysakinmn11())); // 請求先名称11
        resv.setSkysakinmkana(ItemUtil.emptyToNull(resv.getSkysakinmkana())); // 請求先名称カナ
        resv.setSkysakinmn2(ItemUtil.emptyToNull(resv.getSkysakinmn2())); // 請求先名称2
        resv.setSkysakinmryakusho(ItemUtil.emptyToNull(resv.getSkysakinmryakusho())); // 請求先名称略称
        resv.setYubinno2(ItemUtil.emptyToNull(resv.getYubinno2())); // 郵便番号2
        resv.setRenrakusakinon12(ItemUtil.emptyToNull(resv.getRenrakusakinon12())); // 連絡先番号12
        resv.setAdrsn12(ItemUtil.emptyToNull(resv.getAdrsn12())); // 住所12
        resv.setRenrakusakinon22(ItemUtil.emptyToNull(resv.getRenrakusakinon22())); // 連絡先番号22
        resv.setAdrsn22(ItemUtil.emptyToNull(resv.getAdrsn22())); // 住所22
        resv.setRenrakusakinon32(ItemUtil.emptyToNull(resv.getRenrakusakinon32())); // 連絡先番号32
        resv.setAdrsn32(ItemUtil.emptyToNull(resv.getAdrsn32())); // 住所32
        resv.setRenrakusakinon42(ItemUtil.emptyToNull(resv.getRenrakusakinon42())); // 連絡先番号42
        resv.setEigyotnshacd2(ItemUtil.emptyToNull(resv.getEigyotnshacd2())); // 営業担当者コード2
        resv.setKaishutnshacd(ItemUtil.emptyToNull(resv.getKaishutnshacd())); // 回収担当者コード

        return body;
    }

    /**
     * プルダウンリストの中身をシステム管理マスタから得るためのAPI.
     * 
     * <p>
     * 本APIを利用する画面項目は以下の通り
     * </p>
     * <ul>
     * <li>コード種別区分</li>
     * <li>業種区分</li>
     * <li>業種区分詳細</li>
     * </ul>
     * 
     * @param setteino 設定番号
     * @param oyasetteino 親設定番号（任意）
     * @param oyasetteicd 親設定コード（任意）
     * @return プルダウンリストの要素となるBeanのリスト（先頭に未選択の空アイテム）
     */
    @GetMapping("/pd/{setteino}")
    public List<CommonPartsListBean> getDropdownItems(@PathVariable("setteino") String setteino,
        @RequestParam(name = "oyasetteino", required = false) String oyasetteino,
        @RequestParam(name = "oyasetteicd", required = false) String oyasetteicd) {

        // 親設定番号が指定され、親設定コードが「未選択（null または EMPTY_VALUE）」なら空アイテムのみ返す
        if (oyasetteino != null && isParentCodeUnselected(oyasetteicd)) {
            // 親設定コードが設定されているのはコード種別区分以外のプルダウンリストのみ
            return GYOSHU_EMPTY_ITEM_LIST; // static final な不変リスト
        }

        // システム管理マスタサービスによりプルダウンの要素の元となるデータを取得
        S02M006TDto param = new S02M006TDto();
        param.setApkihoncd(CommonConstants.APCD_000);
        param.setShopcd(CommonConstants.SYS_SHOPCD_000);
        param.setSetteino(setteino);
        // 取得したDTOのリストをBeanのリストに変換
        List<CommonPartsListBean> items =
            s02m006tService.search(param, oyasetteino, oyasetteicd).stream()
                .map(dto -> new CommonPartsListBean(dto.getSetteicd(), dto.getMojisetteiatai()))
                .collect(util.toList());
        // 空アイテムのリストとシステム管理マスタから得たデータから生成したリストをマージして返却
        // 親設定コードがNULLなのはコード種別区分のみ
        return util.mergeList(oyasetteino == null ? CDKIND_EMPTY_ITEM_LIST : GYOSHU_EMPTY_ITEM_LIST,
            items);
    }

    /** 親設定コードが「未選択」か（null または トリムした値が空文字）を判定する */
    private static boolean isParentCodeUnselected(String oyasetteicd) {
        return oyasetteicd == null
            || SystemConstants.WORD_BLANK.equals(StringUtils.trimWhitespace(oyasetteicd));
    }

    /**
     * 更新前チェック処理.
     * 
     * @param form B0501W02Form
     * @param mode 0:更新ボタン押下時 / 1:更新後に会員情報印刷ボタン押下時
     * @param dbManagementSession DB管理セッション
     * @return エラーメッセージリスト
     */
    @PutMapping("check/{mode}")
    public List<String> check(@RequestBody B0501W02Form form, @PathVariable("mode") int mode,
        @ModelAttribute(name = "dbManagementSession_B0501W02",
            binding = true) DbManagementSession dbManagementSession) {

        List<String> retMessages = new ArrayList<>();

        // 更新前チェック
        checkParamPre(form, dbManagementSession.getUnyoDate(), mode == 1 ? true : false,
            retMessages);

        // 事前チェック用DTO生成
        B0501W02UpdateDto updateDto = createCheckDto(form);

        // 更新前チェック（Service）
        b0501w02Service.checkParamPre(updateDto, retMessages);

        return retMessages;
    }

    /**
     * 更新処理.
     * 
     * @param form B0501W02Form
     * @param dbManagementSession DB管理セッション
     * @return AciRestResponse<B0501W02Form>
     */
    @PutMapping("update")
    public AciRestResponse<B0501W02Form> update(@Validated @RequestBody B0501W02Form form,
        @ModelAttribute(name = "dbManagementSession_B0501W02",
            binding = true) DbManagementSession dbManagementSession) {

        // 更新共通処理
        B0501W02DisplayDto dto = updateCommon(form, dbManagementSession);

        // formに更新後の情報を設定
        form = getFormAfterExecute(dto);

        // 更新結果の返却
        AciRestResponse<B0501W02Form> res = new AciRestResponse<>(form);

        return res;
    }

    /**
     * 更新後に印刷処理.
     * 
     * @param form B0501W02Form
     * @param dbManagementSession DB管理セッション
     * @param result BindingResult
     * @return AciRestResponse<Z9999W01JobStateBean>
     */
    @PutMapping("updateprint")
    public AciRestResponse<Z9999W01JobStateBean> print(@Validated @RequestBody B0501W02Form form,
        @ModelAttribute(name = "dbManagementSession_B0501W02",
            binding = true) DbManagementSession dbManagementSession,
        BindingResult result) {

        // 更新共通処理
        B0501W02DisplayDto dto = updateCommon(form, dbManagementSession);

        // 更新後のフォーム情報をセッションに退避
        b0501w00session.setForm(getFormAfterExecute(dto));

        // 非同期処理の受付用パラメータModel設定
        AsyncManagementServiceModel<B0501W02DisplayDto> asyncManagementServiceModel =
            new AsyncManagementServiceModel<>(dto, this.userSessionForm, dbManagementSession,
                b0501r02Service);

        try {
            // 非同期業務処理実行＆受付ID取得
            Z9999W01JobStateBean jobStateBean =
                asyncManagementService.entryAsyncService(asyncManagementServiceModel);

            // 取得した受付IDをファイルダウンロード画面へのパラメータとして設定する
            return new AciRestResponse<>(jobStateBean);

        } catch (BusinessException be) {
            // 業務例外の場合、エラーメッセージを画面表示する
            throw BusinessExceptionFactory.validationFailed(be, MessageID.W260, be.getMessage());
        } catch (Exception e) {
            // システム例外の場合、エラーメッセージを画面表示する
            throw BusinessExceptionFactory.validationFailed(e, MessageID.E002);
        }
    }

    /**
     * 更新共通処理.
     * 
     * @param form B0501W02Form
     * @param dbManagementSession DB管理セッション
     * @return B0501W02DisplayDto
     */
    private B0501W02DisplayDto updateCommon(B0501W02Form form,
        DbManagementSession dbManagementSession) {

        // 相関チェック
        checkParam(form, dbManagementSession.getUnyoDate());

        // 更新用DTO生成
        B0501W02UpdateDto updateDto = createUpdateDto(form, dbManagementSession);

        if (StringUtils.isEmpty(updateDto.getTokuisakicd())) {
            // 登録処理実行
            b0501w02Service.insert(updateDto);
        } else {
            // 更新処理実行
            b0501w02Service.update(updateDto);
        }

        // 画面表示用に再取得
        return b0501w02Service.init(updateDto.getApkihoncd(), updateDto.getShopcd(),
            updateDto.getTokuisakicd(), updateDto.getUnyoDate());
    }

    /**
     * フォーム情報再設定処理.
     * 
     * @param dto 表示用DTO
     * @return B0501W02Form
     */
    private B0501W02Form getFormAfterExecute(B0501W02DisplayDto dto) {

        B0501W02Form form = new B0501W02Form();

        // form内のオブジェクトに再設定
        form.setBaseBean(controller.getBaseBean(dto, null, null, null, null, null));
        form.setDeliveryOrderBean(controller.getDeliveryOrderBean(dto));
        form.setSaleBean(controller.getSaleBean(dto));
        form.setAdditionalBean(controller.getAdditionalBean(dto));
        form.setRentalBean(controller.getRentalBean(dto));
        form.setReservationBean(controller.getReservationBean(dto));

        return form;
    }

    /**
     * 登録更新用DTOを生成する.
     * 
     * @param form B0501W02Form
     * @param dbm DB管理セッション
     * @return B0501W02UpdateDto
     */
    private B0501W02UpdateDto createUpdateDto(B0501W02Form form, DbManagementSession dbm) {

        B0501W02BaseBean base = form.getBaseBean();
        B0501W02DeliveryOrderBean deli = form.getDeliveryOrderBean();
        B0501W02SaleBean sale = form.getSaleBean();
        B0501W02AdditionalBean add = form.getAdditionalBean();
        B0501W02RentalBean rent = form.getRentalBean();
        B0501W02ReservationBean resv = form.getReservationBean();

        B0501W02UpdateDto dto = new B0501W02UpdateDto();

        // 得意先情報
        dto.setApkihoncd(base.getApkihoncd()); // ＡＰ基本コード
        dto.setShopcd(base.getShopcd()); // ショップコード
        dto.setTokuisakicd(base.getPtokuisakicd()); // 得意先コード
        dto.setTokuisakicdbefore(base.getPtokuisakicd()); // 得意先コード(初期表示時)
        dto.setHaneimotottcd(base.getHaneimototokuisakicd()); // 反映元得意先コード
        dto.setFttbnyukaiflg(base.getSainyukaifrg()); // 再入会フラグ
        dto.setKaiincd(base.getKaiincd()); // 会員コード
        dto.setKaiincdkbn(base.getKaiincdkbn()); // 会員コード区分
        dto.setTokuisakijotai(base.getTokuisakijotai()); // 得意先状態
        dto.setJotaists(base.getJotaists()); // 状態ステータス
        dto.setTokuisakikbn(base.getTokuisakikbn()); // 得意先区分
        dto.setJutakudlvrycstkbn(base.getJutakudlvrycstkbn()); // 受託配達顧客区分
        dto.setRiyowsvkbn(base.getRiyowsvkbn()); // 利用サーバー区分
        dto.setCdkindkbn(base.getCdkindkbn()); // コード種別区分
        dto.setGyoshukbn(base.getGyoshukbn()); // 業種区分
        dto.setGyoshushskbn(base.getGyoshushskbn()); // 業種詳細区分
        dto.setSkysakicd(base.getSkysakicd()); // 請求先コード
        dto.setSkysakicdbefore(base.getSkysakicdbefore()); // 請求先コード変更前
        dto.setSkysetteikbn(base.getSkysetteikbn()); // 請求設定区分
        dto.setSkysetteikbnbefore(base.getSkysetteikbnbefore()); // 請求設定区分変更前
        dto.setSkysakikanrikbn(""); // 請求先管理区分 ※サービスで設定
        dto.setOyatokuisakicd(base.getOyatokuisakicd()); // 親得意先コード
        dto.setTokuisakishopcd(base.getTokuisakishopcd()); // 得意先ショップコード
        dto.setItakuaiteapkihoncd(base.getItakuaiteapkihoncd()); // 委託相手ＡＰ基本コード
        dto.setItakuaiteshopcd(base.getItakuaiteshopcd()); // 委託相手ショップコード
        dto.setItakuaitetokuisakicd(base.getItakuaitetokuisakicd()); // 委託相手得意先コード
        dto.setJigyoshocd(base.getJigyoshocd()); // 事業所コード
        dto.setTokuisakinmn1(base.getTokuisakinmn1()); // 得意先名称１
        dto.setTokuisakinmn2(base.getTokuisakinmn2()); // 得意先名称２
        dto.setTokuisakikeishokbn(base.getTokuisakikeishokbn()); // 得意先敬称区分
        dto.setTokuisakinmkanan1(base.getTokuisakinmkanan1()); // 得意先名称カナ１
        dto.setTokuisakinmkanan2(base.getTokuisakinmkanan2()); // 得意先名称カナ２
        dto.setYubinno(base.getYubinno()); // 郵便番号
        dto.setChihoupubdanticdn1(base.getChihoupubdanticdn1()); // 地方公共団体コード1
        dto.setChihoupubdanticdn2(base.getChihoupubdanticdn2()); // 地方公共団体コード2
        dto.setAdrsn1(base.getAdrsn1()); // 住所１
        dto.setAdrsn2(base.getAdrsn2()); // 住所２
        dto.setAdrsn3(base.getAdrsn3()); // 住所３
        dto.setRenrakusakinokbnn1(base.getRenrakusakinokbnn1()); // 連絡先番号区分１
        dto.setRenrakusakinon1(base.getRenrakusakinon1()); // 連絡先番号１
        dto.setRenrakusakimemon1(base.getRenrakusakimemon1()); // 連絡先メモ１
        dto.setRenrakusakinokbnn2(base.getRenrakusakinokbnn2()); // 連絡先番号区分２
        dto.setRenrakusakinon2(base.getRenrakusakinon2()); // 連絡先番号２
        dto.setRenrakusakimemon2(base.getRenrakusakimemon2()); // 連絡先メモ２
        dto.setRenrakusakinokbnn3(base.getRenrakusakinokbnn3()); // 連絡先番号区分３
        dto.setRenrakusakinon3(base.getRenrakusakinon3()); // 連絡先番号３
        dto.setRenrakusakimemon3(base.getRenrakusakimemon3()); // 連絡先メモ３
        dto.setRenrakusakinokbnn4(base.getRenrakusakinokbnn4()); // 連絡先番号区分４
        dto.setRenrakusakinon4(base.getRenrakusakinon4()); // 連絡先番号４
        dto.setRenrakusakimemon4(base.getRenrakusakimemon4()); // 連絡先メモ４
        dto.setClckensakuyotelnon1(base.getRenrakusakinon1()); // コールセンター検索用電話番号１
        dto.setClckensakuyotelnon2(base.getRenrakusakinon2()); // コールセンター検索用電話番号２
        dto.setClckensakuyotelnon3(base.getRenrakusakinon3()); // コールセンター検索用電話番号３
        dto.setClckensakuyotelnon4(base.getRenrakusakinon4()); // コールセンター検索用電話番号４
        dto.setMailadrse(base.getMailadrse()); // メールアドレス
        dto.setMerumagakbn(base.getMerumagakbn()); // メルマガ区分
        dto.setNyukaiinptdd(DateTimeUtil.toNoSlashedDate(add.getNyukaishajiyohoinptdd())); // 入会登録日
        dto.setRiyomokutekikbn(add.getRiyomokutekikbn()); // 利用目的区分
        dto.setNyushadydm(DateTimeUtil.toNoSlashedDateYYYYMM(add.getNyushadydm())); // 入社年月
        dto.setSeibetsukbn(add.getSeibetsukbn()); // 性別区分
        dto.setSykugyokbn(add.getSykugyokbn()); // 職業区分
        dto.setHaiguumuflg(add.getHaigusyakbn()); // 配偶有無フラグ
        dto.setSonotadokyoninumuflg(add.getSonotadokyoninumukubun()); // その他同居人有無フラグ
        dto.setShihasyazokukbn(add.getShihasyazokukbn()); // 支払者続柄区分
        dto.setKojinsyoumeikindkbn(add.getKojinsyoumeikindkbn()); // 個人証明種別区分
        dto.setKojinsyoumeino(add.getKojinsyoumeino()); // 個人証明番号
        dto.setKazokukoseikazudansei(add.getSetaininzu()); // 利用者数
        dto.setKazokukoseikazujosei(add.getUchikodomokazu()); // 内子供数
        dto.setSetaijohoinptdd(DateTimeUtil.toNoSlashedDate(add.getSetaijyouhouinptdd())); // 世帯情報登録日
        dto.setSetainushi(add.getSetainushi()); // 世帯主
        dto.setJugyoinkibkbn(add.getJugyoinkibkbn()); // 従業員規模区分
        dto.setKojudykazukbn(add.getKojudykazukbn()); // 居住年数区分
        dto.setPetjohokbn(add.getPetjohokbn()); // ペット情報区分
        dto.setSetaisyunyu(
            !StringUtils.isEmpty(add.getSetaisyunyu()) ? ItemUtil.removeComma(add.getSetaisyunyu())
                : ""); // 世帯収入
        dto.setDoitsusekeninzukbn(add.getDoitsusekeninzukbn()); // 同一生計人数区分
        dto.setGaikokujinninzu(add.getGaikokujinninzu()); // 外国人人数
        dto.setGaikokujinbiko(add.getGaikokujinbiko()); // 外国人備考
        dto.setNyushatanjodd(DateTimeUtil.toNoSlashedDate(add.getNyushatanjodd())); // 入会者誕生日
        dto.setKodomomein1(base.getKodomomein1()); // 子供名１
        dto.setKodomotanjoddn1(DateTimeUtil.toNoSlashedDate(base.getKodomotanjoddn1())); // 子供誕生日１
        dto.setKodomomein2(base.getKodomomein2()); // 子供名２
        dto.setKodomotanjoddn2(DateTimeUtil.toNoSlashedDate(base.getKodomotanjoddn2())); // 子供誕生日２
        dto.setKodomomein3(base.getKodomomein3()); // 子供名３
        dto.setKodomotanjoddn3(DateTimeUtil.toNoSlashedDate(base.getKodomotanjoddn3())); // 子供誕生日３
        dto.setMoshikomircptdd(DateTimeUtil.toNoSlashedDate(base.getMoshikomircptdd())); // 申込受付日
        dto.setMoshikomircptddbefore(DateTimeUtil.toNoSlashedDate(base.getMoshikomircptddbefore())); // 申込受付日変更前
        dto.setNyukaimokoshono(base.getNyukaimokoshono()); // 入会申込書番号
        dto.setMoshikomicncldd(DateTimeUtil.toNoSlashedDate(base.getMoshikomicncldd())); // 申込キャンセル日
        dto.setNyukaidd(DateTimeUtil.toNoSlashedDate(base.getNyukaidd())); // 入会日
        dto.setNyukaiddbefore(DateTimeUtil.toNoSlashedDate(base.getNyukaiddbefore())); // 入会日変更前
        dto.setMoshikomircptkbn(base.getMokoshorcptkbn()); // 申込受付区分
        dto.setKaiinmokoshono(base.getKaiinmokoshono()); // 会員申込書番号
        dto.setTaikaidd(DateTimeUtil.toNoSlashedDate(base.getTaikaidd())); // 退会日
        dto.setTaikaiddbefore(DateTimeUtil.toNoSlashedDate(base.getTaikaiddbefore())); // 退会日変更前
        dto.setTaikairiyukbn(base.getTaikairiyukbn()); // 退会理由区分
        dto.setTaikairiyushs(base.getTaikairiyushs()); // 退会理由詳細
        dto.setEigyoareacd(deli.getEigyoareacd()); // 営業エリアコード
        dto.setTokubetsuuntkbn(base.getRknkindtokubetsuuntkbn()); // 特別単価区分
        dto.setOyakohaneikinoflg(base.getOyakohaneikinoflg()); // 親子反映機能
        dto.setRknkindtokubetsunebikiflg(base.getRknkindtokubetsunebikiflg()); // 料金種別特別値引フラグ
        dto.setRknkindtokubetsuuntflg(base.getRknkindtokubetsuuntkbn()); // 料金種別特別単価フラグ
        dto.setRknkindkihonflg(
            CommonConstants.PLNCD_000.equals(base.getPln()) ? CommonConstants.RKNKINDKIHONFLG_1
                : CommonConstants.RKNKINDKIHONFLG_0); // 料金種別基本フラグ
        dto.setPlncd(base.getPln()); // プランコード
        dto.setRknplnsetteidd(DateTimeUtil.toNoSlashedDate(base.getRknplnsetteidd())); // 料金プラン設定日
        dto.setPlnshokaiiyakukinflg(base.getPlnshokaiiyakukinflg()); // プラン設定日違約金フラグ
        dto.setRknplnkeizokukshndd(DateTimeUtil.toNoSlashedDate(base.getRknplnkeizokukshndd())); // 料金プラン継続更新日
        dto.setPlnkshniyakukinflg(base.getPlnkshniyakukinflg()); // プラン継続更新日違約金フラグ
        dto.setPlnkgndydm(DateTimeUtil.toNoSlashedDateYYYYMM(base.getPlnkgndydm())); // プラン期限年月
        dto.setJishasvcd(base.getJishasvkbn()); // 自社サービス
        dto.setIyakukinkanritrgtgai(base.getIyakukinkanritrgtgai()); // 違約金管理情報管理しないチェック
        dto.setIyakukinkanriflg(
            CHK_ON.equals(base.getIyakukinkanritrgtgai()) ? CommonConstants.IYAKUKINKANRIFLG_0
                : CommonConstants.IYAKUKINKANRIFLG_1); // 違約金管理フラグ
        dto.setEigyotnshacd(deli.getEigyotnshacd()); // 営業担当者コード
        dto.setUrikeijotnshakbn(deli.getUrikeijotnshakbn()); // 売上計上担当者区分
        dto.setKakutokukbn(deli.getKakutokukbn()); // 獲得区分
        dto.setRcptkbn(deli.getRcptkbn()); // 受付区分
        dto.setBaitaikbn(deli.getBaitaikbn()); // 媒体区分
        dto.setKakutokutnshacd(deli.getKakutokutnshacd()); // 獲得担当者コード
        // 本部エージェントＡＰ基本コード、本部エージェントショップコード、本部エージェントコード
        if (CommonConstants.KBN_KIHON_ACI.equals(base.getKihonkbn())) {
            String honbuejntcd = deli.getHonbuejntcd();
            dto.setHonbuejntapkihoncd(!StringUtils.isEmpty(honbuejntcd) ? dbm.getApkihoncd() : ""); // 本部エージェントＡＰ基本コード
            dto.setHonbuejntshopcd(!StringUtils.isEmpty(honbuejntcd) ? dbm.getShopcd() : ""); // 本部エージェントショップコード
            dto.setHonbuejntcd(honbuejntcd); // 本部エージェントコード
        } else {
            // 本部エージェントコードが非活性の場合は、画面表示時の値で更新
            dto.setHonbuejntapkihoncd(base.getHonbuejntapkihoncd()); // 本部エージェントＡＰ基本コード(画面初期表示時の値で更新)
            dto.setHonbuejntshopcd(base.getHonbuejntshopcd()); // 本部エージェントショップコード(画面初期表示時の値で更新)
            dto.setHonbuejntcd(deli.getHonbuejntcdpre()); // 本部エージェントコード(画面初期表示時の値で更新)
        }
        dto.setKakutokuejntcd(deli.getKakutokuejntcd()); // 獲得エージェントコード
        dto.setShoukaitokuisakicd(deli.getShoukaitokuisakicd()); // 紹介得意先コード
        dto.setShoukaitokuisakicdbefore(deli.getShoukaitokuisakicdbefore()); // 紹介得意先コード変更前
        dto.setHansokushscd(deli.getHansokushscd()); // 販促詳細コード
        dto.setKakutokushsmemo(deli.getKakutokushsmemo()); // 獲得詳細メモ
        dto.setToritugitenkaiinid(deli.getToritugitenkaiinid()); // 取次店会員ID
        dto.setHonshokaino(deli.getHonshokaino()); // 本部紹介番号
        dto.setKakutokuhonbukynpncd(deli.getKakutokuhonbukynpncd()); // 獲得本部キャンペーンコード
        dto.setHonbukynpnkeihinkbn(deli.getKakutokuhonbukynpnkeihinkbn()); // 本部キャンペーン景品区分
        dto.setKakutokukynpncd(deli.getKakutokukynpncd()); // 獲得キャンペーンコード
        dto.setKynpnkeihinkbn(deli.getKakutokukeihin()); // キャンペーン景品区分
        dto.setShokaitokutenmemo(deli.getShokaitokutenmemo()); // 初回特典メモ
        dto.setMgntrgtkbn(sale.getMgntrgtkbn()); // マージン対象区分
        dto.setWanshottocd(sale.getWanshottocd()); // ワンショットコード
        dto.setBonasumgncnt(sale.getBonasumgncnt()); // ボーナスマージン件数
        dto.setBonasumgnkin(sale.getBonasumgnkin()); // ボーナスマージン金額
        dto.setBtlmgntrgtdkikan(sale.getBtlmgntrgtdkikan()); // ボトルマージン対象期間
        dto.setMgnyoshncdn1(sale.getMgnyoshncdn1()); // マージン用商品コード１
        dto.setMgnyoshncdn2(sale.getMgnyoshncdn2()); // マージン用商品コード２
        dto.setMgnyoshncdn3(sale.getMgnyoshncdn3()); // マージン用商品コード３
        dto.setMgnyoshncdn4(sale.getMgnyoshncdn4()); // マージン用商品コード４
        dto.setMgncdn1(sale.getMgncdn1()); // マージンコード１
        dto.setMgncdn2(sale.getMgncdn2()); // マージンコード２
        dto.setMgncdn3(sale.getMgncdn3()); // マージンコード３
        dto.setMgncdn4(sale.getMgncdn4()); // マージンコード４
        dto.setJidonebikicdn1(sale.getJidonebikicd1()); // 自動値引コード１
        dto.setNebikistrtdydmn1(DateTimeUtil.toNoSlashedDateYYYYMM(sale.getNebikistrtdydmn1())); // 値引開始年月１
        dto.setNebikishrydydmn1(DateTimeUtil.toNoSlashedDateYYYYMM(sale.getNebikishrydydmn1())); // 値引終了年月１
        dto.setJidonebikicdn2(sale.getJidonebikicd2()); // 自動値引コード２
        dto.setNebikistrtdydmn2(DateTimeUtil.toNoSlashedDateYYYYMM(sale.getNebikistrtdydmn2())); // 値引開始年月２
        dto.setNebikishrydydmn2(DateTimeUtil.toNoSlashedDateYYYYMM(sale.getNebikishrydydmn2())); // 値引終了年月２
        dto.setJidonebikicdn3(sale.getJidonebikicd3()); // 自動値引コード３
        dto.setNebikistrtdydmn3(DateTimeUtil.toNoSlashedDateYYYYMM(sale.getNebikistrtdydmn3())); // 値引開始年月３
        dto.setNebikishrydydmn3(DateTimeUtil.toNoSlashedDateYYYYMM(sale.getNebikishrydydmn3())); // 値引終了年月３
        dto.setPointkbn(sale.getPointkbn()); // ポイント付与管理
        dto.setMyaquakaiininptumu(sale.getMyaquakaiininptumu()); // マイアクア会員登録
        dto.setMtmkipointkbn(sale.getMtmkipointkbn()); // まとめ買いポイント区分
        dto.setMtmkisuryo(!StringUtils.isEmpty(sale.getMtmkisuryo()) ? ItemUtil.removeComma(sale.getMtmkisuryo())
                : ""); // まとめ買い数量
        dto.setMtmkipointsu(!StringUtils.isEmpty(sale.getMtmkipointsu()) ? ItemUtil.removeComma(sale.getMtmkipointsu())
                : ""); // まとめ買いポイント数
        dto.setNyukaitokutenumu(sale.getNyukaitokutenumu()); // 入会特典区分
        dto.setNyukaitokutenpointsu(!StringUtils.isEmpty(sale.getNyukaitokutenpointsu()) ? ItemUtil.removeComma(sale.getNyukaitokutenpointsu())
                : ""); // 入会特典ポイント数
        dto.setPointrnkcdshin(sale.getPointrnkshin()); // ステージ修正（新）
        dto.setTekiyoudd(DateTimeUtil.toNoSlashedDate(sale.getTekiyoudd())); // ステージ切替日
        dto.setPointrnkcdgen(sale.getPointrnkcdgen()); // 適用ステージコード（現）
        dto.setRtlskykanrikbn(base.getRtlskykanrikbn()); // レンタル請求管理区分
        dto.setSaburtlsokocd(base.getSaburtlsokocd()); // サブレンタル倉庫コード
        dto.setShokaiwsvstupdd(DateTimeUtil.toNoSlashedDate(base.getShokaiwsvstupdd())); // 初回サーバー設置日
        dto.setStupddtoshaapkihoncd(
            !StringUtils.isEmpty(base.getStupddtoshacd()) ? base.getApkihoncd() : null); // 設置日登録者ＡＰ基本コード
        dto.setStupddtoshashopcd(
            !StringUtils.isEmpty(base.getStupddtoshacd()) ? base.getShopcd() : null); // 設置日登録者ショップコード
        dto.setStupddtoshacd(base.getStupddtoshacd()); // 設置日登録者コード
        dto.setWsvhikiagedd(DateTimeUtil.toNoSlashedDate(base.getWsvhikiagedd())); // サーバー引上日
        dto.setHikiageddtoshaapkihoncd(
            !StringUtils.isEmpty(base.getHikiagetoshacd()) ? base.getApkihoncd() : null); // 引上日登録者ＡＰ基本コード
        dto.setHikiageddtoshashopcd(
            !StringUtils.isEmpty(base.getHikiagetoshacd()) ? base.getShopcd() : null); // 引上日登録者ショップコード
        dto.setHikiageddtoshacd(base.getHikiagetoshacd()); // 引上日登録者コード
        dto.setDlvrykankatsuapkihoncd(deli.getDlvrykankatsuapkihoncd()); // 配達管轄ＡＰ基本コード
        dto.setDlvrykankatsushopcd(deli.getDlvrykankatsushopcd()); // 配達管轄ショップコード
        dto.setDlvrykankatsukyotencd(deli.getDlvrykankatsukyotencd()); // 配達管轄拠点コード
        dto.setDlvrycmpnycd(deli.getDlvrycmpnycd()); // 配達会社コード
        dto.setDlvrytnshacd(deli.getDlvrytnshacd()); // 配達担当者コード
        dto.setDlvryjun(deli.getDlvryjun()); // 配達順
        dto.setDlvryareacd(deli.getDlvryareacd()); // 配達エリアコード
        dto.setDlvrykbn(deli.getDlvrykbn()); // 配達区分
        dto.setFzaiokikanoflg(deli.getFzaiokikanoflg()); // 不在置き可能フラグ
        dto.setNohinsakijohon1(deli.getNohinsakijohon1()); // 納品先情報１
        dto.setNohinsakijohon2(deli.getNohinsakijohon2()); // 納品先情報２
        dto.setChizuno(deli.getChizuno()); // 地図番号
        dto.setGaiburenkeidlvrycd(deli.getGaiburenkeidlvrycd()); // 外部連携配達コード
        dto.setAcptteishiflg(deli.getAcptteishiflg()); // 受注停止フラグ
        dto.setAcptjikanshiteijikoku(
            DateTimeUtil.toNoColonDateHHMM(deli.getAcptjikanshiteijikoku())); // 受注時間指定時刻
        dto.setAcptjikanshiteijikokushry(
            DateTimeUtil.toNoColonDateHHMM(deli.getAcptjikanshiteijikokushry())); // 受注時間指定時刻終了
        dto.setAcptjikanshiteikbn(deli.getAcptjikanshiteikbn()); // 受注時間指定区分
        dto.setNestlehachusakikbn(deli.getNestlehachusakikbn()); // ネスレ発注先区分
        dto.setChuitokuisakikbnaci(deli.getChuitokuisakikbnaci()); // 注意得意先区分ＡＣＩ
        dto.setChuinaiyoaci(deli.getChuinaiyoaci()); // 注意内容ＡＣＩ
        dto.setChuitokuisakikbnap(deli.getChuitokuisakikbnap()); // 注意得意先区分ＡＰ
        dto.setChuinaiyoap(deli.getChuinaiyoap()); // 注意内容ＡＰ
        dto.setRenrakujikokokai(deli.getRenrakujikokokai()); // 連絡事項公開
        dto.setTokkijikohikokai(deli.getTokkijikohikokai()); // 特記事項非公開
        dto.setPrntmemon1(deli.getPrntmemon1()); // 印刷メモ１
        dto.setPrntmemon2(deli.getPrntmemon2()); // 印刷メモ２
        dto.setPrntmemon3(deli.getPrntmemon3()); // 印刷メモ３
        dto.setKinotptnashiflg(deli.getKinotptnashiflg()); // 金額出力無フラグ
        dto.setHoshokinkbn(sale.getHoshokinkbn()); // 保証金区分
        dto.setHoshokinazukaridd(DateTimeUtil.toNoSlashedDate(sale.getHoshokinazukaridd())); // 保証金預かり日
        dto.setHoshokinazukarikin(!StringUtils.isEmpty(sale.getHoshokinazukarikin())
            ? ItemUtil.removeComma(sale.getHoshokinazukarikin())
            : ""); // 保証金預かり金額
        dto.setAzushono(sale.getAzushono()); // 預かり書番号
        dto.setHoshokinhenkyakudd(DateTimeUtil.toNoSlashedDate(sale.getHoshokinhenkyakudd())); // 保証金返却日
        dto.setHoshokinhenkyakukin(!StringUtils.isEmpty(sale.getHoshokinhenkyakukin())
            ? ItemUtil.removeComma(sale.getHoshokinhenkyakukin())
            : ""); // 保証金返却金額
        dto.setApbetsutokuisakirnk(sale.getApbetsutokuisakirnk()); // ＡＰ別得意先ランク
        dto.setKyuminkbn(""); // 休眠区分 ※サービスで設定
        dto.setKyuminkanri(sale.getKyuminkanri()); // 休眠管理
        dto.setKinmusakin1(add.getKinmusakin1()); // 勤務先１
        dto.setKinmusakin2(add.getKinmusakin2()); // 勤務先２
        dto.setKinmusakiyubinno(add.getKinmusakiyubinno()); // 勤務先郵便番号
        dto.setKinmusakichihoupubdanticdn1(add.getKinmusakichihoupubdanticdn1()); // 勤務先地方公共団体コード1
        dto.setKinmusakichihoupubdanticdn2(add.getKinmusakichihoupubdanticdn2()); // 勤務先地方公共団体コード2
        dto.setKinmusakiadrsn1(add.getKinmusakiadrsn1()); // 勤務先住所１
        dto.setKinmusakiadrsn2(add.getKinmusakiadrsn2()); // 勤務先住所２
        dto.setKinmusakiadrsn3(add.getKinmusakiadrsn3()); // 勤務先住所３
        dto.setKinmusakitelno(add.getKinmusakitelno()); // 勤務先電話番号
        dto.setKinmusakifaxno(add.getKinmusakifaxno()); // 勤務先ＦＡＸ番号
        dto.setYoshingendokin(!StringUtils.isEmpty(sale.getYoshingendokin())
            ? ItemUtil.removeComma(sale.getYoshingendokin())
            : ""); // 与信限度金額
        dto.setGenzaizd(sale.getGenzaizdhid()); // 現在残高
        dto.setTokuisakigaibuskbtscd(sale.getTokuisakigaibuskbtscd1()); // 得意先外部識別コード
        dto.setTokuisakigaibuskbtscd2(sale.getTokuisakigaibuskbtscd2()); // 得意先外部識別コード2
        dto.setTokuisakigaibuskbtscd3(sale.getTokuisakigaibuskbtscd3()); // 得意先外部識別コード3
        dto.setTokuisakishukeicd(sale.getTokuisakishukeicd()); // 得意先集計コード
        dto.setTokuisakishoshakazu(sale.getShoshakazu()); // 得意先紹介者数
        dto.setStartup(base.getStartup()); // スタートアップ
        dto.setMtmkisvkbn(sale.getMtmkisvkbn()); // まとめ買いサービス区分
        dto.setMtmkiyuryohns(sale.getMtmkiyuryohns()); // まとめ買い有料本数
        dto.setMtmkimuryohns(sale.getMtmkimuryohns()); // まとめ買い無料本数
        dto.setMuryobtlsvkbn(sale.getMuryobtlsvkbn()); // 無料ボトルサービス区分
        dto.setMyaqauntjohodsplyumuflg(sale.getMyaqauntjohodsplyumuflg()); // マイアクア単価情報表示有無フラグ
        dto.setMyaqaskyjohodsplyumuflg(sale.getMyaqaskyjohodsplyumuflg()); // マイアクア請求情報表示有無フラグ
        dto.setTashakirikae(deli.getTashakirikae()); // 他社切替
        dto.setCollabokbn(sale.getCollabokbn()); // コラボ区分
        dto.setYukokbn(base.getYukokbn()); // 有効区分

        // 定例売上仕入情報
        dto.setTeireisetteikbn(rent.getTeireisetteikbn()); // 定例設定区分
        dto.setUrisiirekbn(CommonConstants.URISIIREKBN_1); // 売上仕入区分
        dto.setTeireihasseino(rent.getTeireihasseino()); // 定例発生番号
        dto.setHasseikankakukeijoptn(rent.getHasseikankakukeijoptn()); // 発生間隔計上パターン
        dto.setShncd(rent.getShnmei()); // 商品コード
        dto.setSuryo(ItemUtil.removeComma(rent.getSuryo())); // 数量
        dto.setHyojunprice(ItemUtil.removeComma(rent.getUnt())); // 標準価格
        dto.setShokaiunt(ItemUtil.removeComma(rent.getShokaiunt())); // 初回単価
        dto.setShokaikin(""); // 初回金額 ※サービスで設定
        dto.setShokaishohitaxkin(rent.getShohitax1()); // 初回消費税額
        dto.setShohitaxkin(rent.getShohitax1()); // 消費税
        dto.setKeijodd(rent.getKeijodd()); // 計上日
        dto.setShokaikeijodydm(DateTimeUtil.toNoSlashedDateYYYYMM(rent.getStrtdydm())); // 初回計上年月
        dto.setZnkhasseikeijodd(DateTimeUtil.toNoSlashedDate(rent.getZnkhasseikeijodd())); // 前回発生計上日
        dto.setJikaikeijodydm(DateTimeUtil.toNoSlashedDateYYYYMM(rent.getJikaikeijodydm())); // 次回計上年月
        dto.setShrydydm(DateTimeUtil.toNoSlashedDateYYYYMM(rent.getShrydydm())); // 終了年月
        dto.setDnpyhasseiarinashiflg(rent.getUrihasseiarinashiflg()); // 伝票発生有無フラグ
        dto.setRtlrknshukeikbn(rent.getUrisettei()); // レンタル料金集計区分
        dto.setMeisaibiko(rent.getMeisaibiko()); // 明細備考
        dto.setJissekitnshacd(rent.getJissekitnshacd()); // 実績担当者コード
        dto.setUrisettei(rent.getUrisettei()); // 売上設定
        dto.setYukokbn1(rent.getYukokbn1()); // 有効区分１

        // 得意先実績情報
        dto.setAzukaribtln1n2rittoruhns(ItemUtil.removeComma(deli.getAzukaribtlhnsn1())); // 預かりボトル１２Ｌ本数
        dto.setAzukaribtln8rittoruhns(ItemUtil.removeComma(deli.getAzukaribtlhnsn2())); // 預かりボトル８Ｌ本数

        // 得意先予約情報
        dto.setTokuisakimasutahnkflg(resv.getTokuisakimasutahnkflg()); // 得意先マスタ変更フラグ
        dto.setYoyakuinptdd1(resv.getYoyakuinptdd1()); // 予約登録日(得意先予約)
        dto.setTtmasutachgdd(DateTimeUtil.toNoSlashedDate(resv.getTtmasutachgdd())); // 得意先マスタ変更日
        dto.setJigyoshocd1(resv.getJigyoshocd1()); // 事業所コード(得意先予約)
        dto.setTokuisakinmn11(resv.getTokuisakinmn11()); // 得意先名称１(得意先予約)
        dto.setTokuisakinmn21(resv.getTokuisakinmn21()); // 得意先名称２(得意先予約)
        dto.setTokuisakikeishokbn1(resv.getTokuisakikeishokbn1()); // 得意先敬称区分(得意先予約)
        dto.setTokuisakinmkanan11(resv.getTokuisakinmkanan11()); // 得意先名称カナ１(得意先予約)
        dto.setTokuisakinmkanan21(resv.getTokuisakinmkanan21()); // 得意先名称カナ２(得意先予約)
        dto.setYubinno1(resv.getYubinno1()); // 郵便番号(得意先予約)
        dto.setTokuisakichihoupubdanticdn1(resv.getTokuisakichihoupubdanticdn1()); // 得意先地方公共団体コード１（得意先予約）
        dto.setTokuisakichihoupubdanticdn2(resv.getTokuisakichihoupubdanticdn2()); // 得意先地方公共団体コード２（得意先予約）
        dto.setAdrsn11(resv.getAdrsn11()); // 住所１(得意先予約)
        dto.setAdrsn21(resv.getAdrsn21()); // 住所２(得意先予約)
        dto.setAdrsn31(resv.getAdrsn31()); // 住所３(得意先予約)
        dto.setRenrakusakinokbnn11(resv.getRenrakusakinokbnn11()); // 連絡先番号区分１(得意先予約)
        dto.setRenrakusakinon11(resv.getRenrakusakinon11()); // 連絡先番号１(得意先予約)
        dto.setRenrakusakimemon11(resv.getRenrakusakimemon11()); // 連絡先メモ１(得意先予約)
        dto.setRenrakusakinokbnn21(resv.getRenrakusakinokbnn21()); // 連絡先番号区分２(得意先予約)
        dto.setRenrakusakinon21(resv.getRenrakusakinon21()); // 連絡先番号２(得意先予約)
        dto.setRenrakusakimemon21(resv.getRenrakusakimemon21()); // 連絡先メモ２(得意先予約)
        dto.setRenrakusakinokbnn31(resv.getRenrakusakinokbnn31()); // 連絡先番号区分３(得意先予約)
        dto.setRenrakusakinon31(resv.getRenrakusakinon31()); // 連絡先番号３(得意先予約)
        dto.setRenrakusakimemon31(resv.getRenrakusakimemon31()); // 連絡先メモ３(得意先予約)
        dto.setRenrakusakinokbnn41(resv.getRenrakusakinokbnn41()); // 連絡先番号区分４(得意先予約)
        dto.setRenrakusakinon41(resv.getRenrakusakinon41()); // 連絡先番号４(得意先予約)
        dto.setRenrakusakimemon41(resv.getRenrakusakimemon41()); // 連絡先メモ４(得意先予約)
        dto.setEigyoareacd1(resv.getEigyoareacd1()); // 営業エリアコード(得意先予約)
        dto.setEigyotnshacd1(resv.getEigyotnshacd1()); // 営業担当者コード(得意先予約)
        dto.setDlvrykankatsuapkihoncd1(resv.getDlvrykankatsuapkihoncd1()); // 配達管轄ＡＰ基本コード(得意先予約)
        dto.setDlvrykankatsushopcd1(resv.getDlvrykankatsushopcd1()); // 配達管轄ショップコード(得意先予約)
        dto.setDlvrykankatsukyotencd1(resv.getDlvrykankatsukyotencd1()); // 配達管轄拠点コード(得意先予約)
        dto.setDlvrycmpnycd1(resv.getDlvrycmpnycd1()); // 配達会社コード(得意先予約)
        dto.setDlvryjun1(resv.getDlvryjun1()); // 配達順(得意先予約)
        dto.setDlvryareacd1(resv.getDlvryareacd1()); // 配達エリアコード(得意先予約)
        dto.setDlvrykbn1(resv.getDlvrykbn1()); // 配達区分(得意先予約)
        dto.setDlvrytnshacd1(resv.getDlvrytnshacd1()); // 配達担当者コード(得意先予約)
        dto.setNohinsakijohon11(resv.getNohinsakijohon11()); // 納品先情報１(得意先予約)
        dto.setNohinsakijohon21(resv.getNohinsakijohon21()); // 納品先情報２(得意先予約)
        dto.setChizuno1(resv.getChizuno1()); // 地図番号(得意先予約)
        dto.setAcptjikanshiteijikoku1(
            DateTimeUtil.toNoColonDateHHMM(resv.getAcptjikanshiteijikokustr())); // 受注時間指定時刻(得意先予約)
        dto.setAcptjikanshiteijikokushry1(
            DateTimeUtil.toNoColonDateHHMM(resv.getAcptjikanshiteijikokushry1())); // 受注時間指定時刻終了(得意先予約)
        dto.setAcptjikanshiteikbn1(resv.getAcptjikanshiteikbn1()); // 受注時間指定区分(得意先予約)
        dto.setPrntmemon11(resv.getPrntmemon11()); // 印刷メモ１(得意先予約)
        dto.setPrntmemon21(resv.getPrntmemon21()); // 印刷メモ２(得意先予約)
        dto.setPrntmemon31(resv.getPrntmemon31()); // 印刷メモ３(得意先予約)

        // 請求先予約情報
        dto.setSeikyusakimsthnkflg(resv.getSeikyusakimsthnkflg()); // 請求先マスタ変更フラグ
        dto.setYoyakuinptdd2(resv.getYoyakuinptdd2()); // 予約登録日(請求先予約)
        dto.setSksmasutachgdd(DateTimeUtil.toNoSlashedDate(resv.getSkmhnkymd())); // 請求先マスタ変更日
        dto.setJigyoshocd2(resv.getJigyoshocd2()); // 事業所コード(請求先予約)
        dto.setSkysakinmn1(resv.getSkysakinmn11()); // 請求先名称１(請求先予約)
        dto.setSkysakinmn2(resv.getSkysakinmn2()); // 請求先名称２(請求先予約)
        dto.setSkysakikeishokbn(resv.getSkysakikeisho()); // 請求先敬称区分(請求先予約)
        dto.setSkysakinmkana(resv.getSkysakinmkana()); // 請求先名称カナ(請求先予約)
        dto.setSkysakinmryakusho(resv.getSkysakinmryakusho()); // 請求先名称略称(請求先予約)
        dto.setYubinno2(resv.getYubinno2()); // 郵便番号(請求先予約)
        dto.setAdrsn12(resv.getAdrsn12()); // 住所１(請求先予約)
        dto.setAdrsn22(resv.getAdrsn22()); // 住所２(請求先予約)
        dto.setAdrsn32(resv.getAdrsn32()); // 住所３(請求先予約)
        dto.setRenrakusakinokbnn12(resv.getRenrakusakinokbnn12()); // 連絡先番号区分１(請求先予約)
        dto.setRenrakusakinon12(resv.getRenrakusakinon12()); // 連絡先番号１(請求先予約)
        dto.setRenrakusakinokbnn22(resv.getRenrakusakinokbnn22()); // 連絡先番号区分２(請求先予約)
        dto.setRenrakusakinon22(resv.getRenrakusakinon22()); // 連絡先番号２(請求先予約)
        dto.setRenrakusakinokbnn32(resv.getRenrakusakinokbnn32()); // 連絡先番号区分３(請求先予約)
        dto.setRenrakusakinon32(resv.getRenrakusakinon32()); // 連絡先番号３(請求先予約)
        dto.setRenrakusakinokbnn42(resv.getRenrakusakinokbnn42()); // 連絡先番号区分４(請求先予約)
        dto.setRenrakusakinon42(resv.getRenrakusakinon42()); // 連絡先番号４(請求先予約)
        dto.setEigyotnshacd2(resv.getEigyotnshacd2()); // 営業担当者コード(請求先予約)
        dto.setKaishutnshacd(resv.getKaishutnshacd()); // 回収担当者コード

        // 共通情報
        dto.setKshnsystmymd(dbm.getUpdateDateForLock()); // 更新システム日付
        dto.setKshnsystmjikan(dbm.getUpdateTimeForLock()); // 更新システム時間
        dto.setLoginUserApkihoncd(dbm.getApkihoncd()); // ログインユーザーAP基本コード
        dto.setLoginUserShopcd(dbm.getShopcd()); // ログインユーザーショップコード
        dto.setLoginUserTnshacd(dbm.getTnshacd()); // ログインユーザー担当者コード
        dto.setSystemDate(dbm.getSystemDate()); // システム日(yyyymmdd)
        dto.setSystemTime(dbm.getSystemTime()); // システム時間(hhmmss)
        dto.setUnyoDate(dbm.getUnyoDate()); // 運用日付(yyyymmdd)
        dto.setIpAddress(dbm.getIpAddress()); // IPアドレス
        dto.setProgramId(dbm.getProgramId()); // プログラムID

        return dto;
    }

    /**
     * 事前チェック用DTOを生成する.
     * 
     * @param form B0501W02Form
     * @return B0501W02UpdateDto
     */
    private B0501W02UpdateDto createCheckDto(B0501W02Form form) {

        B0501W02BaseBean base = form.getBaseBean();

        B0501W02UpdateDto dto = new B0501W02UpdateDto();

        // ※ サービス側の事前チェックで使用する項目のみセットする

        // 得意先情報
        dto.setApkihoncd(base.getApkihoncd()); // ＡＰ基本コード
        dto.setShopcd(base.getShopcd()); // ショップコード
        dto.setTokuisakicd(base.getPtokuisakicd()); // 得意先コード
        dto.setTokuisakikbn(base.getTokuisakikbn()); // 得意先区分
        dto.setSkysakicd(base.getSkysakicd()); // 請求先コード
        dto.setOyatokuisakicd(base.getOyatokuisakicd()); // 親得意先コード
        dto.setTokuisakinmkanan1(base.getTokuisakinmkanan1()); // 得意先名称カナ１
        dto.setAdrsn1(base.getAdrsn1()); // 住所１
        dto.setRenrakusakinon1(base.getRenrakusakinon1()); // 連絡先番号１
        dto.setRenrakusakinon2(base.getRenrakusakinon2()); // 連絡先番号２
        dto.setRenrakusakinon3(base.getRenrakusakinon3()); // 連絡先番号３
        dto.setRenrakusakinon4(base.getRenrakusakinon4()); // 連絡先番号４
        dto.setOyakohaneikinoflg(base.getOyakohaneikinoflg()); // 親子反映機能
        dto.setYukokbn(base.getYukokbn()); // 有効区分

        return dto;
    }

    /**
     * 事前チェックを実行する
     * 
     * @param form B0501W02Form
     * @param unyodd 運用日付
     * @param printFlg true: 印刷押下時 / false: 更新押下時
     * @param messageList エラーメッセージ格納用リスト
     */
    private void checkParamPre(B0501W02Form form, String unyodd, boolean printFlg,
        List<String> messageList) {

        B0501W02BaseBean base = form.getBaseBean();
        B0501W02RentalBean rent = form.getRentalBean();

        // --------------------
        // 基本情報タブ
        // --------------------
        // 請求設定区分 = 2(自動生成)を選択した場合、確認メッセージ
        if (SKYSETTEIKBN_2.equals(base.getSkysetteikbn())) {
            BusinessExceptionFactory.setMessageList(messageList,
                printFlg ? MessageID.W275 : MessageID.W274);
        }

        // --------------------
        // サーバーレンタル料情報タブ
        // --------------------
        // 設定する＝1(チェックON)の場合
        if (!StringUtils.isEmpty(rent.getTeireisetteikbn())) {
            // 運用日付(yyyymm)＞画面.次回計上年月の場合、確認メッセージ
            if (!StringUtils.isEmpty(rent.getJikaikeijodydm())
                && rent.getJikaikeijodydm().matches(ValidationConstants.REGEXP_YYYY_MM_SLASH)
                && unyodd.substring(0, 6)
                    .compareTo(DateTimeUtil.toNoSlashedDateYYYYMM(rent.getJikaikeijodydm())) > 0) {
                BusinessExceptionFactory.setMessageList(messageList, MessageID.W201, "次回計上年月",
                    "運用日付より過去");
            }
        }
    }

    /**
     * 相関チェックを実行する
     * 
     * @param form
     * @param unyodd 運用日付
     */
    private void checkParam(B0501W02Form form, String unyodd) {

        B0501W02BaseBean base = form.getBaseBean();
        B0501W02DeliveryOrderBean deli = form.getDeliveryOrderBean();
        B0501W02SaleBean sale = form.getSaleBean();
        B0501W02AdditionalBean add = form.getAdditionalBean();
        B0501W02RentalBean rent = form.getRentalBean();
        B0501W02ReservationBean resv = form.getReservationBean();

        // --------------------
        // 基本情報タブ
        // --------------------
        // 事業所管理＝ 1（あり）の場合、事業所コードが選択されていない場合は、エラー
        if (CommonConstants.JIGYOSHOKANRI_1.equals(base.getJigyoshokanri())
            && StringUtils.isEmpty(base.getJigyoshocd())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.jigyoshocd", "事業所コード"), MessageID.W098, "事業所");
        }

        // 得意先区分=2(ショップ)、ショップマスタ.ショップ区分=1(ショップ)の場合、エラー
        if (CommonConstants.KBN_TOKUISAKI_SHOP.equals(base.getTokuisakikbn())
            && CommonConstants.SHOP_KBN_SHOP.equals(base.getShopkbn())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.tokuisakikbn", "得意先区分"), MessageID.W171, "得意先区分にショップ",
                "選択");
        }

        // 郵便番号が7桁でない場合、エラー
        if (base.getYubinno().length() != 7) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.yubinno", "郵便番号"), MessageID.W097, "郵便番号", "7");
        }

        // 連絡先番号１が「-」を除いて11桁を超過する場合、エラー
        if (base.getRenrakusakinon1().replaceAll("-", "").length() > 11) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.renrakusakinon1", "連絡先番号１"), MessageID.W113,
                "連絡先番号１は数値で最大１１桁");
        }

        // 連絡先番号２が「-」を除いて11桁を超過する場合、エラー
        if (!StringUtils.isEmpty(base.getRenrakusakinon2())
            && base.getRenrakusakinon2().replaceAll("-", "").length() > 11) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.renrakusakinon2", "連絡先番号２"), MessageID.W113,
                "連絡先番号２は数値で最大１１桁");
        }

        // 連絡先番号３が「-」を除いて11桁を超過する場合、エラー
        if (!StringUtils.isEmpty(base.getRenrakusakinon3())
            && base.getRenrakusakinon3().replaceAll("-", "").length() > 11) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.renrakusakinon3", "連絡先番号３"), MessageID.W113,
                "連絡先番号３は数値で最大１１桁");
        }

        // 連絡先番号４が「-」を除いて11桁を超過する場合、エラー
        if (!StringUtils.isEmpty(base.getRenrakusakinon4())
            && base.getRenrakusakinon4().replaceAll("-", "").length() > 11) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.renrakusakinon4", "連絡先番号４"), MessageID.W113,
                "連絡先番号４は数値で最大１１桁");
        }

        // プラン初回設定日が1900/01/01～9999/12/31でない場合、エラー
        checkDateRange(base.getRknplnsetteidd(), "baseBean.rknplnsetteidd", "プラン初回設定日");

        // プラン継続更新日が1900/01/01～9999/12/31でない場合、エラー
        checkDateRange(base.getRknplnkeizokukshndd(), "baseBean.rknplnkeizokukshndd", "プラン継続更新日");

        // プラン期限年月が1900/01～9999/12でない場合、エラー
        checkYmRange(base.getPlnkgndydm(), "baseBean.plnkgndydm", "プラン自動更新停止年月");

        // 誕生日１が1900/01/01～9999/12/31でない場合、エラー
        checkDateRange(base.getKodomotanjoddn1(), "baseBean.kodomotanjoddn1", "子供１の誕生日");

        // 誕生日２が1900/01/01～9999/12/31でない場合、エラー
        checkDateRange(base.getKodomotanjoddn2(), "baseBean.kodomotanjoddn2", "子供２の誕生日");

        // 誕生日３が1900/01/01～9999/12/31でない場合、エラー
        checkDateRange(base.getKodomotanjoddn3(), "baseBean.kodomotanjoddn3", "子供３の誕生日");

        // 申込受付日が1900/01/01～9999/12/31でない場合、エラー
        checkDateRange(base.getMoshikomircptdd(), "baseBean.moshikomircptdd", "申込受付日");

        // 入会日が1900/01/01～9999/12/31でない場合、エラー
        checkDateRange(base.getNyukaidd(), "baseBean.nyukaidd", "入会日");

        // 退会受付日が1900/01/01～9999/12/31でない場合、エラー
        checkDateRange(base.getTaikaidd(), "baseBean.taikaidd", "退会受付日");

        // 申込キャンセル日が1900/01/01～9999/12/31でない場合、エラー
        checkDateRange(base.getMoshikomicncldd(), "baseBean.moshikomicncldd", "申込キャンセル日");

        // 初回設置日が1900/01/01～9999/12/31でない場合、エラー
        checkDateRange(base.getShokaiwsvstupdd(), "baseBean.shokaiwsvstupdd", "初回設置日");

        // 引上日が1900/01/01～9999/12/31でない場合、エラー
        checkDateRange(base.getWsvhikiagedd(), "baseBean.wsvhikiagedd", "引上日");

        // 再入会＝チェックONかつ 反映元得意先コードが未入力の場合、エラー
        if (!StringUtils.isEmpty(base.getSainyukaifrg())
            && StringUtils.isEmpty(base.getHaneimototokuisakicd())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.haneimototokuisakicd", "反映元得意先コード"), MessageID.W113,
                "反映元得意先コード");
        }

        // 請求設定区分＝１(コード指定)を選択し、請求先コードが未入力の場合、エラー
        if (SKYSETTEIKBN_1.equals(base.getSkysetteikbn())
            && StringUtils.isEmpty(base.getSkysakicd())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.skysakicd", "請求先コード"), MessageID.W113, "請求先コード");
        }

        // 請求設定区分＝0(指定なし)を選択した場合
        if (SKYSETTEIKBN_0.equals(base.getSkysetteikbn())) {
            // はじめてｾｯﾄ=1(チェックON)の場合、エラー
            if (!StringUtils.isEmpty(base.getStartup())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.startup", "はじめてｾｯﾄ"), MessageID.W134, "はじめてｾｯﾄ",
                    "請求先コード指定なし");
            }

            // 入会日が入力されている場合、エラー
            if (!StringUtils.isEmpty(base.getNyukaidd())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.nyukaidd", "入会日"), MessageID.W106, "請求先区分指定なしで入会日");
            }

            // 請求先コードが入力されている場合、エラー
            if (!StringUtils.isEmpty(base.getSkysakicd())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.skysakicd", "請求先コード"), MessageID.W106,
                    "請求先区分指定なしで請求先コード");
            }
        }

        // 請求設定区分=2(自動生成)を選択し、請求先コードが入力されている場合、エラー
        if (SKYSETTEIKBN_2.equals(base.getSkysetteikbn())
            && !StringUtils.isEmpty(base.getSkysakicd())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.skysakicd", "請求先コード"), MessageID.W106, "請求先区分自動生成で請求先コード");
        }

        // 請求先コード(初期表示の値)を変更し、請求先予約登録日が存在する場合、エラー
        if (!StringUtils.isEmpty(base.getSkysakicdbefore())
            && !base.getSkysakicdbefore().equals(base.getSkysakicd())
            && !StringUtils.isEmpty(resv.getYoyakuinptdd2())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.skysakicd", "請求先コード"), MessageID.W171,
                "請求先変更予約がある場合、請求先コード", "変更");
        }

        // 得意先区分＝2(ショップ)以外 かつ（得意先区分＝1(顧客)以外かつ顧客区分＝2(ショップ顧客)以外)の時、
        // 得意先ショップコードが入力されている場合、エラー
        if (!CommonConstants.KBN_TOKUISAKI_SHOP.equals(base.getTokuisakikbn())
            && !(CommonConstants.KBN_TOKUISAKI_KOKUAKU.equals(base.getTokuisakikbn())
                && CommonConstants.JUTAKUDLVRYCSTKBN_2.equals(base.getJutakudlvrycstkbn()))
            && !StringUtils.isEmpty(base.getTokuisakishopcd())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.tokuisakishopcd", "得意先ショップコード"), MessageID.W124,
                "得意先ショップコード");
        }

        // 得意先区分＝2(ショップ)または、(得意先区分＝1(顧客)かつ顧客区分＝2(ショップ顧客))の場合、
        if (CommonConstants.KBN_TOKUISAKI_SHOP.equals(base.getTokuisakikbn())
            || (CommonConstants.KBN_TOKUISAKI_KOKUAKU.equals(base.getTokuisakikbn())
                && CommonConstants.JUTAKUDLVRYCSTKBN_2.equals(base.getJutakudlvrycstkbn()))) {
            // 得意先ショップコードが未入力の場合、エラー
            if (StringUtils.isEmpty(base.getTokuisakishopcd())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.tokuisakishopcd", "得意先ショップコード"), MessageID.W113,
                    "得意先ショップコード");
            }

            // サブレンタル倉庫コードが未入力の場合、エラー
            if (StringUtils.isEmpty(base.getSaburtlsokocd())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.saburtlsokocd", "サブレンタル倉庫コード"), MessageID.W113,
                    "サブレンタル倉庫コード");
            }
        }

        // 委託相手得意先コードが入力され、委託相手AP基本コード、または委託相手ショップコードのどれか一つが未入力の場合、エラー
        if (!StringUtils.isEmpty(base.getItakuaitetokuisakicd())
            && (StringUtils.isEmpty(base.getItakuaiteapkihoncd())
                || StringUtils.isEmpty(base.getItakuaiteshopcd()))) {
            List<FormField> fields = new ArrayList<>();
            addField(fields, "baseBean.itakuaiteapkihoncd", "委託相手AP基本コード");
            addField(fields, "baseBean.itakuaiteshopcd", "委託相手ショップコード");
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W113,
                "委託相手ＡＰ基本コード、委託相手ショップコード");
        }

        // 委託相手AP基本コード、または委託相手ショップコードのどれか一つが入力され、委託相手得意先コードが未入力の場合、エラー
        if (StringUtils.isEmpty(base.getItakuaitetokuisakicd())
            && (!StringUtils.isEmpty(base.getItakuaiteapkihoncd())
                || !StringUtils.isEmpty(base.getItakuaiteshopcd()))) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.itakuaitetokuisakicd", "委託相手得意先コード"), MessageID.W113,
                "委託相手得意先コード");
        }

        // 委託相手得意先コードが入力され、
        // 顧客区分＝3(NA顧客),4(委託顧客),5(NA受託),6(配達受託)以外の場合、エラー
        if (!StringUtils.isEmpty(base.getItakuaitetokuisakicd())
            && !CommonConstants.JUTAKUDLVRYCSTKBN_3.equals(base.getJutakudlvrycstkbn())
            && !CommonConstants.JUTAKUDLVRYCSTKBN_4.equals(base.getJutakudlvrycstkbn())
            && !CommonConstants.JUTAKUDLVRYCSTKBN_5.equals(base.getJutakudlvrycstkbn())
            && !CommonConstants.JUTAKUDLVRYCSTKBN_6.equals(base.getJutakudlvrycstkbn())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.jutakudlvrycstkbn", "顧客区分"), MessageID.W149, "顧客区分",
                "委託相手コードの対象");
        }

        // 会員コード区分に "1"（コード指定）を選択し、会員コードが未入力の場合、エラー
        if (KAIINCDKBN_1.equals(base.getKaiincdkbn()) && StringUtils.isEmpty(base.getKaiincd())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.kaiincd", "会員コード"), MessageID.W113, "会員コード");
        }

        // 得意先区分が "1"（顧客）以外 で、会員コード区分が "0"（指定なし）ではない場合、エラー
        if (!CommonConstants.KBN_TOKUISAKI_KOKUAKU.equals(base.getTokuisakikbn())
            && !KAIINCDKBN_0.equals(base.getKaiincdkbn())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.kaiincdkbn", "会員コード区分"), MessageID.W117, "得意先区分が顧客以外",
                "会員コード区分は「指定なし」");
        }

        // 親子反映機能=チェックONで請求先コードが未入力の場合、エラー
        if (!StringUtils.isEmpty(base.getOyakohaneikinoflg())
            && StringUtils.isEmpty(base.getSkysakicd())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.skysakicd", "請求先コード"), MessageID.W113,
                "親得意先の商品単価をコピーする場合、請求先コード");
        }

        // 連絡先番号１が入力され、連絡先番号区分１が未選択の場合はエラー
        if (!StringUtils.isEmpty(base.getRenrakusakinon1())
            && StringUtils.isEmpty(base.getRenrakusakinokbnn1())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.renrakusakinokbnn1", "連絡先番号区分１"), MessageID.W011,
                "連絡先番号区分１");
        }

        // 連絡先番号区分１が選択され、連絡先番号１が未入力の場合はエラー
        if (StringUtils.isEmpty(base.getRenrakusakinon1())
            && !StringUtils.isEmpty(base.getRenrakusakinokbnn1())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.renrakusakinon1", "連絡先番号１"), MessageID.W011, "連絡先番号１");
        }

        // 連絡先番号２が入力され、連絡先番号区分２が未選択の場合はエラー
        if (!StringUtils.isEmpty(base.getRenrakusakinon2())
            && StringUtils.isEmpty(base.getRenrakusakinokbnn2())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.renrakusakinokbnn2", "連絡先番号区分２"), MessageID.W011,
                "連絡先番号区分２");
        }

        // 連絡先番号区分２が選択され、連絡先番号２が未入力の場合はエラー
        if (StringUtils.isEmpty(base.getRenrakusakinon2())
            && !StringUtils.isEmpty(base.getRenrakusakinokbnn2())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.renrakusakinon2", "連絡先番号２"), MessageID.W011, "連絡先番号２");
        }

        // 連絡先番号３が入力され、連絡先番号区分３が未選択の場合はエラー
        if (!StringUtils.isEmpty(base.getRenrakusakinon3())
            && StringUtils.isEmpty(base.getRenrakusakinokbnn3())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.renrakusakinokbnn3", "連絡先番号区分３"), MessageID.W011,
                "連絡先番号区分３");
        }

        // 連絡先番号区分３が選択され、連絡先番号３が未入力の場合はエラー
        if (StringUtils.isEmpty(base.getRenrakusakinon3())
            && !StringUtils.isEmpty(base.getRenrakusakinokbnn3())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.renrakusakinon3", "連絡先番号３"), MessageID.W011, "連絡先番号３");
        }

        // 連絡先番号４が入力され、連絡先番号区分４が未選択の場合はエラー
        if (!StringUtils.isEmpty(base.getRenrakusakinon4())
            && StringUtils.isEmpty(base.getRenrakusakinokbnn4())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.renrakusakinokbnn4", "連絡先番号区分４"), MessageID.W011,
                "連絡先番号区分４");
        }

        // 連絡先番号区分４が選択され、連絡先番号４が未入力の場合はエラー
        if (StringUtils.isEmpty(base.getRenrakusakinon4())
            && !StringUtils.isEmpty(base.getRenrakusakinokbnn4())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.renrakusakinon4", "連絡先番号４"), MessageID.W011, "連絡先番号４");
        }

        // プラン＝000(基本プラン)以外かつプラン初回設定日が未入力の場合、エラー
        if (!CommonConstants.PLNCD_000.equals(base.getPln())
            && StringUtils.isEmpty(base.getRknplnsetteidd())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.rknplnsetteidd", "プラン初回設定日"), MessageID.W011, "プラン初回設定日");
        }

        // 違約金(プラン継続更新日)＝チェックONの時、プラン継続更新日が未入力の場合、エラー
        if (!StringUtils.isEmpty(base.getPlnkshniyakukinflg())
            && StringUtils.isEmpty(base.getRknplnkeizokukshndd())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.rknplnkeizokukshndd", "プラン継続更新日"), MessageID.W171,
                "プラン継続の違約金生成を選択した場合", "プラン継続更新日は省略");
        }

        // プラン＝001(子育てアクアプラン)が選択された場合
        if (CommonConstants.PLNCD_001.equals(base.getPln())) {
            // 子供1が未入力の場合、エラー
            if (StringUtils.isEmpty(base.getKodomomein1())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.kodomomein1", "子供１"), MessageID.W011, "子供１");
            }

            // 子供1の誕生日が未入力の場合、エラー
            if (StringUtils.isEmpty(base.getKodomotanjoddn1())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.kodomotanjoddn1", "誕生日１"), MessageID.W011, "子供１の誕生日");
            }
        }

        // 申込受付日＞運用日付の場合、エラー
        if (DateTimeUtil.toNoSlashedDate(base.getMoshikomircptdd()).compareTo(unyodd) > 0) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.moshikomircptdd", "申込受付日"), MessageID.W159, "申込受付日",
                "未来日付");
        }

        // 申込キャンセル日が入力された場合
        if (!StringUtils.isEmpty(base.getMoshikomicncldd())) {
            // 申込キャンセル日＞運用日付の場合、エラー
            if (DateTimeUtil.toNoSlashedDate(base.getMoshikomicncldd()).compareTo(unyodd) > 0) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.moshikomicncldd", "申込キャンセル日"), MessageID.W159,
                    "申込キャンセル日", "未来日付");
            }

            // (得意先状態＝1(申込受付)・2(設置前キャンセル)以外)の場合、または現在残高＝0以外の場合、エラー
            if ((!CommonConstants.KBN_TOKUISAKIJOTAI_MOSHIKOMI.equals(base.getTokuisakijotai())
                && !CommonConstants.KBN_TOKUISAKIJOTAI_SETCANCEL.equals(base.getTokuisakijotai()))
                || !"0".equals(
                    StringUtils.isEmpty(sale.getGenzaizdhid()) ? "0" : sale.getGenzaizdhid())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.moshikomicncldd", "申込キャンセル日"), MessageID.W153,
                    "申込キャンセル日", "「得意先状態」が申込受付または設置前キャンセルで、「残高」が0でなくてはいけません。");
            }

            // キャンセル/退会理由区分が未選択の場合、エラー
            if (StringUtils.isEmpty(base.getTaikairiyukbn())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.taikairiyukbn", "キャンセル／退会理由区分"), MessageID.W153,
                    "申込キャンセル日", "キャンセル理由区分は省略できません。");
            }
        }

        // 退会受付日が入力され、キャンセル/退会理由区分が未選択の場合、エラー
        if (!StringUtils.isEmpty(base.getTaikaidd())
            && StringUtils.isEmpty(base.getTaikairiyukbn())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.taikairiyukbn", "キャンセル／退会理由区分"), MessageID.W153, "退会受付日",
                "退会理由区分は省略できません。");
        }

        // 申込キャンセル日が未入力 かつ 退会受付日が未入力の場合、
        if (StringUtils.isEmpty(base.getMoshikomicncldd())
            && StringUtils.isEmpty(base.getTaikaidd())) {
            // キャンセル/退会理由区分が選択されている場合、エラー
            if (!StringUtils.isEmpty(base.getTaikairiyukbn())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.taikairiyukbn", "キャンセル／退会理由区分"), MessageID.W171,
                    "キャンセル日および退会受付日が未入力の時", "キャンセル/退会理由区分は指定");
            }

            // キャンセル/退会理由詳細が入力されている場合、エラー
            if (!StringUtils.isEmpty(base.getTaikairiyushs())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.taikairiyushs", "キャンセル／退会理由詳細"), MessageID.W171,
                    "キャンセル日および退会受付日が未入力の時", "キャンセル/退会理由詳細は入力");
            }
        }

        // 初回設置日が入力され、設置日登録者コードが未入力の場合、エラー
        if (!StringUtils.isEmpty(base.getShokaiwsvstupdd())
            && StringUtils.isEmpty(base.getStupddtoshacd())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.stupddtoshacd", "設置日登録者コード"), MessageID.W113, "設置担当者");
        }

        // 得意先区分=1(顧客)または2(ショップ)または5(NA受託)または6(配達受託)を選択し、
        // 初回設置日が未入力の時、入会日・退会受付日・引上げ日のいずれかが入力ありの場合、エラー
        if ((CommonConstants.KBN_TOKUISAKI_KOKUAKU.equals(base.getTokuisakikbn())
            || !CommonConstants.KBN_TOKUISAKI_SHOP.equals(base.getTokuisakikbn())
            || !CommonConstants.KBN_TOKUISAKI_NAJUTAKU.equals(base.getTokuisakikbn())
            || !CommonConstants.KBN_TOKUISAKI_DLVRYJUTAKU.equals(base.getTokuisakikbn()))
            && StringUtils.isEmpty(base.getShokaiwsvstupdd())) {
            String target = "";
            if (!StringUtils.isEmpty(base.getNyukaidd())) {
                target += "入会日・";
            }
            if (!StringUtils.isEmpty(base.getTaikaidd())) {
                target += "退会受付日・";
            }
            if (!StringUtils.isEmpty(base.getWsvhikiagedd())) {
                target += "引上日・";
            }
            if (!StringUtils.isEmpty(target)) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.shokaiwsvstupdd", "初回設置日"), MessageID.W164,
                    target.substring(0, target.length() - 1), "初回設置日");
            }
        }

        // 引上日が入力され、引上登録者コードが未入力の場合、エラー
        if (!StringUtils.isEmpty(base.getWsvhikiagedd())
            && StringUtils.isEmpty(base.getHikiagetoshacd())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.hikiagetoshacd", "引上登録者コード"), MessageID.W113, "引上担当者");
        }

        // 得意先区分＝2(ショップ)以外 かつ(得意先区分＝ 1(顧客)かつ顧客区分＝2(ショップ顧客))以外を選択し、
        // サブレンタル倉庫コードが入力されている場合、エラー
        if (!CommonConstants.KBN_TOKUISAKI_SHOP.equals(base.getTokuisakikbn())
            && !(CommonConstants.KBN_TOKUISAKI_KOKUAKU.equals(base.getTokuisakikbn())
                && CommonConstants.JUTAKUDLVRYCSTKBN_2.equals(base.getJutakudlvrycstkbn()))
            && !StringUtils.isEmpty(base.getSaburtlsokocd())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.saburtlsokocd", "ブレンタル倉庫コード"), MessageID.W124,
                "ブレンタル倉庫コード");
        }

        // 初回設置日の入力がある場合
        if (!StringUtils.isEmpty(base.getShokaiwsvstupdd())) {
            // 初回設置日＞運用日付の場合、エラー
            if (DateTimeUtil.toNoSlashedDate(base.getShokaiwsvstupdd()).compareTo(unyodd) > 0) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.shokaiwsvstupdd", "初回設置日"), MessageID.W159, "初回設置日",
                    "未来日付");
            }

            // 申込受付日＞初回設置日の場合、エラー
            if (base.getMoshikomircptdd().compareTo(base.getShokaiwsvstupdd()) > 0) {
                List<FormField> fields = new ArrayList<>();
                addField(fields, "baseBean.shokaiwsvstupdd", "初回設置日");
                addField(fields, "baseBean.moshikomircptdd", "申込受付日");
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W155, "初回設置日",
                    "初回設置日", "申込受付日");
            }

            // 初回設置日＞入会日の場合、エラー
            if (!StringUtils.isEmpty(base.getNyukaidd())
                && base.getShokaiwsvstupdd().compareTo(base.getNyukaidd()) > 0) {
                List<FormField> fields = new ArrayList<>();
                addField(fields, "baseBean.shokaiwsvstupdd", "初回設置日");
                addField(fields, "baseBean.nyukaidd", "入会日");
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W156, "初回設置日・入会日",
                    "初回設置日", "入会日");
            }

            // 初回設置日＞退会受付日の場合、エラー
            if (!StringUtils.isEmpty(base.getTaikaidd())
                && base.getShokaiwsvstupdd().compareTo(base.getTaikaidd()) > 0) {
                List<FormField> fields = new ArrayList<>();
                addField(fields, "baseBean.shokaiwsvstupdd", "初回設置日");
                addField(fields, "baseBean.taikaidd", "退会受付日");
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W156,
                    "初回設置日・退会受付日", "初回設置日", "退会受付日");
            }

            // 初回設置日＞引上日の場合、エラー
            if (!StringUtils.isEmpty(base.getWsvhikiagedd())
                && base.getShokaiwsvstupdd().compareTo(base.getWsvhikiagedd()) > 0) {
                List<FormField> fields = new ArrayList<>();
                addField(fields, "baseBean.shokaiwsvstupdd", "初回設置日");
                addField(fields, "baseBean.wsvhikiagedd", "引上日");
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W156, "初回設置日・引上日",
                    "初回設置日", "引上日");
            }
        }

        // 入会日が入力ありの場合
        if (!StringUtils.isEmpty(base.getNyukaidd())) {
            // 得意先状態＝2(設置前キャンセル)の場合、エラー
            if (CommonConstants.KBN_TOKUISAKIJOTAI_SETCANCEL.equals(base.getTokuisakijotai())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.nyukaidd", "入会日"), MessageID.W124,
                    "得意先状態が設置前キャンセルの場合、入会日");
            }

            // 得意先状態＝4(仮会員退会)の場合、エラー
            if (CommonConstants.KBN_TOKUISAKIJOTAI_KARIKAIYAKU.equals(base.getTokuisakijotai())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.nyukaidd", "入会日"), MessageID.W124,
                    "得意先状態が仮会員退会の場合、入会日");
            }

            // （得意先状態＝2(設置前キャンセル)・4(仮会員退会)）以外の時、申込キャンセル日入力ありの場合、エラー
            if (!CommonConstants.KBN_TOKUISAKIJOTAI_SETCANCEL.equals(base.getTokuisakijotai())
                && !CommonConstants.KBN_TOKUISAKIJOTAI_KARIKAIYAKU.equals(base.getTokuisakijotai())
                && !StringUtils.isEmpty(base.getMoshikomicncldd())) {
                List<FormField> fields = new ArrayList<>();
                addField(fields, "baseBean.moshikomicncldd", "申込キャンセル日");
                addField(fields, "baseBean.nyukaidd", "入会日");
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W127, "申込キャンセル日",
                    "入会日");
            }

            // 入会日＞運用日付の場合、エラー
            if (DateTimeUtil.toNoSlashedDate(base.getNyukaidd()).compareTo(unyodd) > 0) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.nyukaidd", "入会日"), MessageID.W159, "入会日", "未来日付");
            }

            // 申込受付日＞入会日の場合、エラー
            if (base.getMoshikomircptdd().compareTo(base.getNyukaidd()) > 0) {
                List<FormField> fields = new ArrayList<>();
                addField(fields, "baseBean.moshikomircptdd", "申込受付日");
                addField(fields, "baseBean.nyukaidd", "入会日");
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W155, "入会日",
                    "入会日", "申込受付日");
            }

            // 入会日＞退会受付日の場合、エラー
            if (!StringUtils.isEmpty(base.getTaikaidd())
                && base.getNyukaidd().compareTo(base.getTaikaidd()) > 0) {
                List<FormField> fields = new ArrayList<>();
                addField(fields, "baseBean.nyukaidd", "入会日");
                addField(fields, "baseBean.taikaidd", "退会受付日");
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W156, "入会日・退会受付日",
                    "入会日", "退会受付日");
            }

            // 入会日＞引上日の場合、エラー
            if (!StringUtils.isEmpty(base.getWsvhikiagedd())
                && base.getNyukaidd().compareTo(base.getWsvhikiagedd()) > 0) {
                List<FormField> fields = new ArrayList<>();
                addField(fields, "baseBean.nyukaidd", "入会日");
                addField(fields, "baseBean.wsvhikiagedd", "引上日");
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W156, "入会日・引上日",
                    "入会日", "引上日");
            }
        }

        // 退会受付日が未入力の場合
        if (StringUtils.isEmpty(base.getTaikaidd())) {
            // 引上日が入力ありの場合、エラー
            if (!StringUtils.isEmpty(base.getWsvhikiagedd())) {
                List<FormField> fields = new ArrayList<>();
                addField(fields, "baseBean.taikaidd", "退会受付日");
                addField(fields, "baseBean.wsvhikiagedd", "引上日");
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W154, "退会受付日",
                    "引上日");
            }

            // 退会受付日が入力ありの場合
        } else {
            // 退会受付日＞運用日付の場合、エラー
            if (DateTimeUtil.toNoSlashedDate(base.getTaikaidd()).compareTo(unyodd) > 0) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.taikaidd", "退会受付日"), MessageID.W159, "退会受付日", "未来日付");
            }

            // 申込受付日＞退会受付日の場合、エラー
            if (base.getMoshikomircptdd().compareTo(base.getTaikaidd()) > 0) {
                List<FormField> fields = new ArrayList<>();
                addField(fields, "baseBean.moshikomircptdd", "申込受付日");
                addField(fields, "baseBean.taikaidd", "退会受付日");
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W155, "退会受付日",
                    "退会受付日", "申込受付日");
            }

            // 退会受付日＞引上日の場合、エラー
            if (!StringUtils.isEmpty(base.getWsvhikiagedd())
                && base.getTaikaidd().compareTo(base.getWsvhikiagedd()) > 0) {
                List<FormField> fields = new ArrayList<>();
                addField(fields, "baseBean.taikaidd", "退会受付日");
                addField(fields, "baseBean.wsvhikiagedd", "引上日");
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W156, "退会受付日・引上日",
                    "退会受付日", "引上日");
            }
        }

        // 引上日が入力ありの場合
        if (!StringUtils.isEmpty(base.getWsvhikiagedd())) {
            // 引上日＞運用日付の場合、エラー
            if (DateTimeUtil.toNoSlashedDate(base.getWsvhikiagedd()).compareTo(unyodd) > 0) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.wsvhikiagedd", "引上日"), MessageID.W159, "引上日", "未来日付");
            }

            // 申込受付日＞引上日の場合、エラー
            if (base.getMoshikomircptdd().compareTo(base.getWsvhikiagedd()) > 0) {
                List<FormField> fields = new ArrayList<>();
                addField(fields, "baseBean.moshikomircptdd", "申込受付日");
                addField(fields, "baseBean.wsvhikiagedd", "引上日");
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W155, "引上日",
                    "引上日", "申込受付日");
            }
        }

        // プラン継続更新日が入力ありの場合
        if (!StringUtils.isEmpty(base.getRknplnkeizokukshndd())
            && !StringUtils.isEmpty(base.getRknplnsetteidd())) {
            // プラン初回設定日＞プラン継続更新日の場合、エラー
            if (base.getRknplnsetteidd().compareTo(base.getRknplnkeizokukshndd()) > 0) {
                List<FormField> fields = new ArrayList<>();
                addField(fields, "baseBean.rknplnsetteidd", "プラン初回設定日");
                addField(fields, "baseBean.rknplnkeizokukshndd", "プラン継続更新日");
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W093, "プラン初回設定日",
                    "プラン継続更新日");
            }

            // プラン初回設定日(yyyymm)=プラン継続更新日(yyyymm)の場合、エラー)
            if (DateTimeUtil.toNoSlashedDate(base.getRknplnsetteidd()).substring(0, 6).equals(
                DateTimeUtil.toNoSlashedDate(base.getRknplnkeizokukshndd()).substring(0, 6))) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.rknplnkeizokukshndd", "プラン継続更新日"), MessageID.W113,
                    "プラン継続更新日はプラン初回設定日の翌月以降");
            }
        }

        // ACCIS2023-7169 本部紹介月次締での請求漏れ回避のため、申込キャンセル日や退会日のクリア有無チェックを追加
        if (!StringUtils.isEmpty(deli.getHonshokaino())) {
            // 本部紹介番号が設定されている得意先のみチェックの対象とする
            // 申込キャンセル日のクリアチェック
            validateClearedField(deli.getHonshokaino(), base.getMoshikomicncldd(),
                base.getMoshikomicnclddbefore(),
                new FormField("baseBean.moshikomicncldd", "申込キャンセル日"));
            // ACCIS2023-7185 退会受付日のクリアチェック条件を緩和（下記の条件を満たした場合のみチェックを実施）
            // ・入会日が運用日付の1年以内
            // ・クリア前の退会受付日の年月が運用日付の年月と異なっている
            if (shouldCheckResignationDateClearance(
                DateTimeUtil.toNoSlashedDate(base.getNyukaidd()),
                DateTimeUtil.toNoSlashedDate(base.getTaikaiddbefore()), unyodd)) {
                // 退会受付日のクリアチェック
                validateClearedField(deli.getHonshokaino(), base.getTaikaidd(),
                    base.getTaikaiddbefore(), new FormField("baseBean.taikaidd", "退会受付日"));
            }
        }

        // --------------------
        // 営業・配達・受注情報
        // --------------------
        // １２Ｌ預かりボトル本数が正数または0以外の場合エラー
        checkPositiveOrZero(deli.getAzukaribtlhnsn1(), "deliveryOrderBean.azukaribtlhnsn1",
            "１２L預かりボトル本数");

        // ７Ｌ預かりボトル本数が正数または0以外の場合エラー
        checkPositiveOrZero(deli.getAzukaribtlhnsn2(), "deliveryOrderBean.azukaribtlhnsn2",
            "７Ｌ預かりボトル本数");

        // （得意先区分＝1(顧客)かつ 顧客区分＝2(ショップ顧客)）を選択
        // または得意先区分＝ 2(ショップ)を選択した場合、エラー
        if ((CommonConstants.KBN_TOKUISAKI_KOKUAKU.equals(base.getTokuisakikbn())
            && CommonConstants.JUTAKUDLVRYCSTKBN_2.equals(base.getJutakudlvrycstkbn()))
            || CommonConstants.KBN_TOKUISAKI_SHOP.equals(base.getTokuisakikbn())) {
            // 配達拠点に自AP－SHOP以外を選択した場合
            if (!(base.getApkihoncd().equals(deli.getDlvrykankatsuapkihoncd())
                && base.getShopcd().equals(deli.getDlvrykankatsushopcd()))) {
                List<FormField> fields = new ArrayList<>();
                addField(fields, "deliveryOrderBean.dlvrykankatsukyotencd", "配達管轄拠点コード");
                addField(fields, "deliveryOrderBean.dlvrykankatsuapkihoncd", "配達管轄AP基本コード");
                addField(fields, "deliveryOrderBean.dlvrykankatsushopcd", "配達管轄ショップコード");
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W124, "委託配達先");
            }
        }

        // 顧客区分が "3"（ＮＡ顧客）または"4"（委託顧客）の場合
        if ((CommonConstants.JUTAKUDLVRYCSTKBN_3.equals(base.getJutakudlvrycstkbn())
            || CommonConstants.JUTAKUDLVRYCSTKBN_4.equals(base.getJutakudlvrycstkbn()))) {
            // 配達会社コードが未入力の場合、エラー
            if (StringUtils.isEmpty(deli.getDlvrycmpnycd())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("deliveryOrderBean.dlvrycmpnycd", "配達会社コード"), MessageID.W113,
                    "得意先が委託元の場合、配達会社コード");
            }

            // 配達担当者コードが未入力の場合、エラー
            if (StringUtils.isEmpty(deli.getDlvrytnshacd())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("deliveryOrderBean.dlvrytnshacd", "配達担当者コード"), MessageID.W113,
                    "得意先が委託元の場合、配達担当者コード");
            }

            // 委託相手AP基本コード ≠ 配達拠点AP基本コード もしくは 委託相手ショップコード ≠ 配達拠点ショップコード の場合は、エラー
            if ((!StringUtils.isEmpty(base.getItakuaiteapkihoncd())
                && !base.getItakuaiteapkihoncd().equals(deli.getDlvrykankatsuapkihoncd()))
                || (!StringUtils.isEmpty(base.getItakuaiteshopcd())
                    && !base.getItakuaiteshopcd().equals(deli.getDlvrykankatsushopcd()))) {
                List<FormField> fields = new ArrayList<>();
                addField(fields, "deliveryOrderBean.dlvrykankatsuapkihoncd", "配達拠点AP基本コード");
                addField(fields, "deliveryOrderBean.dlvrykankatsushopcd", "配達拠点ショップコード");
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W171,
                    "配達拠点AP基本コード、配達拠点ショップコード", "委託相手AP基本コード、委託相手ショップコードと異なる内容は登録");
            }

            // 顧客区分 が "3"（ＮＡ顧客）か"4"（委託顧客） 以外の場合
        } else {
            // AP基本コード≠ 配達拠点AP基本コード もしくは ショップコード ≠ 配達拠点ショップコードの場合は、エラー
            if (!base.getApkihoncd().equals(deli.getDlvrykankatsuapkihoncd())
                || !base.getShopcd().equals(deli.getDlvrykankatsushopcd())) {
                List<FormField> fields = new ArrayList<>();
                addField(fields, "deliveryOrderBean.dlvrykankatsuapkihoncd", "配達拠点AP基本コード");
                addField(fields, "deliveryOrderBean.dlvrykankatsushopcd", "配達拠点ショップコード");
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W171,
                    "ＮＡ顧客、委託顧客以外", "他拠点を選択");
            }
        }

        // 画面.配達区分＝2(他社便)を選択し、画面.配達会社コードが未入力の場合、エラー
        if (CommonConstants.KBN_DLVRY_TASHA.equals(deli.getDlvrykbn())
            && StringUtils.isEmpty(deli.getDlvrycmpnycd())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("deliveryOrderBean.dlvrycmpnycd", "配達会社コード"), MessageID.W113,
                "配達会社コード");
        }

        // 時間指定区分＝５(指定-頃)または６(指定-以降)を選択し、時間指定時刻開始が未入力の場合、エラー
        if ((CommonConstants.KBN_DLVRYKIBO_KORO.equals(deli.getAcptjikanshiteikbn())
            || CommonConstants.KBN_DLVRYKIBO_IKOU.equals(deli.getAcptjikanshiteikbn()))
            && StringUtils.isEmpty(deli.getAcptjikanshiteijikoku())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("deliveryOrderBean.acptjikanshiteijikoku", "時間指定時刻開始"),
                MessageID.W011, "時間指定時刻");
        }

        // 時間指定区分＝８(指定)を選択した場合
        if (CommonConstants.KBN_DLVRYKIBO_JIKANHANI.equals(deli.getAcptjikanshiteikbn())) {
            // 時間指定時刻開始が未入力の場合、エラー
            if (StringUtils.isEmpty(deli.getAcptjikanshiteijikoku())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("deliveryOrderBean.acptjikanshiteijikoku", "時間指定時刻開始"),
                    MessageID.W011, "時間指定時刻開始");
            }

            // 時間指定時刻終了が未入力の場合、エラー
            if (StringUtils.isEmpty(deli.getAcptjikanshiteijikokushry())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("deliveryOrderBean.acptjikanshiteijikokushry", "時間指定時刻終了"),
                    MessageID.W011, "時間指定時刻終了");
            }

            // 時間指定時刻開始 ＞ 時間指定時刻終了 の場合は、エラー
            if (deli.getAcptjikanshiteijikoku()
                .compareTo(deli.getAcptjikanshiteijikokushry()) > 0) {
                List<FormField> fields = new ArrayList<>();
                addField(fields, "deliveryOrderBean.acptjikanshiteijikoku", "時間指定時刻開始");
                addField(fields, "deliveryOrderBean.acptjikanshiteijikokushry", "時間指定時刻終了");
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W093, "時間指定時刻開始",
                    "時間指定時刻終了");
            }
        }

        // --------------------
        // 営業情報②
        // --------------------
        // 与信限度金額が正数または0以外の場合エラー
        checkPositiveOrZero(sale.getYoshingendokin(), "saleBean.yoshingendokin", "与信限度金額");

        // 保証金預かり.日付が1900/01/01～9999/12/31でない場合、エラー
        checkDateRange(sale.getHoshokinazukaridd(), "saleBean.hoshokinazukaridd", "保証金預かり日付");

        // 保証金預かり額が正数または0以外の場合エラー
        checkPositiveOrZero(sale.getHoshokinazukarikin(), "saleBean.hoshokinazukarikin", "保証金預かり額");

        // 保証金返却.日付が1900/01/01～9999/12/31でない場合、エラー
        checkDateRange(sale.getHoshokinhenkyakudd(), "saleBean.hoshokinhenkyakudd", "保証金返却日付");

        // 保証金返却金額が正数または0以外の場合エラー
        checkPositiveOrZero(sale.getHoshokinhenkyakukin(), "saleBean.hoshokinhenkyakukin",
            "保証金返却金額");

        // まとめ買いサービス区分=1(あり)を選択した場合
        if (CommonConstants.MTMKISVKBN_1.equals(sale.getMtmkisvkbn())) {
            // まとめ買い有料本数が未入力の場合、エラー
            if (StringUtils.isEmpty(sale.getMtmkiyuryohns())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("saleBean.mtmkiyuryohns", "まとめ買い有料本数"), MessageID.W113,
                    "まとめ買い有料本数");
            }

            // まとめ買い無料本数が未入力の場合、エラー
            checkRequired(sale.getMtmkimuryohns(), "saleBean.mtmkimuryohns", "まとめ買い無料本数");
        }

        // 保証金区分＝1(あり)を選択した場合
        if (CommonConstants.HOSHOKINKBN_1.equals(sale.getHoshokinkbn())) {
            // 保証金預かり.日付が未入力の場合、エラー
            if (StringUtils.isEmpty(sale.getHoshokinazukaridd())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("saleBean.hoshokinazukaridd", "保証金預かり日付"), MessageID.W011,
                    "保証金預かり日付");
            }

            // 保証金預かり.金額が未入力の場合、エラー
            if (StringUtils.isEmpty(sale.getHoshokinazukarikin())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("saleBean.hoshokinazukarikin", "保証金預かり金額"), MessageID.W011,
                    "保証金預かり金額");
            }

            // 画面.有効区分＝0(無効)を選択して
            // 保証金預かり.金額から保証金返却.金額を引いた結果が0以外の場合、エラー
            if (CommonConstants.YUKOKBN_0.equals(base.getYukokbn())) {
                int iHoshokinazukarikin =
                    Integer.parseInt(ItemUtil.removeComma(sale.getHoshokinazukarikin()));
                int iHoshokinhenkyakukin = StringUtils.isEmpty(sale.getHoshokinhenkyakukin()) ? 0
                    : Integer.parseInt(ItemUtil.removeComma(sale.getHoshokinhenkyakukin()));
                int diff = iHoshokinazukarikin - iHoshokinhenkyakukin;
                if (diff != 0) {
                    List<FormField> fields = new ArrayList<>();
                    addField(fields, "baseBean.yukokbn", "有効区分");
                    addField(fields, "saleBean.hoshokinazukarikin", "保証金預かり金額");
                    addField(fields, "saleBean.hoshokinhenkyakukin", "保証金返却金額");
                    throw BusinessExceptionFactory.validationFailed(fields, MessageID.W132,
                        "預かり金と保証金の計算結果が０以外", "有効区分を無効に");
                }
            }
        }

        // 保証金返却.日付が入力され、保証金返却.金額が未入力の場合、エラー
        if (!StringUtils.isEmpty(sale.getHoshokinhenkyakudd())
            && StringUtils.isEmpty(sale.getHoshokinhenkyakukin())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("saleBean.hoshokinhenkyakukin", "保証金返却金額"), MessageID.W011,
                "保証金返却金額");
        }

        // 本部エージェントコードと獲得エージェントコードが未入力の時に、マージン対象区分=0(設定なし)以外の場合、エラー
        if (StringUtils.isEmpty(deli.getHonbuejntcd())
            && StringUtils.isEmpty(deli.getKakutokuejntcd())
            && !CommonConstants.TOKUISAKIMGNTRGTKBN_0.equals(sale.getMgntrgtkbn())) {
            List<FormField> fields = new ArrayList<>();
            addField(fields, "deliveryOrderBean.honbuejntcd", "本部エージェントコード");
            addField(fields, "deliveryOrderBean.kakutokuejntcd", "獲得エージェントコード");
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W106,
                "獲得エージェントが未設定なのにマージン対象区分が設定なし以外");
        }

        // マージン対象区分＝1(ワンショット)または3(併用)を選択し、ワンショットコード未入力の場合、エラー
        if ((CommonConstants.TOKUISAKIMGNTRGTKBN_1.equals(sale.getMgntrgtkbn())
            || CommonConstants.TOKUISAKIMGNTRGTKBN_3.equals(sale.getMgntrgtkbn()))
            && StringUtils.isEmpty(sale.getWanshottocd())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("saleBean.wanshottocd", "ワンショットコード"), MessageID.W011, "ワンショットコード");
        }

        // マージン対象区分＝1(ワンショット)を選択た場合
        if (CommonConstants.TOKUISAKIMGNTRGTKBN_1.equals(sale.getMgntrgtkbn())) {
            List<FormField> fields = new ArrayList<>();
            // ボトルマージン情報が入力されている場合、エラー
            if (!StringUtils.isEmpty(sale.getMgnyoshncdn1())) {
                addField(fields, "saleBean.mgnyoshncdn1", "マージン用商品コード１");
            }
            if (!StringUtils.isEmpty(sale.getMgnyoshncdn2())) {
                addField(fields, "saleBean.mgnyoshncdn2", "マージン用商品コード２");
            }
            if (!StringUtils.isEmpty(sale.getMgnyoshncdn3())) {
                addField(fields, "saleBean.mgnyoshncdn3", "マージン用商品コード３");
            }
            if (!StringUtils.isEmpty(sale.getMgnyoshncdn4())) {
                addField(fields, "saleBean.mgnyoshncdn4", "マージン用商品コード４");
            }
            if (!StringUtils.isEmpty(sale.getMgncdn1())) {
                addField(fields, "saleBean.mgncdn1", "マージンコード１");
            }
            if (!StringUtils.isEmpty(sale.getMgncdn2())) {
                addField(fields, "saleBean.mgncdn2", "マージンコード２");
            }
            if (!StringUtils.isEmpty(sale.getMgncdn3())) {
                addField(fields, "saleBean.mgncdn3", "マージンコード３");
            }
            if (!StringUtils.isEmpty(sale.getMgncdn4())) {
                addField(fields, "saleBean.mgncdn4", "マージンコード４");
            }
            if (!StringUtils.isEmpty(sale.getBtlmgntrgtdkikan())) {
                addField(fields, "saleBean.btlmgntrgtdkikan", "ボトルマージン対象期間");
            }
            // エラーがある場合
            if (fields.size() > 0) {
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W124,
                    "マージン対象区分がワンショットマージンの場合は、ボトルマージン");
            }
        }

        // マージン対象区分=2(ボトル)を選択し、
        // ワンショットコード、ボーナスマージン件数、ボーナスマージン金額のいずれかに値がある場合
        if (CommonConstants.TOKUISAKIMGNTRGTKBN_2.equals(sale.getMgntrgtkbn())
            && (!StringUtils.isEmpty(sale.getWanshottocd())
                || !StringUtils.isEmpty(sale.getBonasumgncnt())
                || !StringUtils.isEmpty(sale.getBonasumgnkin()))) {
            // マージンコード１～４のいずれかに入力ある場合、エラー
            if (!StringUtils.isEmpty(sale.getMgncdn1()) || !StringUtils.isEmpty(sale.getMgncdn2())
                || !StringUtils.isEmpty(sale.getMgncdn3())
                || !StringUtils.isEmpty(sale.getMgncdn4())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("saleBean.wanshottocd", "ワンショットコード"), MessageID.W124,
                    "マージン対象区分がボトルマージンの場合は、ワンショットマージン");
            }
        }

        // マージン対象区分=2(ボトル)または3(併用)を選択しタ場合
        if ((CommonConstants.TOKUISAKIMGNTRGTKBN_2.equals(sale.getMgntrgtkbn())
            || CommonConstants.TOKUISAKIMGNTRGTKBN_3.equals(sale.getMgntrgtkbn()))) {
            // 商品コード１未入力の場合、エラー
            if (StringUtils.isEmpty(sale.getMgnyoshncdn1())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("saleBean.mgnyoshncdn1", "マージン用商品コード１"), MessageID.W011,
                    "マージン用商品コード１");
            }

            // ボトルマージン対象期間が未入力または入力値[0]の場合、エラー
            if (StringUtils.isEmpty(sale.getBtlmgntrgtdkikan())
                || "0".equals(sale.getBtlmgntrgtdkikan())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("saleBean.btlmgntrgtdkikan", "ボトルマージン対象期間"), MessageID.W011,
                    "対象期間");
            }

            // 商品コード１入力され、マージンコード１が未入力の場合、エラー
            if (!StringUtils.isEmpty(sale.getMgnyoshncdn1())
                && StringUtils.isEmpty(sale.getMgncdn1())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("saleBean.mgncdn1", "マージンコード１"), MessageID.W011, "マージンコード１");
            }

            // マージンコード２入力され、商品コード２が未入力の場合、エラー
            if (!StringUtils.isEmpty(sale.getMgncdn2())
                && StringUtils.isEmpty(sale.getMgnyoshncdn2())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("saleBean.mgnyoshncdn2", "マージン用商品コード２"), MessageID.W011,
                    "マージン用商品コード２");
            }

            // 商品コード２入力され、マージンコード２が未入力の場合、エラー
            if (!StringUtils.isEmpty(sale.getMgnyoshncdn2())
                && StringUtils.isEmpty(sale.getMgncdn2())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("saleBean.mgncdn2", "マージンコード２"), MessageID.W011, "マージンコード２");
            }

            // マージンコード３入力され、商品コード３が未入力の場合、エラー
            if (!StringUtils.isEmpty(sale.getMgncdn3())
                && StringUtils.isEmpty(sale.getMgnyoshncdn3())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("saleBean.mgnyoshncdn3", "マージン用商品コード３"), MessageID.W011,
                    "マージン用商品コード３");
            }

            // 商品コード３入力され、マージンコード３が未入力の場合、エラー
            if (!StringUtils.isEmpty(sale.getMgnyoshncdn3())
                && StringUtils.isEmpty(sale.getMgncdn3())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("saleBean.mgncdn3", "マージンコード３"), MessageID.W011, "マージンコード３");
            }

            // マージンコード４入力され、商品コード４が未入力の場合、エラー
            if (!StringUtils.isEmpty(sale.getMgncdn4())
                && StringUtils.isEmpty(sale.getMgnyoshncdn4())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("saleBean.mgnyoshncdn4", "マージン用商品コード４"), MessageID.W011,
                    "マージン用商品コード４");
            }

            // 商品コード４入力され、マージンコード４が未入力の場合、エラー
            if (!StringUtils.isEmpty(sale.getMgnyoshncdn4())
                && StringUtils.isEmpty(sale.getMgncdn4())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("saleBean.mgncdn4", "マージンコード４"), MessageID.W011, "マージンコード４");
            }
        }

        // 商品コード１の値と商品コード２～４の値のいずれかが同じ場合、エラー
        if (!StringUtils.isEmpty(sale.getMgnyoshncdn1())) {
            List<FormField> fields = new ArrayList<>();
            if (sale.getMgnyoshncdn1().equals(sale.getMgnyoshncdn2())) {
                addField(fields, "saleBean.mgnyoshncdn2", "マージン用商品コード２");
            }
            if (sale.getMgnyoshncdn1().equals(sale.getMgnyoshncdn3())) {
                addField(fields, "saleBean.mgnyoshncdn3", "マージン用商品コード３");
            }
            if (sale.getMgnyoshncdn1().equals(sale.getMgnyoshncdn4())) {
                addField(fields, "saleBean.mgnyoshncdn4", "マージン用商品コード４");
            }
            if (fields.size() > 0) {
                fields.add(0, new FormField("saleBean.mgnyoshncdn1", "マージン用商品コード１"));
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W103, "商品コード");
            }
        }

        // 商品コード２の値と商品コード１、３、４の値のいずれかが同じ場合、エラー
        if (!StringUtils.isEmpty(sale.getMgnyoshncdn2())) {
            List<FormField> fields = new ArrayList<>();
            if (sale.getMgnyoshncdn2().equals(sale.getMgnyoshncdn1())) {
                addField(fields, "saleBean.mgnyoshncdn1", "マージン用商品コード１");
            }
            if (sale.getMgnyoshncdn2().equals(sale.getMgnyoshncdn3())) {
                addField(fields, "saleBean.mgnyoshncdn3", "マージン用商品コード３");
            }
            if (sale.getMgnyoshncdn2().equals(sale.getMgnyoshncdn4())) {
                addField(fields, "saleBean.mgnyoshncdn4", "マージン用商品コード４");
            }
            if (fields.size() > 0) {
                fields.add(0, new FormField("saleBean.mgnyoshncdn2", "マージン用商品コード２"));
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W103, "商品コード");
            }
        }

        // 商品コード３の値と商品コード１、２、４の値のいずれかが同じ場合、エラー
        if (!StringUtils.isEmpty(sale.getMgnyoshncdn3())) {
            List<FormField> fields = new ArrayList<>();
            if (sale.getMgnyoshncdn3().equals(sale.getMgnyoshncdn1())) {
                addField(fields, "saleBean.mgnyoshncdn1", "マージン用商品コード１");
            }
            if (sale.getMgnyoshncdn3().equals(sale.getMgnyoshncdn2())) {
                addField(fields, "saleBean.mgnyoshncdn2", "マージン用商品コード２");
            }
            if (sale.getMgnyoshncdn3().equals(sale.getMgnyoshncdn4())) {
                addField(fields, "saleBean.mgnyoshncdn4", "マージン用商品コード４");
            }
            if (fields.size() > 0) {
                fields.add(0, new FormField("saleBean.mgnyoshncdn3", "マージン用商品コード３"));
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W103, "商品コード");
            }
        }

        // 商品コード４の値と商品コード１～３の値のいずれかが同じ場合、エラー
        if (!StringUtils.isEmpty(sale.getMgnyoshncdn4())) {
            List<FormField> fields = new ArrayList<>();
            if (sale.getMgnyoshncdn4().equals(sale.getMgnyoshncdn1())) {
                addField(fields, "saleBean.mgnyoshncdn1", "マージン用商品コード１");
            }
            if (sale.getMgnyoshncdn4().equals(sale.getMgnyoshncdn2())) {
                addField(fields, "saleBean.mgnyoshncdn2", "マージン用商品コード２");
            }
            if (sale.getMgnyoshncdn4().equals(sale.getMgnyoshncdn3())) {
                addField(fields, "saleBean.mgnyoshncdn3", "マージン用商品コード３");
            }
            if (fields.size() > 0) {
                fields.add(0, new FormField("saleBean.mgnyoshncdn4", "マージン用商品コード４"));
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W103, "商品コード");
            }
        }

        // 特別値引＝0(なし)を選択かつ
        // 自動値引１.値引コードまたは自動値引２.値引コードまたは自動値引３.値引コードが入力されている場合、エラー
        if (CommonConstants.RKNKINDTOKUBETSUNEBIKIFLG_0.equals(base.getRknkindtokubetsunebikiflg())
            && (!StringUtils.isEmpty(sale.getJidonebikicd1())
                || !StringUtils.isEmpty(sale.getJidonebikicd2())
                || !StringUtils.isEmpty(sale.getJidonebikicd3()))) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.rknkindtokubetsunebikiflg", "特別値引"), MessageID.W098,
                "値引できる料金プラン");
        }

        // 自動値引１(値引コード)＝入力ありの場合
        if (!StringUtils.isEmpty(sale.getJidonebikicd1())) {
            // 自動値引１(適用期間開始)＝未入力の場合、エラー
            checkRequired(sale.getNebikistrtdydmn1(), "saleBean.nebikistrtdydmn1", "値引開始年月１");

            // 値引開始年月１が1900/01～9999/12でない場合、エラー
            checkYmRange(sale.getNebikistrtdydmn1(), "saleBean.nebikistrtdydmn1", "値引開始年月１");

            // 自動値引１(適用期間終了)＝未入力の場合、エラー
            checkRequired(sale.getNebikishrydydmn1(), "saleBean.nebikishrydydmn1", "値引終了年月１");

            // 値引終了年月１が1900/01～9999/12でない場合、エラー
            checkYmRange(sale.getNebikishrydydmn1(), "saleBean.nebikishrydydmn1", "値引終了年月１");

            // 自動値引１(適用期間開始)＞自動値引１(適用期間終了)の場合、エラー
            if (sale.getNebikistrtdydmn1().compareTo(sale.getNebikishrydydmn1()) > 0) {
                List<FormField> fields = new ArrayList<>();
                addField(fields, "saleBean.nebikistrtdydmn1", "値引開始年月１");
                addField(fields, "saleBean.nebikishrydydmn1", "値引終了年月１");
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W093,
                    "値引適用期間１の開始", "終了");
            }

            // 自動値引１(値引コード)＝自動値引２(値引コード)の場合、エラー
            if (sale.getJidonebikicd1().equals(sale.getJidonebikicd2())) {
                List<FormField> fields = new ArrayList<>();
                addField(fields, "saleBean.jidonebikicd1", "値引コード１");
                addField(fields, "saleBean.jidonebikicd2", "値引コード２");
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W103, "自動値引コード");
            }

            // 自動値引１(値引コード)＝自動値引３(値引コード)の場合、エラー
            if (sale.getJidonebikicd1().equals(sale.getJidonebikicd3())) {
                List<FormField> fields = new ArrayList<>();
                addField(fields, "saleBean.jidonebikicd1", "値引コード１");
                addField(fields, "saleBean.jidonebikicd3", "値引コード３");
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W103, "自動値引コード");
            }

            // 自動値引１(値引コード)＝未入力の場合
        } else {
            // 自動値引１(適用期間開始)＝入力ありの場合、エラー
            if (!StringUtils.isEmpty(sale.getNebikistrtdydmn1())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("saleBean.nebikistrtdydmn1", "値引開始年月１"), MessageID.W124,
                    "値引開始年月１");
            }

            // 自動値引１(適用期間終了)＝入力ありの場合、エラー
            if (!StringUtils.isEmpty(sale.getNebikishrydydmn1())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("saleBean.nebikishrydydmn1", "値引終了年月１"), MessageID.W124,
                    "値引終了年月１");
            }
        }

        // 自動値引２(値引コード)＝入力ありの場合
        if (!StringUtils.isEmpty(sale.getJidonebikicd2())) {
            // 自動値引２(適用期間開始)＝未入力の場合、エラー
            checkRequired(sale.getNebikistrtdydmn2(), "saleBean.nebikistrtdydmn2", "値引開始年月２");

            // 値引開始年月２が1900/01～9999/12でない場合、エラー
            checkYmRange(sale.getNebikistrtdydmn2(), "saleBean.nebikistrtdydmn2", "値引開始年月２");

            // 自動値引２(適用期間終了)＝未入力の場合、エラー
            checkRequired(sale.getNebikishrydydmn2(), "saleBean.nebikishrydydmn2", "値引終了年月２");

            // 値引終了年月２が1900/01～9999/12でない場合、エラー
            checkYmRange(sale.getNebikishrydydmn2(), "saleBean.nebikishrydydmn2", "値引終了年月２");

            // 自動値引２(適用期間開始)＞自動値引２(適用期間終了)の場合、エラー
            if (sale.getNebikistrtdydmn2().compareTo(sale.getNebikishrydydmn2()) > 0) {
                List<FormField> fields = new ArrayList<>();
                addField(fields, "saleBean.nebikistrtdydmn2", "値引開始年月２");
                addField(fields, "saleBean.nebikishrydydmn2", "値引終了年月２");
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W093,
                    "値引適用期間２の開始", "終了");
            }

            // 自動値引２(値引コード)＝自動値引３(値引コード)の場合、エラー
            if (sale.getJidonebikicd2().equals(sale.getJidonebikicd3())) {
                List<FormField> fields = new ArrayList<>();
                addField(fields, "saleBean.jidonebikicd2", "値引コード２");
                addField(fields, "saleBean.jidonebikicd3", "値引コード３");
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W103, "自動値引コード");
            }

            // 自動値引２(値引コード)＝未入力の場合
        } else {
            // 自動値引２(適用期間開始)＝入力ありの場合、エラー
            if (!StringUtils.isEmpty(sale.getNebikistrtdydmn2())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("saleBean.nebikistrtdydmn2", "値引開始年月２"), MessageID.W124,
                    "値引開始年月２");
            }

            // 自動値引２(適用期間終了)＝入力ありの場合、エラー
            if (!StringUtils.isEmpty(sale.getNebikishrydydmn2())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("saleBean.nebikishrydydmn2", "値引終了年月２"), MessageID.W124,
                    "値引終了年月２");
            }
        }

        // 自動値引３(値引コード)＝入力ありの場合
        if (!StringUtils.isEmpty(sale.getJidonebikicd3())) {
            // 自動値引３(適用期間開始)＝未入力の場合、エラー
            checkRequired(sale.getNebikistrtdydmn3(), "saleBean.nebikistrtdydmn3", "値引開始年月３");

            // 値引開始年月３が1900/01～9999/12でない場合、エラー
            checkYmRange(sale.getNebikistrtdydmn3(), "saleBean.nebikistrtdydmn3", "値引開始年月３");

            // 自動値引３(適用期間終了)＝未入力の場合、エラー
            checkRequired(sale.getNebikishrydydmn3(), "saleBean.nebikishrydydmn3", "値引終了年月３");

            // 値引終了年月３が1900/01～9999/12でない場合、エラー
            checkYmRange(sale.getNebikishrydydmn3(), "saleBean.nebikishrydydmn3", "値引終了年月３");

            // 自動値引３(適用期間開始)＞自動値引３(適用期間終了)の場合、エラー
            if (sale.getNebikistrtdydmn3().compareTo(sale.getNebikishrydydmn3()) > 0) {
                List<FormField> fields = new ArrayList<>();
                addField(fields, "saleBean.nebikistrtdydmn3", "値引開始年月３");
                addField(fields, "saleBean.nebikishrydydmn3", "値引終了年月３");
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W093,
                    "値引適用期間３の開始", "終了");
            }

            // 自動値引３(値引コード)＝未入力の場合
        } else {
            // 自動値引３(適用期間開始)＝入力ありの場合、エラー
            if (!StringUtils.isEmpty(sale.getNebikistrtdydmn3())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("saleBean.nebikistrtdydmn3", "値引開始年月３"), MessageID.W124,
                    "値引開始年月３");
            }

            // 自動値引３(適用期間終了)＝入力ありの場合、エラー
            if (!StringUtils.isEmpty(sale.getNebikishrydydmn3())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("saleBean.nebikishrydydmn3", "値引終了年月３"), MessageID.W124,
                    "値引終了年月３");
            }
        }

        // ポイント関連チェック
        // ステージ切替日=入力ありの場合
        if (!StringUtils.isEmpty(sale.getTekiyoudd())) {
            // ステージ切替日　＜=　運用日付の場合、エラー
            if (DateTimeUtil.toNoSlashedDate(sale.getTekiyoudd()).compareTo(unyodd) <= 0) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("saleBean.tekiyoudd", "ステージ切替日"), MessageID.W214, "ステージ切替日",
                    "運用日付");
            }

            // ステージ修正（新）が空の場合、エラー
            if (StringUtils.isEmpty(sale.getPointrnkshin())) {
                throw BusinessExceptionFactory.validationFailed(
                        new FormField("saleBean.pointrnkshin", "ステージ修正（新）"), MessageID.W164,
                        "ステージ切替日","ステージ修正（新）");
            }

            // 得意先ポイントランクマスタのステージ切替日重複チェック
            b0501w02Service.checkTokuPointRankDuplicate(base.getApkihoncd(), base.getShopcd(),
                    base.getPtokuisakicd(), DateTimeUtil.toNoSlashedDate(sale.getTekiyoudd()));

            // 年１のポイントランク更新月日チェック
            b0501w02Service.checkPointRankUpdateDD(DateTimeUtil.toNoSlashedDate(sale.getTekiyoudd()));
        }

        // ステージ修正（新）=入力ありの場合
        if (!StringUtils.isEmpty(sale.getPointrnkshin())) {
            // ステージ切替日が空の場合、エラー
            if (StringUtils.isEmpty(sale.getTekiyoudd())) {
                throw BusinessExceptionFactory.validationFailed(
                        new FormField("saleBean.tekiyoudd", "ステージ切替日"), MessageID.W164,
                        "ステージ修正（新）","ステージ切替日");
            }
        }

        // まとめ買いポイント区分=ありの場合
        if (CommonConstants.MTMKIPOINTKBN_1.equals(sale.getMtmkipointkbn())) {
            // まとめ買い数量＝未入力の場合、エラー
            checkRequired(sale.getMtmkisuryo(), "saleBean.mtmkisuryo", "まとめ買い数量");

            // まとめ買いポイント数＝未入力の場合、エラー
            checkRequired(sale.getMtmkipointsu(), "saleBean.mtmkipointsu", "まとめ買いポイント数");
        }

        // 入会特典区分=ありの場合
        if (CommonConstants.NYUKAITOKUTENUMU_1.equals(sale.getNyukaitokutenumu())) {
            // 入会特典ポイント数＝未入力の場合、エラー
            checkRequired(sale.getNyukaitokutenpointsu(), "saleBean.nyukaitokutenpointsu", "入会特典ポイント数");
        }

        // --------------------
        // 営業付帯情報タブ
        // --------------------
        // 入会者情報.登録日が1900/01/01～9999/12/31でない場合、エラー
        checkDateRange(add.getNyukaishajiyohoinptdd(), "additionalBean.nyukaishajiyohoinptdd",
            "登録日");

        // 入社年月が1900/01～9999/12でない場合、エラー
        checkYmRange(add.getNyushadydm(), "additionalBean.nyushadydm", "入社年月");

        // 誕生日が1900/01/01～9999/12/31でない場合、エラー
        checkDateRange(add.getNyushatanjodd(), "additionalBean.nyushatanjodd", "誕生日");

        // ＴＥＬが「-」を除いて11桁を超過する場合、エラー
        if (!StringUtils.isEmpty(add.getKinmusakitelno())
            && add.getKinmusakitelno().replaceAll("-", "").length() > 11) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("additionalBean.kinmusakitelno", "勤務先電話番号"), MessageID.W113,
                "勤務先電話番号は数値で最大１１桁");
        }

        // ＦＡＸが「-」を除いて11桁を超過する場合、エラー
        if (!StringUtils.isEmpty(add.getKinmusakifaxno())
            && add.getKinmusakifaxno().replaceAll("-", "").length() > 11) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("additionalBean.kinmusakifaxno", "勤務先FAX番号"), MessageID.W113,
                "勤務先FAX番号は数値で最大１１桁");
        }

        // 世帯情報.登録日が1900/01/01～9999/12/31でない場合、エラー
        checkDateRange(add.getSetaijyouhouinptdd(), "additionalBean.setaijyouhouinptdd", "登録日");

        // 世帯収入が正数または0以外の場合エラー
        checkPositiveOrZero(add.getSetaisyunyu(), "additionalBean.setaisyunyu", "世帯収入");

        // --------------------
        // サーバーレンタル料情報タブ
        // --------------------
        // 設定する＝1(チェックON)の場合
        if (!StringUtils.isEmpty(rent.getTeireisetteikbn())) {
            // 計上日が未入力の場合、エラー
            if (StringUtils.isEmpty(rent.getKeijodd())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("rentalBean.keijodd", "計上日"), MessageID.W171, "定例設定が選択された場合",
                    "計上日は省略");
            }

            // 開始年月が未入力の場合、エラー
            if (StringUtils.isEmpty(rent.getStrtdydm())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("rentalBean.strtdydm", "開始年月"), MessageID.W171, "定例設定が選択された場合",
                    "開始年月は省略");
            }

            // 開始年月が1900/01～9999/12でない場合、エラー
            checkYmRange(rent.getStrtdydm(), "rentalBean.strtdydm", "開始年月");

            // 開始年月(yyyymm)＞前回発生計上日(yyyymm)の場合、エラー
            if (!StringUtils.isEmpty(rent.getZnkhasseikeijodd())
                && DateTimeUtil.toNoSlashedDateYYYYMM(rent.getStrtdydm()).compareTo(
                    DateTimeUtil.toNoSlashedDate(rent.getZnkhasseikeijodd()).substring(0, 6)) > 0) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("rentalBean.strtdydm", "開始年月"), MessageID.W093, "前回発生計上日",
                    "開始年月");
            }

            // 終了年月が未入力の場合、エラー
            if (StringUtils.isEmpty(rent.getShrydydm())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("rentalBean.shrydydm", "終了年月"), MessageID.W171, "定例設定が選択された場合",
                    "終了年月は省略");
            }

            // 終了年月が1900/01～9999/12でない場合、エラー
            checkYmRange(rent.getShrydydm(), "rentalBean.shrydydm", "終了年月");

            // 開始年月(yyyymm)＞終了年月(yyyymm)の場合、エラー
            if (rent.getStrtdydm().compareTo(rent.getShrydydm()) > 0) {
                List<FormField> fields = new ArrayList<>();
                addField(fields, "rentalBean.strtdydm", "開始年月");
                addField(fields, "rentalBean.shrydydm", "終了年月");
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W093, "開始年月",
                    "終了年月");
            }

            // 次回計上年月が未入力の場合、エラー
            if (StringUtils.isEmpty(rent.getJikaikeijodydm())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("rentalBean.jikaikeijodydm", "次回計上年月"), MessageID.W171,
                    "定例設定が選択された場合", "次回計上年月は省略");
            }

            // 次回計上年月が1900/01～9999/12でない場合、エラー
            checkYmRange(rent.getJikaikeijodydm(), "rentalBean.jikaikeijodydm", "次回計上年月");

            // 開始年月(yyyymm)＞次回計上年月(yyyymm)の場合、エラー
            if (rent.getStrtdydm().compareTo(rent.getJikaikeijodydm()) > 0) {
                List<FormField> fields = new ArrayList<>();
                addField(fields, "rentalBean.strtdydm", "開始年月");
                addField(fields, "rentalBean.jikaikeijodydm", "次回計上年月");
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W093, "次回計上年月",
                    "開始年月");
            }

            // 前回発生計上日(yyyymm)＞次回計上年月(yyyymm)の場合、エラー
            if (!StringUtils.isEmpty(rent.getZnkhasseikeijodd())
                && DateTimeUtil.toNoSlashedDate(rent.getZnkhasseikeijodd()).substring(0, 6)
                    .compareTo(DateTimeUtil.toNoSlashedDateYYYYMM(rent.getJikaikeijodydm())) > 0) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("rentalBean.jikaikeijodydm", "次回計上年月"), MessageID.W093, "前回発生計上日",
                    "次回計上年月");
            }

            // 売上設定=0(定例売上マスタ)の場合、
            if (URISETTEI_0.equals(rent.getUrisettei())) {
                // 数量が未入力または0の場合、エラー
                if (StringUtils.isEmpty(rent.getSuryo()) || "0".equals(rent.getSuryo())) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("rentalBean.suryo", "数量"), MessageID.W011,
                        "売上設定が定例売上マスタの場合は数量");
                }
                // 売上設定≠0(得意先設置サーバーマスタ)の場合
            } else {
                // 数量、単価、初回単価に強制的に0を設定する
                rent.setSuryo("0");
                rent.setUnt("0");
                rent.setShokaiunt("0");
            }

            // 数量が数字とマイナス記号と小数点でない場合、エラー
            checkNumDecimal(rent.getSuryo(), "rentalBean.suryo", "数量");

            // 数量桁数チェック
            checkDigitLength(rent.getSuryo(), 8, 1, "rentalBean.suryo", "数量");

            // 単価が未入力の場合、エラー
            if (StringUtils.isEmpty(rent.getUnt())) {
                throw BusinessExceptionFactory
                    .validationFailed(new FormField("rentalBean.unt", "単価"), MessageID.W011, "単価");
            }

            // 単価が数字とマイナス記号と小数点でない場合、エラー
            checkNumDecimal(rent.getUnt(), "rentalBean.unt", "単価");

            // 単価桁数チェック
            checkDigitLength(rent.getUnt(), 8, 2, "rentalBean.unt", "単価");

            // 初回単価が数字とマイナス記号と小数点でない場合、エラー
            checkNumDecimal(rent.getShokaiunt(), "rentalBean.shokaiunt", "初回単価");

            // 初回単価桁数チェック
            checkDigitLength(rent.getShokaiunt(), 8, 2, "rentalBean.shokaiunt", "初回単価");
        }

        // --------------------
        // 名称、住所変更予約情報タブ
        // --------------------
        // 得意先マスタ変更＝1(チェックON)の場合
        if (!StringUtils.isEmpty(resv.getTokuisakimasutahnkflg())) {
            // 変更日が未入力の場合、エラー
            checkRequired(resv.getTtmasutachgdd(), "reservationBean.ttmasutachgdd", "得意先マスタ変更日");

            // 変更日が1900/01/01～9999/12/31でない場合、エラー
            checkDateRange(resv.getTtmasutachgdd(), "reservationBean.ttmasutachgdd", "変更日");

            // 運用日付>変更日の場合、エラー
            if (unyodd.compareTo(DateTimeUtil.toNoSlashedDate(resv.getTtmasutachgdd())) > 0) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("reservationBean.ttmasutachgdd", "得意先マスタ変更日"), MessageID.W120,
                    "得意先マスタ変更日", "運用日付より大きい日付");
            }

            // 得意先名称１が未入力の場合、エラー
            checkRequired(resv.getTokuisakinmn11(), "reservationBean.tokuisakinmn11", "変更得意先得意先名称");

            // 得意先名称カナ（半角）1が未入力の場合、エラー
            checkRequired(resv.getTokuisakinmkanan11(), "reservationBean.tokuisakinmkanan11",
                "変更得意先得意先名称カナ");

            // 郵便番号が未入力の場合、エラー
            checkRequired(resv.getYubinno1(), "reservationBean.yubinno1", "変更得意先郵便番号");

            // 住所１が未入力の場合、エラー
            checkRequired(resv.getAdrsn11(), "reservationBean.adrsn11", "変更得意先住所");

            // 連絡先番号１が未入力の場合、エラー
            checkRequired(resv.getRenrakusakinon11(), "reservationBean.renrakusakinon11",
                "変更得意先連絡先番号");

            // 営業エリアコードが未入力の場合、エラー
            checkRequired(resv.getEigyoareacd1(), "reservationBean.eigyoareacd1", "変更得意先営業エリアコード");

            // 営業担当者コードが未入力の場合、エラー
            checkRequired(resv.getEigyotnshacd1(), "reservationBean.eigyotnshacd1",
                "変更得意先営業担当者コード");

            // 配達拠点ＡＰ基本コードが未入力の場合、エラー
            checkRequired(resv.getDlvrykankatsuapkihoncd1(),
                "reservationBean.dlvrykankatsuapkihoncd1", "変更得意先配達管轄ＡＰ基本コード");

            // 配達拠点ショップコードが未入力の場合、エラー
            checkRequired(resv.getDlvrykankatsushopcd1(), "reservationBean.dlvrykankatsushopcd1",
                "変更得意先配達管轄ショップコード");

            // 配達拠点コードが未入力の場合、エラー
            checkRequired(resv.getDlvrykankatsukyotencd1(),
                "reservationBean.dlvrykankatsukyotencd1", "変更得意先配達拠点コード");

            // 配達エリアコードが未入力の場合、エラー
            checkRequired(resv.getDlvryareacd1(), "reservationBean.dlvryareacd1", "変更得意先配達エリアコード");

            // 連絡先番号２が入力され、連絡先番号区分２が未選択の場合はエラー
            if (!StringUtils.isEmpty(resv.getRenrakusakinon21())
                && StringUtils.isEmpty(resv.getRenrakusakinokbnn21())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("reservationBean.renrakusakinokbnn21", "変更得意先連絡先番号区分２"),
                    MessageID.W011, "変更得意先連絡先番号区分２");
            }

            // 連絡先番号区分２が選択され、連絡先番号２が未入力の場合はエラー
            if (StringUtils.isEmpty(resv.getRenrakusakinon21())
                && !StringUtils.isEmpty(resv.getRenrakusakinokbnn21())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("reservationBean.renrakusakinon21", "変更得意先連絡番号２"), MessageID.W011,
                    "変更得意先連絡番号２");
            }

            // 連絡先番号３が入力され、連絡先番号区分３が未選択の場合はエラー
            if (!StringUtils.isEmpty(resv.getRenrakusakinon31())
                && StringUtils.isEmpty(resv.getRenrakusakinokbnn31())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("reservationBean.renrakusakinokbnn31", "変更得意先連絡先番号区分３"),
                    MessageID.W011, "変更得意先連絡先番号区分３");
            }

            // 連絡先番号区分３が選択され、連絡先番号３が未入力の場合はエラー
            if (StringUtils.isEmpty(resv.getRenrakusakinon31())
                && !StringUtils.isEmpty(resv.getRenrakusakinokbnn31())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("reservationBean.renrakusakinon31", "変更得意先連絡番号３"), MessageID.W011,
                    "変更得意先連絡番号３");
            }

            // 連絡先番号４が入力され、連絡先番号区分４が未選択の場合はエラー
            if (!StringUtils.isEmpty(resv.getRenrakusakinon41())
                && StringUtils.isEmpty(resv.getRenrakusakinokbnn41())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("reservationBean.renrakusakinokbnn41", "変更得意先連絡先番号区分４"),
                    MessageID.W011, "変更得意先連絡先番号区分４");
            }

            // 連絡先番号区分４が選択され、連絡先番号４が未入力の場合はエラー
            if (StringUtils.isEmpty(resv.getRenrakusakinon41())
                && !StringUtils.isEmpty(resv.getRenrakusakinokbnn41())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("reservationBean.renrakusakinon41", "変更得意先連絡番号４"), MessageID.W011,
                    "変更得意先連絡番号４");
            }

            // （得意先区分＝1(顧客)かつ 顧客区分＝2(ショップ顧客)）を選択
            // または得意先区分＝ 2(ショップ)を選択した場合、エラー
            if ((CommonConstants.KBN_TOKUISAKI_KOKUAKU.equals(base.getTokuisakikbn())
                && CommonConstants.JUTAKUDLVRYCSTKBN_2.equals(base.getJutakudlvrycstkbn()))
                || CommonConstants.KBN_TOKUISAKI_SHOP.equals(base.getTokuisakikbn())) {
                // 配達拠点に自AP－SHOP以外を選択した場合
                if (!(base.getApkihoncd().equals(resv.getDlvrykankatsuapkihoncd1())
                    && base.getShopcd().equals(resv.getDlvrykankatsushopcd1()))) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("reservationBean.dlvrykankatsukyotencd1", "変更得意先配達管轄拠点コード"),
                        MessageID.W124, "委託配達先");
                }
            }

            // 顧客区分が "3"（ＮＡ顧客）または"4"（委託顧客）の場合
            if ((CommonConstants.JUTAKUDLVRYCSTKBN_3.equals(base.getJutakudlvrycstkbn())
                || CommonConstants.JUTAKUDLVRYCSTKBN_4.equals(base.getJutakudlvrycstkbn()))) {
                // 配達会社コードが未入力の場合、エラー
                if (StringUtils.isEmpty(resv.getDlvrycmpnycd1())) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("reservationBean.dlvrycmpnycd1", "変更得意先配達会社コード"),
                        MessageID.W113, "得意先が委託元の場合、変更得意先配達会社コード");
                }

                // 配達担当者コードが未入力の場合、エラー
                if (StringUtils.isEmpty(resv.getDlvrytnshacd1())) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("reservationBean.dlvrytnshacd1", "変更得意先配達担当者コード"),
                        MessageID.W113, "得意先が委託元の場合、変更得意先配達担当者コード");
                }

                // 委託相手AP基本コード ≠ 配達拠点AP基本コード もしくは 委託相手ショップコード ≠ 配達拠点ショップコード の場合は、エラー
                if (!StringUtils.isEmpty(base.getItakuaiteapkihoncd())
                    && !StringUtils.isEmpty(base.getItakuaiteshopcd())
                    && !StringUtils.isEmpty(resv.getDlvrykankatsuapkihoncd1())
                    && !StringUtils.isEmpty(resv.getDlvrykankatsushopcd1())) {
                    if (!base.getItakuaiteapkihoncd().equals(resv.getDlvrykankatsuapkihoncd1())
                        || !base.getItakuaiteshopcd().equals(resv.getDlvrykankatsushopcd1())) {
                        List<FormField> fields = new ArrayList<>();
                        addField(fields, "reservationBean.dlvrykankatsuapkihoncd1", "配達拠点AP基本コード");
                        addField(fields, "reservationBean.dlvrykankatsushopcd1", "配達拠点ショップコード");
                        throw BusinessExceptionFactory.validationFailed(fields, MessageID.W171,
                            "配達拠点AP基本コード、配達拠点ショップコード", "委託相手AP基本コード、委託相手ショップコードと異なる内容は登録");
                    }
                }

                // 顧客区分 が "3"（ＮＡ顧客）か"4"（委託顧客） 以外の場合
            } else {
                // AP基本コード≠ 配達拠点AP基本コード もしくは ショップコード ≠ 配達拠点ショップコードの場合は、エラー
                if (!StringUtils.isEmpty(resv.getDlvrykankatsuapkihoncd1())
                    && !StringUtils.isEmpty(resv.getDlvrykankatsushopcd1())) {
                    if (!base.getApkihoncd().equals(resv.getDlvrykankatsuapkihoncd1())
                        || !base.getShopcd().equals(resv.getDlvrykankatsushopcd1())) {
                        List<FormField> fields = new ArrayList<>();
                        addField(fields, "reservationBean.dlvrykankatsuapkihoncd1", "配達拠点AP基本コード");
                        addField(fields, "reservationBean.dlvrykankatsushopcd1", "配達拠点ショップコード");
                        throw BusinessExceptionFactory.validationFailed(fields, MessageID.W171,
                            "ＮＡ顧客、委託顧客以外", "他拠点を選択");
                    }
                }
            }

            // 配達区分＝2(他社便)を選択し、配達会社コードが未入力の場合、エラー
            if (CommonConstants.KBN_DLVRY_TASHA.equals(resv.getDlvrykbn1())
                && StringUtils.isEmpty(resv.getDlvrycmpnycd1())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("reservationBean.dlvrycmpnycd1", "変更得意先配達会社コード"), MessageID.W113,
                    "変更得意先配達会社コード");
            }

            // 受注時間指定区分＝５(指定-頃)または６(指定-以降)を選択し、
            // 受注時間指定時刻開始が未入力の場合、エラー
            if ((CommonConstants.KBN_DLVRYKIBO_KORO.equals(resv.getAcptjikanshiteikbn1())
                || CommonConstants.KBN_DLVRYKIBO_IKOU.equals(resv.getAcptjikanshiteikbn1()))
                && StringUtils.isEmpty(resv.getAcptjikanshiteijikokustr())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("reservationBean.acptjikanshiteijikokustr", "変更得意先受注時間指定時刻"),
                    MessageID.W011, "変更得意先受注時間指定時刻");
            }

            // 受注時間指定区分＝８(指定)を選択した場合
            if (CommonConstants.KBN_DLVRYKIBO_JIKANHANI.equals(resv.getAcptjikanshiteikbn1())) {
                // 受注時間指定時刻開始が未入力の場合、エラー
                if (StringUtils.isEmpty(resv.getAcptjikanshiteijikokustr())) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("reservationBean.acptjikanshiteijikokustr", "変更得意先受注時間指定時刻"),
                        MessageID.W011, "変更得意先受注時間指定時刻");
                }

                // 受注時間指定時刻終了が未入力の場合、エラー
                if (StringUtils.isEmpty(resv.getAcptjikanshiteijikokushry1())) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("reservationBean.acptjikanshiteijikokushry1",
                            "変更得意先受注時間指定時刻終了"),
                        MessageID.W011, "変更得意先受注時間指定時刻終了");
                }

                // 受注時間指定時刻開始 ＞ 受注時間指定時刻終了 の場合は、エラー
                if (resv.getAcptjikanshiteijikokustr()
                    .compareTo(resv.getAcptjikanshiteijikokushry1()) > 0) {
                    List<FormField> fields = new ArrayList<>();
                    addField(fields, "reservationBean.acptjikanshiteijikokustr", "変更得意先受注時間指定時刻開始");
                    addField(fields, "reservationBean.acptjikanshiteijikokushry1",
                        "変更得意先受注時間指定時刻終了");
                    throw BusinessExceptionFactory.validationFailed(fields, MessageID.W093,
                        "変更得意先受注時間指定時刻開始", "変更得意先受注時間指定時刻終了");
                }
            }
        }

        // 請求先マスタ変更＝1(チェックON)の場合
        if (!StringUtils.isEmpty(resv.getSeikyusakimsthnkflg())) {
            // 変更日が未入力の場合、エラー
            checkRequired(resv.getSkmhnkymd(), "reservationBean.skmhnkymd", "請求先マスタ変更日");

            // 変更日が1900/01/01～9999/12/31でない場合、エラー
            checkDateRange(resv.getSkmhnkymd(), "reservationBean.skmhnkymd", "変更日");

            // 運用日付>変更日の場合、エラー
            if (unyodd.compareTo(DateTimeUtil.toNoSlashedDate(resv.getSkmhnkymd())) > 0) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("reservationBean.skmhnkymd", "請求先マスタ変更日"), MessageID.W120,
                    "請求先マスタ変更日", "運用日付より大きい日付");
            }

            // 請求先名称１が未入力の場合、エラー
            checkRequired(resv.getSkysakinmn11(), "reservationBean.skysakinmn11", "変更請求先請求先名称");

            // 請求先名称カナ（半角）が未入力の場合、エラー
            checkRequired(resv.getSkysakinmkana(), "reservationBean.skysakinmkana", "変更請求先請求先名称カナ");

            // 請求先名称略称が未入力の場合、エラー
            checkRequired(resv.getSkysakinmryakusho(), "reservationBean.skysakinmryakusho",
                "変更請求先請求先名称略称");

            // 郵便番号が未入力の場合、エラー
            checkRequired(resv.getYubinno2(), "reservationBean.yubinno2", "変更請求先郵便番号");

            // 住所１が未入力の場合、エラー
            checkRequired(resv.getAdrsn12(), "reservationBean.adrsn12", "変更請求先住所");

            // 連絡先番号１が未入力の場合、エラー
            checkRequired(resv.getRenrakusakinon12(), "reservationBean.renrakusakinon12",
                "変更請求先連絡先番号");

            // 営業担当者コードが未入力の場合、エラー
            checkRequired(resv.getEigyotnshacd2(), "reservationBean.eigyotnshacd2",
                "変更請求先営業担当者コード");

            // 回収担当者コードが未入力の場合、エラー
            checkRequired(resv.getKaishutnshacd(), "reservationBean.kaishutnshacd",
                "変更請求先回収担当者コード");

            // 連絡先番号２が入力あり、連絡先番号区分２が未選択の場合はエラー
            if (!StringUtils.isEmpty(resv.getRenrakusakinon22())
                && StringUtils.isEmpty(resv.getRenrakusakinokbnn22())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("reservationBean.renrakusakinokbnn22", "変更請求先連絡先番号区分２"),
                    MessageID.W011, "変更請求先連絡先番号区分２");
            }

            // 連絡先番号区分２が選択され、連絡先番号２が未入力の場合はエラー
            if (StringUtils.isEmpty(resv.getRenrakusakinon22())
                && !StringUtils.isEmpty(resv.getRenrakusakinokbnn22())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("reservationBean.renrakusakinon22", "変更請求先連絡番号２"), MessageID.W011,
                    "変更請求先連絡番号２");
            }

            // 連絡先番号３が入力あり、連絡先番号区分３が未選択の場合はエラー
            if (!StringUtils.isEmpty(resv.getRenrakusakinon32())
                && StringUtils.isEmpty(resv.getRenrakusakinokbnn32())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("reservationBean.renrakusakinokbnn32", "変更請求先連絡先番号区分３"),
                    MessageID.W011, "変更請求先連絡先番号区分３");
            }

            // 連絡先番号区分３が選択され、連絡先番号３が未入力の場合はエラー
            if (StringUtils.isEmpty(resv.getRenrakusakinon32())
                && !StringUtils.isEmpty(resv.getRenrakusakinokbnn32())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("reservationBean.renrakusakinon32", "変更請求先連絡番号３"), MessageID.W011,
                    "変更請求先連絡番号３");
            }

            // 連絡先番号４が入力あり、連絡先番号区分４が未選択の場合はエラー
            if (!StringUtils.isEmpty(resv.getRenrakusakinon42())
                && StringUtils.isEmpty(resv.getRenrakusakinokbnn42())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("reservationBean.renrakusakinokbnn42", "変更請求先連絡先番号区分４"),
                    MessageID.W011, "変更請求先連絡先番号区分４");
            }

            // 連絡先番号区分４が選択され、連絡先番号４が未入力の場合はエラー
            if (StringUtils.isEmpty(resv.getRenrakusakinon42())
                && !StringUtils.isEmpty(resv.getRenrakusakinokbnn42())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("reservationBean.renrakusakinon42", "変更請求先連絡番号４"), MessageID.W011,
                    "変更請求先連絡番号４");
            }
        }
    }

    /**
     * 指定されたフィールドの値がクリアされていないかを検証する。 <br>
     * もし新しい値が空であり、以前の値が存在する場合は、ビジネス例外をスローする。
     *
     * @param honshokaino 本部紹介番号
     * @param newValue 新しい値（変更後の値）
     * @param oldValue 以前の値（変更前の値）
     * @param field 検証対象のフィールド情報
     * @throws BusinessException 検証に失敗した場合
     */
    private void validateClearedField(String honshokaino, String newValue, String oldValue,
        FormField field) {
        if (StringUtils.isEmpty(newValue) && !StringUtils.isEmpty(oldValue)) {
            log.debug("本部紹介得意先の{}がクリアされました。(本部紹介番号:{})", field.getLabel(), honshokaino);
            throw BusinessExceptionFactory.validationFailed(field, MessageID.W260,
                "エラー発生、本部システム部へ連絡して下さい。");
        }
    }

    /**
     * 退会受付日のクリアチェックを行うべきか判定する。<br>
     * 以下の条件のいずれかを満たす場合、クリアチェックを実施する。<br>
     * <ul>
     * <li>入会日が運用日付の1年以内</li>
     * <li>退会受付日が運用日付の年月と異なる</li>
     * </ul>
     *
     * @param nyukaidd 入会日（yyyymmdd形式）
     * @param taikaiddBefore 変更前の退会受付日（変更前, yyyymmdd形式）
     * @param unyodd 運用日付（yyyymmdd形式）
     * @return クリアチェックが必要なら true、不要なら false
     */
    private boolean shouldCheckResignationDateClearance(String nyukaidd, String taikaiddBefore,
        String unyodd) {
        // 入会日が未入力の場合はクリアチェックする必要がない
        if (StringUtils.isEmpty(nyukaidd)) {
            return false;
        }
        // 更新前の退会受付日が未入力の場合はチェックする必要がない
        if (StringUtils.isEmpty(taikaiddBefore)) {
            return false;
        }
        // 各種日付文字列（yyyymmdd）をLocalDateに変換
        LocalDate joiningDate = util.getLocalDate(nyukaidd);
        LocalDate resignationDate = util.getLocalDate(taikaiddBefore);
        LocalDate operationDate = util.getLocalDate(unyodd);
        // 入会日と運用日付による条件（入会日が運用日付の1年以内）
        boolean joiningDateCondition = joiningDate.isAfter(operationDate.minusYears(1L));
        // 更新前の退会受付日と運用日付による条件（退会受付日が運用日付の年月と異なる）
        boolean resignationDateCondition = !Objects.equals(util.getYearMonthString(resignationDate),
            util.getYearMonthString(operationDate));
        log.debug("入会日:{}", joiningDate);
        log.debug("変更前の退会受付日:{}", resignationDate);
        log.debug("運用日付:{}", operationDate);
        log.debug("入会日が運用日付の1年以内か:{}", joiningDateCondition);
        log.debug("変更前の退会受付日の年月が運用年月と異なるか:{}", resignationDateCondition);
        // 2つの条件のいずれかを満たした場合はクリアチェックが必要
        return joiningDateCondition || resignationDateCondition;
    }

    /**
     * 数値桁数チェック
     * 
     * @param target チェック対象数値
     * @param intLength 整数桁数
     * @param decLength 少数桁数
     * @param name フィールド名
     * @param label フィールドラベル
     * @return
     */
    private void checkDigitLength(String target, int intLength, int decLength, String name,
        String label) {

        if (!StringUtils.isEmpty(target)) {
            // カンマを除去する
            target = ItemUtil.removeComma(target);
            BigDecimal bdTarget = new BigDecimal(target);
            BigDecimal absTarget = bdTarget.abs();
            // マイナス記号を除去
            String strTarget = String.valueOf(absTarget);
            int index = strTarget.indexOf(".");

            // 整数部桁数がオーバーする場合、エラー
            if ((index < 0 && strTarget.length() > intLength)
                || (index > 0 && strTarget.substring(0, index - 1).length() > intLength)) {
                throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                    MessageID.W018, label, String.valueOf(intLength));
            }

            // 少数部桁数がオーバーする場合、エラー
            if (index > 0 && strTarget.substring(index + 1).length() > decLength) {
                throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                    MessageID.W010, label, String.valueOf(decLength));
            }
        }
    }

    /**
     * FormFieldリストに要素を追加する
     * 
     * @param list FormFieldリスト
     * @param name フィールド名
     * @param label フィールドラベル
     * @return
     */
    private void addField(List<FormField> list, String name, String label) {
        list.add(new FormField(name, label));
    }

    /**
     * 正数または0チェック（メッセージID：W013）
     * 
     * @param target チェック対象
     * @param name フィールド名
     * @param label フィールドラベル
     * @return
     */
    private void checkPositiveOrZero(String target, String name, String label) {
        if (!StringUtils.isEmpty(target)) {
            // カンマを除去する
            target = ItemUtil.removeComma(target);
            // チェック対象が正数または0でない場合、エラー
            if (!target.matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS)) {
                throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                    MessageID.W013, label, "数字のみ");
            }
        }
    }

    /**
     * 数字マイナス小数点チェック（メッセージID：W013）
     * 
     * @param target チェック対象
     * @param name フィールド名
     * @param label フィールドラベル
     * @return
     */
    private void checkNumDecimal(String target, String name, String label) {
        if (!StringUtils.isEmpty(target)) {
            // カンマを除去する
            target = ItemUtil.removeComma(target);
            // チェック対象が数字とマイナス記号と小数点でない場合、エラー
            if (!target.matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS_DECIMAL)) {
                throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                    MessageID.W013, label, "数字とマイナス記号と小数点");
            }
        }
    }

    /**
     * 入力必須チェック（メッセージID：W113）
     * 
     * @param target チェック対象
     * @param name フィールド名
     * @param label フィールドラベル
     * @return
     */
    private void checkRequired(String target, String name, String label) {
        // チェック対象が未入力の場合、エラー
        if (StringUtils.isEmpty(target)) {
            throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                MessageID.W113, label);
        }
    }

    /**
     * 日付範囲チェック
     * 
     * @param targetDate チェック対象日付
     * @param name フィールド名
     * @param label フィールドラベル
     * @return
     */
    private void checkDateRange(String targetDate, String name, String label) {
        // チェック対象日付が1900/01/01～9999/12/31でない場合、エラー
        if (!StringUtils.isEmpty(targetDate)
            && !(DateTimeUtil.isLater(CommonConstants.DATE_MIN, targetDate)
                && DateTimeUtil.isLater(targetDate, CommonConstants.DATE_MAX))) {
            throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                MessageID.W033, label, CommonConstants.DATE_MIN, CommonConstants.DATE_MAX);
        }
    }

    /**
     * 年月範囲チェック
     * 
     * @param targetDate チェック対象年月
     * @param name フィールド名
     * @param label フィールドラベル
     * @return
     */
    private void checkYmRange(String targetYm, String name, String label) {
        // チェック対象年月が1900/01～9999/12でない場合、エラー
        if (!StringUtils.isEmpty(targetYm)
            && !(DateTimeUtil.isLater(CommonConstants.YEARMONTH_MIN, targetYm)
                && DateTimeUtil.isLater(targetYm, CommonConstants.YEARMONTH_MAX))) {
            throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                MessageID.W033, label, CommonConstants.YEARMONTH_MIN,
                CommonConstants.YEARMONTH_MAX);
        }
    }

    /**
     * 商品マスタビューから商品名セレクトボックス情報を取得.
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param blank 先頭にブランク有無（先頭にブランクを追加する場合は「t」を指定）
     * @return CommonPartsListBeanリスト(商品コード、商品名称)
     */
    @GetMapping("shnmei/{apkihoncd}/{shopcd}")
    public List<CommonPartsListBean> getShnList(@PathVariable("apkihoncd") String apkihoncd,
        @PathVariable("shopcd") String shopcd,
        @RequestParam(name = "blank", required = false) String blank,
        @ModelAttribute(name = "dbManagementSession_B0501W02",
            binding = true) DbManagementSession dbManagementSession) {

        List<B0501W02ShnDto> resultList =
            b0501w02Service.getShnList(apkihoncd, shopcd, dbManagementSession.getUnyoDate());

        List<CommonPartsListBean> result = getShnPartsList(resultList);

        if (!StringUtils.isEmpty(blank) && "t".equals(blank)) {
            // 先頭にブランクを追加する
            result.add(0, CommonPartsListBean.getBlank());
        }

        // 商品コードをキーとしたMapを作成
        Map<String, B0501W02ShnDto> shnMap =
            resultList.stream().collect(Collectors.toMap(B0501W02ShnDto::getShncd, dto -> dto));

        // 商品プルダウンMapをセッションに退避
        b0501w00session.setShnMap(shnMap);

        return result;
    }

    /**
     * CommonPartsListBeanリスト取得.
     * 
     * @param resultList 商品DTOリスト
     * @return CommonPartsListBeanリスト(商品コード、商品名称略称)
     */
    private List<CommonPartsListBean> getShnPartsList(List<B0501W02ShnDto> resultList) {
        List<CommonPartsListBean> result = new LinkedList<>();
        for (B0501W02ShnDto resultDto : resultList) {
            // 商品コード、商品名称略称を設定
            CommonPartsListBean commonBean =
                new CommonPartsListBean(resultDto.getShncd(), resultDto.getShnnmryakusho());
            result.add(commonBean);
        }
        return result;
    }

    /**
     * 商品の標準価格を取得.
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param shncd 商品コード
     * @return 標準価格、税区分
     */
    @GetMapping("hyojunprice/{apkihoncd}/{shopcd}/{shncd}")
    public List<String> getHyojunprice(@PathVariable("apkihoncd") String apkihoncd,
        @PathVariable("shopcd") String shopcd, @PathVariable("shncd") String shncd) {

        List<String> ret = new ArrayList<>();

        // セッションに保持するMapから商品コードをキーにDTOを取得
        if (!ObjectUtils.isEmpty(b0501w00session.getShnMap())) {
            B0501W02ShnDto dto = b0501w00session.getShnMap().get(shncd);
            ret.add(dto.getHyojunprice()); // 標準価格
            ret.add(dto.getTaxkbn()); // 税区分
        }

        return ret;
    }

    /**
     * 反映元得意先取得処理.
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @param dbManagementSession DB管理セッション
     * @return B0501W02Form
     */
    @GetMapping("haneitokuisaki/{apkihoncd}/{shopcd}")
    public B0501W02Form getHaneimotoTokuisaki(@PathVariable("apkihoncd") String apkihoncd,
        @PathVariable("shopcd") String shopcd,
        @RequestParam(name = "tokuisakicd", required = false) String tokuisakicd,
        @ModelAttribute(name = "dbManagementSession_B0501W02",
            binding = true) DbManagementSession dbManagementSession) {

        // 得意先コードが未入力の場合、エラー
        if (StringUtils.isEmpty(tokuisakicd)) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.haneimototokuisakicd", "反映元得意先コード"), MessageID.W113,
                "反映元得意先コード");
        }

        // 反映元得意先情報取得
        B0501W02DisplayDto dto = b0501w02Service.getHaneimotTokuisaki(apkihoncd, shopcd,
            tokuisakicd, dbManagementSession.getUnyoDate());

        // form内のオブジェクトに設定
        B0501W02Form form = new B0501W02Form();
        form.setBaseBean(controller.getBaseBean(dto, null, null, null, null, null));
        form.setDeliveryOrderBean(controller.getDeliveryOrderBean(dto));

        return form;
    }

    /**
     * 反映元委託相手取得処理.
     * 
     * @param jutakudlvrycstkbn 顧客区分
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @param dbManagementSession DB管理セッション
     * @return B0501W02Form
     */
    @GetMapping("haneiitakuaite/{jutakudlvrycstkbn}")
    public B0501W02Form getHaneimotoItakuaite(
        @PathVariable("jutakudlvrycstkbn") String jutakudlvrycstkbn,
        @RequestParam(name = "apkihoncd", required = false) String apkihoncd,
        @RequestParam(name = "shopcd", required = false) String shopcd,
        @RequestParam(name = "tokuisakicd", required = false) String tokuisakicd,
        @ModelAttribute(name = "dbManagementSession_B0501W02",
            binding = true) DbManagementSession dbManagementSession) {

        // 委託相手AP基本コードか、委託相手ショップコードに値がない場合、エラー
        if (StringUtils.isEmpty(apkihoncd) || StringUtils.isEmpty(shopcd)) {
            List<FormField> fields = new ArrayList<>();
            addField(fields, "baseBean.itakuaiteapkihoncd", "委託相手AP基本コード");
            addField(fields, "baseBean.itakuaiteshopcd", "委託相手ショップコード");
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W011, "ＡＰ・ショップ");
        }

        // 得意先コードが未入力の場合、エラー
        if (StringUtils.isEmpty(tokuisakicd)) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.itakuaitetokuisakicd", "委託相手得意先コード"), MessageID.W113,
                "委託相手得意先コード");
        }

        // 反映元委託相手取得
        B0501W02DisplayDto dto = b0501w02Service.getHaneimotoItakuaite(apkihoncd, shopcd,
            tokuisakicd, jutakudlvrycstkbn, dbManagementSession.getUnyoDate());

        // form内のオブジェクトに設定
        B0501W02Form form = new B0501W02Form();
        form.setBaseBean(controller.getBaseBean(dto, null, null, null, null, null));
        form.setDeliveryOrderBean(controller.getDeliveryOrderBean(dto));

        return form;
    }

    /**
     * 委託相手チェック処理.
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @return 正常終了数値
     */
    @GetMapping("checkitakuaite")
    public int checkItakuaite(@RequestParam(name = "apkihoncd", required = false) String apkihoncd,
        @RequestParam(name = "shopcd", required = false) String shopcd) {

        // 委託相手AP基本コードか、委託相手ショップコードに値がない場合、エラー
        if (StringUtils.isEmpty(apkihoncd) || StringUtils.isEmpty(shopcd)) {
            List<FormField> fields = new ArrayList<>();
            addField(fields, "baseBean.itakuaiteapkihoncd", "委託相手AP基本コード");
            addField(fields, "baseBean.itakuaiteshopcd", "委託相手ショップコード");
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W011, "ＡＰ・ショップ");
        }

        // 委託相手チェック処理
        b0501w02Service.checkItakuaite(apkihoncd, shopcd);

        return 1;
    }

    /**
     * 会員コードチェック処理.
     * 
     * @param kaiincd 会員コード
     * @return 正常終了数値
     */
    @GetMapping("checkkaiin")
    public int checkKaiin(@RequestParam(name = "kaiincd", required = false) String kaiincd) {

        // 会員コードが未入力の場合はエラー
        if (StringUtils.isEmpty(kaiincd)) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.kaiincd", "会員コード"), MessageID.W113, "会員コード");
        }

        // 会員チェック処理
        b0501w02Service.checkKaiin(kaiincd);

        return 1;
    }

    /**
     * 住所取得処理.
     * 
     * @param yubinno 郵便番号
     * @param name フィールド名
     * @param label フィールドラベル
     * @return 住所情報Beanリスト
     */
    @GetMapping("address")
    public List<B0501W02AddressBean> getAddress(
        @RequestParam(name = "yubinno", required = false) String yubinno,
        @RequestParam(name = "name", required = false) String name,
        @RequestParam(name = "label", required = false) String label) {

        List<B0501W02AddressBean> resultList = new LinkedList<>();

        // 郵便番号が未入力の場合、エラー
        if (StringUtils.isEmpty(yubinno)) {
            throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                MessageID.W113, label);
        }

        // 郵便番号が数字でない場合、エラー
        if (!yubinno.matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS)) {
            throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                MessageID.W013, label, "0～9のみ");
        }

        // 郵便番号管理マスタを取得
        List<B0501W02AddressDto> dtoList = b0501w02Service.getAddressList(yubinno);

        // 郵便番号管理マスタに存在しないコード値はエラー
        if (ObjectUtils.isEmpty(dtoList) || dtoList.size() == 0) {
            throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                MessageID.W084, label);
        }

        // 有効区分＝１(有効)以外の場合、エラー
        for (B0501W02AddressDto dto : dtoList) {
            if (!CommonConstants.YUKOKBN_1.equals(dto.getYukokbn())) {
                throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                    MessageID.W123, label);
            }
        }

        // Beanに格納
        dtoList.forEach(d -> resultList.add(createAddressBean(d)));

        return resultList;
    }

    /**
     * 住所情報Beanを作成する
     *
     * @param d 得意先マスタ(登録画面)住所DTO
     * @return 住所情報Bean
     */
    private B0501W02AddressBean createAddressBean(B0501W02AddressDto d) {

        B0501W02AddressBean bean = new B0501W02AddressBean(d.getChihoupubdanticdn1(), // 地方公共団体コード１
            d.getChihoupubdanticdn2(), // 地方公共団体コード２
            d.getYubinno(), // 郵便番号
            d.getTdoufknmei(), // 都道府県名
            d.getShikutyosonmei(), // 市区町村名
            d.getTyoikimei() // 町域名
        );

        return bean;
    }

    /**
     * 得意先名称取得処理.
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @return 得意先名称
     */
    @GetMapping("tokuisakinm/{apkihoncd}/{shopcd}/{tokuisakicd}")
    public String getTokuisakinm(@PathVariable("apkihoncd") String apkihoncd,
        @PathVariable("shopcd") String shopcd, @PathVariable("tokuisakicd") String tokuisakicd) {

        S02M013TDto dto = new S02M013TDto();
        dto.setApkihoncd(apkihoncd);
        dto.setShopcd(shopcd);
        dto.setTokuisakicd(tokuisakicd);

        // 戻り値初期化
        String ret = "";

        // サービス実行
        S02M013TDto result = s02m013tService.searchPrimary(dto);

        // 有効区分＝1の場合
        if (!Objects.isNull(result) && CommonConstants.YUKOKBN_1.equals(result.getYukokbn())) {
            ret = result.getTokuisakinmn1();
        }

        return ret;
    }

    /**
     * ショップ名称取得処理.
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param shopkbn ショップ区分
     * @return ショップ名称
     */
    @GetMapping("shop/{apkihoncd}/{shopcd}")
    public String getShopNmSearch(@PathVariable("apkihoncd") String apkihoncd,
        @PathVariable("shopcd") String shopcd,
        @RequestParam(name = "shopkbn", required = false) String shopkbn) {

        // 戻り値初期化
        String ret = "";

        // サービス実行
        S02M046TDto result = s02m046tService.getByPrimaryKey(apkihoncd, shopcd, true);

        if (!Objects.isNull(result)) {
            if (StringUtils.isEmpty(shopkbn)) {
                ret = result.getShopnm();

                // ショップ区分で絞る場合
            } else if (shopkbn.equals(result.getShopkbn())) {
                ret = result.getShopnm();
            }
        }

        return ret;
    }

    /**
     * 倉庫名称取得処理.
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param sokocd 倉庫コード
     * @return 倉庫名称
     */
    @GetMapping("soko/{apkihoncd}/{shopcd}/{sokocd}")
    public String getSokoNmSearch(@PathVariable("apkihoncd") String apkihoncd,
        @PathVariable("shopcd") String shopcd, @PathVariable("sokocd") String sokocd) {

        // 戻り値初期化
        String ret = "";

        // サービス実行
        S02M041TDto result = s02m041tService.getByPrimaryKey(apkihoncd, shopcd, sokocd, true);

        // 有効区分＝1の場合
        if (!Objects.isNull(result) && CommonConstants.YUKOKBN_1.equals(result.getYukokbn())) {
            ret = result.getSokonmn1();
        }

        return ret;
    }

    /**
     * 担当者名取得処理.
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param tnshacd 担当者コード
     * @return 担当者名
     */
    @GetMapping("tnsha/{apkihoncd}/{shopcd}/{tnshacd}")
    public String getTnshanm(@PathVariable("apkihoncd") String apkihoncd,
        @PathVariable("shopcd") String shopcd, @PathVariable("tnshacd") String tnshacd) {

        S02M003TDto dto = new S02M003TDto();
        dto.setApkihoncd(apkihoncd);
        dto.setShopcd(shopcd);
        dto.setTnshacd(tnshacd);

        // 戻り値初期化
        String ret = "";

        // サービス実行
        S02M003TDto result = s02m003tService.searchPrimary(dto);

        // 有効区分＝1の場合
        if (!ObjectUtils.isEmpty(result) && CommonConstants.YUKOKBN_1.equals(result.getYukokbn())) {
            ret = result.getTnshamei();
        }

        return ret;
    }

    /**
     * 請求先名称取得処理.
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param skysakicd 請求先コード
     * @return 請求先名称,親得意先コード
     */
    @GetMapping("skysaki/{apkihoncd}/{shopcd}/{skysakicd}")
    public List<String> getSkysakiNmSearch(@PathVariable("apkihoncd") String apkihoncd,
        @PathVariable("shopcd") String shopcd, @PathVariable("skysakicd") String skysakicd) {

        List<String> resultList = new ArrayList<>();

        // サービス実行
        S02M017TDto result =
            b0501w02Service.searchSkysakiByPrimaryKey(apkihoncd, shopcd, skysakicd);

        // 有効区分＝1の場合
        if (!Objects.isNull(result) && CommonConstants.YUKOKBN_1.equals(result.getYukokbn())) {
            resultList.add(result.getSkysakinmn1()); // 請求先名称
            resultList.add(result.getOyatokuisakicd()); // 親得意先コード
        }

        return resultList;
    }

    /**
     * 営業エリア名称取得処理.
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param eigyoareacd 営業エリアコード
     * @return 営業エリア名称
     */
    @GetMapping("eigyoarea/{apkihoncd}/{shopcd}/{eigyoareacd}")
    public String getEigyoareaNmSearch(@PathVariable("apkihoncd") String apkihoncd,
        @PathVariable("shopcd") String shopcd, @PathVariable("eigyoareacd") String eigyoareacd) {

        S02M020TDto dto = new S02M020TDto();
        dto.setApkihoncd(apkihoncd);
        dto.setShopcd(shopcd);
        dto.setEigyoareacd(eigyoareacd);

        // 戻り値初期化
        String ret = "";

        // サービス実行
        S02M020TDto result = s02m020tService.searchPrimary(dto);

        // 有効区分＝1の場合
        if (!Objects.isNull(result) && CommonConstants.YUKOKBN_1.equals(result.getYukokbn())) {
            ret = result.getEigyoareanm();
        }

        return ret;
    }

    /**
     * AP名称取得処理.
     * 
     * @param apkihoncd ＡＰ基本コード
     * @return AP名称
     */
    @GetMapping("ap/{apkihoncd}")
    public String getApNmSearch(@PathVariable("apkihoncd") String apkihoncd) {

        S02M001TDto dto = new S02M001TDto();
        dto.setApkihoncd(apkihoncd);

        // 戻り値初期化
        String ret = "";

        // サービス実行
        S02M001TDto result = s02m001tService.searchPrimary(dto);

        // 有効区分＝1の場合
        if (!Objects.isNull(result) && CommonConstants.YUKOKBN_1.equals(result.getYukokbn())) {
            ret = result.getApnm();
        }

        return ret;
    }

    /**
     * エージェント名称取得処理.
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param ejntcd エージェントコード
     * @param honbuflg 本部判定フラグ
     * @return エージェント名称
     */
    @GetMapping("ejnt/{apkihoncd}/{shopcd}/{ejntcd}")
    public List<String> getEjntNmSearch(@PathVariable("apkihoncd") String apkihoncd,
        @PathVariable("shopcd") String shopcd, @PathVariable("ejntcd") String ejntcd,
        @RequestParam(name = "honbuflg", required = false) String honbuflg) {

        List<String> resultList = new ArrayList<>();

        S02M014TDto key = new S02M014TDto();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setEjntcd(ejntcd);

        // サービス実行
        S02M014TDto result = s02m014tService.searchPrimary(key);

        // 有効区分＝1の場合
        if (!Objects.isNull(result) && CommonConstants.YUKOKBN_1.equals(result.getYukokbn())) {
            resultList.add(result.getEjntmein1()); // エージェント名称
            resultList.add(result.getShanaitnshacd()); // 社内担当者コード
            // 獲得エージェントコード名取得の場合は社内担当者を追加で取得する
            if (honbuflg.equals("f")) {
                S02M003TDto dto = new S02M003TDto();
                dto.setApkihoncd(apkihoncd);
                dto.setShopcd(shopcd);
                dto.setTnshacd(result.getShanaitnshacd());

                // サービス実行
                S02M003TDto tnshaDto = s02m003tService.searchPrimary(dto);

                resultList.add(!ObjectUtils.isEmpty(tnshaDto) ? tnshaDto.getTnshamei() : "");
            }
        }

        return resultList;
    }

    /**
     * キャンペーン名称取得処理.
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param kynpncd キャンペーンコード
     * @return キャンペーン名称
     */
    @GetMapping("kynpn/{apkihoncd}/{shopcd}/{kynpncd}")
    public String getKynpnNmSearch(@PathVariable("apkihoncd") String apkihoncd,
        @PathVariable("shopcd") String shopcd, @PathVariable("kynpncd") String kynpncd) {

        S02M059TDto dto = new S02M059TDto();
        dto.setApkihoncd(apkihoncd);
        dto.setShopcd(shopcd);
        dto.setKynpncd(kynpncd);

        // 戻り値初期化
        String ret = "";

        // サービス実行
        S02M059TDto result = s02m059tService.searchPrimary(dto);

        // 有効区分＝1、かつキャンペーン区分≠3(CS)の場合
        if (!Objects.isNull(result) && CommonConstants.YUKOKBN_1.equals(result.getYukokbn())
            && !CommonConstants.KYNPNKBN_3.equals(result.getKynpnkbn())) {
            ret = result.getKynpnnm();
        }

        return ret;
    }

    /**
     * 配達会社名称取得処理.
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param dlvrycmpnycd 配達会社コード
     * @return 配達会社名称
     */
    @GetMapping("dlvrycmpny/{apkihoncd}/{shopcd}/{dlvrycmpnycd}")
    public String getDlvrycmpnyNmSearch(@PathVariable("apkihoncd") String apkihoncd,
        @PathVariable("shopcd") String shopcd, @PathVariable("dlvrycmpnycd") String dlvrycmpnycd) {

        S02M019TDto dto = new S02M019TDto();
        dto.setApkihoncd(apkihoncd);
        dto.setShopcd(shopcd);
        dto.setDlvrycmpnycd(dlvrycmpnycd);

        // 戻り値初期化
        String ret = "";

        // サービス実行
        S02M019TDto result = s02m019tService.searchPrimary(dto);

        // 有効区分＝1の場合
        if (!Objects.isNull(result) && CommonConstants.YUKOKBN_1.equals(result.getYukokbn())) {
            ret = result.getDlvrycmpnynm();
        }

        return ret;
    }

    /**
     * 販促名称取得処理.
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param hansokushscd 販促詳細コード
     * @return 販促名称
     */
    @GetMapping("hansokushs/{apkihoncd}/{shopcd}/{hansokushscd}")
    public String getHansokushsNmSearch(@PathVariable("apkihoncd") String apkihoncd,
        @PathVariable("shopcd") String shopcd, @PathVariable("hansokushscd") String hansokushscd) {

        S02M027TDto dto = new S02M027TDto();
        dto.setApkihoncd(apkihoncd);
        dto.setShopcd(shopcd);
        dto.setHansokushscd(hansokushscd);

        // 戻り値初期化
        String ret = "";

        // サービス実行
        S02M027TDto result = s02m027tService.searchPrimary(dto);

        // 有効区分＝1の場合
        if (!Objects.isNull(result) && CommonConstants.YUKOKBN_1.equals(result.getYukokbn())) {
            ret = result.getHansokunm();
        }

        return ret;
    }

    /**
     * 配達エリア名称取得処理.
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param dlvrykyotencd 配達拠点コード
     * @param dlvryareacd 配達エリアコード
     * @param dbManagementSession DB管理セッション
     * @return 配達エリア名称
     */
    @GetMapping("dlvryarea/{apkihoncd}/{shopcd}/{dlvrykyotencd}/{dlvryareacd}")
    public String getDlvryareaNmSearch(@PathVariable("apkihoncd") String apkihoncd,
        @PathVariable("shopcd") String shopcd, @PathVariable("dlvrykyotencd") String dlvrykyotencd,
        @PathVariable("dlvryareacd") String dlvryareacd,
        @ModelAttribute(name = "dbManagementSession_B0501W02",
            binding = true) DbManagementSession dbManagementSession) {

        // 戻り値初期化
        String ret = "";

        // サービス実行
        S02M018TDto result = b0501w02Service.searchDlvryAreaByUnyodd(apkihoncd, shopcd,
            dlvrykyotencd, dlvryareacd, dbManagementSession.getUnyoDate());

        // 有効区分＝1の場合
        if (!Objects.isNull(result) && CommonConstants.YUKOKBN_1.equals(result.getYukokbn())) {
            ret = result.getDlvryareanm();
        }

        return ret;
    }

    /**
     * 配達拠点名称取得処理.
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param dlvrykyotencd 配達拠点コード
     * @return 配達拠点名称
     */
    @GetMapping("dlvrykyoten/{apkihoncd}/{shopcd}/{dlvrykyotencd}")
    public String getDlvryKyotenNmSearch(@PathVariable("apkihoncd") String apkihoncd,
        @PathVariable("shopcd") String shopcd,
        @PathVariable("dlvrykyotencd") String dlvrykyotencd) {

        S02M031TDto dto = new S02M031TDto();
        dto.setApkihoncd(apkihoncd);
        dto.setShopcd(shopcd);
        dto.setDlvrykyotencd(dlvrykyotencd);

        // 戻り値初期化
        String ret = "";

        // サービス実行
        S02M031TDto result = s02m031tService.searchPrimary(dto);

        // 有効区分＝1の場合
        if (!Objects.isNull(result) && CommonConstants.YUKOKBN_1.equals(result.getYukokbn())) {
            ret = result.getDlvrykyotennm();
        }

        return ret;
    }

    /**
     * マージン名称取得処理.
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param mgncd マージンコード
     * @return マージン名称
     */
    @GetMapping("mgncd/{apkihoncd}/{shopcd}/{mgncd}")
    public String getMgnNmSearch(@PathVariable("apkihoncd") String apkihoncd,
        @PathVariable("shopcd") String shopcd, @PathVariable("mgncd") String mgncd) {

        // 戻り値初期化
        String ret = "";

        // サービス実行
        S02M015TDto result = b0501w02Service.searchMgnByPrimaryKey(apkihoncd, shopcd, mgncd,
            CommonConstants.KBN_MARGINTARGET_ONESHOT);

        // 有効区分＝1の場合
        if (!Objects.isNull(result) && CommonConstants.YUKOKBN_1.equals(result.getYukokbn())) {
            ret = result.getMgnnm();
        }

        return ret;
    }

    /**
     * 得意先違約金金額取得処理.
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @param dbManagementSession DB管理セッション
     * @return 違約金金額
     */
    @GetMapping("iyakukin/{apkihoncd}/{shopcd}/{tokuisakicd}")
    public long getIyakukinSearch(@PathVariable("apkihoncd") String apkihoncd,
        @PathVariable("shopcd") String shopcd, @PathVariable("tokuisakicd") String tokuisakicd,
        @ModelAttribute(name = "dbManagementSession_B0501W02",
            binding = true) DbManagementSession dbManagementSession) {

        // 得意先違約金集計値を取得する
        return b0501w02Service.getIyakukinSummary(apkihoncd, shopcd, tokuisakicd,
            dbManagementSession.getUnyoDate());
    }

    /**
     * フォーム情報を取得.
     * 
     * @return B0501W02Form
     */
    @GetMapping("form")
    public B0501W02Form getFormInfo() {
        return b0501w00session.getForm();
    }

    /**
     * セッションクリア処理
     * 
     * @return
     */
    @PutMapping("/clearsession")
    public AciRestResponse<Void> clearSession() {

        // セッションで保持する情報をクリアする
        b0501w00session.setShnMap(null);
        b0501w00session.setForm(null);

        // 更新結果の返却
        AciRestResponse<Void> res = new AciRestResponse<>(null);

        return res;
    }
}
