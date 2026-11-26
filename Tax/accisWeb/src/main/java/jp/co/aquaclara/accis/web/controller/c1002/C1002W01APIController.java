package jp.co.aquaclara.accis.web.controller.c1002;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S06T001TC1002W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S06T002TC1002W01Entity;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.web.controller.c1002.bean.C1002W01BaseBean;
import jp.co.aquaclara.accis.web.controller.c1002.bean.C1002W01MeisaiBean;
import jp.co.aquaclara.accis.web.controller.c1002.bean.C1002W01SlipBean;
import jp.co.aquaclara.accis.web.controller.c1002.form.C1002W01Form;
import jp.co.aquaclara.accis.web.controller.c1002.session.C1002W01Session;
import jp.co.aquaclara.accis.web.controller.commons.AccisWebBaseController;
import jp.co.aquaclara.accis.web.controller.commons.AciRestResponse;
import jp.co.aquaclara.accis.web.controller.commons.DbManagementSession;
import jp.co.aquaclara.accis.web.domain.dto.c1002.C1002W01CalcTaxDto;
import jp.co.aquaclara.accis.web.domain.dto.c1002.C1002W01UriageDenpyoDto;
import jp.co.aquaclara.accis.web.domain.service.c1002.C1002W01CommonService;
import jp.co.aquaclara.accis.web.domain.service.c1002.C1002W01Service;
import jp.co.aquaclara.accis.web.domain.service.c1002.C1002W01TokuisakiService;
import jp.co.aquaclara.accis.web.domain.service.c1002.C1002W01UriageService;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;
import jp.co.aquaclara.accis.web.exception.dto.FormField;

/**
 * C1002W01_売上入力APIコントローラクラス.
 */
@RestController
@RequestMapping("/api/c1002w01")
public class C1002W01APIController extends AccisWebBaseController {

    /**
     * セッション情報
     */
    @Autowired
    private C1002W01Session session;

    /**
     * 売上入力_Service
     */
    @Autowired
    private C1002W01Service service;

    /**
     * 売上入力_共通Service
     */
    @Autowired
    private C1002W01CommonService commonService;

    /**
     * 売上入力_売上テーブル関連Service
     */
    @Autowired
    private C1002W01UriageService uriageService;

    /**
     * 売上入力_得意先マスタ関連Service
     */
    @Autowired
    private C1002W01TokuisakiService tokuisakiService;

    /**
     * 得意先チェック（得意先共通ヘッダ「確定」ボタン押下後処理）
     * 
     * @param form
     * @param apkihoncd
     * @param shopcd
     * @param tokuisakicd
     * @param dbManagementSession
     * @return
     */
    @GetMapping("/tokuisakicheck/{apkihoncd}/{shopcd}/{tokuisakicd}")
    public List<String> checkTokuisaki(@PathVariable("apkihoncd") String apkihoncd,
        @PathVariable("shopcd") String shopcd, @PathVariable("tokuisakicd") String tokuisakicd) {

        // 得意先情報を取得
        S02M013TEntity tokuisakiEntity = tokuisakiService.search(apkihoncd, shopcd, tokuisakicd);

        // 得意先情報が取得できない場合はエラー
        if (ObjectUtils.isEmpty(tokuisakiEntity)) {
            throw BusinessExceptionFactory.validationFailed(MessageID.E002);
        }

        // 得意先情報の有効区分が「無効」の場合はエラー
        if (CommonConstants.YUKOKBN_0.equals(tokuisakiEntity.getYukokbn())) {
            throw BusinessExceptionFactory.validationFailed(MessageID.W123, "得意先コード");
        }

        // 請求先コードが空の場合はエラー
        if (StringUtils.isEmpty(tokuisakiEntity.getSkysakicd())) {
            throw BusinessExceptionFactory.validationFailed(MessageID.W084, "請求先情報");
        }

        // 警告メッセージリスト
        List<String> messageList = new ArrayList<>();

        String tokuisakijotai = tokuisakiEntity.getTokuisakijotai();

        // 得意先状態が「仮解約」または「退会」の場合、警告メッセージを表示
        if (tokuisakijotai.equals(CommonConstants.KBN_TOKUISAKIJOTAI_KARIKAIYAKU)
            || tokuisakijotai.equals(CommonConstants.KBN_TOKUISAKIJOTAI_TAIKAI)) {

            String message = messageSource.getMessage(MessageID.W188.toString());
            messageList.add(message);
        }

        return messageList;
    }

