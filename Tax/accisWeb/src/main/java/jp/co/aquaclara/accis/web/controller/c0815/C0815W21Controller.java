package jp.co.aquaclara.accis.web.controller.c0815;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.ListUtils;
import org.thymeleaf.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.constants.SystemConstants;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.common.util.business.BusinessUtil;
import jp.co.aquaclara.accis.web.controller.c0815.bean.C0815W21BaseBean;
import jp.co.aquaclara.accis.web.controller.c0815.bean.C0815W21FooterBean;
import jp.co.aquaclara.accis.web.controller.c0815.bean.C0815W21MeisaiHeaderBean;
import jp.co.aquaclara.accis.web.controller.c0815.form.C0815W21Form;
import jp.co.aquaclara.accis.web.controller.commons.AccisWebBaseController;
import jp.co.aquaclara.accis.web.controller.commons.DbManagementSession;
import jp.co.aquaclara.accis.web.controller.commons.MenuAuthSession;
import jp.co.aquaclara.accis.web.controller.commons.TaxInfo;
import jp.co.aquaclara.accis.web.controller.commons.TaxSession;
import jp.co.aquaclara.accis.web.domain.dto.c0815.C0815W21DisplayDto;
import jp.co.aquaclara.accis.web.domain.dto.c0815.C0815W21KeyDto;
import jp.co.aquaclara.accis.web.domain.service.c0815.C0815W21Service;

/**
 * C0815W21_配達実績登録（個別入力）コントローラクラス.
 */
@Controller
@RequestMapping("/c0815w21")
public class C0815W21Controller extends AccisWebBaseController {

    private static final String VIEW_NAME = "c0815/c0815w21_form";

    private static final String SHUSEIHUKA_STR = "※修正不可";

    /* 使用権限レベル と DB管理項目を追加 */
    @ModelAttribute("menuAuthSession_C0815W21")
    public MenuAuthSession setMenuAuthSession() {
        return new MenuAuthSession();
    }

    @ModelAttribute("dbManagementSession_C0815W21")
    public DbManagementSession setDbManagementSession() {
        return new DbManagementSession();
    }

    /**
     * 消費税セッション情報
     */
    @Autowired
    private TaxSession taxSession;

    /**
     * 配達実績登録（個別入力）サービスクラス
     */
    @Autowired
    private C0815W21Service c0815w21Service;

    /**
     * 初期表示.
     * 
     * @param form C0815W21Form
     * @param mv ModelAndView
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @param dnpyno 伝票番号
     * @param edano 枝番号
     * @param selfposition 行情報
     * @return ModelAndView
     */
    @GetMapping("/{apkihoncd}/{shopcd}/{tokuisakicd}/{dnpyno}/{edano}/{selfposition}")
    public ModelAndView init(@ModelAttribute("c0815W21Form") C0815W21Form form,
        @ModelAttribute(name = "menuAuthSession_C0815W21",
            binding = false) MenuAuthSession menuAuthSession,
        @ModelAttribute(name = "dbManagementSession_C0815W21",
            binding = false) DbManagementSession dbManagementSession,
        ModelAndView mv, @PathVariable("apkihoncd") String apkihoncd,
        @PathVariable("shopcd") String shopcd, @PathVariable("tokuisakicd") String tokuisakicd,
        @PathVariable("dnpyno") String dnpyno, @PathVariable("edano") String edano,
        @PathVariable("selfposition") String selfposition) {

        initForm(form, apkihoncd, shopcd, tokuisakicd, dnpyno, edano, selfposition, menuAuthSession,
            dbManagementSession);

        // 表示画面設定
        mv.setViewName(VIEW_NAME);
        return mv;
    }

    /**
     * formの初期化.
     * 
     * @param form C0815W21Form
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @param dnpyno 伝票番号
     * @param edano 枝番号
     * @param selfposition 行情報
     * @param menuAuthSession 権限情報
     * @param dbManagementSession DBセッション
     */
    private void initForm(C0815W21Form form, String apkihoncd, String shopcd, String tokuisakicd,
        String dnpyno, String edano, String selfposition, MenuAuthSession menuAuthSession,
        DbManagementSession dbManagementSession) {

        // 配達実績情報取得
        C0815W21KeyDto keyDto = new C0815W21KeyDto();
        keyDto.setApkihoncd(apkihoncd);
        keyDto.setTokuisakicd(tokuisakicd);
        keyDto.setShopcd(shopcd);
        keyDto.setDnpyno(dnpyno);
        keyDto.setEdano(edano);
        keyDto.setSelfposition(selfposition);
        keyDto.setUnyoymd(dbManagementSession.getUnyoDate());
        C0815W21DisplayDto displayDto = c0815w21Service.init(keyDto);

        // form内のオブジェクトを初期化
        form.setKttcd(tokuisakicd);
        setForm(displayDto, form, apkihoncd, shopcd, tokuisakicd, dnpyno, edano, selfposition,
            menuAuthSession.getMenuAuth());
    }

