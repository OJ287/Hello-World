package jp.co.aquaclara.accis.web.controller.a0301;

import java.util.ArrayList;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.constants.SystemConstants;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.web.controller.a0301.bean.A0301W02BaseBean;
import jp.co.aquaclara.accis.web.controller.a0301.bean.A0301W02BaseBean.MODE;
import jp.co.aquaclara.accis.web.controller.a0301.bean.A0301W02BaseTabBean;
import jp.co.aquaclara.accis.web.controller.a0301.bean.A0301W02SetMeisaiBean;
import jp.co.aquaclara.accis.web.controller.a0301.bean.A0301W02ShopTabBean;
import jp.co.aquaclara.accis.web.controller.a0301.form.A0301W02Form;
import jp.co.aquaclara.accis.web.controller.a0301.session.A0301W02Session;
import jp.co.aquaclara.accis.web.controller.commons.AccisWebBaseController;
import jp.co.aquaclara.accis.web.controller.commons.DbManagementSession;
import jp.co.aquaclara.accis.web.controller.commons.MenuAuthSession;
import jp.co.aquaclara.accis.web.controller.commons.UserSessionForm;
import jp.co.aquaclara.accis.web.domain.dto.a0301.A0301W02DisplayDto;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M046TDto;
import jp.co.aquaclara.accis.web.domain.service.a0301.A0301W02Service;
import jp.co.aquaclara.accis.web.domain.service.master.S02M046TService;
import lombok.extern.slf4j.Slf4j;

/**
 * A0301W02_商品マスタ(登録画面)コントローラクラス.
 */
@Controller
@RequestMapping("/a0301w02")
@Slf4j
public class A0301W02Controller extends AccisWebBaseController {

    private static final String VIEW_NAME = "a0301/a0301w02_form";

    /* 担当メニューマスタ(S02M045T)の使用権限レベル と DB管理項目を追加 */
    @ModelAttribute("menuAuthSession_A0301W02")
    public MenuAuthSession setMenuAuthSession() {
        return new MenuAuthSession();
    }

    @ModelAttribute("dbManagementSession_A0301W02")
    public DbManagementSession setDbManagementSession() {
        return new DbManagementSession();
    }

    /**
     * 商品マスタ(登録画面)サービスクラス
     */
    @Autowired
    private A0301W02Service a0301w02Service;

    /**
     * 商品マスタサービスクラス
     */
    @Autowired
    S02M046TService s02m046tService;

    /**
     * ユーザーセッション
     */
    @Autowired
    private UserSessionForm userSession;

    /**
     * 商品マスタ（登録）セッション情報
     */
    @Autowired
    private A0301W02Session a0301w02session;

    /**
     * 初期表示（登録）
     * 
     * @param form
     * @param menuAuthSession
     * @param apkihoncd
     * @param shopcd
     * @param pageable
     * @param mv
     * @return
     */
    @GetMapping("/{apkihoncd}/{shopcd}")
    public ModelAndView initRegist(@ModelAttribute("a0301W02Form") A0301W02Form form,
        @ModelAttribute(name = "menuAuthSession_A0301W02",
            binding = false) MenuAuthSession menuAuthSession,
        @ModelAttribute(name = "dbManagementSession_A0301W02",
            binding = false) DbManagementSession dbManagementSession,
        @PathVariable("apkihoncd") String apkihoncd, @PathVariable("shopcd") String shopcd,
        @PageableDefault(size = 999) Pageable pageable, ModelAndView mv) {

        String authflg = "t";
        String menuAuth = menuAuthSession.getMenuAuth();
        if (menuAuth != null && menuAuth.equals("ROLE_MENU_AUTH_UPDATE")) {
            authflg = "f";
        }

        initForm(form, apkihoncd, shopcd, authflg, dbManagementSession.getUnyoDate());
        initMeisai(mv, pageable);

        // 表示画面設定
        mv.setViewName(VIEW_NAME);

        return mv;
    }