    /**
     * 初期表示（得意先チェック後処理）
     * 
     * @param form
     * @param apkihoncd
     * @param shopcd
     * @param tokuisakicd
     * @param dbManagementSession
     * @return
     */
    @GetMapping("/{apkihoncd}/{shopcd}/{tokuisakicd}")
    public C1002W01UriageDenpyoDto searchUriageDenpyo(
        @ModelAttribute("c1002W01Form") C1002W01Form form,
        @PathVariable("apkihoncd") String apkihoncd, @PathVariable("shopcd") String shopcd,
        @PathVariable("tokuisakicd") String tokuisakicd,
        @ModelAttribute(name = "dbManagementSession_C1002W01",
            binding = true) DbManagementSession dbManagementSession) {

        return getInitUriageDenpyoDto(form, apkihoncd, shopcd, tokuisakicd, dbManagementSession);
    }

    /**
     * 初期表示用の売上伝票DTOを生成
     * 
     * @param form
     * @param apkihoncd
     * @param shopcd
     * @param tokuisakicd
     * @param dbManagementSession
     * @return
     */
    private C1002W01UriageDenpyoDto getInitUriageDenpyoDto(C1002W01Form form, String apkihoncd,
        String shopcd, String tokuisakicd, DbManagementSession dbManagementSession) {

        C1002W01UriageDenpyoDto dto = new C1002W01UriageDenpyoDto();

        // 端数計算区分を設定
        String urikinhasukeisankbn = commonService.searchUrikinhasukeisankbn(apkihoncd, shopcd);
        dto.setUrikinhasukeisankbn(urikinhasukeisankbn);

        // ヘッダエリアの値を設定
        dto.setPrcskbn(CommonConstants.KBN_PRCSKBN1_SHINKI);
        dto.setUridnpyno(null);
        dto.setUridd(DateTimeUtil.toSlashedDate(dbManagementSession.getUnyoDate()));
        dto.setDlvryhoho(CommonConstants.KBN_DLVRY_HOHO_HAITATSU);

        // 入荷元エリアの値を設定
        dto.setJissekitnshacd1(null);
        dto.setTnshamei1(null);
        dto.setSokocd(null);
        dto.setSokonm(null);

        // 以降、出荷先エリアの値を設定

        // 得意先情報を取得
        S02M013TEntity tokuisakiEntity = tokuisakiService.search(apkihoncd, shopcd, tokuisakicd);
        dto.setTokuisakicd(tokuisakicd);
        dto.setTokuisakinm(tokuisakiEntity.getTokuisakinmn1());
        dto.setTokuisakikbn(tokuisakiEntity.getTokuisakikbn());
        dto.setTokuisakijotai(tokuisakiEntity.getTokuisakijotai());

        // 配達担当者コードが空でない場合
        if (!StringUtils.isEmpty(tokuisakiEntity.getDlvrytnshacd())) {

            // 配達担当者コード
            String dlvrytnshacd = tokuisakiEntity.getDlvrytnshacd();
            dto.setDlvrytnshacd(dlvrytnshacd);

            // 配達担当者名
            String dlvrytnshamei = commonService.getTnshamei(apkihoncd, shopcd, dlvrytnshacd);
            dto.setDlvrytnshamei(dlvrytnshamei);

            // 実績担当者（出荷先）
            if (!"2".equals(tokuisakiEntity.getUrikeijotnshakbn())) {
                dto.setJissekitnshacd2(dlvrytnshacd);
                dto.setJissekitnshamei2(dlvrytnshamei);
            }
        }

        // 営業担当者コードが空でない場合
        if (!StringUtils.isEmpty(tokuisakiEntity.getEigyotnshacd())) {

            // 営業担当者コード
            String eigyotnshacd = tokuisakiEntity.getEigyotnshacd();
            dto.setEigyotnshacd(eigyotnshacd);

            // 営業担当者名
            String eigyotnshamei = commonService.getTnshamei(apkihoncd, shopcd, eigyotnshacd);
            dto.setEigyotnshamei(eigyotnshamei);

            // 実績担当者（出荷先）
            if ("2".equals(tokuisakiEntity.getUrikeijotnshakbn())) {
                dto.setJissekitnshacd2(eigyotnshacd);
                dto.setJissekitnshamei2(eigyotnshamei);
            }
        }

        // 配達管轄AP基本コード
        dto.setDlvrykankatsuapkihoncd(tokuisakiEntity.getDlvrykankatsuapkihoncd());

        // 配達管轄ショップコード
        dto.setDlvrykankatsushopcd(tokuisakiEntity.getDlvrykankatsushopcd());

        // ポイントフラグを取得
        boolean pointFlg = service.getPointFlg(apkihoncd, shopcd, tokuisakicd);
        // ポイントフラグを設定する
        C1002W01BaseBean base = form.getBaseBean();
        if (base != null) {
            base.setPointflg(pointFlg ? CommonConstants.FLG_ON : CommonConstants.FLG_OFF);
        }
        dto.setPointFlg(pointFlg ? CommonConstants.FLG_ON : CommonConstants.FLG_OFF);

        return dto;
    }

