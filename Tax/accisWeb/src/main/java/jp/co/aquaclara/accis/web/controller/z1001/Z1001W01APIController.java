package jp.co.aquaclara.accis.web.controller.z1001;

import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S06T002TZ1001W01Entity;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.web.controller.commons.AccisWebBaseController;
import jp.co.aquaclara.accis.web.controller.z1001.bean.Z1001W01MeisaiDetailBean;
import jp.co.aquaclara.accis.web.controller.z1001.bean.Z1001W01MeisaiSummaryBean;
import jp.co.aquaclara.accis.web.controller.z1001.form.Z1001W01Form;
import jp.co.aquaclara.accis.web.domain.dto.z1001.Z1001W01MainDisplayDto;
import jp.co.aquaclara.accis.web.domain.service.z1001.Z1001W01Service;

/**
 * Z1001w01_売上照会-APIコントローラクラス.
 */
@RestController
@RequestMapping("/api/z1001w01")
public class Z1001W01APIController extends AccisWebBaseController {

    /**
     * 売上照会サービスクラス
     */
    @Autowired
    private Z1001W01Service z1001w01Service;

    /**
     * 明細部(M1)検索処理
     *
     * @param form Z1001W01Form
     * @param apkihoncd String
     * @param shopcd String
     * @param pageable Pageable
     * @param response HttpServletResponse
     * @return Page<Z1001W01M1MeisaiDetailBean>
     */
    @SuppressWarnings("unchecked")
    @GetMapping("/search/{apkihoncd}/{shopcd}")
    public Page<Z1001W01MeisaiDetailBean> search1(@ModelAttribute("z1001W01Form") Z1001W01Form form,
        @PathVariable("apkihoncd") String apkihoncd, @PathVariable("shopcd") String shopcd,
        @PageableDefault(size = 50) Pageable pageable, HttpServletResponse response) {

        List<Z1001W01MeisaiDetailBean> meisaiData =
            getInitMeisaiList1Bean(apkihoncd, shopcd, form.getBaseBean().getUridnpyno());
        int totalRecordCount = meisaiData.size();
        
        // 最大表示件数オーバーの場合、ステータスコードを206とする
        meisaiData = (List<Z1001W01MeisaiDetailBean>) chkLength(meisaiData, response);

        if (totalRecordCount != 0) {
            meisaiData = super.selectPage(pageable, meisaiData);
        }

        Page<Z1001W01MeisaiDetailBean> result =
            new PageImpl<Z1001W01MeisaiDetailBean>(meisaiData, pageable, totalRecordCount);

        return result;
    }

    /**
     * サマリー部分(M1)取得処理
     * 
     * @param form Z1001W01Form
     * @param apkihoncd String
     * @param shopcd String
     * @return Z1001W01MeisaiSummaryBean
     */
    @GetMapping("/summary/{apkihoncd}/{shopcd}")
    public Z1001W01MeisaiSummaryBean summary1(@ModelAttribute("z1001w01Form") Z1001W01Form form,
        @PathVariable("apkihoncd") String apkihoncd, @PathVariable("shopcd") String shopcd) {

        Z1001W01MainDisplayDto mainDto = z1001w01Service.searchUriageSyokai(apkihoncd, shopcd,
            form.getBaseBean().getUridnpyno());


        return getInitMeisaiSummaryBean(mainDto);
    }

    /**
     * Z1001W01MeisaiSummaryBean設定.
     *
     * @return Z1001W01MeisaiSummaryBean
     */
    private Z1001W01MeisaiSummaryBean getInitMeisaiSummaryBean(Z1001W01MainDisplayDto mainDto) {

        Z1001W01MeisaiSummaryBean meisaiSummaryBean = new Z1001W01MeisaiSummaryBean();
        if (StringUtils.isEmpty(mainDto.getUrikin())) {
            mainDto.setUrikin("0");
        }
        if (StringUtils.isEmpty(mainDto.getShohitaxkin())) {
            mainDto.setShohitaxkin("0");
        }
        meisaiSummaryBean.setUrikin1(String.valueOf(
            Integer.parseInt(mainDto.getUrikin()) + Integer.parseInt(mainDto.getShohitaxkin())));
        meisaiSummaryBean.setUrikin2(mainDto.getUrikin());
        meisaiSummaryBean.setShohitaxkin1(mainDto.getShohitaxkin());
        meisaiSummaryBean.setSiirekin1(mainDto.getSiirekin1());
        meisaiSummaryBean.setSiirekin2(mainDto.getSiirekin());
        meisaiSummaryBean.setShohitaxkin2(mainDto.getSiireshohitaxkin());

        return meisaiSummaryBean;
    }