    /**
     * 初期表示（更新）
     * 
     * @param form
     * @param menuAuthSession
     * @param dbManagementSession
     * @param apkihoncd
     * @param shopcd
     * @param shncd
     * @param pageable
     * @param mv
     * @return
     */
    @GetMapping("/{apkihoncd}/{shopcd}/{shncd}")
    public ModelAndView initUpdate(@ModelAttribute("a0301W02Form") A0301W02Form form,
        @ModelAttribute(name = "menuAuthSession_A0301W02",
            binding = false) MenuAuthSession menuAuthSession,
        @ModelAttribute(name = "dbManagementSession_A0301W02",
            binding = false) DbManagementSession dbManagementSession,
        @PathVariable("apkihoncd") String apkihoncd, @PathVariable("shopcd") String shopcd,
        @PathVariable("shncd") String shncd, @PageableDefault(size = 999) Pageable pageable,
        ModelAndView mv) {

        String unyodate = dbManagementSession.getUnyoDate();

        String authflg = "t";
        String menuAuth = menuAuthSession.getMenuAuth();
        if (menuAuth != null && menuAuth.equals("ROLE_MENU_AUTH_UPDATE")) {
            authflg = "f";
        }

        initForm(form, apkihoncd, shopcd, shncd, unyodate, authflg);
        initMeisai(mv, pageable);

        // 表示画面設定
        mv.setViewName(VIEW_NAME);
        return mv;
    }

    /**
     * アクセス範囲区分と商品属性の組み合わせに応じてタブ入力可否を判定する
     * 
     * @param akshanikbn 選択されたAP/ショップのアクセス範囲区分
     * @param shnzokusei 商品属性
     * @return 入力不可の場合、true
     */
    private boolean isDisabledTab(String akshanikbn, String shnzokusei) {
        log.debug("ログインユーザのアクセス範囲区分 : {}", userSession.getAkshanikbn());
        log.debug("選択されたAP/ショップのアクセス範囲区分 : {}", akshanikbn);
        log.debug("商品属性 : {}", shnzokusei);
        // 基本情報タブ、セット商品情報タブの入力可否判定
        switch (userSession.getAkshanikbn()) {
            case CommonConstants.AKSHANIKBN_ACI:
                // ログイン担当者アクセス範囲区分：ACI
                if (CommonConstants.AKSHANIKBN_SHOP.equals(akshanikbn)) {
                    // 選択されたAP/ショップがショップの場合 … 入力不可
                    return true;
                } else if (CommonConstants.AKSHANIKBN_AP.equals(akshanikbn)) {
                    // 選択されたAP/ショップがAPの場合
                    if (CommonConstants.KBN_SHNZOKUSEI_HONBU.equals(shnzokusei)) {
                        // 商品属性が本部商品 … 入力不可
                        return true;
                    }
                }
                break;
            case CommonConstants.AKSHANIKBN_AP:
                // ログイン担当者アクセス範囲区分：AP
                if (!CommonConstants.AKSHANIKBN_AP.equals(akshanikbn)
                    || !CommonConstants.KBN_SHNZOKUSEI_AP.equals(shnzokusei)) {
                    // 選択されたAP/ショップがAP以外または商品属性がAPではない … 入力不可
                    return true;
                }
                break;
            case CommonConstants.AKSHANIKBN_SHOP:
                // ログイン担当者アクセス範囲区分：ショップ … 入力不可
                return true;
        }
        return false;
    }