    /**
     * 売上伝票検索（伝票番号チェック後処理）
     * 
     */
    @GetMapping("/searchuriagedenpyo/{apkihoncd}/{shopcd}/{tokuisakicd}/{uridnpyno}")
    public C1002W01UriageDenpyoDto searchUriageDenpyo(@PathVariable("apkihoncd") String apkihoncd,
        @PathVariable("shopcd") String shopcd, @PathVariable("tokuisakicd") String tokuisakicd,
        @PathVariable("uridnpyno") String uridnpyno) {

        return getUriageDenpyoDto(apkihoncd, shopcd, tokuisakicd, uridnpyno);
    }

    /**
     * 伝票番号未入力エラー処理
     * 
     */
    @GetMapping("/searchuriagedenpyo/empty")
    public void showUridnpyonoEmtpyMessage() {

        List<FormField> fields = List.of(new FormField("baseBean.uridnpyno"));
        throw BusinessExceptionFactory.validationFailed(fields, MessageID.W011, "伝票番号");
    }

    /**
     * 伝票番号入力不正エラー処理
     * 
     */
    @GetMapping("/searchuriagedenpyo/invalid")
    public void showUridnpyonoInvalidMessage() {

        // 検索結果が見つからない旨のメッセージを表示する
        List<FormField> fields = List.of(new FormField("baseBean.uridnpyno"));
        throw BusinessExceptionFactory.validationFailed(fields, MessageID.W083);
    }

    /**
     * 売上伝票DTOを生成
     * 
     * @param apkihoncd
     * @param shopcd
     * @param uridnpyno
     * @return
     */
    private C1002W01UriageDenpyoDto getUriageDenpyoDto(String apkihoncd, String shopcd,
        String tokuisakicd, String uridnpyno) {

        // 売上伝票を取得
        S06T001TC1002W01Entity entity =
            uriageService.searchUriageDenpyo(apkihoncd, shopcd, uridnpyno);

        // 売上伝票のチェックを実施
        service.checkUriageDenpyo(entity, apkihoncd, shopcd, tokuisakicd);

        // 取得した売上伝票情報をDTOに設定する
        C1002W01UriageDenpyoDto dto = new C1002W01UriageDenpyoDto();
        dto.setUridd(DateTimeUtil.toSlashedDate(entity.getUridd()));
        dto.setJissekitnshacd2(entity.getJissekitnshacd());
        dto.setJissekitnshamei2(entity.getJissekitnshamei());
        dto.setSokocd(entity.getNyukamotocd());
        dto.setSokonm(entity.getSokonm());
        dto.setDlvrytnshacd(entity.getDlvrytnshacd());
        dto.setDlvrytnshamei(entity.getDlvrytnshamei());
        dto.setEigyotnshacd(entity.getEigyotnshacd());
        dto.setEigyotnshamei(entity.getEigyotnshamei());
        dto.setDlvryhoho(entity.getDlvrykbn());
        dto.setDnpybiko(entity.getDnpybiko());
        dto.setTokuisakikbn(entity.getTokuisakikbn());

        return dto;
    }

