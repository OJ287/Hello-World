package jp.co.aquaclara.accis.web.domain.service.c1006;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TC1006W01ConditionEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TC1006W01CsvDataEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501W00KaishuHohoConditionEntity;
import jp.co.aquaclara.accis.common.exception.BusinessException;
import jp.co.aquaclara.accis.common.util.ListCreatorUtil;
import jp.co.aquaclara.accis.web.domain.dto.c1006.C1006W01SearchConditionDto;
import jp.co.aquaclara.accis.web.domain.service.common.OnlineBatchBaseService;
import jp.co.aquaclara.accis.web.domain.service.common.OnlineBatchCommonModel;

/**
 * 非同期処理C1006R01 入金消込履歴データCSV（CSV出力）-OnlineBatchサービスクラス
 */
@Service
public class C1006R01Service extends OnlineBatchBaseService<C1006W01SearchConditionDto> {

    /**
     * 処理ID
     */
    private final String processId = "C1006B01";

    /**
     * 請求残高テーブルDAO
     */
    @Autowired
    private S07T001TDao s07T001TDao;

    /**
     * C1006W01 入金管理サービスクラス
     */
    @Autowired
    private C1006W01Service service;

    /**
     * 処理ID取得
     */
    @Override
    public String getProcessId() {
        return this.processId;
    }

    /**
     * CSV出力処理
     * 
     * @param dto 入金管理検索条件Dto
     * @param commonInfo 非同期処理共有情報
     * @throws Exception システム例外
     * @throws BusinessException 業務例外
     */
    @Override
    protected void process(C1006W01SearchConditionDto dto, OnlineBatchCommonModel commonInfo)
        throws BusinessException, Exception {

        // 見出し情報をCSVリストに追加する
        List<String> titleList = new ArrayList<>();
        titleList.add("ＡＰ基本コード");
        titleList.add("ショップコード");
        titleList.add("事業所コード");
        titleList.add("請求先コード");
        titleList.add("請求先名称");
        titleList.add("請求先名称カナ");
        titleList.add("請求締日");
        titleList.add("請求書No");
        titleList.add("得意先コード");
        titleList.add("得意先名称");
        titleList.add("税込売上金額");
        titleList.add("消込残額");
        titleList.add("入金日");
        titleList.add("消込日");
        titleList.add("入金種別");
        titleList.add("消込種別");
        titleList.add("消込金額");
        titleList.add("入金伝票No");
        // CSVデータを格納するCSVリストを初期化する
        List<List<String>> csvList = new ArrayList<>();
        // タイトル項目名をCSVリストに追加する
        csvList.add(titleList);

        // 回収方法選択リスト取得
        List<S07T001TD1501W00KaishuHohoConditionEntity> kaishuHohoList =
            S07T001TD1501W00KaishuHohoConditionEntity.create(dto.getKaishuhohokbn1(),
                dto.getKaishuhohokbn2(), dto.getKaishuhohokbn3(), dto.getKaishuhohokbn4(),
                dto.getKaishuhohokbn5(), dto.getKaishuhohokbn6(), dto.getKaishuhohokbn7(),
                dto.getKaishuhohokbn8(), dto.getKaishuhohokbn9(), dto.getKaishuhohokbn10(),
                dto.getKaishuhohokbn11(), dto.getCrdtsentaku(), dto.getKzfurikaesentaku());

        // 入金管理一覧検索条件Entityの各項目へ値を設定する
        S07T001TC1006W01ConditionEntity entity = service.setEntity(dto, kaishuHohoList);

        // CSV出力情報を取得する
        List<S07T001TC1006W01CsvDataEntity> entityList = s07T001TDao.selectForC1006R01List(entity);
        // List<入金消込履歴データCSV情報Entity>の全データからCSVリストを作成する
        // List<入金消込履歴データCSV情報Entity>のデータ全件に対して以下を行う
        for (S07T001TC1006W01CsvDataEntity result : entityList) {
            List<String> list = new ArrayList<>();
            // ＡＰ基本コード
            list.add(ListCreatorUtil.wrapQuote(result.getApkihoncd()));
            // ショップコード
            list.add(ListCreatorUtil.wrapQuote(result.getShopcd()));
            // 事業所コード
            list.add(ListCreatorUtil.wrapQuote(result.getJigyoshosentaku()));
            // 請求先コード
            list.add(ListCreatorUtil.wrapQuote(result.getSkysakicd()));
            // 請求先名称
            list.add(ListCreatorUtil.wrapQuote(result.getSkysakinmn()));
            // 請求先名称カナ
            list.add(ListCreatorUtil.wrapQuote(result.getSkysakinmkana()));
            // 請求締日
            list.add(ListCreatorUtil.wrapQuote(result.getShimeymd()));
            // 請求書No
            list.add(ListCreatorUtil.wrapQuote(result.getSkyshono()));
            // 得意先コード
            list.add(ListCreatorUtil.wrapQuote(result.getTokuisakicd()));
            // 得意先名称
            list.add(ListCreatorUtil.wrapQuote(result.getTokuisakinmn1()));
            // 税込売上金額
            list.add(ListCreatorUtil.wrapQuote(nullToString(result.getSumkin())));
            // 消込残額
            list.add(ListCreatorUtil.wrapQuote(nullToString(result.getKeshikomizd())));
            // 入金日
            list.add(ListCreatorUtil.wrapQuote(result.getNyknymd()));
            // 消込日
            list.add(ListCreatorUtil.wrapQuote(result.getKshkmdd()));
            // 入金種別
            list.add(ListCreatorUtil.wrapQuote(result.getNykinkind()));
            // 消込種別
            list.add(ListCreatorUtil.wrapQuote(result.getKskinkind()));
            // 消込金額
            list.add(ListCreatorUtil.wrapQuote(nullToString(result.getKeshikomikin())));
            // 入金伝票No
            list.add(ListCreatorUtil.wrapQuote(result.getDenpyono()));
            // CSV行データをCSVリストに追加する
            csvList.add(list);
        }

        // CSVファイルを出力する
        super.createCsvFile(csvList, commonInfo.getFileNamne());
    }

    /**
     * nullの場合空文字を取得
     * 
     * @param value チェックする値
     * @return valueがnullの場合は空文字を、そうでない場合元のvalue値を文字列に変換して返す
     */
    private String nullToString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
