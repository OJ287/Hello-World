package jp.co.aquaclara.accis.web.domain.service.d1503;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M017TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M046TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M017TD1503R01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1503R01Entity;
import jp.co.aquaclara.accis.common.exception.BusinessException;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.common.util.ListCreatorUtil;
import jp.co.aquaclara.accis.web.domain.dto.b0503.D0503W02VirtualAccountDto;
import jp.co.aquaclara.accis.web.domain.dto.d1503.D1503W01PrintContentsDto;
import jp.co.aquaclara.accis.web.domain.dto.d1503.D1503W01PrintDetailDto;
import jp.co.aquaclara.accis.web.domain.service.b0503.B0503W02VirtualAccountService;
import jp.co.aquaclara.accis.web.domain.service.common.OnlineBatchBaseService;
import jp.co.aquaclara.accis.web.domain.service.common.OnlineBatchCommonModel;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;

/**
 * 帳票：督促状／督促状（控）
 * 
 * @author accisdev008
 */
@Service
public class D1503R01Service extends OnlineBatchBaseService<D1503W01PrintContentsDto> {

    @Autowired
    private S02M046TDao s02m046tdao;

    @Autowired
    private S02M017TDao s02m017tdao;

    @Autowired
    private S07T001TDao s07t001tdao;

    @Autowired
    private B0503W02VirtualAccountService vaService;

    /**
     * 処理ID
     */
    private final String processId = "D1503B01";

    /**
     * 処理ID取得
     */
    @Override
    public String getProcessId() {
        return this.processId;
    }

    /**
     * 帳票出力処理（督促状／督促状（控））
     * 
     * @param parammodel 督促状データ検索情報
     * @param commonInfo 非同期処理共有情報
     * @throws Exception システム例外
     * @throws BusinessException 業務例外
     */
    @Override
    protected void process(D1503W01PrintContentsDto parammodel, OnlineBatchCommonModel commonInfo)
        throws BusinessException, Exception {

        List<List<String>> datalist = new ArrayList<List<String>>();
        List<List<String>> datalisthikae = new ArrayList<List<String>>();
        List<String> imageFileNames = new ArrayList<String>();

        // ショップマスタ検索（差出人イメージ取得）
        S02M046TEntityKey s02m046key = new S02M046TEntityKey();
        s02m046key.setApkihoncd(parammodel.getApkihoncd());
        s02m046key.setShopcd(parammodel.getShopcd());
        S02M046TEntity s02m046entity = s02m046tdao.selectByPrimaryKey(s02m046key);

        // 画像ファイル名の設定
        imageFileNames.add(s02m046entity.getSshdshhitoimage());

        // 印刷対象リストの数分繰り返し処理を行う。
        for (D1503W01PrintDetailDto parammodellist : parammodel.getPrintDetailDtoList()) {

            // 請求残高テーブル検索（消込残高取得）
            String keshikomizd = getKeshikomizd(parammodel, parammodellist);

            // 請求残高テーブル更新
            updateTable(parammodel, parammodellist);

            // 請求残高テーブル検索（督促状データ取得）
            List<S02M017TD1503R01Entity> meisai = chohyoMeisai(parammodel, parammodellist);

            // 印刷判定処理フラグ
            String printFlag = "0";

            // 請求残高テーブルの検索件数分繰り返し処理を行う。
            for (S02M017TD1503R01Entity e : meisai) {

                if ("0".equals(keshikomizd)) {
                    // CSVファイル（督促状）を出力する。
                    datalist = outputCsv(s02m046entity, parammodel, parammodellist, keshikomizd, e,
                        datalist, "督促状");

                    // 控え印刷が"1：する"の場合、CSVファイル（督促状（控））を出力する。
                    if ("1".equals(parammodel.getHikaeprnt())) {
                        datalisthikae = outputCsv(s02m046entity, parammodel, parammodellist,
                            keshikomizd, e, datalisthikae, "督促状（控）");
                    }

                } else {

                    // 請求残高テーブル.消込残高が"0"以外の場合、印刷対象の判定処理を行う。
                    if ("0".equals(printFlag)) {
                        // 消込残高=0の判定
                        if ("0".equals(e.getKeshikomizd())) {
                            continue;
                        } else {
                            // 消込残高が"0"以外のレコードであれば、次レコードも印刷対象とする。
                            printFlag = "1";
                        }
                    }
                }

                // 消込残高が"0"以外で印刷対象である場合のみ、下記の処理を行う。
                // CSVファイル（督促状）を出力する。
                datalist = outputCsv(s02m046entity, parammodel, parammodellist, keshikomizd, e,
                    datalist, "督促状");

                // 控え印刷が"1：する"の場合、CSVファイル（督促状（控））を出力する。
                if ("1".equals(parammodel.getHikaeprnt())) {
                    datalisthikae = outputCsv(s02m046entity, parammodel, parammodellist,
                        keshikomizd, e, datalisthikae, "督促状（控）");
                }
            }
        }
        // 督促状（控）をリストに追加する。
        datalist.addAll(datalisthikae);
        // 帳票（PDFファイル）を出力する
        super.createReportFile(datalist, commonInfo.getFileNamne(), imageFileNames);
    }