    /**
     * 売上明細検索（伝票番号チェック後処理）
     * 
     */
    @GetMapping("/searchuriagemeisai")
    public Page<C1002W01MeisaiBean> searchUriageMeisai(
        @ModelAttribute("c1002W01Form") C1002W01Form form,
        @ModelAttribute(name = "dbManagementSession_C1002W01",
            binding = true) DbManagementSession dbManagementSession) {

        C1002W01BaseBean baseBean = form.getBaseBean();

        if (ObjectUtils.isEmpty(session.getMeisaiBeanList())) {
            session.setMeisaiBeanList(new ArrayList<>());
        }

        // 行追加の場合
        if ("1".equals(baseBean.getAddrowflag())) {
            C1002W01MeisaiBean meisaiBean = service.getBlankRow(form);

            // 有効な商品の場合のみ明細行を追加する（旧システム踏襲）
            if (!ObjectUtils.isEmpty(meisaiBean)) {
                session.getMeisaiBeanList().add(meisaiBean);
            }

            return new PageImpl<>(session.getMeisaiBeanList());
        }

        // 行挿入の場合
        if (!StringUtils.isEmpty(baseBean.getInsertindex())) {
            C1002W01MeisaiBean meisaiBean = service.getBlankRow(form);

            // 有効な商品の場合のみ明細行を挿入する（旧システム踏襲）
            if (!ObjectUtils.isEmpty(meisaiBean)) {
                int index = Integer.parseInt(baseBean.getInsertindex());
                session.getMeisaiBeanList().add(index, service.getBlankRow(form));
            }

            return new PageImpl<>(session.getMeisaiBeanList());
        }

        // 行削除の場合
        if (!StringUtils.isEmpty(baseBean.getDeleteindex())) {
            int index = Integer.parseInt(baseBean.getDeleteindex());
            session.getMeisaiBeanList().remove(index);
            return new PageImpl<>(session.getMeisaiBeanList());
        }

        // 検索の場合
        C1002W01SlipBean slipBean = form.getSlipBean();

        List<C1002W01MeisaiBean> meisaiList =
            getInitMeisaiBeanList(baseBean.getApkihoncd(), baseBean.getShopcd(),
                baseBean.getUridnpyno(), slipBean.getUridd(), baseBean.getTokuisakikbn());

        session.setMeisaiBeanList(meisaiList);

        return new PageImpl<>(session.getMeisaiBeanList());
    }

