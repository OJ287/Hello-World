package jp.co.aquaclara.accis.web.controller.z1503;

import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TZ1503W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S10T004TZ1503W01Entity;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.web.controller.commons.AccisWebBaseController;
import jp.co.aquaclara.accis.web.controller.z1503.bean.Z1503W01M1MeisaiDetailBean;
import jp.co.aquaclara.accis.web.controller.z1503.bean.Z1503W01M1MeisaiSummaryBean;
import jp.co.aquaclara.accis.web.controller.z1503.bean.Z1503W01M2MeisaiDetailBean;
import jp.co.aquaclara.accis.web.controller.z1503.bean.Z1503W01M2MeisaiSummaryBean;
import jp.co.aquaclara.accis.web.controller.z1503.form.Z1503W01Form;
import jp.co.aquaclara.accis.web.domain.service.z1503.Z1503W01Service;

/**
 * Z1503w01_未入金分照会-APIコントローラクラス.
 */
@RestController
@RequestMapping("/api/z1503w01")
public class Z1503W01APIController extends AccisWebBaseController {

    /**
     * 未入金分照会サービスクラス
     */
    @Autowired
    private Z1503W01Service z1503w01Service;

    /**
     * 明細部(M1)検索処理
     *
     * @param form Z1503W01Form
     * @param pageable Pageable
     * @return Page<Z1503W01M1MeisaiDetailBean>
     */
    @GetMapping("/search1")
    public Page<Z1503W01M1MeisaiDetailBean> search1(
        @ModelAttribute("z1503W01Form") Z1503W01Form form,
        @PageableDefault(size = 999) Pageable pageable) {

        System.out.println(form);

        List<Z1503W01M1MeisaiDetailBean> meisaiData =
            getInitMeisaiList1Bean(form.getBaseBean().getApkihoncd(),
                form.getBaseBean().getShopcd(), form.getSkysakishokaiBean().getTokuisakicd());
        int totalRecordCount = meisaiData.size();

        if (meisaiData.size() > 0) {
            meisaiData = super.selectPage(pageable, meisaiData);
        }

        Page<Z1503W01M1MeisaiDetailBean> result =
            new PageImpl<Z1503W01M1MeisaiDetailBean>(meisaiData, pageable, totalRecordCount);

        return result;
    }

    /**
     * 明細部(M2)検索処理
     *
     * @param form Z1503W01Form
     * @param pageable Pageable
     * @return Page<Z1503W01M2MeisaiDetailBean>
     */
    @GetMapping("/search2")
    public Page<Z1503W01M2MeisaiDetailBean> search2(
        @ModelAttribute("z1503W01Form") Z1503W01Form form,
        @PageableDefault(size = 999) Pageable pageable) {

        System.out.println(form);

        List<Z1503W01M2MeisaiDetailBean> sampleData =
            getInitMeisaiList2Bean(form.getBaseBean().getApkihoncd(),
                form.getBaseBean().getShopcd(), form.getBaseBean().getNykndnpyno());
        int totalRecordCount = sampleData.size();
        if (totalRecordCount > 0) {
            sampleData = super.selectPage(pageable, sampleData);
        }

        Page<Z1503W01M2MeisaiDetailBean> result =
            new PageImpl<Z1503W01M2MeisaiDetailBean>(sampleData, pageable, totalRecordCount);

        return result;
    }

    /**
     * Z1503W01M1MeisaiBean設定.
     * 
     * @return List<Z1503W01M1MeisaiDetailBean>
     */
    private List<Z1503W01M1MeisaiDetailBean> getInitMeisaiList1Bean(String apkihoncd, String shopcd,
        String skysakicd) {

        // 残高一覧取得する
        List<S07T001TZ1503W01Entity> resultList =
            z1503w01Service.searchZandaka(apkihoncd, shopcd, skysakicd);
        List<Z1503W01M1MeisaiDetailBean> meisaiBeanList =
            new LinkedList<Z1503W01M1MeisaiDetailBean>();

        for (S07T001TZ1503W01Entity entity : resultList) {
            Z1503W01M1MeisaiDetailBean bean = new Z1503W01M1MeisaiDetailBean();
            bean.setM1skydd(DateTimeUtil.toSlashedDate(entity.getSkydd()));
            bean.setM1skyshono(entity.getSkyshono());
            bean.setM1tokuisakicd(entity.getTokuisakicd());
            bean.setM1tokuisakimei(entity.getTokuisakinmn1());
            bean.setM1keshikomizd(entity.getKeshikomizd());
            bean.setM1shrytidydmdd(DateTimeUtil.toSlashedDate(entity.getShrytidydmdd()));
            bean.setM1totsukiurikin(entity.getTotsukiurikin());
            meisaiBeanList.add(bean);

        }

        return meisaiBeanList;
    }