    /**
     * 消込残高を取得する。
     * 
     * @param parammodel 印刷条件Dto
     * @param parammodelList 印刷条件リストDto
     */
    private String getKeshikomizd(D1503W01PrintContentsDto parammodel,
        D1503W01PrintDetailDto parammodellist) {

        return s07t001tdao.selectByD1503R01keshikomizd(parammodel.getApkihoncd(),
            parammodel.getShopcd(), parammodellist.getSkysakicd());
    }

    /**
     * 請求残高テーブルを更新する。
     * 
     * @param parammodel 印刷条件Dto
     * @param parammodelList 印刷条件リストDto
     */
    @Transactional
    private void updateTable(D1503W01PrintContentsDto parammodel,
        D1503W01PrintDetailDto parammodellist) {

        // システム日時を設定
        parammodel.setSystemDate(DateTimeUtil.changeFormatToSystemDate(LocalDateTime.now()));
        parammodel.setSystemTime(DateTimeUtil.changeFormatToSystemTime(LocalDateTime.now()));

        try {

            // 更新情報取得
            S07T001TD1503R01Entity entity = createUpdSetS07T001TEntity(parammodel, parammodellist);

            // UPDATE実行
            s07t001tdao.updateByD1503R01Tokusokujoprntdd(entity);

        } catch (RuntimeException e) {
            throw BusinessExceptionFactory.validationFailed(MessageID.E002);
        }
    }

    /**
     * 請求残高テーブル更新時の情報を設定する。
     * 
     * @param parammodel 印刷条件Dto
     * @param parammodelList 印刷条件リストDto
     * @return S07T001D1503R01Entity
     */
    private S07T001TD1503R01Entity createUpdSetS07T001TEntity(D1503W01PrintContentsDto parammodel,
        D1503W01PrintDetailDto parammodellist) {

        S07T001TD1503R01Entity entity = new S07T001TD1503R01Entity();
        entity.setJigyoshocd(parammodellist.getJigyoshocd()); // 事業所コード
        entity.setApkihoncd(parammodel.getApkihoncd()); // AP基本コード
        entity.setShopcd(parammodel.getShopcd()); // ショップコード
        entity.setSkysakicd(parammodellist.getSkysakicd()); // 請求先コード
        entity.setShimedydm(parammodellist.getShimedydm().substring(0, 6)); // 締年月（YYYYMM）
        entity.setShimedd(parammodellist.getShimedydm().substring(6, 8)); // 締日（DD）
        entity.setTokusokujoprntdd(parammodel.getUnyoDate()); // 督促状印刷日
        entity.setKoshaapkihoncd(parammodel.getLoginUserApkihoncd()); // 更新者AP基本コード
        entity.setKoshashopcd(parammodel.getLoginUserShopcd()); // 更新者ショップコード
        entity.setKoshacd(parammodel.getLoginUserTnshacd()); // 更新者コード
        entity.setKshnsystmymd(parammodel.getSystemDate()); // システム日付
        entity.setKshnsystmjikan(parammodel.getSystemTime()); // システム時間
        entity.setKshnunyoymd(parammodel.getUnyoDate());// 更新運用日付
        entity.setKshnipadrse(parammodel.getIpAddress());// 更新IPアドレス
        return entity;
    }

    /**
     * 督促状データの取得
     * 
     * @param parammodel 印刷条件Dto
     * @param parammodelList 印刷条件リストDto
     * @return List<S02M017TD1503R01Entity>
     */
    private List<S02M017TD1503R01Entity> chohyoMeisai(D1503W01PrintContentsDto parammodel,
        D1503W01PrintDetailDto parammodellist) {

        return s02m017tdao.selectByD1503R01meisai(parammodel.getApkihoncd(), parammodel.getShopcd(),
            parammodellist.getSkysakicd(), parammodellist.getTairyukbn());
    }