    /**
     * MeisaiBeanList生成
     * 
     * @param apkihoncd
     * @param shopcd
     * @param uridenpyno
     * @param uridd
     * @param tokuisakikbn
     * @return
     */
    public List<C1002W01MeisaiBean> getInitMeisaiBeanList(String apkihoncd, String shopcd,
        String uridenpyno, String uridd, String tokuisakikbn) {

        List<C1002W01MeisaiBean> meisaiBeanList = new ArrayList<>();

        List<S06T002TC1002W01Entity> resultList = service.searchUriageMeisai(apkihoncd, shopcd,
            uridenpyno, loginSession.getMaxDisplayCount());

        // インボイス施行済み判定結果を取得
        S02M046TEntity shopEntity = commonService.searchShop(apkihoncd, shopcd);
        boolean invoiceEnforced = commonService.isInvoiceEnforced(shopEntity.getInvoicshikoymd(),
            DateTimeUtil.toNoSlashedDate(uridd));

        for (S06T002TC1002W01Entity e : resultList) {

            // 入力行以外はスキップ
            if (!(CommonConstants.NYURYOKUMEISAIFLG_1.equals(e.getNyuryokumeisaiflg()))) {
                continue;
            }

            // 配布物の場合はスキップ
            if (CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(e.getMeisaikbn())) {
                continue;
            }

            // 消費税レコードの場合はスキップ
            if (CommonConstants.SHNCD_099901.equals(e.getShncd())
                || CommonConstants.SHNCD_099902.equals(e.getShncd())) {
                continue;
            }

            C1002W01MeisaiBean meisaiBean = new C1002W01MeisaiBean();

            meisaiBean.setApkihoncd(e.getApkihoncd());
            meisaiBean.setShncdmei(e.getShncd());
            meisaiBean.setShnnmryakushomei(e.getShnnmryakusho());
            meisaiBean.setIromei(e.getIro());
            meisaiBean.setSzmei(e.getSz());
            meisaiBean.setKatasikimei(e.getKatasiki());

            meisaiBean.setKeijokbnmei(e.getKeijokbn());
            meisaiBean.setSuryo1mei((e.getSuryo() == null) ? "0" : e.getSuryo().toString());
            meisaiBean.setTrtskitani1mei(e.getUritrtskitani());
            meisaiBean
                .setSuryo2mei((e.getSiiresuryo() == null) ? "0" : e.getSiiresuryo().toString());
            meisaiBean.setTrtskitani2mei(e.getSiiretrtskitani());
            meisaiBean.setIrisu1mei((e.getUriirisu() == null) ? "0" : e.getUriirisu().toString());
            meisaiBean.setIrisutani1mei(e.getUritani());
            meisaiBean
                .setIrisu2mei((e.getSiireirisu() == null) ? "0" : e.getSiireirisu().toString());
            meisaiBean.setIrisutani2mei(e.getSiiretani());
            meisaiBean.setSokazu1mei((e.getSokazu() == null) ? "0" : e.getSokazu().toString());
            meisaiBean
                .setSokazu2mei((e.getSiiresokazu() == null) ? "0" : e.getSiiresokazu().toString());
            meisaiBean.setUnt1mei((e.getUnt() == null) ? "0" : e.getUnt().toString());
            meisaiBean.setUnt2mei((e.getSiireunt() == null) ? "0" : e.getSiireunt().toString());
            meisaiBean.setKin1mei((e.getKin() == null) ? "0" : e.getKin().toString());
            meisaiBean.setKin2mei((e.getSiirekin() == null) ? "0" : e.getSiirekin().toString());

            String shohitaxkin = (e.getShohitaxkin() == null) ? "0" : e.getShohitaxkin().toString();
            String sankoushohitaxkin =
                (e.getSankoushohitaxkin() == null) ? "0" : e.getSankoushohitaxkin().toString();

            // 裏で持つ消費税金額、参考消費税金額
            // 取消時、売上テーブルの消費税金額、参考消費税金額にはこの合計値を設定する
            meisaiBean.setShohitaxkinnodisp(shohitaxkin);
            meisaiBean.setSankoushohitaxkinnodisp(sankoushohitaxkin);

            // 画面に表示する消費税の金額
            if (invoiceEnforced) {
                // インボイス施行済みの場合
                meisaiBean.setShohitaxkinn1mei(
                    (e.getSankoushohitaxkin() == null) ? "0" : e.getSankoushohitaxkin().toString());
            } else {
                // インボイス施行前の場合
                meisaiBean.setShohitaxkinn1mei(
                    (e.getSankoushohitaxkin() == null) ? "0" : e.getShohitaxkin().toString());
            }

            meisaiBean.setShohitaxkinn2mei(
                (e.getSiireshohitaxkin() == null) ? "0" : e.getSiireshohitaxkin().toString());

            meisaiBean.setMeisaibikomei(e.getMeisaibiko());

            meisaiBean.setTokuisakikbn(tokuisakikbn);

            meisaiBeanList.add(meisaiBean);
        }

        return meisaiBeanList;
    }