    /**
     * Beanの設定.
     * 
     * @param displayDto C0815W21DisplayDto
     * @param form C0815W21Form
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @param dnpyno 伝票番号
     * @param edano 枝番号
     * @param selfposition 行情報
     * @param menuAuth 権限情報
     */
    private void setForm(C0815W21DisplayDto displayDto, C0815W21Form form, String apkihoncd,
        String shopcd, String tokuisakicd, String dnpyno, String edano, String selfposition,
        String menuAuth) {
        C0815W21BaseBean baseBean = new C0815W21BaseBean();
        C0815W21MeisaiHeaderBean meisaiHeaderBean = new C0815W21MeisaiHeaderBean();
        C0815W21FooterBean footerBean = new C0815W21FooterBean();

        String authflg = setAuthflg(menuAuth);
        String shuseihukaflg = SystemConstants.WORD_BLANK;
        String dlvrydd = displayDto.getDlvrydd();
        String acptdd = displayDto.getAcptdd();
        String dlvryytidd = displayDto.getDlvryytidd();
        String dlvrykibojikan = displayDto.getDlvrykibojikan();
        String dlvrykibojikanto = displayDto.getDlvrykibojikanto();
        if (CommonConstants.FLG_ON.equals(displayDto.getShuseihuka())) {
            shuseihukaflg = SHUSEIHUKA_STR;
        }
        if (!StringUtils.isEmpty(dlvrydd)) {
            dlvrydd = DateTimeUtil.toSlashedDate(dlvrydd);
        }
        if (!StringUtils.isEmpty(acptdd)) {
            acptdd = DateTimeUtil.toSlashedDate(acptdd);
        }
        if (!StringUtils.isEmpty(dlvryytidd)) {
            dlvryytidd = DateTimeUtil.toSlashedDate(dlvryytidd);
        }
        if (!StringUtils.isEmpty(dlvrykibojikan)) {
            dlvrykibojikan = DateTimeUtil.toColonedDateHHMM(dlvrykibojikan);
        }
        if (!StringUtils.isEmpty(dlvrykibojikanto)) {
            dlvrykibojikanto = DateTimeUtil.toColonedDateHHMM(dlvrykibojikanto);
        }
        // フラグ、初期値設定
        baseBean.setApkihoncd1(apkihoncd);
        baseBean.setShopcd(shopcd);
        baseBean.setTokuisakikbn(displayDto.getTokuisakikbn());
        baseBean.setAuthflag(authflg);
        baseBean.setDnpyno(dnpyno);
        baseBean.setEdano(edano);
        baseBean.setPriceinfo(displayDto.getPriceInfo());
        baseBean.setJsshohitaxvo(getJsshohitaxvo());
        baseBean.setShuseihuka(shuseihukaflg);
        baseBean.setHdnshuseihuka(displayDto.getShuseihuka());
        baseBean.setDlvrykyotennm(displayDto.getDlvrykyotennm());
        baseBean.setDlvrydd(dlvrydd);
        baseBean.setDlvryareanm(displayDto.getDlvryareanm());
        baseBean.setDlvryptnnm(displayDto.getDlvryptnnm());
        baseBean.setDlvrytnshacd(displayDto.getDlvrytnshacd());
        baseBean.setDlvrycmpnynm(displayDto.getDlvrycmpnynm());
        baseBean.setTnshamei(displayDto.getDlvrytnshamei());
        baseBean.setAcptdd(acptdd);
        baseBean.setDatsksmotokbnnm(displayDto.getDatsksmotokbn());
        baseBean.setDlvryytidd(dlvryytidd);
        baseBean.setDlvrykibojikan(dlvrykibojikan);
        baseBean.setDlvrykibojikankbnnm(displayDto.getDlvrykibojikankbnnm());
        baseBean.setDlvrykibojikanto(dlvrykibojikanto);
        baseBean.setFzaiokikanoflg(displayDto.getFzaiokikanoflg());
        baseBean.setFzaiokijissekiflg(displayDto.getFzaiokijissekiflg());
        baseBean.setNohinsakijoho1(displayDto.getNohinsakijoho1());
        baseBean.setNohinsakijoho2(displayDto.getNohinsakijoho2());
        baseBean.setSelfposition(selfposition);
        baseBean.setDlvrykbn(displayDto.getDlvrykbn());
        baseBean.setDnpysts(displayDto.getDnpysts());
        meisaiHeaderBean.setAcptkbn(displayDto.getAcptkbn());
        meisaiHeaderBean.setHdnacptkbn(displayDto.getHdnacptkbn());
        footerBean.setKinsm(SystemConstants.WORD_BLANK);
        footerBean.setUchishohitaxkin(SystemConstants.WORD_BLANK);
        footerBean.setSm(SystemConstants.WORD_BLANK);
        footerBean.setPrntmemon1(displayDto.getPrntmemon1());
        footerBean.setPrntmemon2(displayDto.getPrntmemon2());
        footerBean.setPrntmemon3(displayDto.getPrntmemon3());
        footerBean.setKaishun1n2rittoruazuk(BusinessUtil
            .getString(displayDto.getKaishun1n2rittoruazuk(), BigDecimal.ZERO.toString()));
        footerBean.setKaishun1n2rittorukaishu(displayDto.getKaishun1n2rittorukaishu());
        footerBean.setKaishun8rittoruazuk(BusinessUtil
            .getString(displayDto.getKaishun8rittoruazuk(), BigDecimal.ZERO.toString()));
        footerBean.setKaishun8rittorukaishu(displayDto.getKaishun8rittorukaishu());
        footerBean.setJutakudenpyoflg(displayDto.getJutakudenpyoflg());
        footerBean.setHaifuflg1(displayDto.getHaifuflg1());
        footerBean.setHaifubutsucd1(displayDto.getHaifubutsucd1());
        footerBean.setHaifubutsunm1(displayDto.getHaifubutsunm1());
        footerBean.setHaifubutsuapkihoncd1(displayDto.getHaifubutsuapkihoncd1());
        footerBean.setHaifubutsushopcd1(displayDto.getHaifubutsushopcd1());
        footerBean.setHaifuflg2(displayDto.getHaifuflg2());
        footerBean.setHaifubutsucd2(displayDto.getHaifubutsucd2());
        footerBean.setHaifubutsunm2(displayDto.getHaifubutsunm2());
        footerBean.setHaifubutsuapkihoncd2(displayDto.getHaifubutsuapkihoncd2());
        footerBean.setHaifubutsushopcd2(displayDto.getHaifubutsushopcd2());
        footerBean.setHaifuflg3(displayDto.getHaifuflg3());
        footerBean.setHaifubutsucd3(displayDto.getHaifubutsucd3());
        footerBean.setHaifubutsunm3(displayDto.getHaifubutsunm3());
        footerBean.setHaifubutsuapkihoncd3(displayDto.getHaifubutsuapkihoncd3());
        footerBean.setHaifubutsushopcd3(displayDto.getHaifubutsushopcd3());
        footerBean.setHaifuflg4(displayDto.getHaifuflg4());
        footerBean.setHaifubutsucd4(displayDto.getHaifubutsucd4());
        footerBean.setHaifubutsunm4(displayDto.getHaifubutsunm4());
        footerBean.setHaifubutsuapkihoncd4(displayDto.getHaifubutsuapkihoncd4());
        footerBean.setHaifubutsushopcd4(displayDto.getHaifubutsushopcd4());
        footerBean.setAcptkbnshs(displayDto.getAcptkbnshs());

        // Beanをセット
        form.setBaseBean(baseBean);
        form.setMeisaiHeaderBean(meisaiHeaderBean);
        form.setFooterBean(footerBean);
    }

    /**
     * 消費税情報取得
     * 
     * @return List<String>
     */
    private String getJsshohitaxvo() {
        List<String> taxinfoList = new ArrayList<>();
        if (!ListUtils.isEmpty(taxSession.getTaxInfo())) {
            for (TaxInfo taxinfo : taxSession.getTaxInfo()) {
                StringBuilder buf = new StringBuilder();
                buf.append("{taxkbn:").append(taxinfo.getTaxkbn());
                buf.append(",tekiyostrtdd:").append(taxinfo.getTekiyostrtdd());
                buf.append(",tekiyoshrydd:").append(taxinfo.getTekiyoshrydd());
                buf.append(",shohitax:").append(taxinfo.getShohitax());
                buf.append("}");
                taxinfoList.add(buf.toString());
            }
        }
        return String.join(",", taxinfoList);
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
        if (!StringUtils.isEmpty(menuAuth) && menuAuth.equals("ROLE_MENU_AUTH_UPDATE")) {
            authflg = "f";
        }

        return authflg;
    }
}