    /**
     * Z1001W01MeisaiBean設定.
     * 
     * @return List<Z1001W01MeisaiDetailBean>
     */
    private List<Z1001W01MeisaiDetailBean> getInitMeisaiList1Bean(String apkihoncd, String shopcd,
        String uridnpyno) {

        List<Z1001W01MeisaiDetailBean> meisaiBeanList = new LinkedList<Z1001W01MeisaiDetailBean>();
        // 検索処理
        List<S06T002TZ1001W01Entity> resultList =
            z1001w01Service.searchUriageMeisai(apkihoncd, shopcd, uridnpyno, loginSession.getMaxDisplayCount());

        for (S06T002TZ1001W01Entity entity : resultList) {

            // 入力明細フラグが"1"(フラグON)、かつ明細区分が"1"(明細区分：配布物)以外の場合のみ、レコードを設定
            if (CommonConstants.FLG_ON.equals(entity.getNyuryokumeisaiflg())
                && !CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(entity.getMeisaikbn())) {

                Z1001W01MeisaiDetailBean bean = new Z1001W01MeisaiDetailBean();
                bean.setShncd(entity.getShncd());
                bean.setShnnmryakusho(entity.getShnnmryakusho());
                bean.setIro(entity.getIro());
                bean.setSz(entity.getSz());
                bean.setKatasikinm(entity.getKatasiki());
                bean.setKeijokbnnm(entity.getKeijokbnnm());
                bean.setSuryo1(entity.getSuryo());
                bean.setTrtskitani11(entity.getUritrtskitani());
                bean.setIrisu1(entity.getUriirisu());
                bean.setIrisutani1(entity.getUritani());
                bean.setSokazu1(entity.getSokazu());
                bean.setUnt1(entity.getUnt());
                bean.setKin1(entity.getKin());
                bean.setShohitaxkinn1(entity.getShohitaxkin());
                bean.setSuryo2(entity.getSiiresuryo());
                bean.setTrtskitani2(entity.getSiiretrtskitani());
                bean.setIrisu2(entity.getSiireirisu());
                bean.setIrisutani2(entity.getSiiretani());
                bean.setSokazu2(entity.getSiiresokazu());
                bean.setUnt2(entity.getSiireunt());
                bean.setKin2(entity.getSiirekin());
                bean.setShohitaxkinn2(entity.getSiireshohitaxkin());
                bean.setMeisaibiko(entity.getMeisaibiko());
                bean.setInptsystmymd(DateTimeUtil.toSlashedDate(entity.getInptsystmymd()));
                bean.setInptsystmjikan(DateTimeUtil.toColonedDateHHMMSS(entity.getInptsystmjikan()));
                bean.setToshaapkihoncd(entity.getToshaapkihoncd());
                bean.setToshashopcd(entity.getToshashopcd());
                bean.setToshacd(entity.getToshacd());
                bean.setToshamei(entity.getToshamei());
                bean.setKshnsystmymd(DateTimeUtil.toSlashedDate(entity.getKshnsystmymd()));
                bean.setKshnsystmjikan(DateTimeUtil.toColonedDateHHMMSS(entity.getKshnsystmjikan()));
                bean.setKoshaapkihoncd(entity.getKoshaapkihoncd());
                bean.setKoshashopcd(entity.getKoshashopcd());
                bean.setKoshacd(entity.getKoshacd());
                bean.setKoshamei(entity.getKoshamei());
                bean.setLctgrycd(entity.getLctgrycd());
                bean.setMctgrycd(entity.getMctgrycd());
                bean.setSctgrycd(entity.getSctgrycd());
                bean.setPricekbn(entity.getPricekbn());
                bean.setHyojunpricekirikaedd(entity.getHyojunpricekirikaedd());
                bean.setGenzhyojunprice(entity.getGenzhyojunprice());
                bean.setGenzhyojunmochikaeriprice(entity.getGenzopnmochikaeriprice());
                bean.setShinhyojunprice(entity.getShinhyojunprice());
                bean.setShinhyojunmochikaeriprice(entity.getShinopnmochikaeriprice());
                bean.setZaikount(entity.getZaikount());
                bean.setShntaxkbn(entity.getTaxkbn());

                meisaiBeanList.add(bean);
            }
        }
        return meisaiBeanList;

    }

}