    /**
     * formの初期化（登録）
     * 
     * @param form
     * @param apkihoncd
     * @param shopcd
     * @param authflg
     */
    private void initForm(A0301W02Form form, String apkihoncd, String shopcd, String authflg,
        String unyoDate) {

        // BaseBean 初期化
        A0301W02BaseBean baseBean = new A0301W02BaseBean();
        baseBean.setApkihoncd(apkihoncd);
        baseBean.setShopcd(shopcd);
        baseBean.setAuthflag(authflg);
        baseBean.setSyoristatus(MODE.INSERT.getValue());
        baseBean.setOpedate(DateTimeUtil.toSlashedDate(unyoDate));
        baseBean.setStkbn(CommonConstants.STKBN_0);

        String cdkensakuflg =
            (CommonConstants.APCD_001.equals(baseBean.getApkihoncd())) ? "2" : "3";
        baseBean.setCdkensakuflg(cdkensakuflg);

        S02M046TDto conditionDto = new S02M046TDto();
        conditionDto.setApkihoncd(apkihoncd);
        conditionDto.setShopcd(shopcd);
        S02M046TDto searchResultDto = s02m046tService.getByPrimaryKey(apkihoncd, shopcd, true);
        baseBean.setUrikinhasukeisankbn(searchResultDto.getUrikinhasukeisankbn());
        // 売上端数管理区分をセッションに保持する
        a0301w02session.setUrikinhasukeisankbn(searchResultDto.getUrikinhasukeisankbn());

        // 選択されたAP/ショップに関するアクセス範囲区分を取得
        String akshanikbn = a0301w02Service.getApShopAkshanikbn(apkihoncd, shopcd);
        // 商品属性を決定する
        String shnzokusei =
            // 新規モードの場合、選択されたAP/ショップがACIかどうか
            CommonConstants.AKSHANIKBN_ACI.equals(akshanikbn)
                // ACIなら本部商品
                ? CommonConstants.KBN_SHNZOKUSEI_HONBU
                // それ以外はAP商品
                : CommonConstants.KBN_SHNZOKUSEI_AP;
        baseBean.setShnzokusei(shnzokusei);
        baseBean.setShnzokuseinm(
            CommonConstants.KBN_SHNZOKUSEI_HONBU.equals(shnzokusei) ? "本部商品" : "AP商品");
        // 基本情報タブ、セット商品情報タブの入力可否判定
        baseBean.setDisabledTabItem(isDisabledTab(akshanikbn, shnzokusei));

        form.setBaseBean(baseBean);

        // BaseTabBean 初期化
        A0301W02BaseTabBean baseTabBean = new A0301W02BaseTabBean();
        baseTabBean.setShinpricekbn(CommonConstants.KBN_PRICE_HYOJUN);
        baseTabBean.setGenzpricekbn(CommonConstants.KBN_PRICE_HYOJUN);
        baseTabBean.setTrtskikbn("1");
        baseTabBean.setSuryohasukanrikbn(CommonConstants.SURYOHASUKANRIKBN_0);
        baseTabBean.setShnkbn2("1");
        baseTabBean.setKihonshnyukokbn(CommonConstants.YUKOKBN_1);
        form.setBaseTabBean(baseTabBean);

        // ShopTabBean 初期化
        A0301W02ShopTabBean shopTabBean = new A0301W02ShopTabBean();
        shopTabBean.setMgntrgtkbn("1");
        shopTabBean.setShnkbn("1");
        shopTabBean.setMobileriyoumu("0");
        shopTabBean.setTaxsetteikbn("1");
        shopTabBean.setShinshnpointhasseikbn(CommonConstants.SHNPOINTHASSEIKBN_VAL_0);
        shopTabBean.setGenshnpointhasseikbn(CommonConstants.SHNPOINTHASSEIKBN_VAL_0);
        shopTabBean.setShinmtmkipointkbn(CommonConstants.MTMKIPOINTKBNMEI_VAL_0);
        shopTabBean.setGenmtmkipointkbn(CommonConstants.MTMKIPOINTKBNMEI_VAL_0);
        shopTabBean.setHansokusettei("0");
        shopTabBean.setHansokuyusenkbn("1");
        shopTabBean.setZaikokanrikbn(CommonConstants.ZAIKOKANRITRGTFLG_1);
        shopTabBean.setGenkakeisankbn("1");
        shopTabBean.setSiiretanihenkankbn("0");
        shopTabBean.setYukokbn(CommonConstants.YUKOKBN_1);
        form.setShopTabBean(shopTabBean);
    }

