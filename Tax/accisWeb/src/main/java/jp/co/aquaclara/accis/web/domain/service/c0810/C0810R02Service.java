package jp.co.aquaclara.accis.web.domain.service.c0810;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S04T001TC0810R00DeliveryEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S04T001TC0810R00HandoutsEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S04T001TC0810R00SvInfoEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S04T001TC0810R00SysMstEntity;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.web.domain.dto.c0809.C0809R40PrintContentsDto;
import jp.co.aquaclara.accis.web.domain.dto.c0809.C0809R40PrintDetailDto;
import jp.co.aquaclara.accis.web.domain.dto.c0810.C0810R00ResultSvInfoDto;

/**
 * C0810R02_納品書兼受領書（CSV)サービスクラス
 */
@Service
public class C0810R02Service {

    /**
     * 受注配達テーブルDAOクラス
     */
    @Autowired
    private S04T001TDao s04t001tDao;

    /**
     * 納品数量（比較用）
     */
    private String NOHINSURYO_ZERO = "0";

    /**
     * 納品書兼受領書出力処理
     * 
     * @param detail 印刷対象明細DTO
     * @param parammodel 印刷対象DTO
     * @param unyodate 運用日付
     * @param tnshaPrint 担当者印刷
     * @param invoiceFlg インボイス施行フラグ
     * @param sender 差出人イメージ
     * @param invoiceBizInsNo インボイス事業者登録番号
     * @return 中間CSVリスト
     */
    public List<List<String>> print(C0809R40PrintDetailDto detail,
        C0809R40PrintContentsDto parammodel, String unyodate, String tnshaPrint, String invoiceFlg,
        String sender, String invoiceBizInsNo) {

        // 印刷対象データマップ作成
        HashMap<String, String> dateMap = new HashMap<>();

        // 納品書兼受領書データ取得
        List<S04T001TC0810R00DeliveryEntity> deliveryEntity =
            getStatementofDelivery(detail, unyodate, invoiceFlg);
        // サーバー情報データ取得
        C0810R00ResultSvInfoDto svInfoDto = getServerInfo(detail);
        // 配布物情報データ取得
        List<S04T001TC0810R00HandoutsEntity> handoutsEntity = getHandoutsInfo(detail);
        // システム管理マスタデータ取得
        S04T001TC0810R00SysMstEntity sysMstEntity = getSystemMaster(detail);
        // サーバー情報マップ設定
        getSvInfoMapper(svInfoDto, dateMap);
        // 配布物情報マップ設定
        getHandoutsMapper(handoutsEntity, dateMap);
        // システム管理マスタマップ設定
        getSysMstMapper(sysMstEntity, dateMap);

        // 納品書兼受領書中間CSV作成
        return createDeliveryData(deliveryEntity, dateMap, detail, tnshaPrint, invoiceFlg, sender,
            invoiceBizInsNo);
    }