    /**
     * CSV出力（督促状／督促状（控））
     * 
     * @param s02m046entity
     * @param parammodel
     * @param parammodellist
     * @param keshikomizd
     * @param e
     * @param datalist
     * @param prntTitle
     * @return List<List<String>>
     */
    private List<List<String>> outputCsv(S02M046TEntity s02m046entity,
        D1503W01PrintContentsDto parammodel, D1503W01PrintDetailDto parammodellist,
        String keshikomizd, S02M017TD1503R01Entity e, List<List<String>> datalist,
        String prnttitle) {

        List<String> columns = new ArrayList<>();

        // 発行日
        columns.add(ListCreatorUtil.wrapQuote(e.getCurrdate()));
        // 帳票タイトル
        columns.add(ListCreatorUtil.wrapQuote(prnttitle));
        // 郵便番号
        columns.add(ListCreatorUtil.wrapQuote(e.getYubinno()));
        // ロゴ
        columns.add(ItemUtil.nullToString(s02m046entity.getSshdshhitoimage()));
        // 住所１
        columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getAdrsn1())));
        // 住所２
        columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getAdrsn2())));
        // 住所付帯情報１
        columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getAdrsn3())));
        // AP基本コード
        columns.add(ListCreatorUtil.wrapQuote(parammodel.getApkihoncd()));
        // ショップコード
        columns.add(ListCreatorUtil.wrapQuote(parammodel.getShopcd()));
        // 請求先コード１
        columns.add(ListCreatorUtil.wrapQuote(parammodellist.getSkysakicd()));
        // 請求先名称１
        columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getSkysakinmn1())));
        // 請求先名称２
        columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getSkysakinmn2())));
        // 敬称
        columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getSkysakikeishokbnnm())));
        // 郵便番号バーコード
        columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getYubinnobakodo())));
        // 伝票お知らせ内容１
        columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getMojisetteiatai1())));
        // 伝票お知らせ内容２
        columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getMojisetteiatai2())));
        // 伝票お知らせ内容３
        columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getMojisetteiatai3())));
        // 伝票お知らせ内容４
        columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getMojisetteiatai4())));
        // 伝票お知らせ内容５
        columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getMojisetteiatai5())));
        // 消込残高が"0"でない場合設定する。
        if (!"0".equals(keshikomizd)) {
            // ご請求月
            columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getShimedydm() + "01")));
            // 前回ご請求額
            columns.add(ItemUtil.nullToString(e.getZnkkurikoshikin()));
            // ご請求金額
            columns.add(ItemUtil.nullToString(e.getSeikyukin()));
            // ご入金額
            columns.add(ItemUtil.nullToString(e.getKonkainyknkin()));
            // ご請求残高額
            columns.add(ItemUtil.nullToString(e.getKurikoshikin()));
        } else {
            // ご請求月
            columns.add("");
            // 前回ご請求額
            columns.add("");
            // ご請求金額
            columns.add("");
            // ご入金額
            columns.add("");
            // ご請求残高額
            columns.add("");
        }
        // お支払期日
        columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(parammodel.getShrymd())));
        // 御請求額
        columns.add(ItemUtil.nullToString(e.getTairyukin()));

        // ACCIS2023-7283 バーチャル口座対応に伴う振込先設定
        setTransferAccountToList(columns, parammodel, parammodellist, e);

        datalist.add(columns);

        return datalist;

    }

    /**
     * CSVデータ（List）に振込先に関する情報を設定する
     * 
     * @param columns CSVデータ1行分のリスト
     * @param parammodel 督促状検索条件DTO
     * @param parammodellist 督促状（印刷対象リスト）
     * @param e 請求残高データ
     */
    private void setTransferAccountToList(List<String> columns, D1503W01PrintContentsDto parammodel,
        D1503W01PrintDetailDto parammodellist, S02M017TD1503R01Entity e) {

        // 請求先のバーチャル口座情報を取得
        D0503W02VirtualAccountDto vaDto = vaService.getVirtualAccount(parammodel.getApkihoncd(),
            parammodel.getShopcd(), parammodellist.getSkysakicd());

        if (vaDto.isUsed()) {
            // 請求先がバーチャル口座を利用する場合
            // バーチャル口座の情報を振込先①として設定
            // 銀行名称１
            columns.add(
                ItemUtil.nullToString(ListCreatorUtil.wrapQuote(vaService.getBankName(vaDto))));
            // 支店名称１
            columns.add(
                ItemUtil.nullToString(ListCreatorUtil.wrapQuote(vaService.getBranchName(vaDto))));
            // バーチャル口座利用時、督促状は下記項目を設定しない
            // 口座種別１
            columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(null)));
            // 振込口座番号１
            columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(null)));
            // 銀行名称２
            columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(null)));
            // 支店名称２
            columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(null)));
            // 口座種別２
            columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(null)));
            // 振込口座番号２
            columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(null)));
        } else {
            // 銀行名称１
            columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getKinyukknnm())));
            // 支店名称１
            columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getKinyukknshitennm())));
            // 口座種別１
            columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getKinyukknkzkindnm())));
            // 振込口座番号１
            columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getKinyukknkzno())));
            // 銀行名称２
            columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getKinyukknn2nm())));
            // 支店名称２
            columns
                .add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getKinyukknn2shitennm())));
            // 口座種別２
            columns
                .add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getKinyukknn2kzkindnm())));
            // 振込口座番号２
            columns.add(ItemUtil.nullToString(ListCreatorUtil.wrapQuote(e.getKinyukknn2kzno())));
        }
    }
}