    /**
     * Z1503W01M2MeisaiBean設定.
     * 
     * @return List<Z1503W01M2MeisaiDetailBean>
     */
    private List<Z1503W01M2MeisaiDetailBean> getInitMeisaiList2Bean(String apkihoncd, String shopcd,
        String skysakicd) {

        List<S10T004TZ1503W01Entity> resultList =
            z1503w01Service.searchNyukinIchiran(apkihoncd, shopcd, skysakicd);
        List<Z1503W01M2MeisaiDetailBean> meisaiBeanList =
            new LinkedList<Z1503W01M2MeisaiDetailBean>();

        for (S10T004TZ1503W01Entity entity : resultList) {
            Z1503W01M2MeisaiDetailBean bean = new Z1503W01M2MeisaiDetailBean();

            bean.setM2skydd(DateTimeUtil.toSlashedDate(entity.getSkydd()));
            bean.setM2skyshono(entity.getSkyshono());
            bean.setM2tokuisakicd(entity.getTokuisakicd());
            bean.setM2tokuisakimei(entity.getTokuisakinmn1());
            bean.setM2totsukiurikin(entity.getTotsukiurikin());
            bean.setM2keshikomizd(entity.getKeshikomizd());
            bean.setM2shrytidydmdd(DateTimeUtil.toSlashedDate(entity.getShrytidydmdd()));
            bean.setM2keshikomikin(entity.getKeshikomikin());
            bean.setM2totsukiurikin(entity.getTotsukiurikin());
            meisaiBeanList.add(bean);

        }

        return meisaiBeanList;

    }

    /**
     * サマリー部分(M1)取得処理
     * 
     * @param form Z1503W01Form
     * @param apkihoncd String
     * @param shopcd String
     * @return Z1503W01M1MeisaiSummaryBean
     */
    @GetMapping("/summary1/{apkihoncd}/{shopcd}/{tokuisakicd}")
    public Z1503W01M1MeisaiSummaryBean summary1(@ModelAttribute("z1503w01Form") Z1503W01Form form,
        @PathVariable("apkihoncd") String apkihoncd, @PathVariable("shopcd") String shopcd,
        @PathVariable("tokuisakicd") String tokuisakicd) {

        // 残高一覧取得する
        List<S07T001TZ1503W01Entity> resultList =
            z1503w01Service.searchZandaka(apkihoncd, shopcd, tokuisakicd);

        int total1 = 0;;
        int total2 = 0;
        for (S07T001TZ1503W01Entity entity : resultList) {
            total1 += Integer.parseInt(entity.getTotsukiurikin());
            total2 += Integer.parseInt(entity.getKeshikomizd());

        }

        return getInitM1MeisaiSummaryBean(String.valueOf(total1), String.valueOf(total2));
    }

    /**
     * Z1503W01M1MeisaiSummaryBean設定.
     *
     * @return Z1503W01M1MeisaiSummaryBean
     */
    private Z1503W01M1MeisaiSummaryBean getInitM1MeisaiSummaryBean(String total1, String total2) {

        // 当月売上金額合計, 未消し残額合計
        Z1503W01M1MeisaiSummaryBean meisaiSummaryBean = new Z1503W01M1MeisaiSummaryBean();
        meisaiSummaryBean.setTotsukiurikinm1sm(total1);
        meisaiSummaryBean.setKeshikomizdm1sm(total2);
        return meisaiSummaryBean;
    }

    /**
     * サマリー部分(M2)取得処理
     * 
     * @param form Z1503W01Form
     * @param apkihoncd String
     * @param shopcd String
     * @return Z1503W01M2MeisaiSummaryBean
     */
    @GetMapping("/summary2/{apkihoncd}/{shopcd}")
    public Z1503W01M2MeisaiSummaryBean summary2(@ModelAttribute("z1503w01Form") Z1503W01Form form,
        @PathVariable("apkihoncd") String apkihoncd, @PathVariable("shopcd") String shopcd) {

        return getInitM2MeisaiSummaryBean(form.getBaseBean().getNyknkin());
    }

    /**
     * Z1503W01M2MeisaiSummaryBean設定.
     *
     * @return Z1503W01M2MeisaiSummaryBean
     */
    private Z1503W01M2MeisaiSummaryBean getInitM2MeisaiSummaryBean(String nyknkin) {

        //明細0件の場合（Controllerにて取得）は、入金合計欄に０を設定
        String nyknsmkin = StringUtils.isEmpty(nyknkin) ? "0" : nyknkin;

        // 入金合計
        Z1503W01M2MeisaiSummaryBean meisaiSummaryBean = new Z1503W01M2MeisaiSummaryBean();
        meisaiSummaryBean.setNyknsmkin(nyknsmkin);

        return meisaiSummaryBean;
    }
}
