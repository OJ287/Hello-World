package jp.co.aquaclara.accis.web.domain.service.d1002;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M013TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T002TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M013TD1002R01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T002TD1002R01Entity;
import jp.co.aquaclara.accis.common.exception.BusinessException;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.common.util.ListCreatorUtil;
import jp.co.aquaclara.accis.web.domain.dto.d1002.D1002W01SearchConditionDto;
import jp.co.aquaclara.accis.web.domain.service.common.OnlineBatchBaseService;
import jp.co.aquaclara.accis.web.domain.service.common.OnlineBatchCommonModel;

/**
 * D1002r01_売掛金残高表1Service
 * 
 * @author accisdev008
 *
 */
@Service
public class D1002R01Service extends OnlineBatchBaseService<D1002W01SearchConditionDto> {

    @Autowired
    private S02M013TDao s02m013tdao;

    @Autowired
    private S07T002TDao s07t002tdao;

    /**
     * 処理ID
     */
    private final String processid = "D1002B01";

    /**
     * 処理ID取得
     */
    @Override
    public String getProcessId() {
        return this.processid;
    }

    /**
     * 帳票出力処理（売掛金残高表1）
     * 
     * @param parammodel 売掛金残高表データ検索情報
     * @param commonInfo 非同期処理共有情報
     * @throws Exception システム例外
     * @throws BusinessException 業務例外
     */
    @Override
    protected void process(D1002W01SearchConditionDto parammodel, OnlineBatchCommonModel commonInfo)
        throws BusinessException, Exception {

        // 売掛金残高表(未締め)情報取得
        if (parammodel.getDydm().equals(parammodel.getNextdydm())) {
            List<S02M013TD1002R01Entity> meisai =
                s02m013tdao.selectByD1002R01Meisai(parammodel.getApkihoncd(),
                    parammodel.getShopcd(), parammodel.getDydm(), parammodel.getRiyowsvkbn(),
                    parammodel.getJigyoshosentaku(), parammodel.getTokuisakikbnlist(),
                    parammodel.getPrntkbn(), parammodel.getShukeihoho(), parammodel.getOtpttani());
            // CSV出力
            OtpCsvMishime(parammodel, meisai, commonInfo);
        } else {
            // 売掛金残高表情報取得
            List<S07T002TD1002R01Entity> meisai =
                s07t002tdao.selectByD1002R01Meisai(parammodel.getApkihoncd(),
                    parammodel.getShopcd(), parammodel.getDydm(), parammodel.getRiyowsvkbn(),
                    parammodel.getJigyoshosentaku(), parammodel.getTokuisakikbnlist(),
                    parammodel.getPrntkbn(), parammodel.getShukeihoho(), parammodel.getOtpttani());
            // CSV出力
            OtpCsvShime(parammodel, meisai, commonInfo);
        }
    }