    /**
     * 消費税計算.
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param shncd 商品コード
     * @param tokuisakicd 得意先コード
     * @param suryo 数量
     * @param keijokbn 計上区分
     * @param tnk 単価
     * @param uridd 売上日
     * @return 消費税金額
     */
    @GetMapping("calctax/{apkihoncd}/{shopcd}")
    public String calcTax(@PathVariable("apkihoncd") String apkihoncd,
        @PathVariable("shopcd") String shopcd,
        @RequestParam(name = "shncd", required = true) String shncd,
        @RequestParam(name = "tokuisakicd", required = true) String tokuisakicd,
        @RequestParam(name = "suryo", required = true) String suryo,
        @RequestParam(name = "keijokbn", required = true) String keijokbn,
        @RequestParam(name = "unt", required = true) String unt,
        @RequestParam(name = "uridd", required = true) String uridd) {

        // 消費税計算
        C1002W01CalcTaxDto taxDto = new C1002W01CalcTaxDto();
        taxDto.setApkihoncd(apkihoncd);
        taxDto.setShopcd(shopcd);
        taxDto.setTokuisakicd(tokuisakicd);
        taxDto.setShncd(shncd);
        taxDto.setSuryo(ItemUtil.removeComma(suryo));
        taxDto.setKeijokbn(keijokbn);
        taxDto.setUnt(unt);
        taxDto.setUridd(DateTimeUtil.toNoSlashedDate(uridd));

        return service.calcTax(taxDto);
    }

    /**
     * 処理区分変更確認（新規→取消）
     * 
     * @param form
     * @return
     */
    @PutMapping("/confirmtorikeshi")
    public List<String> confirmTorikeshi(@RequestBody C1002W01Form form) {
        List<String> messages = new ArrayList<>();
        BusinessExceptionFactory.setMessageList(messages, MessageID.W260,
            "編集中の明細情報が失われます。よろしいですか？");
        return messages;
    }

    /**
     * セッションクリア
     * 
     * @return AciRestResponse
     */
    @PutMapping("/clearsession")
    public AciRestResponse<Void> clearSession() {
        session.setMeisaiBeanList(null);
        return new AciRestResponse<>(null);
    }

    // 登録更新処理
    @PutMapping("update")
    public AciRestResponse<C1002W01Form> update(@RequestBody @Validated C1002W01Form form,
        @ModelAttribute(name = "dbManagementSession_C1002W01",
            binding = false) DbManagementSession dbManagementSession) {

        // 画面からのポイント付与単独処理
        // 商品コード:099990単品で「更新」ボタンを押下した場合の処理
        if(!ObjectUtils.isEmpty(form.getMeisaiBeanList()) && form.getMeisaiBeanList().size() == 1
                && isGamenPoint(form.getMeisaiBeanList().get(0).getShncdmei())) {
            // ポイント用入力チェック
            checkForPoint(form, dbManagementSession);

            // 画面発生用ポイント登録処理
            service.execInsertPointProcess(form, dbManagementSession);
        }else {
            // 入力値等のチェック
            checkForUpdate(form, dbManagementSession);

            // 請求締処理管理テーブル処理状態チェック
            String apkihoncd = form.getBaseBean().getApkihoncd();
            String shopcd = form.getBaseBean().getShopcd();
            service.checkSeikyuShimeJotai(apkihoncd, shopcd);

            // 登録更新処理を実施、採番された売上伝票番号を取得
            String uridnpyno =
                service.execUpdateProcess(form, dbManagementSession, loginSession.getMaxDisplayCount());

            // 前回入力伝票No.を設定
            form.getSlipBean().setDnpyno(uridnpyno);
        }
        AciRestResponse<C1002W01Form> res = new AciRestResponse<>(form);

        return res;
    }