    /**
     * formの初期化（更新）
     * 
     * @param form
     * @param apkihoncd
     * @param shopcd
     * @param shncd
     * @param unyodate
     * @param authflg
     */
    private void initForm(A0301W02Form form, String apkihoncd, String shopcd, String shncd,
        String unyodate, String authflg ) {

        A0301W02DisplayDto resultDto = a0301w02Service.init(apkihoncd, shopcd, shncd, unyodate);
        String shnkbnForBaseTab = a0301w02Service.getBaseShnkbn(resultDto.getShnapkihoncd(), shncd);

        // BaseBean 初期化
        A0301W02BaseBean baseBean = new A0301W02BaseBean();
        baseBean.setSyoristatus("2");
        baseBean.setAuthflag(authflg);
        baseBean.setApkihoncd(resultDto.getApkihoncd());
        baseBean.setShopcd(resultDto.getShopcd());
        baseBean.setShncd(resultDto.getShncd());
        baseBean.setShnnm(resultDto.getShnnm());
        baseBean.setTrtskikbnnm(resultDto.getTrtskikbnnm());
        baseBean.setStkbn(resultDto.getStkbn());
        baseBean.setStkbnhidden(resultDto.getStkbn());
        baseBean.setShnzokusei(resultDto.getShnzokusei());
        baseBean.setShnzokuseinm(resultDto.getShnzokuseinm());
        baseBean.setPricekbn(resultDto.getPricekbn());
        baseBean.setPricekbnnm(resultDto.getPricekbnnm());

        String cdkensakuflg =
            (CommonConstants.APCD_001.equals(baseBean.getApkihoncd())) ? "2" : "3";
        baseBean.setCdkensakuflg(cdkensakuflg);

        S02M046TDto conditionDto = new S02M046TDto();
        conditionDto.setApkihoncd(apkihoncd);
        conditionDto.setShopcd(shopcd);
        S02M046TDto searchResultDto = s02m046tService.getByPrimaryKey(apkihoncd, shopcd, true);
        baseBean.setUrikinhasukeisankbn(searchResultDto.getUrikinhasukeisankbn());
        // 売上端数管理区分をセッションに保持する
        a0301w02session.setUrikinhasukeisankbn(searchResultDto.getUrikinhasukeisankbn());

        // 得意先別セット商品単価マスタに存在する商品の場合はボタン利用可能フラグをOFFにする
        long count = a0301w02Service.getStSpecialUntCount(shncd);
        if (count > 0) {
            baseBean.setBtnusableflg(CommonConstants.FLG_OFF);
        }

        // 選択されたAP/ショップに関するアクセス範囲区分を取得
        String akshanikbn = a0301w02Service.getApShopAkshanikbn(apkihoncd, shopcd);
        // 基本情報タブ、セット商品情報タブの入力可否判定
        baseBean.setDisabledTabItem(isDisabledTab(akshanikbn, resultDto.getShnzokusei()));

        form.setBaseBean(baseBean);

        // BaseTabBean 初期化
        A0301W02BaseTabBean baseTabBean = new A0301W02BaseTabBean();
        baseTabBean.setLctgrycd(resultDto.getLctgrycd());
        baseTabBean.setMctgrycd(resultDto.getMctgrycd());
        baseTabBean.setSctgrycd(resultDto.getSctgrycd());
        baseTabBean.setLctgrynm(resultDto.getLctgrynm());
        baseTabBean.setMctgrynm(resultDto.getMctgrynm());
        baseTabBean.setSctgrynm(resultDto.getSctgrynm());
        baseTabBean.setShinshnnm(resultDto.getShinshnnm());
        baseTabBean.setShinshnnmkana(resultDto.getShinshnnmkana());
        baseTabBean.setShinshnnmryakusho(resultDto.getShinshnnmryakusho());
        baseTabBean.setShnnmkirikaedd(resultDto.getShnnmkirikaedd());
        baseTabBean.setInitshnnmkirikaedd(resultDto.getShnnmkirikaedd());
        baseTabBean.setGenzshnnm(resultDto.getGenzshnnm());
        baseTabBean.setGenzshnnmkana(resultDto.getGenzshnnmkana());
        baseTabBean.setGenzshnnmryakusho(resultDto.getGenzshnnmryakusho());
        baseTabBean.setShinhyojundlvryprice(ItemUtil.editComma(resultDto.getShinhyojunprice()));
        baseTabBean
            .setShinhyojunmochikaeriprice(ItemUtil.editComma(resultDto.getShinmochikaeriprice()));
        baseTabBean.setShinpricekbn(resultDto.getShinpricekbn());
        baseTabBean.setHyojunpricekirikaedd(resultDto.getHyojunpricekirikaedd());
        baseTabBean.setInithyojunpricekirikaedd(resultDto.getHyojunpricekirikaedd());
        baseTabBean.setGenzhyojundlvryprice(ItemUtil.editComma(resultDto.getGenzhyojunprice()));
        baseTabBean
            .setGenzhyojunmochikaeriprice(ItemUtil.editComma(resultDto.getGenzmochikaeriprice()));
        baseTabBean.setGenzpricekbn(resultDto.getGenzpricekbn());
        baseTabBean.setSz(resultDto.getSz());
        baseTabBean.setIro(resultDto.getIro());
        baseTabBean.setTrtskikbn(resultDto.getTrtskikbn());
        baseTabBean.setSuryohasukanrikbn(resultDto.getSuryohasukanrikbn());
        if (Objects.nonNull(shnkbnForBaseTab)) {
            // 基本情報タブ.商品区分は商品マスタビューではなく基本商品マスタの商品区分を設定する
            baseTabBean.setShnkbn2(shnkbnForBaseTab);
        }
        baseTabBean.setBiko1(resultDto.getBiko1());
        baseTabBean.setKihonshnyukokbn(resultDto.getKihonshnyukokbn());
        baseTabBean.setKihonshnyukokbn0(resultDto.getKihonshnyukokbn());
        baseTabBean.setKihonshninptsystmymd(resultDto.getKihonshninptsystmymd());
        baseTabBean.setKihonshninptsystmjikan(resultDto.getKihonshninptsystmjikan());
        baseTabBean.setKihonshntoshaapkihoncd(resultDto.getKihonshntoshaapkihoncd());
        baseTabBean.setKihonshntoshashopcd(resultDto.getKihonshntoshashopcd());
        baseTabBean.setKihonshntoshacd(resultDto.getKihonshntoshacd());
        baseTabBean.setKihonshntoshamei(resultDto.getKihonshntoshanm());
        baseTabBean.setKihonshnkshnsystmymd(resultDto.getKihonshnkshnsystmymd());
        baseTabBean.setKihonshnkshnsystmjikan(resultDto.getKihonshnkshnsystmjikan());
        baseTabBean.setKihonshnkoshaapkihoncd(resultDto.getKihonshnkoshaapkihoncd());
        baseTabBean.setKihonshnkoshashopcd(resultDto.getKihonshnkoshashopcd());
        baseTabBean.setKihonshnkoshacd(resultDto.getKihonshnkoshacd());
        baseTabBean.setKihonshnkoshamei(resultDto.getKihonshnkoshanm());

        form.setBaseTabBean(baseTabBean);

        // ShopTabBean 初期化
        A0301W02ShopTabBean shopTabBean = new A0301W02ShopTabBean();
        shopTabBean.setShnapkihoncd(resultDto.getShnapkihoncd());
        shopTabBean.setHanbaidkikanstr(DateTimeUtil.toSlashedDate(resultDto.getHanbaistrtdd()));
        shopTabBean.setHanbaidkikanend(DateTimeUtil.toSlashedDate(resultDto.getHanbaishrydd()));
        shopTabBean.setGaiburenkeishncd(resultDto.getGaiburenkeishncd());
        shopTabBean.setHonbuhanbaikanrishncd(resultDto.getHonbuhanbaikanrishncd());
        shopTabBean.setMgntrgtkbn(resultDto.getMgntrgtkbn());
        shopTabBean.setShnkbn(resultDto.getShnkbn());
        shopTabBean.setMobileriyoumu(resultDto.getMobileriyoumu());
        shopTabBean.setTaxsetteikbn(resultDto.getTaxsetteikbn());
        shopTabBean.setTaxkbn(resultDto.getTaxkbn());
        shopTabBean.setSiiretrtskitani(resultDto.getSiiretrtskitani());
        shopTabBean.setSiireirisu(resultDto.getSiireirisu());
        shopTabBean.setIrisutani1(resultDto.getSiiretani());
        shopTabBean.setSiiretaninm(resultDto.getSiiretaninm());
        shopTabBean.setSiireunt(ItemUtil.editComma(resultDto.getSiireunt()));
        shopTabBean.setHacchurottokazu(resultDto.getHacchurottokazu());
        shopTabBean.setUritrtskitani(resultDto.getUritrtskitani());
        shopTabBean.setUriirisu(resultDto.getUriirisu());
        shopTabBean.setIrisutani2(resultDto.getUritani());
        shopTabBean.setShindlvryprice1(ItemUtil.editComma(resultDto.getShinopnprice()));
        shopTabBean
            .setShinmochikaeriprice2(ItemUtil.editComma(resultDto.getShinopnmochikaeriprice()));
        shopTabBean.setOpnpricekirikaedd(resultDto.getOpnpricekirikaedd());
        shopTabBean.setInitopnpricekirikaedd(resultDto.getOpnpricekirikaedd());
        shopTabBean.setGendlvryprice2(ItemUtil.editComma(resultDto.getGenzopnprice()));
        shopTabBean
            .setGenzmochikaeriprice(ItemUtil.editComma(resultDto.getGenzopnmochikaeriprice()));
        shopTabBean.setHyojundlvryprice(ItemUtil.editComma(resultDto.getHyojundlvryprice()));
        shopTabBean
            .setHyojunmochikaeriprice(ItemUtil.editComma(resultDto.getHyojunmochikaeriprice()));
        shopTabBean.setShinshnpointhasseikbn(resultDto.getShinshnpointhasseikbn());
        shopTabBean.setShinshnpointsu1(ItemUtil.editComma(resultDto.getShinshnpointsu1()));
        shopTabBean.setShinshnpointsu2(ItemUtil.editComma(resultDto.getShinshnpointsu2()));
        shopTabBean.setGenshnpointhasseikbn(resultDto.getGenshnpointhasseikbn());
        shopTabBean.setGenshnpointsu1(ItemUtil.editComma(resultDto.getGenshnpointsu1()));
        shopTabBean.setGenshnpointsu2(ItemUtil.editComma(resultDto.getGenshnpointsu2()));
        shopTabBean.setShnpointkirikaedd(resultDto.getShnpointkirikaedd());
        shopTabBean.setInitshnpointkirikaedd(resultDto.getShnpointkirikaedd());
        shopTabBean.setShinmtmkipointkbn(resultDto.getShinmtmkipointkbn());
        shopTabBean.setShinmtmkisuryo(ItemUtil.editComma(resultDto.getShinmtmkisuryo()));
        shopTabBean.setShinmtmkipointsu(ItemUtil.editComma(resultDto.getShinmtmkipointsu()));
        shopTabBean.setShinmtmkidankaisuryo1(ItemUtil.editComma(resultDto.getShinmtmkidankaisuryo1()));
        shopTabBean.setShinmtmkidankaipointsu1(ItemUtil.editComma(resultDto.getShinmtmkidankaipointsu1()));
        shopTabBean.setShinmtmkidankaisuryo2(ItemUtil.editComma(resultDto.getShinmtmkidankaisuryo2()));
        shopTabBean.setShinmtmkidankaipointsu2(ItemUtil.editComma(resultDto.getShinmtmkidankaipointsu2()));
        shopTabBean.setShinmtmkidankaisuryo3(ItemUtil.editComma(resultDto.getShinmtmkidankaisuryo3()));
        shopTabBean.setShinmtmkidankaipointsu3(ItemUtil.editComma(resultDto.getShinmtmkidankaipointsu3()));
        shopTabBean.setShinmtmkidankaisuryo4(ItemUtil.editComma(resultDto.getShinmtmkidankaisuryo4()));
        shopTabBean.setShinmtmkidankaipointsu4(ItemUtil.editComma(resultDto.getShinmtmkidankaipointsu4()));
        shopTabBean.setShinmtmkidankaisuryo5(ItemUtil.editComma(resultDto.getShinmtmkidankaisuryo5()));
        shopTabBean.setShinmtmkidankaipointsu5(ItemUtil.editComma(resultDto.getShinmtmkidankaipointsu5()));
        shopTabBean.setGenmtmkipointkbn(resultDto.getGenmtmkipointkbn());
        shopTabBean.setGenmtmkisuryo(ItemUtil.editComma(resultDto.getGenmtmkisuryo()));
        shopTabBean.setGenmtmkipointsu(ItemUtil.editComma(resultDto.getGenmtmkipointsu()));
        shopTabBean.setGenmtmkidankaisuryo1(ItemUtil.editComma(resultDto.getGenmtmkidankaisuryo1()));
        shopTabBean.setGenmtmkidankaipointsu1(ItemUtil.editComma(resultDto.getGenmtmkidankaipointsu1()));
        shopTabBean.setGenmtmkidankaisuryo2(ItemUtil.editComma(resultDto.getGenmtmkidankaisuryo2()));
        shopTabBean.setGenmtmkidankaipointsu2(ItemUtil.editComma(resultDto.getGenmtmkidankaipointsu2()));
        shopTabBean.setGenmtmkidankaisuryo3(ItemUtil.editComma(resultDto.getGenmtmkidankaisuryo3()));
        shopTabBean.setGenmtmkidankaipointsu3(ItemUtil.editComma(resultDto.getGenmtmkidankaipointsu3()));
        shopTabBean.setGenmtmkidankaisuryo4(ItemUtil.editComma(resultDto.getGenmtmkidankaisuryo4()));
        shopTabBean.setGenmtmkidankaipointsu4(ItemUtil.editComma(resultDto.getGenmtmkidankaipointsu4()));
        shopTabBean.setGenmtmkidankaisuryo5(ItemUtil.editComma(resultDto.getGenmtmkidankaisuryo5()));
        shopTabBean.setGenmtmkidankaipointsu5(ItemUtil.editComma(resultDto.getGenmtmkidankaipointsu5()));
        shopTabBean.setMtmkipointkirikaedd(resultDto.getMtmkipointkirikaedd());
        shopTabBean.setInitmtmkipointkirikaedd(resultDto.getMtmkipointkirikaedd());
        shopTabBean.setHansokusettei(resultDto.getHansokukbn());
        shopTabBean.setTekiyodkikanstr(resultDto.getHansokustrtdd());
        shopTabBean.setTekiyodkikanend(resultDto.getHansokushrydd());
        shopTabBean.setHansokunm(resultDto.getHansokunm());
        shopTabBean.setDlvryprice(ItemUtil.editComma(resultDto.getHansokudlvryprice()));
        shopTabBean.setOmchkrprice(ItemUtil.editComma(resultDto.getHansokumochikaeriprice()));
        shopTabBean.setHansokuyusenkbn(resultDto.getHansokuyusenkbn());
        shopTabBean.setZaikokanrikbn(resultDto.getZaikokanritrgtflg());
        shopTabBean.setGenkakeisankbn(resultDto.getGenkakeisankbn());
        shopTabBean.setZaikount1(ItemUtil.editComma(resultDto.getZaikount()));
        shopTabBean.setSiiretanihenkankbn(resultDto.getSiiretanihenkankbn());
        shopTabBean.setSiiretani(resultDto.getSiiretani());
        shopTabBean.setZaikoaramusuryo(resultDto.getZaikoaramusuryo());
        shopTabBean.setBiko2(resultDto.getShopbiko());
        shopTabBean.setYukokbn(resultDto.getShopyukokbn());
        shopTabBean.setYukokbn0(resultDto.getShopyukokbn());

        shopTabBean.setShopinptsystmymd(resultDto.getShopinptsystmymd());
        shopTabBean.setShopinptsystmjikan(resultDto.getShopinptsystmjikan());
        shopTabBean.setShoptoshaapkihoncd(resultDto.getShoptoshaapkihoncd());
        shopTabBean.setShoptoshashopcd(resultDto.getShoptoshashopcd());
        shopTabBean.setShoptoshacd(resultDto.getShoptoshacd());
        shopTabBean.setShoptoshamei(resultDto.getShoptoshanm());
        shopTabBean.setShopkshnsystmymd(resultDto.getShopkshnsystmymd());
        shopTabBean.setShopkshnsystmjikan(resultDto.getShopkshnsystmjikan());
        shopTabBean.setShopkoshaapkihoncd(resultDto.getShopkoshaapkihoncd());
        shopTabBean.setShopkoshashopcd(resultDto.getShopkoshashopcd());
        shopTabBean.setShopkoshacd(resultDto.getShopkoshacd());
        shopTabBean.setShopkoshamei(resultDto.getShopkoshanm());

        form.setShopTabBean(shopTabBean);
    }

    /**
     * 明細情報の初期化
     * 
     * @param mv ModelAndView
     * @param pageable Pageable
     */
    private void initMeisai(ModelAndView mv, Pageable pageable) {
        Page<A0301W02SetMeisaiBean> result = new PageImpl<>(new ArrayList<>(), pageable, 0);
        mv.addObject(SystemConstants.MODEL_KEY_PAGE, result);
        // 次に付与するセット商品明細の連番を初期化する
        a0301w02session.setNextSeq(A0301W02SetMeisaiBean.FIRST_SEQUENCE);
    }
}