    /**
     * 納品書兼受領書中間CSV作成
     * 
     * @param deliveryEntity 納品書データ取得結果Entity
     * @param dateMap 印刷対象データマップ
     * @param detail 印刷対象明細DTO
     * @param tnshaPrint 担当者印刷
     * @param invoiceFlg インボイス施行フラグ
     * @param sender 差出人イメージ
     * @param invoiceBizInsNo インボイス事業者登録番号
     * @return 中間CSVリスト
     */
    private List<List<String>> createDeliveryData(
        List<S04T001TC0810R00DeliveryEntity> deliveryEntity, HashMap<String, String> dateMap,
        C0809R40PrintDetailDto detail, String tnshaPrint, String invoiceFlg, String sender,
        String invoiceBizInsNo) {

        // 納品書兼受領書中間CSV作成
        List<List<String>> csvList = new ArrayList<>();
        // 合計金額
        BigDecimal sumMoney = new BigDecimal("0");
        // 合計消費税
        BigDecimal sumTax = new BigDecimal("0");
        // 消費税計算用（８％）
        BigDecimal sumTax8per = new BigDecimal("0");
        // 消費税計算用（１０％）
        BigDecimal sumTax10per = new BigDecimal("0");
        // 未締め消費税計算用（８％）
        BigDecimal sumTaxMishime8per = new BigDecimal("0");
        // 未締め消費税計算用（１０％）
        BigDecimal sumTaxMishime10per = new BigDecimal("0");
        // 代引き残高金額
        long daibikikin = 0;
        // データ切替の判定用
        String breakKey = null;

        for (int i = 0; i < deliveryEntity.size(); i++) {
            // 伝票ステータス≠配達売上済または、伝票ステータス＝配達売上済で受注数量＜＞０の場合、出力対象とする
            if (!CommonConstants.DNPYSTS_12.equals(deliveryEntity.get(i).getDnpystsmei())
                || (CommonConstants.DNPYSTS_12.equals(deliveryEntity.get(i).getDnpystsmei())
                    && !"0".equals(deliveryEntity.get(i).getAcptsuryo()))) {
            } else {
                // 上記以外は出力対象外
                continue;
            }
            // 納品書兼受領書明細カラムリスト
            List<String> columns = new ArrayList<>();
            // 対象連絡先番号
            int trgtRenrakusakinon = 0;
            // 配達週
            StringBuffer deliveryWeek = new StringBuffer();
            // 配達曜日
            StringBuffer deliveryDate = new StringBuffer();
            // 納品書兼受領書データEntity
            S04T001TC0810R00DeliveryEntity entity = deliveryEntity.get(i);
            // 帳票ID
            columns.add("C0810R02");
            // BREAK
            if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                columns.add(ItemUtil.nullToString(entity.getUridnpyno() + 1));
            } else {
                columns.add(ItemUtil.nullToString(entity.getAcptdnpynomei() + 1));
            }
            // ロゴ
            columns.add(ItemUtil.nullToString(sender));
            // タイトル１
            if (CommonConstants.FLG_ON.equals(entity.getAcptthdaibikiflg())
                || CommonConstants.FLG_ON.equals(entity.getSingldnpyflg())) {
                columns.add("納品書兼請求書");
            } else {
                columns.add("納品書");
            }
            // 発行日
            columns.add(ItemUtil.nullToString(entity.getYmd()));
            // 伝票番号
            if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                columns.add(ItemUtil.nullToString(entity.getUridnpyno()));
            } else {
                columns.add(ItemUtil.nullToString(entity.getAcptdnpynomei()));
            }
            // 伝票番号枝番
            if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                columns.add(ItemUtil.nullToString(entity.getUridnpyedano()));
            } else {
                columns.add(ItemUtil.nullToString(entity.getEdanomei()));
            }
            // APコード
            if (CommonConstants.FLG_ON.equals(detail.getContractSts())) {
                if (StringUtils.isEmpty(detail.getTrustApkihoncd())
                    || StringUtils.isEmpty(detail.getTrustShopcd())) {
                    columns.add(ItemUtil.nullToString(detail.getTokuisakiTrustApkihoncd()));
                } else {
                    columns.add(ItemUtil.nullToString(detail.getTrustApkihoncd()));
                }
            } else {
                columns.add(ItemUtil.nullToString(detail.getApkihoncd()));
            }
            // ショップコード
            if (CommonConstants.FLG_ON.equals(detail.getContractSts())) {
                if (StringUtils.isEmpty(detail.getTrustApkihoncd())
                    || StringUtils.isEmpty(detail.getTrustShopcd())) {
                    columns.add(ItemUtil.nullToString(detail.getTokuisakiTrustShopcd()));
                } else {
                    columns.add(ItemUtil.nullToString(detail.getTrustShopcd()));
                }
            } else {
                columns.add(ItemUtil.nullToString(detail.getShopcd()));
            }
            // 得意先コード
            if (CommonConstants.FLG_ON.equals(detail.getContractSts())) {
                if (StringUtils.isEmpty(detail.getTrustApkihoncd())
                    || StringUtils.isEmpty(detail.getTrustShopcd())) {
                    columns.add(ItemUtil.nullToString(detail.getTokuisakiTrustTokuisakicd()));
                } else {
                    columns.add(ItemUtil.nullToString(detail.getTrustTokuisakicd()));
                }
            } else {
                columns.add(ItemUtil.nullToString(detail.getTokuisakicd()));
            }
            // 受託得意先コード
            if (CommonConstants.FLG_ON.equals(detail.getContractSts())) {
                columns.add(ItemUtil.nullToString(detail.getApkihoncd() + "-" + detail.getShopcd()
                    + "-" + detail.getTokuisakicd()));
            } else {
                columns.add("");
            }
            // 得意先状態１
            columns.add(ItemUtil.nullToString(dateMap.get("CUSTOMERS")));
            // 得意先郵便番号
            columns.add(ItemUtil.nullToString(entity.getYubinno()));
            // 得意先住所１
            columns.add(ItemUtil.nullToString(entity.getAdrsn1()));
            // 得意先住所２
            columns.add(ItemUtil.nullToString(entity.getAdrsn2()));
            // 得意先住所３
            columns.add(ItemUtil.nullToString(entity.getAdrsn3()));
            // 得意先電話番号１
            if (!CommonConstants.RENRAKUSAKI_FAX.equals(entity.getRenrakusakinokbnn1())) {
                columns.add(ItemUtil.nullToString(entity.getRenrakusakinon1()));
                trgtRenrakusakinon = 1;
            } else if (!CommonConstants.RENRAKUSAKI_FAX.equals(entity.getRenrakusakinokbnn2())) {
                columns.add(ItemUtil.nullToString(entity.getRenrakusakinon2()));
                trgtRenrakusakinon = 2;
            } else if (!CommonConstants.RENRAKUSAKI_FAX.equals(entity.getRenrakusakinokbnn3())) {
                columns.add(ItemUtil.nullToString(entity.getRenrakusakinon3()));
                trgtRenrakusakinon = 3;
            } else if (!CommonConstants.RENRAKUSAKI_FAX.equals(entity.getRenrakusakinokbnn4())) {
                columns.add(ItemUtil.nullToString(entity.getRenrakusakinon4()));
                trgtRenrakusakinon = 4;
            } else {
                columns.add("");
            }
            // 得意先電話番号２
            if (!CommonConstants.RENRAKUSAKI_FAX.equals(entity.getRenrakusakinokbnn1())
                && trgtRenrakusakinon != 1) {
                columns.add(ItemUtil.nullToString(entity.getRenrakusakinon1()));
            } else if (!CommonConstants.RENRAKUSAKI_FAX.equals(entity.getRenrakusakinokbnn2())
                && trgtRenrakusakinon != 2) {
                columns.add(ItemUtil.nullToString(entity.getRenrakusakinon2()));
            } else if (!CommonConstants.RENRAKUSAKI_FAX.equals(entity.getRenrakusakinokbnn3())
                && trgtRenrakusakinon != 3) {
                columns.add(ItemUtil.nullToString(entity.getRenrakusakinon3()));
            } else if (!CommonConstants.RENRAKUSAKI_FAX.equals(entity.getRenrakusakinokbnn4())
                && trgtRenrakusakinon != 4) {
                columns.add(ItemUtil.nullToString(entity.getRenrakusakinon4()));
            } else {
                columns.add("");
            }
            // 得意先名称１
            columns.add(ItemUtil.nullToString(entity.getTokuisakinmn1()));
            // 得意先名称２
            columns.add(ItemUtil.nullToString(entity.getTokuisakinmn2()));
            // 敬称
            if (!StringUtils.isEmpty(entity.getTokuisakikeishokbn())) {
                columns.add(ItemUtil.nullToString(entity.getTokuisakikeishokbnnm()));
            } else {
                columns.add("");
            }
            // 事業所名
            columns.add(ItemUtil.nullToString(entity.getJigyoshonm()));
            // 伝票お知らせ内容
            columns.add(ItemUtil.nullToString(entity.getDnpyoshirasenaiyon1()));
            // ご注文日
            if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                columns.add("");
            } else {
                columns.add(ItemUtil.nullToString(entity.getAcptdd()));
            }
            // 宅配希望日
            if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                columns.add("");
            } else {
                columns.add(ItemUtil.nullToString(entity.getDlvryytidd()));
            }
            // 特記事項
            if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                columns.add("");
            } else {
                String tokki1 = null;
                String tokki2 = null;
                // 特記事項1の設定
                if (!CommonConstants.DNPYSTS_12.equals(entity.getDnpystsmei())
                    && !StringUtils.isEmpty(entity.getDlvrykibojikan())) {
                    tokki1 = DateTimeUtil.toColonedDateHHMM(entity.getDlvrykibojikan());
                }
                // 特記事項2の設定
                if (!CommonConstants.DNPYSTS_12.equals(entity.getDnpystsmei())
                    && !StringUtils.isEmpty(entity.getDlvrykibojikanshry())
                    && CommonConstants.KBN_DLVRYKIBO_JIKANHANI
                        .equals(entity.getDlvrykibojikankbn())) {
                    tokki2 = dateMap.get("DELIVERYSYSTEMKBNNM")
                        + DateTimeUtil.toColonedDateHHMM(entity.getDlvrykibojikanshry());
                } else if (!CommonConstants.DNPYSTS_12.equals(entity.getDnpystsmei())
                    && !StringUtils.isEmpty(dateMap)) {
                    tokki2 = dateMap.get("DELIVERYSYSTEMKBNNM");
                }
                // 特記事項1+特記事項2の設定
                if (!StringUtils.isEmpty(tokki1) && StringUtils.isEmpty(tokki2)) {
                    columns.add(tokki1);
                } else if (StringUtils.isEmpty(tokki1) && !StringUtils.isEmpty(tokki2)) {
                    columns.add(tokki2);
                } else if (!StringUtils.isEmpty(tokki1) && !StringUtils.isEmpty(tokki2)) {
                    columns.add(tokki1 + tokki2);
                } else {
                    columns.add("");
                }
            }
            // 不在置
            if (!StringUtils.isEmpty(entity.getUridnpyno())
                || CommonConstants.FLG_OFF.equals(entity.getFzaiokikanoflg())) {
                columns.add("");
            } else {
                columns.add("可");
            }
            // 納品先情報１
            columns.add(ItemUtil.nullToString(dateMap.get("NATHANINFO1")));
            // 納品先情報２
            columns.add(ItemUtil.nullToString(dateMap.get("NATHANINFO2")));
            // 配達会社名
            if (CommonConstants.FLG_OFF.equals(detail.getContractSts())
                || CommonConstants.FLG_ON.equals(detail.getContractSts())
                    && StringUtils.isEmpty(entity.getShopnm())) {
                columns.add("");
            } else {
                columns.add(ItemUtil.nullToString("配達会社：" + entity.getShopnm()));
            }
            // 担当者名
            if (CommonConstants.FLG_OFF.equals(tnshaPrint)) {
                columns.add("");
            } else {
                columns.add(ItemUtil.nullToString(entity.getDlvrytnshamei()));
            }
            // 次回配達予定日
            if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                columns.add("");
            } else {
                columns.add(ItemUtil.nullToString(entity.getJikaidlvryytidd()));
            }
            // 商品名称
            if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                if (CommonConstants.FLG_OFF.equals(entity.getNyuryokumeisaiflgmei())) {
                    columns.add(
                        ItemUtil.nullToString("    " + "(" + ItemUtil.getItem(entity.getGenzshnnm(),
                            entity.getShinshnnm(), entity.getShnnmkirikaedd(), entity.getUridd())
                            + ")"));
                } else {
                    columns.add(ItemUtil.getItem(entity.getGenzshnnm(), entity.getShinshnnm(),
                        entity.getShnnmkirikaedd(), entity.getUridd()));
                }
            } else {
                if (CommonConstants.FLG_OFF.equals(entity.getNyuryokumeisaiflgmei())) {
                    columns.add(ItemUtil.nullToString("    " + "("
                        + ItemUtil.getItem(entity.getGenzshnnm(), entity.getShinshnnm(),
                            entity.getShnnmkirikaedd(), entity.getDlvryytidd())
                        + ")"));
                } else {
                    columns.add(ItemUtil.getItem(entity.getGenzshnnm(), entity.getShinshnnm(),
                        entity.getShnnmkirikaedd(), entity.getDlvryytidd()));
                }
            }
            // 税区分印
            if (CommonConstants.KBN_TAX_KEIGEN.equals(entity.getTaxkbn())) {
                columns.add("※");
            } else {
                columns.add("");
            }
            // 区分
            if (CommonConstants.KBN_KEIJO_URIAGE.equals(entity.getKeijokbn())) {
                columns.add("");
            } else {
                columns.add(ItemUtil.nullToString(entity.getKeijokbnnm()));
            }
            // 数量１
            columns.add(ItemUtil.nullToString(entity.getAcptsuryo()));
            // 単位
            columns.add(ItemUtil.nullToString(entity.getUritrtskitaninm()));
            // 単価
            if (CommonConstants.FLG_ON.equals(entity.getKinotptnashiflg())
                || CommonConstants.KBN_TOKUISAKI_KEIHI.equals(entity.getTokuisakikbn())
                || CommonConstants.FLG_OFF.equals(entity.getNyuryokumeisaiflgmei())) {
                columns.add("");
            } else {
                columns.add(ItemUtil.nullToString(entity.getTanka()));
            }
            // 金額1
            if (CommonConstants.FLG_ON.equals(entity.getKinotptnashiflg())
                || CommonConstants.KBN_TOKUISAKI_KEIHI.equals(entity.getTokuisakikbn())
                || CommonConstants.FLG_OFF.equals(entity.getNyuryokumeisaiflgmei())) {
                columns.add("");
            } else {
                if (CommonConstants.KBN_TAXKEISAN_IN.equals(entity.getTaxkeisankbn())) {
                    columns.add(ItemUtil.nullToString(entity.getUzei()));
                } else {
                    columns.add(ItemUtil.nullToString(entity.getAcptkin()));
                }
            }
            // 消費税金額
            if (CommonConstants.FLG_ON.equals(entity.getKinotptnashiflg())
                || CommonConstants.KBN_TOKUISAKI_KEIHI.equals(entity.getTokuisakikbn())
                || CommonConstants.FLG_OFF.equals(entity.getNyuryokumeisaiflgmei())
                || CommonConstants.FLG_ON.equals(invoiceFlg)) {
                columns.add("");
            } else {
                columns.add(ItemUtil.nullToString(entity.getAcptshohitaxkin()));
            }
            // 備考
            columns.add(ItemUtil.nullToString(entity.getMeisaibiko()));
            // メモ１
            if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                columns.add(
                    ItemUtil.nullToString("売上日：" + DateTimeUtil.toSlashedDate(entity.getUridd())));
            } else {
                if (!CommonConstants.KBN_PRNTMEMPOTPT_2.equals(entity.getPrntmemootptkbn())) {
                    columns.add("");
                } else {
                    columns.add(ItemUtil.nullToString(entity.getPrntmemon1()));
                }
            }
            // メモ２
            if (!StringUtils.isEmpty(entity.getUridnpyno())
                || !CommonConstants.KBN_PRNTMEMPOTPT_2.equals(entity.getPrntmemootptkbn())) {
                columns.add("");
            } else {
                columns.add(ItemUtil.nullToString(entity.getPrntmemon2()));
            }
            // メモ３
            if (!StringUtils.isEmpty(entity.getUridnpyno())
                || !CommonConstants.KBN_PRNTMEMPOTPT_2.equals(entity.getPrntmemootptkbn())) {
                columns.add("");
            } else {
                columns.add(ItemUtil.nullToString(entity.getPrntmemon3()));
            }
            // 配達エリア
            columns.add(ItemUtil.nullToString(entity.getDlvryareanm()));
            // 配達ＷＥＥＫ
            if (CommonConstants.FLG_ON.equals(entity.getDlvryshua())) {
                deliveryWeek.append(CommonConstants.NM_DLVRYWEEK_A);
            }
            if (CommonConstants.FLG_ON.equals(entity.getDlvryshub())) {
                deliveryWeek.append(CommonConstants.NM_DLVRYWEEK_B);
            }
            if (CommonConstants.FLG_ON.equals(entity.getDlvryshuc())) {
                deliveryWeek.append(CommonConstants.NM_DLVRYWEEK_C);
            }
            if (CommonConstants.FLG_ON.equals(entity.getDlvryshud())) {
                deliveryWeek.append(CommonConstants.NM_DLVRYWEEK_D);
            }
            if (!StringUtils.isEmpty(deliveryWeek)) {
                columns.add(deliveryWeek.toString());
            } else {
                columns.add("");
            }
            // 配達可能日
            if (CommonConstants.FLG_ON.equals(entity.getSunddlvrykanodd())) {
                deliveryDate.append(CommonConstants.DDLVRYKANODD_SUN);
            }
            if (CommonConstants.FLG_ON.equals(entity.getMonddlvrykanodd())) {
                deliveryDate.append(CommonConstants.DDLVRYKANODD_MON);
            }
            if (CommonConstants.FLG_ON.equals(entity.getTueddlvrykanodd())) {
                deliveryDate.append(CommonConstants.DDLVRYKANODD_TUE);
            }
            if (CommonConstants.FLG_ON.equals(entity.getWedddlvrykanodd())) {
                deliveryDate.append(CommonConstants.DDLVRYKANODD_WED);
            }
            if (CommonConstants.FLG_ON.equals(entity.getTheddlvrykanodd())) {
                deliveryDate.append(CommonConstants.DDLVRYKANODD_THE);
            }
            if (CommonConstants.FLG_ON.equals(entity.getFriddlvrykanodd())) {
                deliveryDate.append(CommonConstants.DDLVRYKANODD_FRI);
            }
            if (CommonConstants.FLG_ON.equals(entity.getSatddlvrykanodd())) {
                deliveryDate.append(CommonConstants.DDLVRYKANODD_SAT);
            }
            if (!StringUtils.isEmpty(deliveryDate)) {
                columns.add(deliveryDate.toString());
            } else {
                columns.add("");
            }
            // 地図番号
            if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                columns.add("");
            } else {
                columns.add(ItemUtil.nullToString(entity.getChizuno()));
            }
            // 配達順
            if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                columns.add("");
            } else {
                columns.add(ItemUtil.nullToString(entity.getDlvryjun()));
            }
            // 担当営業
            columns.add(ItemUtil.nullToString(entity.getEigyotnshamei()));
            // 会員お申込日
            columns.add(ItemUtil.nullToString(entity.getNyukaidd()));
            // お支払方法
            columns.add(ItemUtil.nullToString(dateMap.get("COLLECTIONMETHOD")));
            // 手続き中表示
            if (CommonConstants.KAISHUHOHOKBN_2.equals(entity.getKaishuhohokbn())
                && CommonConstants.FLG_ON.equals(entity.getKsikkntetsuzukiflg())) {
                columns.add("手続中");
            } else {
                columns.add("");
            }
            // 請求締日
            columns.add(ItemUtil.nullToString(entity.getSkyshimedd()));
            // 支払サイト
            columns.add(ItemUtil.nullToString(entity.getShrsaitonm()));
            // 支払予定日
            columns.add(ItemUtil.nullToString(entity.getShrdd()));
            // 配布物１
            if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                columns.add("");
            } else {
                // 伝票ステータス(12:配達・売上済)以外 かつ 配布物名称が未設定でない場合、配布物１の名称を設定
                if (!CommonConstants.DNPYSTS_12.equals(entity.getDnpystsmei())
                    && !StringUtils.isEmpty(dateMap.get("DISTRIBUTENM0"))) {
                    columns.add(dateMap.get("DISTRIBUTENM0"));
                    // 伝票ステータス(12:配達・売上済) かつ 納品数量が"0"でない かつ 配布物名称が未設定でない場合、配布物１の名称を設定
                } else if (CommonConstants.DNPYSTS_12.equals(entity.getDnpystsmei())
                    && !NOHINSURYO_ZERO.equals(dateMap.get("NATHANITEM0"))
                    && !StringUtils.isEmpty(dateMap.get("DISTRIBUTENM0"))) {
                    columns.add(dateMap.get("DISTRIBUTENM0"));
                } else {
                    // 上記以外の場合、空文字を設定
                    columns.add("");
                }
            }
            // 配布物２
            if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                columns.add("");
            } else {
                // 伝票ステータス(12:配達・売上済)以外 かつ 配布物名称が未設定でない場合、配布物２の名称を設定
                if (!CommonConstants.DNPYSTS_12.equals(entity.getDnpystsmei())
                    && !StringUtils.isEmpty(dateMap.get("DISTRIBUTENM1"))) {
                    columns.add(dateMap.get("DISTRIBUTENM1"));
                    // 伝票ステータス(12:配達・売上済) かつ 納品数量が"0"でない かつ 配布物名称が未設定でない場合、配布物２の名称を設定
                } else if (CommonConstants.DNPYSTS_12.equals(entity.getDnpystsmei())
                    && !NOHINSURYO_ZERO.equals(dateMap.get("NATHANITEM1"))
                    && !StringUtils.isEmpty(dateMap.get("DISTRIBUTENM1"))) {
                    columns.add(dateMap.get("DISTRIBUTENM1"));
                } else {
                    // 上記以外の場合、空文字を設定
                    columns.add("");
                }
            }
            // 配布物３
            if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                columns.add("");
            } else {
                // 伝票ステータス(12:配達・売上済)以外 かつ 配布物名称が未設定でない場合、配布物３の名称を設定
                if (!CommonConstants.DNPYSTS_12.equals(entity.getDnpystsmei())
                    && !StringUtils.isEmpty(dateMap.get("DISTRIBUTENM2"))) {
                    columns.add(dateMap.get("DISTRIBUTENM2"));
                    // 伝票ステータス(12:配達・売上済) かつ 納品数量が"0"でない かつ 配布物名称が未設定でない場合、配布物３の名称を設定
                } else if (CommonConstants.DNPYSTS_12.equals(entity.getDnpystsmei())
                    && !NOHINSURYO_ZERO.equals(dateMap.get("NATHANITEM2"))
                    && !StringUtils.isEmpty(dateMap.get("DISTRIBUTENM2"))) {
                    columns.add(dateMap.get("DISTRIBUTENM2"));
                } else {
                    // 上記以外の場合、空文字を設定
                    columns.add("");
                }
            }
            // 配布物４
            if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                columns.add("");
            } else {
                // 伝票ステータス(12:配達・売上済)以外 かつ 配布物名称が未設定でない場合、配布物４の名称を設定
                if (!CommonConstants.DNPYSTS_12.equals(entity.getDnpystsmei())
                    && !StringUtils.isEmpty(dateMap.get("DISTRIBUTENM3"))) {
                    columns.add(dateMap.get("DISTRIBUTENM3"));
                    // 伝票ステータス(12:配達・売上済) かつ 納品数量が"0"でない かつ 配布物名称が未設定でない場合、配布物４の名称を設定
                } else if (CommonConstants.DNPYSTS_12.equals(entity.getDnpystsmei())
                    && !NOHINSURYO_ZERO.equals(dateMap.get("NATHANITEM3"))
                    && !StringUtils.isEmpty(dateMap.get("DISTRIBUTENM3"))) {
                    columns.add(dateMap.get("DISTRIBUTENM3"));
                } else {
                    // 上記以外の場合、空文字を設定
                    columns.add("");
                }
            }
            // 代引き項目名
            if (CommonConstants.FLG_ON.equals(invoiceFlg)) {
                columns.add("");
            } else {
                if (CommonConstants.KBN_ACPTSYS_TAIKAIHIKIAGE.equals(detail.getAcptkbnshs())) {
                    columns.add("現在残高");
                } else {
                    if (CommonConstants.FLG_ON.equals(entity.getKinotptnashiflg())
                        || (!CommonConstants.KAISHUHOHOKBN_7.equals(entity.getKaishuhohokbn())
                            && !CommonConstants.KBN_ACPTSYS_TAIKAIHIKIAGE
                                .equals(detail.getAcptkbnshs()))) {
                        columns.add("");
                    } else {
                        columns.add("代引き残高");
                    }
                }
            }
            // 代引き残高日付格納
            if (CommonConstants.FLG_ON.equals(invoiceFlg)) {
                columns.add("");
            } else {
                if (CommonConstants.FLG_ON.equals(entity.getKinotptnashiflg())
                    || (!CommonConstants.KAISHUHOHOKBN_7.equals(entity.getKaishuhohokbn())
                        && !CommonConstants.KBN_ACPTSYS_TAIKAIHIKIAGE
                            .equals(detail.getAcptkbnshs()))) {
                    columns.add("");
                } else {
                    columns.add(ItemUtil.nullToString(entity.getZnkshimedd()));
                }
            }
            // 代引き残高金額格納
            if (CommonConstants.FLG_ON.equals(invoiceFlg)) {
                columns.add("");
            } else {
                if (CommonConstants.KBN_ACPTSYS_TAIKAIHIKIAGE.equals(detail.getAcptkbnshs())) {
                    if (CommonConstants.KBN_KINDSPLY_0
                        .equals(entity.getTaikaithmikaishukindsplykbn())) {
                        columns.add("");
                    } else {
                        columns.add(ItemUtil.nullToString(entity.getZnkkurikoshikin()));
                    }
                } else {
                    if (CommonConstants.FLG_ON.equals(entity.getKinotptnashiflg())
                        || (!CommonConstants.KAISHUHOHOKBN_7.equals(entity.getKaishuhohokbn())
                            && !CommonConstants.KBN_ACPTSYS_TAIKAIHIKIAGE
                                .equals(detail.getAcptkbnshs()))) {
                        columns.add("");
                    } else {
                        columns.add(ItemUtil.nullToString(entity.getZnkkurikoshikin()));
                    }
                }
            }
            // 受注時代引フラグ
            if (CommonConstants.FLG_ON.equals(invoiceFlg)) {
                columns.add("");
            } else {
                if (CommonConstants.KBN_ACPTSYS_TAIKAIHIKIAGE.equals(detail.getAcptkbnshs())) {
                    columns.add("1");
                } else {
                    columns.add(ItemUtil.nullToString(entity.getAcptthdaibikiflg()));
                }
            }
            // サーバー台数
            if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                columns.add("");
            } else {
                columns.add(ItemUtil.nullToString(dateMap.get("SERVERITEM")));
            }
            // サーバー情報区分
            if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                columns.add("");
            } else {
                if (!StringUtils.isEmpty(dateMap.get("MAINTENANCEDATE"))) {
                    String dateIssued = entity.getYmd().substring(0, 6);
                    int wsvkbn = dateIssued.compareTo(dateMap.get("MAINTENANCEDATE"));
                    if (wsvkbn > 0) {
                        // 次回メンテナンスが過去月
                        columns.add("●");
                    } else if (wsvkbn == 0) {
                        // 次回メンテナンスが当月
                        columns.add("◎");
                    } else if (wsvkbn < 0) {
                        Calendar workDate = Calendar.getInstance();
                        workDate.clear();
                        // 一度カレンダー型に変換
                        workDate.set(Integer.parseInt(entity.getYmd().substring(0, 4)),
                            Integer.parseInt(entity.getYmd().substring(4, 6)) - 1,
                            Integer.parseInt(entity.getYmd().substring(6, 8)));
                        // 1カ月を加算
                        workDate.add(Calendar.MONTH, 1);
                        // Stringに再度変換
                        String hassei = new SimpleDateFormat("yyyyMM").format(workDate.getTime());
                        int workkbn = hassei.compareTo(dateMap.get("MAINTENANCEDATE"));
                        if (workkbn == 0) {
                            // 次回メンテナンスが1カ月後
                            columns.add("○");
                        } else {
                            columns.add("");
                        }
                    }
                } else {
                    columns.add("");
                }
            }
            // シリアルNo
            if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                columns.add("");
            } else {
                columns.add(ItemUtil.nullToString(dateMap.get("SERIALNO")));
            }
            // レギュラーお預けボトル数
            if (!StringUtils.isEmpty(entity.getUridnpyno())
                || CommonConstants.KBN_NOSHOAZYOKIKAZUDSPLY_0
                    .equals(entity.getNoshoazyokikazudsplykbn())) {
                columns.add("");
            } else {
                columns.add(ItemUtil.nullToString(entity.getAzukaribtln1n2rittoruhns()));
            }
            // スリムお預けボトル数
            if (!StringUtils.isEmpty(entity.getUridnpyno())
                || CommonConstants.KBN_NOSHOAZYOKIKAZUDSPLY_0
                    .equals(entity.getNoshoazyokikazudsplykbn())) {
                columns.add("");
            } else {
                columns.add(ItemUtil.nullToString(entity.getAzukaribtln8rittoruhns()));
            }
            // レギュラー回収予定
            if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                columns.add("");
            } else {
                columns.add(ItemUtil.nullToString(entity.getKybtln1n2rittoruhns()));
            }
            // スリム回収予定
            if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                columns.add("");
            } else {
                columns.add(ItemUtil.nullToString(entity.getKybtln8rittoruhns()));
            }
            // 前回配達日
            if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                columns.add("");
            } else {
                columns.add(ItemUtil.nullToString(entity.getZnkdlvrydd()));
            }
            // 前回ご注文本数
            if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                columns.add("");
            } else if (CommonConstants.DNPYSTS_12.equals(entity.getDnpystsmei())) {
                columns.add("0");
            } else {
                columns.add(ItemUtil.nullToString(entity.getZnkdlvryhns()));
            }
            // タイトル２
            columns.add("受領書");
            // 滞留区分
            if (!CommonConstants.FLG_ON.equals(detail.getContractSts())) {
                if (CommonConstants.TAIRYUKBN_1.equals(entity.getTairyukbn())) {
                    columns.add("A");
                } else if (CommonConstants.TAIRYUKBN_2.equals(entity.getTairyukbn())) {
                    columns.add("B");
                } else if (CommonConstants.TAIRYUKBN_3.equals(entity.getTairyukbn())) {
                    columns.add("C");
                } else {
                    columns.add("");
                }
            } else {
                columns.add("");
            }
            // 注意得意先区分AP
            if (CommonConstants.CHUITOKUISAKIKBNAP_1.equals(entity.getChuitokuisakikbnap())) {
                columns.add("1");
            } else if (CommonConstants.CHUITOKUISAKIKBNAP_2
                .equals(entity.getChuitokuisakikbnap())) {
                columns.add("2");
            } else {
                columns.add("");
            }
            // 金額合計
            columns.add("");
            // 消費税金額合計
            columns.add("");
            // 消費税項目名１
            if (CommonConstants.FLG_OFF.equals(invoiceFlg)) {
                if (CommonConstants.FLG_ON.equals(entity.getKinotptnashiflg())) {
                    columns.add("");
                } else if (CommonConstants.KBN_TAXKEISAN_IN.equals(entity.getTaxkeisankbn())) {
                    columns.add("内消費税");
                } else {
                    columns.add("消費税");
                }
            } else {
                if (CommonConstants.FLG_ON.equals(entity.getAcptthdaibikiflg())
                    || CommonConstants.FLG_ON.equals(entity.getSingldnpyflg())) {
                    columns.add("消費税");
                } else {
                    columns.add("");
                }
            }
            // 内消費税合計
            columns.add("");
            // 総合計項目名
            columns.add("");
            // ８パーセント対象文言
            columns.add("");
            // ８パーセント税別金額
            columns.add("");
            // ８パーセント消費税金額
            columns.add("");
            // １０パーセント対象文言
            columns.add("");
            // １０パーセント税別金額
            columns.add("");
            // １０パーセント消費税金額
            columns.add("");
            // 金額文言１
            columns.add("");
            // 税別金額１
            columns.add("");
            // 消費税金額１
            columns.add("");
            // 金額文言２
            columns.add("");
            // 税別金額２
            columns.add("");
            // 消費税金額２
            columns.add("");
            // 金額出力無フラグ
            columns.add(entity.getKinotptnashiflg());
            // 取扱店名
            if (StringUtils.isEmpty(entity.getTokushopnm())) {
                columns.add("");
            } else {
                columns.add("取扱店：" + entity.getTokushopnm());
            }
            // 継続ランク
            if (CommonConstants.FLG_ON.equals(detail.getContractSts())) {
                columns.add("");
            } else {
                columns.add(entity.getKeizokurnk());
            }
            // 消費ランク
            if (CommonConstants.FLG_ON.equals(detail.getContractSts())) {
                columns.add("");
            } else {
                columns.add(entity.getShohirnk());
            }
            // ＡＰ別ランク
            columns.add(ItemUtil.nullToString(entity.getApbetsutokuisakirnk()));
            // 受注受付区分
            if (CommonConstants.KBN_ACPTRCPT_TSUJO.equals(entity.getAcptrcptkbn())) {
                columns.add("通常");
            } else if (CommonConstants.KBN_ACPTRCPT_TEIKI.equals(entity.getAcptrcptkbn())) {
                columns.add("定期");
            } else {
                columns.add("");
            }
            // インボイス事業者登録番号
            if (!StringUtils.isEmpty(invoiceBizInsNo)) {
                columns.add(invoiceBizInsNo);
            }
            csvList.add(columns);
        }

        // 全明細設定後に合計等を最終行に追加するため、明細カラムリストを再処理
        for (int i = 0; i < csvList.size(); i++) {
            S04T001TC0810R00DeliveryEntity entity = deliveryEntity.get(i);
            List<String> columns = csvList.get(i);

            // 項目：BREAK（伝票番号）を設定（データ切替わり判定用）
            breakKey = csvList.get(i).get(1);

            // 金額１の合計を計算
            if (!StringUtils.isEmpty(columns.get(38))) {
                sumMoney = sumMoney.add(new BigDecimal(columns.get(38).replace("\"", "")));
            }
            // 消費税金額の合計を計算
            if (!StringUtils.isEmpty(columns.get(39))) {
                sumTax = sumTax.add(new BigDecimal(columns.get(39).replace("\"", "")));
            }

            // 未締め伝票の場合、消費税レコードの消費税金額毎（８％、１０％）の金額を計算
            if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                if (CommonConstants.FLG_OFF.equals(invoiceFlg)) {
                    if (CommonConstants.KBN_TAX_KEIGEN.equals(entity.getTaxkbn())) {
                        if (!StringUtils.isEmpty(entity.getAcptshohitaxkin())) {
                            // ８％消費税金額の金額を計算
                            sumTaxMishime8per =
                                sumTaxMishime8per.add(new BigDecimal(entity.getAcptshohitaxkin()));
                        }
                    } else {
                        if (!StringUtils.isEmpty(entity.getAcptshohitaxkin())) {
                            // １０％消費税金額の金額を計算
                            sumTaxMishime10per =
                                sumTaxMishime10per.add(new BigDecimal(entity.getAcptshohitaxkin()));
                        }
                    }
                } else {
                    if (CommonConstants.KBN_TAX_KEIGEN.equals(entity.getTaxkbn())) {
                        if (!StringUtils.isEmpty(entity.getZei8permei())) {
                            // ８％消費税金額の金額を計算
                            sumTaxMishime8per = new BigDecimal(entity.getZei8permei());
                        }
                    } else {
                        if (!StringUtils.isEmpty(entity.getZei10permei())) {
                            // １０％消費税金額の金額を計算
                            sumTaxMishime10per = new BigDecimal(entity.getZei10permei());
                        }
                    }
                }
            } else {
                // インボイス施行日前の場合、消費税毎（８％、１０％）の金額を計算
                if (CommonConstants.FLG_OFF.equals(invoiceFlg)) {
                    // 入力明細フラグ'1：入力行'かつ明細区分'0：親商品'のみ計算する。
                    if (CommonConstants.NYURYOKUMEISAIFLG_1.equals(entity.getNyuryokumeisaiflgmei())
                        && CommonConstants.KBN_MEISAI_SHOHIN.equals(entity.getMeisaikbn())) {
                        if (CommonConstants.KBN_TAX_KEIGEN.equals(entity.getTaxkbn())) {
                            if (!StringUtils.isEmpty(entity.getAcptshohitaxkin())) {
                                // ８％消費税金額の金額を計算
                                sumTax8per =
                                    sumTax8per.add(new BigDecimal(entity.getAcptshohitaxkin()));
                            }
                        } else {
                            if (!StringUtils.isEmpty(entity.getAcptshohitaxkin())) {
                                // １０％消費税金額の金額を計算
                                sumTax10per =
                                    sumTax10per.add(new BigDecimal(entity.getAcptshohitaxkin()));
                            }
                        }
                    }
                }
            }

            if (!CommonConstants.KBN_TOKUISAKI_KEIHI.equals(entity.getTokuisakikbn())
                && CommonConstants.FLG_OFF.equals(entity.getKinotptnashiflg())) {

                // 処理件数と中間CSVリストのサイズが一致した場合 または 次レコードのキーと異なる場合に処理
                if (csvList.size() == i + 1 || !breakKey.equals(csvList.get(i + 1).get(1))) {

                    // 金額合計
                    if (CommonConstants.FLG_OFF.equals(invoiceFlg)) {
                        // インボイス施行日前の場合、金額１の合計を設定
                        columns.set(76, sumMoney.toString());
                    } else {
                        // インボイス施行日後の場合、金額合計を設定
                        columns.set(76, entity.getTotalkin());
                    }
                    // 消費税金額合計
                    if (CommonConstants.FLG_OFF.equals(invoiceFlg)) {
                        // 未締め伝票の場合、消費税金額の合計を設定
                        if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                            // 未締め伝票の消費税金額を加算する。
                            columns.set(77,
                                String.valueOf(Long.parseLong(sumTaxMishime8per.toString())
                                    + Long.parseLong(sumTaxMishime10per.toString())));
                        } else {
                            // インボイス施行日前の場合、消費税金額の合計を設定
                            columns.set(77, sumTax.toString());
                        }
                    } else {
                        // インボイス施行日後
                        if (!CommonConstants.FLG_ON.equals(entity.getAcptthdaibikiflg())
                            && CommonConstants.FLG_OFF.equals(entity.getSingldnpyflg())) {
                            // 受注時代引フラグが「代引き」以外且つ、単体伝票締フラグが「フラグOFF」の場合、空欄を設定
                            columns.set(77, "");
                        } else {
                            // 上記以外の場合、消費税金額合計を設定
                            columns.set(77, entity.getTotalzei());
                        }
                    }
                    // 内消費税合計
                    if (CommonConstants.FLG_OFF.equals(invoiceFlg)) {
                        // インボイス施行日前
                        if (CommonConstants.KBN_ACPTSYS_TAIKAIHIKIAGE
                            .equals(detail.getAcptkbnshs())) {
                            if (!StringUtils.isEmpty(columns.get(62))) {
                                // 受注区分詳細が「退会引上」の場合、代引き残高金額格納を設定
                                columns.set(79, columns.get(62));
                            }
                        } else {
                            // 受注区分詳細が「退会引上」以外
                            if (CommonConstants.FLG_ON.equals(entity.getAcptthdaibikiflg())
                                && CommonConstants.KBN_TAXKEISAN_IN
                                    .equals(entity.getTaxkeisankbn())) {
                                if (!StringUtils.isEmpty(columns.get(62))) {
                                    daibikikin = Long.parseLong(columns.get(62));
                                }
                                // 受注時代引フラグが「フラグON」且つ、税計算区分が「内消費税」の場合、代引き残高金額を加算した値を設定
                                columns.set(79, String
                                    .valueOf(Long.parseLong(sumMoney.toString()) + daibikikin));
                            } else if (CommonConstants.FLG_ON
                                .equals(entity.getAcptthdaibikiflg())) {
                                if (!StringUtils.isEmpty(columns.get(62))) {
                                    daibikikin = Long.parseLong(columns.get(62));
                                }
                                // 未締め伝票の場合、合計金額および消費税金額を加算した金額を設定
                                if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                                    columns.set(79,
                                        String.valueOf(Long.parseLong(sumMoney.toString())
                                            + Long.parseLong(sumTaxMishime8per.toString())
                                            + Long.parseLong(sumTaxMishime10per.toString())));
                                } else {
                                    // 受注時代引フラグが「フラグON」且つ、税計算区分が「外消費税」の場合、代引き残高金額と消費税金額を加算した値を設定
                                    columns.set(79,
                                        String.valueOf(Long.parseLong(sumMoney.toString())
                                            + Long.parseLong(sumTax.toString()) + daibikikin));
                                }
                            } else {
                                if (CommonConstants.KBN_TAXKEISAN_IN
                                    .equals(entity.getTaxkeisankbn())) {
                                    // 内税の場合、消費税金額を含む金額を設定
                                    columns.set(79,
                                        String.valueOf(Long.parseLong(sumMoney.toString())));
                                } else {
                                    // 外税の場合、金額に消費税を加算した金額を設定
                                    columns.set(79,
                                        String.valueOf(Long.parseLong(sumMoney.toString())
                                            + Long.parseLong(sumTax.toString())));
                                }
                            }
                        }
                    } else {
                        // インボイス施行日後
                        if (CommonConstants.FLG_ON.equals(entity.getAcptthdaibikiflg())
                            || CommonConstants.FLG_ON.equals(entity.getSingldnpyflg())) {
                            if (!StringUtils.isEmpty(columns.get(76))
                                && !StringUtils.isEmpty(columns.get(77))) {
                                // 受注時代引フラグが「代引き」、または、単体伝票締フラグが「フラグON」の場合、金額合計と消費税金額合計の合計値を設定
                                columns.set(79,
                                    String.valueOf(Long.parseLong(columns.get(76).replace("\"", ""))
                                        + Long.parseLong(columns.get(77).replace("\"", ""))));
                            }
                        } else {
                            // 上記以外の場合、空欄を設定（ListCreator側で固定文言を設定）
                            columns.set(79, "");
                        }
                    }
                    // 総合計項目名
                    columns.set(80, "合計");
                    // ８パーセント対象文言
                    columns.set(81, "8%対象");
                    // ８パーセント税別金額
                    // 内税の場合は消費税金額を含む金額を設定
                    if (CommonConstants.KBN_TAXKEISAN_IN.equals(entity.getTaxkeisankbn())) {
                        // 未締め伝票の場合
                        if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                            BigDecimal total8per = new BigDecimal(entity.getZeinuki8per()); // BigDecimal型に変換
                            total8per = total8per.add(sumTaxMishime8per); // 消費税金額を加算
                            columns.set(82, String.valueOf(total8per));
                        } else {
                            BigDecimal total8per = new BigDecimal(entity.getZeinuki8per()); // BigDecimal型に変換
                            total8per = total8per.add(sumTax8per); // 消費税金額を加算
                            columns.set(82, String.valueOf(total8per));
                        }
                    } else {
                        columns.set(82, entity.getZeinuki8per());
                    }
                    // ８パーセント消費税金額
                    if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                        // 未締め伝票の場合、消費税レコードの消費税金額（8%）の合計を設定
                        columns.set(83, String.valueOf(sumTaxMishime8per));
                    } else {
                        if (CommonConstants.FLG_ON.equals(invoiceFlg)) {
                            // インボイス施行日後
                            if (!CommonConstants.FLG_ON.equals(entity.getAcptthdaibikiflg())
                                && CommonConstants.FLG_OFF.equals(entity.getSingldnpyflg())) {
                                // 受注時代引フラグが「代引き」以外且つ、単体伝票締フラグが「フラグOFF」の場合、空を設定
                                columns.set(83, "");
                            } else {
                                // 上記以外の場合、8%消費税を設定
                                columns.set(83, entity.getZei8permei());
                            }
                        } else {
                            // インボイス施行日前の場合、8%消費税を設定
                            columns.set(83, String.valueOf(sumTax8per));
                        }
                    }
                    // １０パーセント対象文言
                    columns.set(84, "10%対象");
                    // １０パーセント税別金額
                    // 内税の場合は消費税金額を含む金額を設定
                    if (CommonConstants.KBN_TAXKEISAN_IN.equals(entity.getTaxkeisankbn())) {
                        // 未締め伝票の場合
                        if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                            BigDecimal total10per = new BigDecimal(entity.getZeinuki10per()); // BigDecimal型に変換
                            total10per = total10per.add(sumTaxMishime10per); // 消費税金額を加算
                            columns.set(85, String.valueOf(total10per));
                        } else {
                            BigDecimal total10per = new BigDecimal(entity.getZeinuki10per()); // BigDecimal型に変換
                            total10per = total10per.add(sumTax10per); // 消費税金額を加算
                            columns.set(85, String.valueOf(total10per));
                        }
                    } else {
                        columns.set(85, entity.getZeinuki10per());
                    }
                    // １０パーセント消費税金額
                    if (!StringUtils.isEmpty(entity.getUridnpyno())) {
                        // 未締め伝票の場合、消費税レコードの消費税金額（10%）の合計を設定
                        columns.set(86, String.valueOf(sumTaxMishime10per));
                    } else {
                        if (CommonConstants.FLG_ON.equals(invoiceFlg)) {
                            // インボイス施行日後
                            if (!CommonConstants.FLG_ON.equals(entity.getAcptthdaibikiflg())
                                && CommonConstants.FLG_OFF.equals(entity.getSingldnpyflg())) {
                                // 受注時代引フラグが「代引き」以外且つ、単体伝票締フラグが「フラグOFF」の場合、空を設定
                                columns.set(86, "");
                            } else {
                                // 上記以外の場合、10%消費税を設定
                                columns.set(86, entity.getZei10permei());
                            }
                        } else {
                            // インボイス施行日前の場合、10%消費税を設定
                            columns.set(86, String.valueOf(sumTax10per));
                        }
                    }
                    // 金額文言１
                    if (CommonConstants.FLG_OFF.equals(entity.getZeinukihikazei())) {
                        // 非課税税抜が0の場合、空を設定
                        columns.set(87, "");
                    } else {
                        // 上記以外の場合、固定文言を設定
                        columns.set(87, "非課税売上");
                    }
                    // 税別金額１
                    if (CommonConstants.FLG_OFF.equals(entity.getZeinukihikazei())) {
                        // 非課税税抜が0の場合、空を設定
                        columns.set(88, "");
                    } else {
                        // 上記以外の場合、非課税税抜を設定
                        columns.set(88, entity.getZeinukihikazei());
                    }
                    // 消費税金額１
                    if (CommonConstants.FLG_ON.equals(invoiceFlg)) {
                        // インボイス施行日後
                        if (!CommonConstants.FLG_ON.equals(entity.getAcptthdaibikiflg())
                            && CommonConstants.FLG_OFF.equals(entity.getSingldnpyflg())) {
                            // 受注時代引フラグが「代引き」以外且つ、単体伝票締フラグが「フラグOFF」の場合、空を設定
                            columns.set(89, "");
                        } else if (CommonConstants.FLG_OFF.equals(entity.getZeihikazei())) {
                            // 非課税税抜が0の場合、空を設定
                            columns.set(89, "");
                        } else {
                            // 上記以外の場合、非課税消費税
                            columns.set(89, entity.getZeihikazei());
                        }
                    } else {
                        // 税別金額１が空欄の場合、空を設定
                        if (StringUtils.isEmpty(entity.getZeihikazei())) {
                            columns.set(89, "");
                        } else {
                            // 上記以外の場合、"0"を設定
                            columns.set(89, "0");
                        }
                    }
                    // 金額文言２
                    if (CommonConstants.FLG_OFF.equals(entity.getZeinukifukazei())) {
                        // 不課税税抜が0の場合、空を設定
                        columns.set(90, "");
                    } else {
                        // 上記以外の場合、固定文言を設定
                        columns.set(90, "不課税売上");
                    }
                    // 税別金額２
                    if (CommonConstants.FLG_OFF.equals(entity.getZeinukifukazei())) {
                        // 不課税税抜が0の場合、空を設定
                        columns.set(91, "");
                    } else {
                        // 上記以外の場合、不課税税抜を設定
                        columns.set(91, entity.getZeinukifukazei());
                    }
                    // 消費税金額２
                    if (CommonConstants.FLG_ON.equals(invoiceFlg)) {
                        // インボイス施行日後
                        if (!CommonConstants.FLG_ON.equals(entity.getAcptthdaibikiflg())
                            && CommonConstants.FLG_OFF.equals(entity.getSingldnpyflg())) {
                            // 受注時代引フラグが「代引き」以外且つ、単体伝票締フラグが「フラグOFF」の場合、空を設定
                            columns.set(92, "");
                        } else if (CommonConstants.FLG_OFF.equals(entity.getZeifukakazei())) {
                            // 不課税税抜が0の場合、空を設定
                            columns.set(92, "");
                        } else {
                            // 上記以外の場合、不課税消費税を設定
                            columns.set(92, entity.getZeifukakazei());
                        }
                    } else {
                        // 税別金額２が空欄の場合、空を設定
                        if (StringUtils.isEmpty(entity.getZeinukifukazei())) {
                            columns.set(92, "");
                        } else {
                            // 上記以外の場合、"0"を設定
                            columns.set(92, "0");
                        }
                    }
                    // 受注伝票の消費税の変数初期化
                    sumTax8per = new BigDecimal(0);
                    sumTax10per = new BigDecimal(0);
                    // 未締め伝票の消費税の変数初期化
                    sumTaxMishime8per = new BigDecimal(0);
                    sumTaxMishime10per = new BigDecimal(0);
                    // 合計金額と合計消費税の変数初期化
                    sumMoney = new BigDecimal(0);
                    sumTax = new BigDecimal(0);
                }
            }
        }
        return csvList;
    }

    /**
     * システム管理マスタデータマップ取得
     * 
     * @param sysMstEntity システム管理マスタ検索結果Entity
     * @param dataMap 印刷対象データマップ作成
     */
    private void getSysMstMapper(S04T001TC0810R00SysMstEntity sysMstEntity,
        HashMap<String, String> dateMap) {

        /// システム管理マスタデータをマップに設定
        dateMap.put("DELIVERYSYSTEMKBNNM", sysMstEntity.getDlvrykibojikannm());
        dateMap.put("NATHANINFO1", sysMstEntity.getNohinsakijohon1());
        dateMap.put("NATHANINFO2", sysMstEntity.getNohinsakijohon2());
        dateMap.put("COLLECTIONMETHOD", sysMstEntity.getKaishuhoho());
        dateMap.put("CUSTOMERS", sysMstEntity.getTokuisakijotai());
    }

    /**
     * 配布物情報データマップ取得
     * 
     * @param handoutsEntity 配布物情報データEntity
     * @param dataMap 印刷対象データマップ作成
     */
    private void getHandoutsMapper(List<S04T001TC0810R00HandoutsEntity> handoutsEntity,
        HashMap<String, String> dateMap) {

        // 配布物情報データをマップに設定
        for (int i = 0; i < handoutsEntity.size(); i++) {
            S04T001TC0810R00HandoutsEntity entity = handoutsEntity.get(i);

            dateMap.put("DISTRIBUTENM" + i, entity.getHaifubutsunm());
            dateMap.put("NATHANITEM" + i, entity.getNohinsuryo());
        }
    }

    /**
     * サーバー情報データマップ取得
     * 
     * @param svInfoDto サーバー情報データ取得結果DTO
     * @param dataMap 印刷対象データマップ作成
     */
    private void getSvInfoMapper(C0810R00ResultSvInfoDto svInfoDto,
        HashMap<String, String> dateMap) {

        // サーバー情報データをマップに設定
        dateMap.put("SERVERITEM", String.valueOf(svInfoDto.getWsvdaisu()));
        dateMap.put("MAINTENANCEDATE", svInfoDto.getJikaimntytidydm());
        dateMap.put("SERIALNO", svInfoDto.getSrlno());
    }

    /**
     * システム管理マスタデータ取得
     * 
     * @param detail 印刷対象明細DTO
     * @return システム管理マスタ取得結果Entity
     */
    private S04T001TC0810R00SysMstEntity getSystemMaster(C0809R40PrintDetailDto detail) {

        // システム管理マスタデータ取得
        S04T001TC0810R00SysMstEntity entity = new S04T001TC0810R00SysMstEntity();

        // AP基本コード
        entity.setApkihoncd(detail.getApkihoncd());
        // ショップコード
        entity.setShopcd(detail.getShopcd());
        // 受注伝票番号
        entity.setAcptdnpyno(detail.getAcptDnpyno());
        // 枝番号
        entity.setEdano(detail.getEdano());
        // 税区分_子明細商品の構成
        entity.setTaxkbn_5(CommonConstants.KBN_TAX_KOMEISAI);
        // セット商品明細印字フラグ_印字する
        entity.setStshnmeisaiinjiflg(CommonConstants.STSHNMEISAIINJIFLG_1);
        // 入力明細フラグ_入力行
        entity.setNyuryokumeisaiflg(CommonConstants.NYURYOKUMEISAIFLG_1);
        // システム管理マスタ用AP基本コード
        entity.setSys_apkihoncd(CommonConstants.APCD_000);
        // システム管理マスタ用ショップコード
        entity.setSys_shopcd(CommonConstants.SYS_SHOPCD_000);
        // システム管理マスタ用レコード区分
        entity.setSys_recordkbn(CommonConstants.RECORD_KBN_M);
        // 配達希望時間区分取得用設定番号
        entity.setSetteino_012(CommonConstants.SYS_SETTEINO_012);
        // 回収方法区分取得用設定番号
        entity.setSetteino_051(CommonConstants.SYS_SETTEINO_051);
        // 納品先情報１取得用設定番号
        entity.setSetteino_056(CommonConstants.SYS_SETTEINO_056);
        // 納品先情報２取得用設定番号
        entity.setSetteino_057(CommonConstants.SYS_SETTEINO_057);
        // 得意先状態取得用設定番号
        entity.setSetteino_014(CommonConstants.SYS_SETTEINO_014);

        List<S04T001TC0810R00SysMstEntity> resultList = s04t001tDao.selectByC0810R00SysMst(entity);

        return resultList.get(0);
    }

    /**
     * 配布物情報データ取得
     * 
     * @param detail 印刷対象明細DTO
     * @return 配布物情報データ取得結果Entity
     */
    private List<S04T001TC0810R00HandoutsEntity> getHandoutsInfo(C0809R40PrintDetailDto detail) {

        // 配布物情報データ取得
        S04T001TC0810R00HandoutsEntity entity = new S04T001TC0810R00HandoutsEntity();

        // AP基本コード
        entity.setApkihoncd(detail.getApkihoncd());
        // ショップコード
        entity.setShopcd(detail.getShopcd());
        // 受注伝票番号
        entity.setAcptdnpyno(detail.getAcptDnpyno());
        // 枝番号
        entity.setEdano(detail.getEdano());
        // 明細区分_配布物
        entity.setMeisaikbn_1(CommonConstants.KBN_MEISAI_HAIHUBUTSU);
        // 税区分_子明細商品の構成
        entity.setTaxkbn_5(CommonConstants.KBN_TAX_KOMEISAI);
        // セット商品明細印字フラグ_印字する
        entity.setStshnmeisaiinjiflg(CommonConstants.STSHNMEISAIINJIFLG_1);
        // 入力明細フラグ_入力行
        entity.setNyuryokumeisaiflg(CommonConstants.NYURYOKUMEISAIFLG_1);

        return s04t001tDao.selectByC0810R00Handouts(entity);
    }

    /**
     * サーバー情報データ取得
     * 
     * @param detail 印刷対象明細DTO
     * @return サーバー情報データ取得結果DTO
     */
    private C0810R00ResultSvInfoDto getServerInfo(C0809R40PrintDetailDto detail) {

        // サーバー情報データ取得
        S04T001TC0810R00SvInfoEntity entity = new S04T001TC0810R00SvInfoEntity();

        // AP基本コード
        entity.setApkihoncd(detail.getApkihoncd());
        // ショップコード
        entity.setShopcd(detail.getShopcd());
        // 受注伝票番号
        entity.setAcptdnpyno(detail.getAcptDnpyno());
        // 枝番号
        entity.setEdano(detail.getEdano());
        // 受託配達顧客区分_NA受託
        entity.setJutakudlvrycstkbn_5(CommonConstants.JUTAKUDLVRYCSTKBN_5);
        // 受託配達顧客区分_配達受託
        entity.setJutakudlvrycstkbn_6(CommonConstants.JUTAKUDLVRYCSTKBN_6);
        // 得意先区分_NA受託
        entity.setTokuisakikbn_5(CommonConstants.KBN_TOKUISAKI_NAJUTAKU);
        // 得意先区分_配達受託
        entity.setTokuisakikbn_6(CommonConstants.KBN_TOKUISAKI_DLVRYJUTAKU);
        // 税区分_子明細商品の構成
        entity.setTaxkbn_5(CommonConstants.KBN_TAX_KOMEISAI);
        // セット商品明細印字フラグ_印字する
        entity.setStshnmeisaiinjiflg(CommonConstants.STSHNMEISAIINJIFLG_1);
        // 入力明細フラグ_入力行
        entity.setNyuryokumeisaiflg(CommonConstants.NYURYOKUMEISAIFLG_1);

        // サーバー情報データ取得結果設定
        return createServerInfoDto(s04t001tDao.selectByC0810R00SvInfo(entity));
    }

    /**
     * サーバー情報データ検索処理
     * 
     * @param resultEntity サーバー指示書データ検索結果Entityリスト
     * @return サーバー情報データ取得結果DTO
     */
    private C0810R00ResultSvInfoDto createServerInfoDto(
        List<S04T001TC0810R00SvInfoEntity> resultEntity) {

        // 取得結果をDTOに設定
        C0810R00ResultSvInfoDto resultDto = new C0810R00ResultSvInfoDto();

        if (!StringUtils.isEmpty(resultEntity) && resultEntity.size() != 0) {
            for (S04T001TC0810R00SvInfoEntity result : resultEntity) {
                // 受注伝票番号
                resultDto.setAcptdnpynomei(result.getAcptdnpynomei());
                // 枝番号
                resultDto.setEdanomei(result.getEdanomei());
                // サーバー台数
                if (CommonConstants.KBN_TOKUISAKI_NAJUTAKU.equals(result.getTokuisakikbn())
                    && CommonConstants.KBN_JUTAKUDLVTOKUI_NAJUTAKU
                        .equals(result.getJutakudlvrycstkbn())
                    || CommonConstants.KBN_TOKUISAKI_DLVRYJUTAKU.equals(result.getTokuisakikbn())
                        && CommonConstants.KBN_JUTAKUDLVTOKUI_DLVRYJUTAKU
                            .equals(result.getJutakudlvrycstkbn())) {
                    resultDto.setWsvdaisu(result.getWsvdaisu() + result.getItakuaitewsvdaisu());
                } else {
                    resultDto.setWsvdaisu(result.getWsvdaisu());
                }
                // シリアル番号、次回メンテナンス予定日
                if (CommonConstants.KBN_TOKUISAKI_NAJUTAKU.equals(result.getTokuisakikbn())
                    && CommonConstants.KBN_JUTAKUDLVTOKUI_NAJUTAKU
                        .equals(result.getJutakudlvrycstkbn())
                    || CommonConstants.KBN_TOKUISAKI_DLVRYJUTAKU.equals(result.getTokuisakikbn())
                        && CommonConstants.KBN_JUTAKUDLVTOKUI_DLVRYJUTAKU
                            .equals(result.getJutakudlvrycstkbn())) {
                    // 受託会社、委託会社の判定
                    int srlnoResult = 0;
                    if (StringUtils.isEmpty(result.getSrlno())) {
                        srlnoResult = 1;
                    } else if (StringUtils.isEmpty(result.getItakuaitesrlno())) {
                        srlnoResult = -1;
                    } else {
                        srlnoResult = result.getItakuaitesrlno().compareTo(result.getSrlno());
                    }

                    if (srlnoResult <= 0) {
                        // 受託会社の場合
                        resultDto.setSrlno(result.getSrlno());
                        resultDto.setJikaimntytidydm(result.getJikaimntytidydm());
                    } else {
                        // 委託会社の場合
                        resultDto.setSrlno(result.getItakuaitesrlno());
                        resultDto.setJikaimntytidydm(result.getItakuaitejikaimntytidydm());
                    }
                } else {
                    // 受託会社でない
                    resultDto.setSrlno(result.getSrlno());
                    resultDto.setJikaimntytidydm(result.getJikaimntytidydm());
                }
            }
        }
        return resultDto;
    }

    /**
     * 納品書兼受領書データ取得
     * 
     * @param detail 印刷対象明細DTO
     * @param unyodate 運用日付
     * @param invoiceFlg インボイス施行フラグ
     * @return 納品書兼受領書データ取得結果Entity
     */
    private List<S04T001TC0810R00DeliveryEntity> getStatementofDelivery(
        C0809R40PrintDetailDto detail, String unyodate, String invoiceFlg) {

        // 納品書兼受領書データ取得
        S04T001TC0810R00DeliveryEntity entity = new S04T001TC0810R00DeliveryEntity();

        // AP基本コード
        entity.setApkihoncd(detail.getApkihoncd());
        // ショップコード
        entity.setShopcd(detail.getShopcd());
        // 受注伝票番号
        entity.setAcptdnpyno(detail.getAcptDnpyno());
        // 枝番号
        entity.setEdano(detail.getEdano());
        // 運用日付
        entity.setUnyodate(unyodate);
        // 伝票ステータス_配達済
        entity.setDnpysts(CommonConstants.DNPYSTS_12);
        // 配達済みフラグ
        entity.setDlvryedflg(CommonConstants.DNPYSTS_12.equals(detail.getDnpysts()));
        // 印字フラグ1
        entity.setPrntmemon1injiflg(CommonConstants.INJIFLG_1);
        // 印字フラグ2
        entity.setPrntmemon2injiflg(CommonConstants.INJIFLG_1);
        // 印字フラグ3
        entity.setPrntmemon3injiflg(CommonConstants.INJIFLG_1);
        // 受託配達顧客区分_NA受託
        entity.setJutakudlvrycstkbn_5(CommonConstants.JUTAKUDLVRYCSTKBN_5);
        // 受託配達顧客区分_配達受託
        entity.setJutakudlvrycstkbn_6(CommonConstants.JUTAKUDLVRYCSTKBN_6);
        // 得意先区分_NA受託
        entity.setTokuisakikbn_5(CommonConstants.KBN_TOKUISAKI_NAJUTAKU);
        // 得意先区分_配達受託
        entity.setTokuisakikbn_6(CommonConstants.KBN_TOKUISAKI_DLVRYJUTAKU);
        // 回収方法区分_代引き
        entity.setKaishuhohokbn_7(CommonConstants.KAISHUHOHOKBN_7);
        // 明細区分_配布物
        entity.setMeisaikbn_1(CommonConstants.KBN_MEISAI_HAIHUBUTSU);
        // 明細区分_代引きレンタル代等
        entity.setMeisaikbn_2(CommonConstants.KBN_MEISAI_2);
        // 明細区分_消費税調整
        entity.setMeisaikbn_3(CommonConstants.KBN_MEISAI_3);
        // 税区分_課税
        entity.setTaxkbn_1(CommonConstants.KBN_TAX_KAZEI);
        // 税区分_非課税
        entity.setTaxkbn_2(CommonConstants.KBN_TAX_HIKAZEI);
        // 税区分_不課税
        entity.setTaxkbn_3(CommonConstants.KBN_TAX_HUKAZEI);
        // 税区分_課税軽減
        entity.setTaxkbn_4(CommonConstants.KBN_TAX_KEIGEN);
        // 税区分_子明細商品の構成
        entity.setTaxkbn_5(CommonConstants.KBN_TAX_KOMEISAI);
        // 単品明細フラグ_単品明細行
        entity.setOnemeisaicolumn(CommonConstants.TANPINMEISAIFLG_1);
        // 消費税_8%
        entity.setZei8per(CommonConstants.TAX_8);
        // 消費税_10%
        entity.setZei10per(CommonConstants.TAX_10);
        // 伝票種別_納品書兼受領書
        entity.setDnpykind(CommonConstants.DNPYKIND_1);
        // 有効区分
        entity.setYukokbn(CommonConstants.YUKOKBN_1);
        // セット商品明細印字フラグ_印字する
        entity.setStshnmeisaiinjiflg(CommonConstants.STSHNMEISAIINJIFLG_1);
        // 入力明細フラグ_入力行
        entity.setNyuryokumeisaiflg(CommonConstants.NYURYOKUMEISAIFLG_1);
        // 得意先区分_顧客
        entity.setTokuisakikbn_1(CommonConstants.KBN_TOKUISAKI_KOKUAKU);
        // 支払いサイト用コンディションID
        entity.setConditionid_086(CommonConstants.CONDITION_ID_CN086);
        // 得意先敬称区分用コンディションID
        entity.setConditionid_082(CommonConstants.CONDITION_ID_CN082);
        // インボイス施行フラグ
        entity.setInvoiceFlg(invoiceFlg);
        // システム管理マスタ用AP基本コード
        entity.setSys_apkihoncd(CommonConstants.APCD_000);
        // システム管理マスタ用ショップコード
        entity.setSys_shopcd(CommonConstants.SYS_SHOPCD_000);
        // システム管理マスタ用レコード区分
        entity.setSys_recordkbn(CommonConstants.RECORD_KBN_M);
        // 計上区分取得用設定番号
        entity.setSetteino_009(CommonConstants.SYS_SETTEINO_009);

        return s04t001tDao.selectByC0810R00DeliveryAndReceipt(entity);
    }
}