    /**
     * CSV出力項目編集（未締めデータ）
     * 
     * @param parammodel 売掛金残高表データ検索情報
     * @param meisailist 売掛金残高表(未締め)情報
     * @param commonInfo 非同期処理共有情報
     */
    private void OtpCsvMishime(D1002W01SearchConditionDto parammodel,
        List<S02M013TD1002R01Entity> meisailist, OnlineBatchCommonModel commonInfo)
        throws BusinessException, Exception {

        List<List<String>> datalist = new ArrayList<List<String>>();

        // 表示区分カウンタ用変数
        // 事業所コード
        String jigyoshocd = "";
        // 営業担当者コード
        String eigyotnshacd = "";
        boolean isFirst = true;
        int i = 0;

        for (S02M013TD1002R01Entity e : meisailist) {

            BigDecimal konkaishunodaikokei = new BigDecimal(0);
            List<String> columns = new ArrayList<String>();
            // プログラムID
            columns.add(ListCreatorUtil.wrapQuote("D1002R01"));
            // AP基本コード
            columns.add(ListCreatorUtil.wrapQuote(parammodel.getApkihoncd()));
            // ショップコード
            columns.add(ListCreatorUtil.wrapQuote(parammodel.getShopcd()));
            // ショップ名称
            columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getShopnm())));
            // 利用サーバー区分
            columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getRiyowsvkbn())));
            // 利用サーバー区分名称
            columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getRiyowsvkbnnm())));
            if (CommonConstants.SHUKEIHOHO_1.equals(parammodel.getShukeihoho())
                || CommonConstants.SHUKEIHOHO_3.equals(parammodel.getShukeihoho())
                || !StringUtils.isEmpty(parammodel.getJigyoshosentaku())) {
                // 事業所コード
                columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getJigyoshocd())));
                // 事業所名称
                columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getJigyoshonm())));
            } else {
                // 事業所コード
                columns.add("");
                // 事業所名称
                columns.add("");
            }
            // 印刷指示担当者コード
            columns.add(ListCreatorUtil.wrapQuote(parammodel.getTnshacd()));
            // 印刷指示担当者
            columns.add(ListCreatorUtil.wrapQuote(parammodel.getTnshamei()));
            if (CommonConstants.SHUKEIHOHO_2.equals(parammodel.getShukeihoho())
                || CommonConstants.SHUKEIHOHO_3.equals(parammodel.getShukeihoho())) {
                // 営業担当者項目名
                columns.add(ListCreatorUtil.wrapQuote("営業担当者："));
                // 営業担当者コード
                columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getEigyotnshacd())));
                // 営業担当者名
                columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getTnshamei())));
            } else {
                // 営業担当者項目名
                columns.add("");
                // 営業担当者コード
                columns.add("");
                // 営業担当者名
                columns.add("");
            }
            if (CommonConstants.KBN_TOKUISAKI_KOKUAKU.equals(e.getTokuisakikbn())) {
                // 得意先・受託配達顧客区分名称
                columns.add(ItemUtil.nullToString(ListCreatorUtil
                    .wrapQuote(e.getTokuisakikbnnm() + "/" + e.getJutakudlvrycstkbnnm())));
            } else {
                // 得意先・受託配達顧客区分名称
                columns
                    .add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getTokuisakikbnnm())));
            }
            // 請求先コード
            columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getSkysakicd())));
            // 得意先状態
            if (CommonConstants.INIT_TOKUISAKIJOTAI.equals(e.getTokuisakijotai())) {
                // 得意先状態が[5:本会員]の場合は、空出力をする。
                columns.add("");
            } else {
                columns
                    .add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getTokuisakijotainm())));
            }
            // 得意先コード
            columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getTokuisakicd())));
            // 得意先名称
            columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getTokuisakinmn1())));
            // レギュラー有料
            columns.add(ItemUtil.nullToValue(e.getRgrayuryo(), "0"));
            // レギュラー無料
            columns.add(ItemUtil.nullToValue(e.getRgramuryo(), "0"));
            // スリム有料
            columns.add(ItemUtil.nullToValue(e.getSrmyuryo(), "0"));
            // スリム無料
            columns.add(ItemUtil.nullToValue(e.getSrmmuryo(), "0"));
            // その他有料
            columns.add(ItemUtil.nullToValue(e.getSonotayuryo(), "0"));
            // その他無料
            columns.add(ItemUtil.nullToValue(e.getSonotamuryo(), "0"));
            // 製品水売上
            columns.add(ItemUtil.nullToValue(e.getSeihinmizuuri(), "0"));
            // レンタル料金
            columns.add(ItemUtil.nullToValue(e.getRtlrkn(), "0"));
            // その他売上
            columns.add(ItemUtil.nullToValue(e.getSonotaurikin(), "0"));
            // 税抜売上金額
            columns.add(ItemUtil.nullToValue(e.getTotsukiurikeimei(), "0"));
            // 今回消費税金額
            columns.add(ItemUtil.nullToValue(e.getKonkaishohitaxkin(), "0"));
            // 前月残高
            columns.add(ItemUtil.nullToValue(e.getZnkkurikoshikin(), "0"));
            // 当月入金
            columns.add(ItemUtil.nullToValue(e.getKonkainyknkin(), "0"));
            // 当月残高
            columns.add(ItemUtil.nullToValue(e.getKurikoshikin(), "0"));
            // 収納代行（今回収納代行売上金額+今回収納代行消費税金額）
            konkaishunodaikokei = konkaishunodaikokei
                .add(new BigDecimal(ItemUtil.nullToValue(e.getKonkaishunodaikourikin(), "0")));
            konkaishunodaikokei = konkaishunodaikokei
                .add(new BigDecimal(ItemUtil.nullToValue(e.getKonkaishunodaikoshohitaxkin(), "0")));
            columns.add(String.valueOf(konkaishunodaikokei));
            // 8%売上
            columns.add(ItemUtil.nullToValue(e.getN8pertaxnukiurikin(), "0"));
            // 8%消費税
            columns.add(ItemUtil.nullToValue(e.getN8pershohitaxkin(), "0"));
            // 10%売上
            columns.add(ItemUtil.nullToValue(e.getN10pertaxnukiurikin(), "0"));
            // 10%消費税
            columns.add(ItemUtil.nullToValue(e.getN10pershohitaxkin(), "0"));
            // 申込受付区分（ListCreator制御項目）
            if (CommonConstants.KBN_TOKUISAKIJOTAI_MOSHIKOMI.equals(e.getTokuisakijotai())) {
                columns.add(ListCreatorUtil.wrapQuote("1"));
            } else {
                columns.add(ListCreatorUtil.wrapQuote("0"));
            }
            // 設置前キャンセル区分（ListCreator制御項目）
            if (CommonConstants.KBN_TOKUISAKIJOTAI_SETCANCEL.equals(e.getTokuisakijotai())) {
                columns.add(ListCreatorUtil.wrapQuote("1"));
            } else {
                columns.add(ListCreatorUtil.wrapQuote("0"));
            }
            // 仮会員区分（ListCreator制御項目）
            if (CommonConstants.KBN_TOKUISAKIJOTAI_KARIKAIIN.equals(e.getTokuisakijotai())) {
                columns.add(ListCreatorUtil.wrapQuote("1"));
            } else {
                columns.add(ListCreatorUtil.wrapQuote("0"));
            }
            // 仮会員退会区分（ListCreator制御項目）
            if (CommonConstants.KBN_TOKUISAKIJOTAI_KARIKAIYAKU.equals(e.getTokuisakijotai())) {
                columns.add(ListCreatorUtil.wrapQuote("1"));
            } else {
                columns.add(ListCreatorUtil.wrapQuote("0"));
            }
            // 本会員区分（ListCreator制御項目）
            if (CommonConstants.KBN_TOKUISAKIJOTAI_HONKAIIN.equals(e.getTokuisakijotai())) {
                columns.add(ListCreatorUtil.wrapQuote("1"));
            } else {
                columns.add(ListCreatorUtil.wrapQuote("0"));
            }
            // 退会区分（ListCreator制御項目）
            if (CommonConstants.KBN_TOKUISAKIJOTAI_TAIKAI.equals(e.getTokuisakijotai())) {
                columns.add(ListCreatorUtil.wrapQuote("1"));
            } else {
                columns.add(ListCreatorUtil.wrapQuote("0"));
            }
            // 表示区分（ListCreaotr制御項目（改ページ判定用））
            if (StringUtils.isEmpty(parammodel.getJigyoshosentaku())
                && CommonConstants.SHUKEIHOHO_1.equals(parammodel.getShukeihoho())) {
                // 事業所コードの切り替わりを確認
                if (!jigyoshocd.equals(ItemUtil.nullToString(e.getJigyoshocd()))) {
                    // 事業所コードが切り替わっていた場合、カウンターを＋１（初回はゼロ）
                    columns.add(ListCreatorUtil.wrapQuote(String.valueOf(isFirst ? 0 : ++i)));
                } else {
                    // 事業所コードが切り替わっていない場合、カウンターは変更しない
                    columns.add(ListCreatorUtil.wrapQuote(String.valueOf(i)));
                }
            } else if (StringUtils.isEmpty(parammodel.getJigyoshosentaku())
                && CommonConstants.SHUKEIHOHO_2.equals(parammodel.getShukeihoho())) {
                // 営業担当者コードの切り替わりを確認
                if (!eigyotnshacd.equals(ItemUtil.nullToString(e.getEigyotnshacd()))) {
                    // 営業担当者コードが切り替わっていた場合、カウンターを＋１（初回はゼロ）
                    columns.add(ListCreatorUtil.wrapQuote(String.valueOf(isFirst ? 0 : ++i)));
                } else {
                    // 営業担当者コードが切り替わっていない場合、カウンターは変更しない
                    columns.add(ListCreatorUtil.wrapQuote(String.valueOf(i)));
                }
            } else if (StringUtils.isEmpty(parammodel.getJigyoshosentaku())
                && CommonConstants.SHUKEIHOHO_3.equals(parammodel.getShukeihoho())) {
                // 営業担当者コードの切り替わりを確認
                if (!eigyotnshacd.equals(ItemUtil.nullToString(e.getEigyotnshacd()))) {
                    // 営業担当者コードが切り替わっていた場合、カウンターを＋１（初回はゼロ）
                    columns.add(ListCreatorUtil.wrapQuote(String.valueOf(isFirst ? 0 : ++i)));
                } else {
                    // 営業担当者コードが切り替わっていない場合、カウンターは変更しない
                    columns.add(ListCreatorUtil.wrapQuote(String.valueOf(i)));
                }
            } else {
                // 上記以外の場合、カウンターは変更しない
                columns.add(ListCreatorUtil.wrapQuote(String.valueOf(i)));
            }
            // 指定締年月
            columns.add(ListCreatorUtil.wrapQuote(parammodel.getDydm().substring(0, 4) + "年"
                + parammodel.getDydm().substring(4, 6) + "月（仮締め）"));
            datalist.add(columns);

            // 表示区分のカウンタ用変数の更新
            jigyoshocd = ItemUtil.nullToString(e.getJigyoshocd());
            eigyotnshacd = ItemUtil.nullToString(e.getEigyotnshacd());
            isFirst = false;

        }
        // 帳票（PDFファイル）を出力する
        super.createReportFile(datalist, commonInfo.getFileNamne(), null);
    }

    /**
     * CSV出力項目編集（締めデータ）
     * 
     * @param parammodel 売掛金残高表データ検索情報
     * @param meisailist 売掛金残高表(締め)情報
     * @param commonInfo 非同期処理共有情報
     */
    private void OtpCsvShime(D1002W01SearchConditionDto parammodel,
        List<S07T002TD1002R01Entity> meisailist, OnlineBatchCommonModel commonInfo)
        throws BusinessException, Exception {
        List<List<String>> datalist = new ArrayList<List<String>>();

        // 項目：表示区分カウンタ用変数
        // 事業所コード
        String jigyoshocd = "";
        // 営業担当者コード
        String eigyotnshacd = "";
        boolean isFirst = true;
        int i = 0;

        for (S07T002TD1002R01Entity e : meisailist) {

            BigDecimal shunodaikokei = new BigDecimal(0);
            List<String> columns = new ArrayList<String>();
            // プログラムID
            columns.add(ListCreatorUtil.wrapQuote("D1002R01"));
            // AP基本コード
            columns.add(ListCreatorUtil.wrapQuote(parammodel.getApkihoncd()));
            // ショップコード
            columns.add(ListCreatorUtil.wrapQuote(parammodel.getShopcd()));
            // ショップ名称
            columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getShopnm())));
            // 利用サーバー区分
            columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getRiyowsvkbn())));
            // 利用サーバー区分名称
            columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getRiyowsvkbnnm())));
            if (CommonConstants.SHUKEIHOHO_1.equals(parammodel.getShukeihoho())
                || CommonConstants.SHUKEIHOHO_3.equals(parammodel.getShukeihoho())
                || !StringUtils.isEmpty(parammodel.getJigyoshosentaku())) {
                // 事業所コード
                columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getJigyoshocd())));
                // 事業所名称
                columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getJigyoshonm())));
            } else {
                // 事業所コード
                columns.add("");
                // 事業所名称
                columns.add("");
            }
            // 印刷指示担当者コード
            columns.add(ListCreatorUtil.wrapQuote(parammodel.getTnshacd()));
            // 印刷指示担当者
            columns.add(ListCreatorUtil.wrapQuote(parammodel.getTnshamei()));
            if (CommonConstants.SHUKEIHOHO_2.equals(parammodel.getShukeihoho())
                || CommonConstants.SHUKEIHOHO_3.equals(parammodel.getShukeihoho())) {
                // 営業担当者項目名
                columns.add(ListCreatorUtil.wrapQuote("営業担当者："));
                // 営業担当者コード
                columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getEigyotnshacd())));
                // 営業担当者名
                columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getTnshamei())));
            } else {
                // 営業担当者項目名
                columns.add("");
                // 営業担当者コード
                columns.add("");
                // 営業担当者名
                columns.add("");
            }
            if (CommonConstants.KBN_TOKUISAKI_KOKUAKU.equals(e.getTokuisakikbn())) {
                // 得意先・受託配達顧客区分名称
                columns.add(ItemUtil.nullToString(ListCreatorUtil
                    .wrapQuote(e.getTokuisakikbnnm() + "/" + e.getJutakudlvrycstkbnnm())));
            } else {
                // 得意先・受託配達顧客区分名称
                columns
                    .add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getTokuisakikbnnm())));
            }
            // 請求先コード
            columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getSkysakicd())));
            // 得意先状態
            if (CommonConstants.INIT_TOKUISAKIJOTAI.equals(e.getTokuisakijotai())) {
                // 得意先状態が[5:本会員]の場合は、空出力をする。
                columns.add("");
            } else {
                columns
                    .add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getTokuisakijotainm())));
            }
            // 得意先コード
            columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getTokuisakicd())));
            // 得意先名称
            columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getTokuisakinmn1())));
            // レギュラー有料
            columns.add(ItemUtil.nullToValue(e.getRgrayuryo(), "0"));
            // レギュラー無料
            columns.add(ItemUtil.nullToValue(e.getRgramuryo(), "0"));
            // スリム有料
            columns.add(ItemUtil.nullToValue(e.getSrmyuryo(), "0"));
            // スリム無料
            columns.add(ItemUtil.nullToValue(e.getSrmmuryo(), "0"));
            // その他有料
            columns.add(ItemUtil.nullToValue(e.getSonotayuryo(), "0"));
            // その他無料
            columns.add(ItemUtil.nullToValue(e.getSonotamuryo(), "0"));
            // 製品水売上
            columns.add(ItemUtil.nullToValue(e.getSeihinmizuuri(), "0"));
            // レンタル料金
            columns.add(ItemUtil.nullToValue(e.getRtlrkn(), "0"));
            // その他売上
            columns.add(ItemUtil.nullToValue(e.getSonotaurikin(), "0"));
            // 税抜売上金額
            columns.add(ItemUtil.nullToValue(e.getTotsukiurikeimei(), "0"));
            // 今回消費税金額
            columns.add(ItemUtil.nullToValue(e.getKonkaishohitaxkin(), "0"));
            // 前月残高
            columns.add(ItemUtil.nullToValue(e.getZnkkurikoshikin(), "0"));
            // 当月入金
            columns.add(ItemUtil.nullToValue(e.getKonkainyknkin(), "0"));
            // 当月残高
            columns.add(ItemUtil.nullToValue(e.getKurikoshikin(), "0"));
            // 収納代行（今回収納代行売上金額+今回収納代行消費税金額）
            shunodaikokei = shunodaikokei
                .add(new BigDecimal(ItemUtil.nullToValue(e.getKonkaishunodaikourikin(), "0")));
            shunodaikokei = shunodaikokei
                .add(new BigDecimal(ItemUtil.nullToValue(e.getKonkaishunodaikoshohitaxkin(), "0")));
            columns.add(String.valueOf(shunodaikokei));
            // 8%売上
            columns.add(ItemUtil.nullToValue(e.getN8perzeinukiurikin(), "0"));
            // 8%消費税
            columns.add(ItemUtil.nullToValue(e.getN8pershohitaxkin(), "0"));
            // 10%売上
            columns.add(ItemUtil.nullToValue(e.getN10perzeinukiurikin(), "0"));
            // 10%消費税
            columns.add(ItemUtil.nullToValue(e.getN10pershohitaxkin(), "0"));
            // 申込受付区分（ListCreator制御項目）
            if (CommonConstants.KBN_TOKUISAKIJOTAI_MOSHIKOMI.equals(e.getTokuisakijotai())) {
                columns.add(ListCreatorUtil.wrapQuote("1"));
            } else {
                columns.add(ListCreatorUtil.wrapQuote("0"));
            }
            // 設置前キャンセル区分（ListCreator制御項目）
            if (CommonConstants.KBN_TOKUISAKIJOTAI_SETCANCEL.equals(e.getTokuisakijotai())) {
                columns.add(ListCreatorUtil.wrapQuote("1"));
            } else {
                columns.add(ListCreatorUtil.wrapQuote("0"));
            }
            // 仮会員区分（ListCreator制御項目）
            if (CommonConstants.KBN_TOKUISAKIJOTAI_KARIKAIIN.equals(e.getTokuisakijotai())) {
                columns.add(ListCreatorUtil.wrapQuote("1"));
            } else {
                columns.add(ListCreatorUtil.wrapQuote("0"));
            }
            // 仮会員退会区分（ListCreator制御項目）
            if (CommonConstants.KBN_TOKUISAKIJOTAI_KARIKAIYAKU.equals(e.getTokuisakijotai())) {
                columns.add(ListCreatorUtil.wrapQuote("1"));
            } else {
                columns.add(ListCreatorUtil.wrapQuote("0"));
            }
            // 本会員区分（ListCreator制御項目）
            if (CommonConstants.KBN_TOKUISAKIJOTAI_HONKAIIN.equals(e.getTokuisakijotai())) {
                columns.add(ListCreatorUtil.wrapQuote("1"));
            } else {
                columns.add(ListCreatorUtil.wrapQuote("0"));
            }
            // 退会区分（ListCreator制御項目）
            if (CommonConstants.KBN_TOKUISAKIJOTAI_TAIKAI.equals(e.getTokuisakijotai())) {
                columns.add(ListCreatorUtil.wrapQuote("1"));
            } else {
                columns.add(ListCreatorUtil.wrapQuote("0"));
            }
            // 表示区分（ListCreaotr制御項目（改ページ判定用））
            if (StringUtils.isEmpty(parammodel.getJigyoshosentaku())
                && CommonConstants.SHUKEIHOHO_1.equals(parammodel.getShukeihoho())) {
                // 事業所コードの切り替わりを確認
                if (!jigyoshocd.equals(ItemUtil.nullToString(e.getJigyoshocd()))) {
                    // 事業所コードが切り替わっていた場合、カウンターを＋１（初回はゼロ）
                    columns.add(ListCreatorUtil.wrapQuote(String.valueOf(isFirst ? 0 : ++i)));
                } else {
                    // 事業所コードが切り替わっていない場合、カウンターは変更しない
                    columns.add(ListCreatorUtil.wrapQuote(String.valueOf(i)));
                }
            } else if (StringUtils.isEmpty(parammodel.getJigyoshosentaku())
                && CommonConstants.SHUKEIHOHO_2.equals(parammodel.getShukeihoho())) {
                // 営業担当者コードの切り替わりを確認
                if (!eigyotnshacd.equals(ItemUtil.nullToString(e.getEigyotnshacd()))) {
                    // 営業担当者コードが切り替わっていた場合、カウンターを＋１（初回はゼロ）
                    columns.add(ListCreatorUtil.wrapQuote(String.valueOf(isFirst ? 0 : ++i)));
                } else {
                    // 営業担当者コードが切り替わっていない場合、カウンターは変更しない
                    columns.add(ListCreatorUtil.wrapQuote(String.valueOf(i)));
                }
            } else if (StringUtils.isEmpty(parammodel.getJigyoshosentaku())
                && CommonConstants.SHUKEIHOHO_3.equals(parammodel.getShukeihoho())) {
                // 営業担当者コードの切り替わりを確認
                if (!eigyotnshacd.equals(ItemUtil.nullToString(e.getEigyotnshacd()))) {
                    // 営業担当者コードが切り替わっていた場合、カウンターを＋１（初回はゼロ）
                    columns.add(ListCreatorUtil.wrapQuote(String.valueOf(isFirst ? 0 : ++i)));
                } else {
                    // 営業担当者コードが切り替わっていない場合、カウンターは変更しない
                    columns.add(ListCreatorUtil.wrapQuote(String.valueOf(i)));
                }
            } else {
                // 上記以外の場合、カウンターは変更しない
                columns.add(ListCreatorUtil.wrapQuote(String.valueOf(i)));
            }
            // 指定締年月
            columns.add(ListCreatorUtil.wrapQuote(parammodel.getDydm().substring(0, 4) + "年"
                + parammodel.getDydm().substring(4, 6) + "月"));
            datalist.add(columns);

            // 表示区分のカウンタ用変数の更新
            jigyoshocd = ItemUtil.nullToString(e.getJigyoshocd());
            eigyotnshacd = ItemUtil.nullToString(e.getEigyotnshacd());
            isFirst = false;
        }
        // 帳票（PDFファイル）を出力する
        super.createReportFile(datalist, commonInfo.getFileNamne(), null);
    }
}