    /**
     * 更新前チェックを実施
     * 
     * @param form C1002W01Form
     * @param dbManagementSession DbManagementSession
     */
    private void checkForUpdate(C1002W01Form form, DbManagementSession dbManagementSession) {

        // 検索実施有無（明細件数0件）チェック
        String prcskbn = form.getBaseBean().getPrcskbn();
        if (CommonConstants.KBN_PRCSKBN1_TORIKESI.equals(prcskbn)) {
            List<C1002W01MeisaiBean> meisaiBeanList = form.getMeisaiBeanList();
            if (ObjectUtils.isEmpty(meisaiBeanList) || meisaiBeanList.size() == 0) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W174, "検索ボタン");
            }
        }

        // 伝票チェック
        service.uridnpynoCheck(form);

        // 売上日チェック
        service.uriddCheck(form, dbManagementSession.getUnyoDate());

        // 入荷元担当者コードチェック
        service.jissekitnshacd1Check(form);

        // 倉庫コードチェック
        service.sokocdCheck(form);

        // 出荷先担当者コードチェック
        service.jissekitnshacd2Check(form);

        // 配達担当者コードチェック
        service.dlvrytnshacdCheck(form);

        // 営業担当者コードチェック
        service.eigyotnshacdCheck(form);

        // 備考チェック
        service.bikoCheck(form.getSlipBean().getBiko());

        // 与信限度額チェック
        service.yoshinCheck(form);

        // 明細チェック
        service.checkMeisaiForUpdate(form);
        
        //　売上入力での明細の売上金額の合計値と売上金額テーブルの値が合わない場合がある。その原因を見つけるためにエラーを発生させる相関チェックを追加する
        service.checkIfSalesTotalMatchesDetail(form);

        // ポイントチェック
        service.checkGamenPoint(form);
    }

    /**
     * 画面からのポイント付与単体用_更新前チェックを実施
     * 
     * @param form C1002W01Form
     * @param dbManagementSession DbManagementSession
     */
    private void checkForPoint(C1002W01Form form, DbManagementSession dbManagementSession) {

        // 検索実施有無（明細件数0件）チェック
        String prcskbn = form.getBaseBean().getPrcskbn();
        if (CommonConstants.KBN_PRCSKBN1_TORIKESI.equals(prcskbn)) {
            List<C1002W01MeisaiBean> meisaiBeanList = form.getMeisaiBeanList();
            if (ObjectUtils.isEmpty(meisaiBeanList) || meisaiBeanList.size() == 0) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W174, "検索ボタン");
            }
        }

        // 売上日チェック
        service.uriddCheck(form, dbManagementSession.getUnyoDate());

        // 備考チェック
        service.bikoCheck(form.getSlipBean().getBiko());

        // 明細チェック
        service.checkMeisaiForUpdate(form);

        // ポイントチェック
        service.checkGamenPoint(form);
    }

    /**
     * 明細行変更処理
     * 
     * @param meisaiBean C1002W01MeisaiBean
     * @param index 変更する明細のインデックス
     * @return
     */
    @PutMapping("/changerow/{index}")
    public AciRestResponse<Void> changeRow(@RequestBody C1002W01MeisaiBean meisaiBean,
        @PathVariable("index") int index) {

        // セッションで保持する明細行の入力項目を変更する
        C1002W01MeisaiBean sessionBean = getSessionMeisai(index);

        if (meisaiBean.getKatasikimei() != null) {
            sessionBean.setKatasikimei(meisaiBean.getKatasikimei()); // 型式
        }
        if (meisaiBean.getKeijokbnmei() != null) {
            sessionBean.setKeijokbnmei(meisaiBean.getKeijokbnmei()); // 計上区分
        }
        if (meisaiBean.getSuryo1mei() != null) {
            sessionBean.setSuryo1mei(meisaiBean.getSuryo1mei()); // 売上数量
        }
        if (meisaiBean.getSokazu1mei() != null) {
            sessionBean.setSokazu1mei(meisaiBean.getSokazu1mei()); // 売上総数
        }
        if (meisaiBean.getUnt1mei() != null) {
            sessionBean.setUnt1mei(meisaiBean.getUnt1mei()); // 売上単価
        }
        if (meisaiBean.getKin1mei() != null) {
            sessionBean.setKin1mei(meisaiBean.getKin1mei()); // 売上金額
        }
        if (meisaiBean.getShohitaxkinn1mei() != null) {
            sessionBean.setShohitaxkinn1mei(meisaiBean.getShohitaxkinn1mei()); // 売上消費税金額
        }
        if (meisaiBean.getSuryo2mei() != null) {
            sessionBean.setSuryo2mei(meisaiBean.getSuryo2mei()); // 仕入数量
        }
        if (meisaiBean.getSokazu2mei() != null) {
            sessionBean.setSokazu2mei(meisaiBean.getSokazu2mei()); // 仕入総数
        }
        if (meisaiBean.getUnt2mei() != null) {
            sessionBean.setUnt2mei(meisaiBean.getUnt2mei()); // 仕入単価
        }
        if (meisaiBean.getKin2mei() != null) {
            sessionBean.setKin2mei(meisaiBean.getKin2mei()); // 仕入金額
        }
        if (meisaiBean.getShohitaxkinn2mei() != null) {
            sessionBean.setShohitaxkinn2mei(meisaiBean.getShohitaxkinn2mei()); // 仕入消費税金額
        }
        if (meisaiBean.getPointsu() != null) {
            sessionBean.setPointsu(meisaiBean.getPointsu()); // 予定ポイント
        }
        if (meisaiBean.getMeisaibikomei() != null) {
            sessionBean.setMeisaibikomei(meisaiBean.getMeisaibikomei()); // 明細備考
        }

        return new AciRestResponse<>(null);
    }

    /**
     * 共通部品を実行して予定ポイントを再計算する
     * 
     * @param form C1002W01Form
     * @param index
     * @param dbManagementSession DbManagementSession
     * @return points
     */
    @PutMapping("/points/recalc/{index}")
    public AciRestResponse<Integer> recalcPointsPerRow( @RequestBody C1002W01Form form,
           @PathVariable("index") int index, @ModelAttribute(name = "dbManagementSession_C1002W01", binding = true)
           DbManagementSession dbManagementSession) {

        // ポイントフラグが無効なら共通部品を実行しない
        if (!checkPointFlg(form)) return new AciRestResponse<>(0);

        // 明細行の取り出し
        C1002W01MeisaiBean bean = getSessionMeisai(index);
        // 売上日のデータ修正（yyyy/MM/dd）→（yyyyMMdd）
        String uriDateNoSlash = DateTimeUtil.toNoSlashedDate(form.getSlipBean().getUridd());
        // 配達方法
        String dlvryhoho = form.getSlipBean().getDlvryhoho();

        int points = service.recalcPoint(dbManagementSession, uriDateNoSlash, form, dlvryhoho, bean);

        return new AciRestResponse<>(points);
    }

     /**
      * C1002W01MeisaiBeanの内容をチェックし、空だった場合はnullを返す
      * @param index
      * @return indexに該当するlistの内容、もしくはnull
      */
     private C1002W01MeisaiBean getSessionMeisai(int index) {

        List<C1002W01MeisaiBean> list = session.getMeisaiBeanList();
        if (ObjectUtils.isEmpty(list)) {
            return null;
        }
        if (index < 0 || index >= list.size()) {
            return null;
        }
        return list.get(index);
     }

    /**
     * ポイントフラグの有効判定
     * @param form C1002W01Form
     * @return baseBean
     */
    private boolean checkPointFlg(C1002W01Form form) {
        C1002W01BaseBean baseBean = form.getBaseBean();
        return baseBean != null && CommonConstants.FLG_ON.equals(baseBean.getPointflg());
    }

    /**
     * 画面からの付与ポイントかの判定
     * @param syohin 判定対象の商品コード
     * @return boolean 099990であればtrue それ以外はfalse
     */
    private boolean isGamenPoint(String shncd) {
        return CommonConstants.SHNCD_099990.equals(shncd);
    }

}
