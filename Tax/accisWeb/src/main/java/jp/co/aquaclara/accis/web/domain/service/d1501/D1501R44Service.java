package jp.co.aquaclara.accis.web.domain.service.d1501;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501R30ListResultEntity;

/**
 * 帳票出力 ご利用明細書(引落予定額印刷なし)-Service.
 * 
 * @author accisdev047
 *
 */
@Service
public class D1501R44Service {
    /**
     * 帳票出力 請求書（1行明細）-サービスクラス
     */
    @Autowired
    private D1501R32Service d1501r32Service;

    /**
     * ご利用明細書(引落予定額印刷なし)CSVを作成する
     * 
     * @param entityList S07T001TD1501R40ResultEntityList
     * @param shimedydm 締年月
     * @param skyshimeday 締日
     * @param reportType 帳票区分
     * @param invoiceEnforcementFlg インボイス施行フラグ
     * @param creditPostingDay クレジット計上日
     * @param noramlTaxRate 通常税率
     * @param reducedTaxRate 軽減税率
     * @param transeferInfoFlag 振込情報印刷フラグ
     * @param systemDate システム時間
     * @param sshdshhitoImage 差出人イメージ
     * @param prntpaykbn お振込先印刷区分
     * @param tairyuoverprntkbn 滞留超過額の印刷区分
     * @return 中間データリスト
     */
    public ArrayList<List<String>> printDataEdit(List<S07T001TD1501R30ListResultEntity> entityList,
        String shimedydm, String skyshimeday, String reportType, String invoiceEnforcementFlg,
        int creditPostingDay, String noramlTaxRate, String reducedTaxRate, String transeferInfoFlag,
        String systemDate, String sshdshhitoImage, String prntpaykbn, String tairyuoverprntkbn) {
        // 中間データ作成処理
        ArrayList<List<String>> printDataList = d1501r32Service.printDataEdit(entityList, shimedydm,
            skyshimeday, reportType, invoiceEnforcementFlg, Integer.toString(creditPostingDay),
            noramlTaxRate, reducedTaxRate, transeferInfoFlag, systemDate, sshdshhitoImage,
            prntpaykbn, tairyuoverprntkbn);
        return printDataList;
    }

}
