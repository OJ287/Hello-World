/*
   システム名:ACCIS2023
   機能名:得意先マスタ(登録画面)

 */
import {$ACI_EVENT_ELE,EVENTS,ErrorMessageDialog} from "../aciCore.js";
import {AciAjaxParts} from "../commons/modules/aciAjaxParts.js";

// 更新成功後の遷移先区分値
const AFTER_UPDATE_ACTION = {
    NONE: 0,      // 遷移なし
    JUCHU: 1      // 受注登録
};

export default class B0501W02{

    // 更新成功後の遷移先区分（デフォルト遷移なし）
    #afterUpdateAction = AFTER_UPDATE_ACTION.NONE;

    constructor(){
        this.#setFieldVariables();
        this.#setEvents();
    }
    
    #setEvents(){
        // 初期表示時の制御処理
        $ACI_EVENT_ELE.on(EVENTS.MODULE_LOAD_COMPLETE, this.hideBackButton);
        $ACI_EVENT_ELE.on(EVENTS.MODULE_LOAD_COMPLETE, this.changedKshnFlag);
        $ACI_EVENT_ELE.on(EVENTS.MODULE_LOAD_COMPLETE, this.changedTokuisakiJutakuKbn);
        $ACI_EVENT_ELE.on(EVENTS.MODULE_LOAD_COMPLETE, this.changedTokuisakiJutakuKbnSoko);
        $ACI_EVENT_ELE.on(EVENTS.MODULE_LOAD_COMPLETE, this.changedTokuisakiJutakuKbnPoint);
        $ACI_EVENT_ELE.on(EVENTS.MODULE_LOAD_COMPLETE, this.changedRknkindtokubetsuuntkbn);
        $ACI_EVENT_ELE.on(EVENTS.MODULE_LOAD_COMPLETE, this.changedSkysetteikbn);
        $ACI_EVENT_ELE.on(EVENTS.MODULE_LOAD_COMPLETE, this.changedPln);
        $ACI_EVENT_ELE.on(EVENTS.MODULE_LOAD_COMPLETE, this.changedKaiincdSakuseiKbn);
        $ACI_EVENT_ELE.on(EVENTS.MODULE_LOAD_COMPLETE, this.changedDlvrykbn);
        $ACI_EVENT_ELE.on(EVENTS.MODULE_LOAD_COMPLETE, this.changedDlvrykbn1);
        $ACI_EVENT_ELE.on(EVENTS.MODULE_LOAD_COMPLETE, this.changedMtmkisvkbn);
        $ACI_EVENT_ELE.on(EVENTS.MODULE_LOAD_COMPLETE, this.changedSainyukaifrg);
        $ACI_EVENT_ELE.on(EVENTS.MODULE_LOAD_COMPLETE, this.changedHoshokinkbn);
        $ACI_EVENT_ELE.on(EVENTS.MODULE_LOAD_COMPLETE, this.changeUrikeijoChuiAci);
        $ACI_EVENT_ELE.on(EVENTS.MODULE_LOAD_COMPLETE, this.changedMtmkipointkbn);
        $ACI_EVENT_ELE.on(EVENTS.MODULE_LOAD_COMPLETE, this.changedNyukaitokutenumu);
        $ACI_EVENT_ELE.on(EVENTS.MODULE_LOAD_COMPLETE, this.teireisetteiObj, this.changedTeireiHnkCheckbox);
        $ACI_EVENT_ELE.on(EVENTS.MODULE_LOAD_COMPLETE, this.tokuisakiObj, this.changedTokuisakiHnkCheckbox);
        $ACI_EVENT_ELE.on(EVENTS.MODULE_LOAD_COMPLETE, this.skysakiObj, this.changedSkysakiHnkCheckbox);
        $ACI_EVENT_ELE.on(EVENTS.MODULE_LOAD_COMPLETE, this.dataKakutokuHonbuKynpn, this.getKynpnkeihinkbnPulldown);
        $ACI_EVENT_ELE.on(EVENTS.MODULE_LOAD_COMPLETE, this.dataKakutokuKynpn, this.getKynpnkeihinkbnPulldown);
        // 反映元得意先コード、紹介元得意先コード、委託相手AP基本コード、委託相手ショップコード、委託相手得意先コード変更時イベント
        this.dataHaneimotoTokuisaki.cd.on("change", this.dataHaneimotoTokuisaki, this.getTokuisakiNm);
        this.dataShoukaimotoTokuisaki.cd.on("change", this.dataShoukaimotoTokuisaki, this.getTokuisakiNm);
        this.dataItakuaiteTokuisaki.apkihoncd.on("change", this.dataItakuaiteTokuisaki, this.getItakuTokuisakiNm);
        this.dataItakuaiteTokuisaki.shopcd.on("change", this.dataItakuaiteTokuisaki, this.getItakuTokuisakiNm);
        this.dataItakuaiteTokuisaki.cd.on("change", this.dataItakuaiteTokuisaki, this.getItakuTokuisakiNm);
        // 事業所プルダウンロード完了時イベント
        $(".aci-ajax-parts").on(EVENTS.TEMPLATE_LOAD_COMPLETE,this.selectFirstIndex);
        $(".jigyoshocd").on(EVENTS.TEMPLATE_LOAD_COMPLETE,this.jigyoshocdLoadComplete);
        // 商品名プルダウンロード完了時イベント
        this.$shnmei.on(EVENTS.TEMPLATE_LOAD_COMPLETE,this.shncdLoadComplete);
        // サブレンタル倉庫コード変更時イベント
        this.dataSaburtlSoko.cd.on("change", this.dataSaburtlSoko, this.getSokoNm);
        // 請求先コード変更時イベント
        this.dataSkysaki.cd.on("change", this.dataSkysaki, this.getSkysakiNm);
        this.dataSkysaki.cd.on("change", this.changedRknkindtokubetsuuntkbn);
        // 得意先ショップコード変更時イベント
        this.dataShop.cd.on("change", this.dataShop, this.getShopNm);
        this.dataShop.cd.on("change", this.showTokuisakiShopNm);
        // プラン初回設定日オンフォーカス時イベント
        this.plnsetteiObj.date.on("focus", this.plnsetteiObj, this.focusPlandd);
        // プラン初回設定日変更時イベント
        this.plnsetteiObj.date.on("change", this.plnsetteiObj, this.changePlandd);
        // プラン継続更新日オンフォーカス時イベント
        this.plnkeizokuObj.date.on("focus", this.plnkeizokuObj, this.focusPlandd);
        // プラン継続更新日変更時イベント
        this.plnkeizokuObj.date.on("change", this.plnkeizokuObj, this.changePlandd);
        // 設置登録者コード変更時イベント
        this.dataStupdd.cd.on("change", this.dataStupdd, this.getTnshaNm);
        // 引上登録者コード変更時イベント
        this.dataHikiage.cd.on("change", this.dataHikiage, this.getTnshaNm);
        // 営業担当者コード変更時イベント
        this.dataEigyo.cd.on("change", this.dataEigyo, this.getTnshaNm);
        // 得意先マスタ情報営業担当者コード変更時イベント
        this.dataTokuisakiEigyo.cd.on("change", this.dataTokuisakiEigyo, this.getTnshaNm);
        // 請求先マスタ情報営業担当者コード変更時イベント
        this.dataSkysakiEigyo.cd.on("change", this.dataSkysakiEigyo, this.getTnshaNm);
        // 獲得担当者コード変更時イベント
        this.dataKakutoku.cd.on("change", this.dataKakutoku, this.getTnshaNm);
        // 配達担当者コード変更時イベント
        this.dataDlvry.cd.on("change", this.dataDlvry, this.getTnshaNm);
        // 得意先マスタ情報配達担当者コード変更時イベント
        this.dataTokuisakiDlvry.cd.on("change", this.dataTokuisakiDlvry, this.getTnshaNm);
        // 回収担当者コード変更時イベント
        this.dataKaishu.cd.on("change", this.dataKaishu, this.getTnshaNm);
        // 実績担当者コード変更時イベント
        this.dataJisseki.cd.on("change", this.dataJisseki, this.getTnshaNm);
        // 営業エリアコード変更時イベント
        this.dataEigyoArea.cd.on("change", this.dataEigyoArea, this.getEigyoAreaNm);
        // 得意先マスタ情報営業エリアコード変更時イベント
        this.dataTokuisakiEigyoArea.cd.on("change", this.dataTokuisakiEigyoArea, this.getEigyoAreaNm);
        // 配達拠点AP基本コード変更時イベント
        this.dataDlvrykankatsuKyotencd.apkihoncd.on("change", this.dataDlvrykankatsuKyotencd, this.getDlvryKyotenApNm);
        // 配達拠点ショップコード変更時イベント
        this.dataDlvrykankatsuKyotencd.shopcd.on("change", this.dataDlvrykankatsuKyotencd, this.getDlvryKyotenShopNm);
        // 配達拠点コード変更時イベント
        this.dataDlvrykankatsuKyotencd.cd.on("change", this.dataDlvrykankatsuKyotencd, this.getDlvryKyotenNm);
        // 得意先マスタ情報配達拠点AP基本コード変更時イベント
        this.dataTokuisakiDlvrykankatsuKyotencd.apkihoncd.on("change", this.dataTokuisakiDlvrykankatsuKyotencd, this.getDlvryKyotenApNm);
        // 得意先マスタ情報配達拠点ショップコード変更時イベント
        this.dataTokuisakiDlvrykankatsuKyotencd.shopcd.on("change", this.dataTokuisakiDlvrykankatsuKyotencd, this.getDlvryKyotenShopNm);
        // 得意先マスタ情報配達拠点コード変更時イベント
        this.dataTokuisakiDlvrykankatsuKyotencd.cd.on("change", this.dataTokuisakiDlvrykankatsuKyotencd, this.getDlvryKyotenNm);
        // 本部エージェントコード変更時イベント
        this.dataHonbuEjnt.cd.on("change", this.dataHonbuEjnt, this.getEjntNm);
        // 獲得エージェントコード変更時イベント
        this.dataKakutokuEjnt.cd.on("change", this.dataKakutokuEjnt, this.getEjntNm);
        // 獲得本部キャンペーンコード変更時イベント
        this.dataKakutokuHonbuKynpn.cd.on("change", this.dataKakutokuHonbuKynpn, this.getKynpnNm);
        this.dataKakutokuHonbuKynpn.cd.on("change", this.dataKakutokuHonbuKynpn, this.getKynpnkeihinkbnPulldown);
        // 獲得キャンペーンコード変更時イベント
        this.dataKakutokuKynpn.cd.on("change", this.dataKakutokuKynpn, this.getKynpnNm);
        this.dataKakutokuKynpn.cd.on("change", this.dataKakutokuKynpn, this.getKynpnkeihinkbnPulldown);
        // 配達会社コード変更時イベント
        this.dataDlvrycmpny.cd.on("change", this.dataDlvrycmpny, this.getDlvryCmpnyNm);
        // 得意先マスタ情報配達会社コード変更時イベント
        this.dataTokuisakiDlvrycmpny.cd.on("change", this.dataTokuisakiDlvrycmpny, this.getDlvryCmpnyNm);
        // 獲得詳細コード変更時イベント
        this.dataHansokushs.cd.on("change", this.dataHansokushs, this.getHansokuShsNm);
        // 配達エリアコード変更時イベント
        this.dataDlvryArea.cd.on("change", this.dataDlvryArea, this.getDlvryAreaNm);
        // ワンショットコード変更時イベント
        this.dataWanshottocd.cd.on("change", this.dataWanshottocd, this.getMgnNm);
        // 開始年月ロストフォーカス時イベント
        this.$strtdydm.on("blur", this.blurStrtdydm);
        // 得意先マスタ情報配達エリアコード変更時イベント
        this.dataTokuisakiDlvryArea.cd.on("change", this.dataTokuisakiDlvryArea, this.getDlvryAreaNm);
        // 計上日変更時イベント
        this.$keijodd.on("change", this.untTaxCalcObj, this.recalcKingaku);
        this.$keijodd.on("change", this.untShokaiTaxCalcObj, this.recalcKingaku);
        // 開始年月変更時イベント
        this.$strtdydm.on("change", this.untShokaiTaxCalcObj, this.recalcKingaku);
        // 次回計上年月変更時イベント
        this.$jikaikeijodydm.on("change", this.untTaxCalcObj, this.recalcKingaku);
        // 数量変更時イベント
        this.$suryo.on("change", this.untTaxCalcObj, this.recalcKingaku);
        this.$suryo.on("change", this.untShokaiTaxCalcObj, this.recalcKingaku);
        // 単価変更時イベント
        this.$unt.on("change", this.untTaxCalcObj, this.recalcKingaku);
        // 初回単価変更時イベント
        this.$shokaiunt.on("change", this.untShokaiTaxCalcObj, this.recalcKingaku);
        
        // 更新処理成功時
        this.$btnkshn.on(EVENTS.AJAX_TRIGGER_SUCCESS,this.onUpdated);
        this.$btnacptinpt.on(EVENTS.AJAX_TRIGGER_SUCCESS,this.onUpdated);
        // 更新処理失敗時
        this.$btnkshn.on(EVENTS.AJAX_TRIGGER_FAILED,this.onUpdateFailed);
        this.$btnacptinpt.on(EVENTS.AJAX_TRIGGER_FAILED,this.onUpdateFailed);
        // 更新後に会員情報印刷処理成功時
        this.$btnprnt.on(EVENTS.AJAX_TRIGGER_SUCCESS,this.onPrinted);

        // 商品コードクリアボタン押下時イベント
        this.$btnkuriashn1.on("click", this.shnCdNm1, this.kuriaCdAndNm);
        this.$btnkuriashn2.on("click", this.shnCdNm2, this.kuriaCdAndNm);
        this.$btnkuriashn3.on("click", this.shnCdNm3, this.kuriaCdAndNm);
        this.$btnkuriashn4.on("click", this.shnCdNm4, this.kuriaCdAndNm);
        // マージンコードクリアボタン押下時イベント
        this.$btnkuriamgn1.on("click", this.mgnCdNm1, this.kuriaCdAndNm);
        this.$btnkuriamgn2.on("click", this.mgnCdNm2, this.kuriaCdAndNm);
        this.$btnkuriamgn3.on("click", this.mgnCdNm3, this.kuriaCdAndNm);
        this.$btnkuriamgn4.on("click", this.mgnCdNm4, this.kuriaCdAndNm);
        // 自動値引コードクリアボタン押下時イベント
        this.$btnkuriajidonebiki1.on("click", this.jidonebiki1Obj, this.kuriaCdAndNm);
        this.$btnkuriajidonebiki2.on("click", this.jidonebiki2Obj, this.kuriaCdAndNm);
        this.$btnkuriajidonebiki3.on("click", this.jidonebiki3Obj, this.kuriaCdAndNm);
        
        // 得意先マスタ子画面リンククリック時
        this.$refhaneimototokuisakicdBtn.on("click", this.#clickTokuisakicdBtn);
        this.$itakuaitesakicdBtn.on("click", this.#clickTokuisakicdBtn);
        this.$reftokuisakicdBtn.on("click", this.#clickTokuisakicdBtn);
        // 請求先マスタ子画面リンククリック時
        this.$refskysakicdBtn.on("click", this.#clickSkysakicdBtn);
        // APショップヘッダ子画面リンククリック時
        this.$refshopcdBtn.on("click", this.#clickShopcdBtn);
        // 会員マスタ子画面リンククリック時
        this.$refkaiincdBtn.on("click", this.#clickKaiincdBtn);
        // 倉庫マスタ子画面リンククリック時
        this.$refsokocdBtn.on("click", this.#clickSokocdBtn);
        // 担当者マスタ子画面リンククリック時
        this.$refsecchitoshacdBtn.on("click", this.#clickTnshacdBtn);
        this.$refhikiagetoshacdBtn.on("click", this.#clickTnshacdBtn);
        this.$linkjissekitnshacdBtn.on("click", this.#clickTnshacdBtn);
        this.$reftnshacdBtn.on("click", this.#clickTnshacdBtn);
        this.$reftnshacd1Btn.on("click", this.#clickTnshacdBtn);
        this.$reftnshacd3Btn.on("click", this.#clickTnshacdBtn);
        this.$reftnshacd2Btn.on("click", this.#clickTnshacdBtn);
        this.$reftnshacd4Btn.on("click", this.#clickTnshacdBtn);
        this.$reftnshacd5Btn.on("click", this.#clickTnshacdBtn);
        this.$reftnshacd6Btn.on("click", this.#clickTnshacdBtn);
        // 営業エリアマスタ子画面リンククリック時
        this.$refeigyoareacdBtn.on("click", this.#clickEigyoareacdBtn);
        this.$refeigyoareacd1Btn.on("click", this.#clickEigyoareacdBtn);
        // キャンペーンマスタ子画面リンククリック時
        this.$linkkakutokuhonbukynpncdBtn.on("click", this.#clickKakutokukynpncdBtn);
        this.$refkakutokukynpncdBtn.on("click", this.#clickKakutokukynpncdBtn);
        // 得意先獲得販促詳細マスタ子画面リンククリック時
        this.$refhansokushscdBtn.on("click", this.#clickHansokushscdBtn);
        // 配達拠点マスタ子画面リンククリック時
        this.$refdlvrykyotenBtn.on("click", this.#clickDlvrykyotencdBtn);
        this.$refdlvrykyoten1Btn.on("click", this.#clickDlvrykyotencdBtn);
        // エージェントマスタ子画面リンククリック時
        this.$refejntcdBtn.on("click", this.#clickEjntcdBtn);
        this.$refejntcd3Btn.on("click", this.#clickEjntcdBtn);
        // 配達会社マスタ子画面リンククリック時
        this.$refdlvrycmpnycdBtn.on("click", this.#clickDlvrycmpnycdBtn);
        this.$refdlvrycmpnycd1Btn.on("click", this.#clickDlvrycmpnycdBtn);
        // 配達エリアマスタ子画面リンククリック時
        this.$refdlvryareacdBtn.on("click", this.#clickDlvryareacdBtn);
        this.$refdlvryareacd1Btn.on("click", this.#clickDlvryareacdBtn);
        // マージンマスタ子画面リンククリック時
        this.$sanshokensakuwanshottocdBtn.on("click", this.#clickMgncdBtn);
        this.$refmgncd1Btn.on("click", this.#clickMgncdBtn);
        this.$refmgncd2Btn.on("click", this.#clickMgncdBtn);
        this.$refmgncd3Btn.on("click", this.#clickMgncdBtn);
        this.$refmgncd4Btn.on("click", this.#clickMgncdBtn);
        // 商品マスタ子画面リンククリック時
        this.$refshncd1Btn.on("click", this.#clickShncdBtn);
        this.$refshncd2Btn.on("click", this.#clickShncdBtn);
        this.$refshncd3Btn.on("click", this.#clickShncdBtn);
        this.$refshncd4Btn.on("click", this.#clickShncdBtn);
        // 自動値引マスタ子画面リンククリック時
        this.$refjidonebikin1Btn.on("click", this.#clickJidonebikicdBtn);
        this.$refjidonebikin2Btn.on("click", this.#clickJidonebikicdBtn);
        this.$refjidonebikin3Btn.on("click", this.#clickJidonebikicdBtn);
        // 表示を更新リンククリック時
        this.$linkiyakukinkshnBtn.on("click", this.#clickLinkiyakukinkshnBtn);
        // 反映得意先ボタンクリック時
        this.$btnhaneitokuisaki.on("click", this.#clickHaneiTokuisakiBtn);
        // 反映委託相手ボタンクリック時
        this.$btnhneiitakuaite.on("click", this.#clickHaneiItakuaiteBtn);
        // 更新ボタンクリック時
        this.$btnkshn.on("click", this.#btnkshnClick);
        // 更新後に受注登録へボタンクリック時
        this.$btnacptinpt.on("click", this.#btnacptinptClick);
                
        // セレクトボックス変更時イベント
        // セレクトボックス変更時共通
        $(".aci-ajax-parts").on("change", this.changedPullDown);
        // 得意先区分セレクトボックス変更時
        this.$tokuisakikbn.on("change", this.changedTokuisakiKbn);
        this.$tokuisakikbn.on("change", this.changeUrikeijoChuiAci);
        // 受託顧客区分セレクトボックス変更時
        this.$jutakudlvrycstkbn.on("change", this.changedJutakuKbn);
        this.$jutakudlvrycstkbn.on("change", this.changeKinotptnashiflg);
        // プランセレクトボックス変更時
        this.$pln.on("change", this.changedPln);
        // 受注時間指定区分セレクトボックス変更時
        this.$acptjikanshiteikbn.on("change", this.changedAcptjikanshiteikbn);
        // 得意先マスタ情報受注時間指定区分セレクトボックス変更時
        this.$acptjikanshiteikbn1.on("change", this.changedAcptjikanshiteikbn1);
        // 商品名セレクトボックス変更時
        this.$shnmei.on("change",this.shnmeiChanged);
        // 獲得本部キャンペーン景品区分変更時
        this.$kakutokuhonbukynpnkeihinkbn.on("change", this.changedKynpnkeihinkbn);
        // 獲得キャンペーン景品区分変更時
        this.$kakutokukeihin.on("change", this.changedKynpnkeihinkbn);
        
        // ラジオボタン変更時イベント
        // 特別単価区分ラジオボタン変更時
        this.$rknkindtokubetsuuntkbn.on("change", this.changedRknkindtokubetsuuntkbn);
        // 請求設定区分ラジオボタン変更時
        this.$skysetteikbn.on("change", this.changedSkysetteikbn);
        // 会員コード区分ラジオボタン変更時
        this.$kaiincdkbn.on("change", this.changedKaiincdkbn);
        // 配達区分ラジオボタン変更時
        this.$dlvrykbn.on("change", this.changedDlvrykbn);
        // 得意先マスタ情報配達区分ラジオボタン変更時
        this.$dlvrykbn1.on("change", this.changedDlvrykbn1);
        // まとめ買い区分ラジオボタン変更時
        this.$mtmkisvkbn.on("change", this.changedMtmkisvkbn);
        // 保証金区分ラジオボタン変更時
        this.$hoshokinkbn.on("change", this.changedHoshokinkbn);
        // 売上設定ラジオボタン変更時
        this.$urisettei.on("change", this.changedUrisettei);
        // まとめ買いポイント区分ラジオボタン変更時
        this.$mtmkipointkbn.on("change", this.changedMtmkipointkbn);
        // 入会特典区分ラジオボタン変更時
        this.$nyukaitokutenumu.on("change", this.changedNyukaitokutenumu);
        // チェックボックス変更時
        // 再入会チェックボックス変更時
        this.$sainyukaifrg.on("change", this.changedSainyukaifrg);
        // 定例売上設定チェックボックス変更時
        this.$teireisetteikbn.on("change", this.teireisetteiObj, this.changedTeireiHnkCheckbox);
        // 得意先マスタ変更チェックボックス変更時
        this.$tokuisakimasutahnkflg.on("change", this.tokuisakiObj, this.changedTokuisakiHnkCheckbox);
        // 請求先マスタ変更チェックボックス変更時
        this.$seikyusakimsthnkflg.on("change", this.skysakiObj, this.changedSkysakiHnkCheckbox);
        
        // 参照画面呼び出し時イベント
        // 定例売上マスタ呼び出し時
        this.$teireiurimasutalnkBtn.on("click", this.dataTeireiuriMasuta, this.clickTeireiuriMasuta);
        // 得意先設置サーバー管理マスタ呼び出し時
        this.$ttstupwsvmasutalnkBtn.on("click", this.dataTtstupwsvMasuta, this.clickTtstupwsvMasuta);
        // 請求先マスタ呼び出し時
        this.$linkrenkeiskysakiBtn.on("click", this.dataSkysakiMasuta, this.clickSkysakiMasuta);
        // 会員マスタ呼び出し時
        this.$refkaiincdlnkBtn.on("click", this.dataKaiinMasuta, this.clickKaiinMasuta);
        // 得意先違約金情報呼び出し時
        this.$linkiyakukinjohoBtn.on("click", this.dataIyakukin, this.clickIyakukinJoho);
        // 紹介者情報呼び出し時
        this.$refshoshakazu.on("click", this.dataShokai, this.clickShoshaJoho);
        // 定期受注マスタ呼び出し時
        this.$teikiJuchulnkBtn.on("click", this.dataTeikiacpt, this.clickTeikiacptMasuta);
        
        // 郵便番号反映ボタン押下時イベント
        // ボタン郵便番号1押下時
        this.$btnyubinno1.on("click", this.adrsnObj, this.clickYubinnoHanei);
        // ボタン郵便番号2押下時
        this.$btnyubinno2.on("click", this.kinmusakiAdrsnObj, this.clickYubinnoHanei);
        // ボタン郵便番号3押下時
        this.$btnyubinno3.on("click", this.tokuisakiAdrsnObj, this.clickYubinnoHanei);
        // ボタン郵便番号4押下時
        this.$btnyubinno4.on("click", this.skysakiAdrsnObj, this.clickYubinnoHanei);
        
        // 画面から移動する前のイベント
        $ACI_EVENT_ELE.on('beforeunload', this.#onBeforeUnload);
    }
    
    #setFieldVariables(){
        this.$display = $("#display");
        this.$pageNum = $("#aci-page_current");
        // 更新ボタン
        this.$btnkshn= $("#btnkshn");
        // 更新後に会員情報印刷ボタン
        this.$btnprnt= $("#btnprnt");
        // 更新後に受注登録へボタン
        this.$btnacptinpt= $("#btnacptinpt");
        // 戻るボタン
        this.$btnmodoru= $("#btnmodoru");
        // 反映ボタン
        // ボタン反映得意先
        this.$btnhaneitokuisaki= $("#btnhaneitokuisaki");
        // ボタン反映委託相手
        this.$btnhneiitakuaite= $("#btnhneiitakuaite");
        // ボタン郵便番号1反映
        this.$btnyubinno1= $("#btnyubinno1");
        // ボタン郵便番号２反映
        this.$btnyubinno2= $("#btnyubinno2");
        // 得意先マスタ情報ボタン郵便番号反映
        this.$btnyubinno3= $("#btnyubinno3");
        // 請求先マスタ情報ボタン郵便番号反映
        this.$btnyubinno4= $("#btnyubinno4");
        // ボタンクリア商品１
        this.$btnkuriashn1= $("#btnkuriashn1");
        // ボタンクリア商品２
        this.$btnkuriashn2= $("#btnkuriashn2");
        // ボタンクリア商品３
        this.$btnkuriashn3= $("#btnkuriashn3");
        // ボタンクリア商品４
        this.$btnkuriashn4= $("#btnkuriashn4");
        // ボタンクリアマージン１
        this.$btnkuriamgn1= $("#btnkuriamgn1");
        // ボタンクリアマージン２
        this.$btnkuriamgn2= $("#btnkuriamgn2");
        // ボタンクリアマージン３
        this.$btnkuriamgn3= $("#btnkuriamgn3");
        // ボタンクリアマージン４
        this.$btnkuriamgn4= $("#btnkuriamgn4");
        // ボタンクリア自動値引１
        this.$btnkuriajidonebiki1= $("#btnkuriajidonebiki1");
        // ボタンクリア自動値引２
        this.$btnkuriajidonebiki2= $("#btnkuriajidonebiki2");
        // ボタンクリア自動値引３
        this.$btnkuriajidonebiki3= $("#btnkuriajidonebiki3");
        // 更新モード判定フラグ
        this.$kshnFlag = $("*[id='baseBean.kshnflag']");
        // hidden遷移元機能ID
        this.$kinoid = $("*[id='baseBean.kinoid']");
        // AP基本コード
        this.$apkihoncd = $("*[id='baseBean.apkihoncd']");
        // ショップコード
        this.$shopcd = $("*[id='baseBean.shopcd']");
        // AP名称
        this.$apnm = $("*[id='subheaderapname']");
        // ショップ名称
        this.$shopnm = $("*[id='subheadershopname']");
        // アクセス範囲区分
        this.$akshanikbn = $("*[id='baseBean.akshanikbn']");
        // 得意先コード
        this.$tokuisakicd = $("*[id='baseBean.tokuisakicd1']");
        // hidden得意先コード
        this.$ptokuisakicd = $("*[id='baseBean.ptokuisakicd']");
        // 得意先名称
        this.$tokuisakinm = $("*[id='baseBean.tokuisakinm']");
        // 得意先状態
        this.$tokuisakijotainm = $("*[id='baseBean.tokuisakijotainm']");
        // hidden得意先状態
        this.$tokuisakijotai = $("*[id='baseBean.tokuisakijotai']");
        // 状態ステータス
        this.$jotaistsnm = $("*[id='baseBean.jotaistsnm']");
        // hidden状態ステータス
        this.$jotaists = $("*[id='baseBean.jotaists']");
        // 得意先名称１
        this.$tokuisakinmn1 = $("*[id='baseBean.tokuisakinmn1']");
        // 得意先名称２
        this.$tokuisakinmn2 = $("*[id='baseBean.tokuisakinmn2']");
        // 得意先名称カナ1
        this.$tokuisakinmkanan1 = $("*[id='baseBean.tokuisakinmkanan1']");
        // 得意先名称カナ2
        this.$tokuisakinmkanan2 = $("*[id='baseBean.tokuisakinmkanan2']");
        // 基本区分
        this.$b0501w02kihonkbn = $("*[id='baseBean.kihonkbn']");
        // 共通ヘッダショップ区分
        this.$kihonkbn = $("*[id='kihonkbn']");
        // 共通ヘッダショップ区分
        this.$shopkbn = $("*[id='shopkbn']");
        // 事業所管理
        this.$jigyoshokanri = $("*[id='jigyoshokanri']");
        // 親得意先コード
        this.$oyatokuisakicd = $("*[id='baseBean.oyatokuisakicd']");
        // 定期受注マスタリンク表示
        this.$teikiJuchulink = $("*[id='teikiJuchulink']");
        // 定期受注マスタラベル表示
        this.$teikiJuchulinklbl = $("*[id='teikiJuchulinklbl']");
        // 定期受注マスタリンク
        this.$teikiJuchulnkBtn = $("*[id='b0501w02-linkrenkeiteikiacpt']");
        // 定例売上マスタリンク表示
        this.$teireiurimasutalink = $("*[id='teireiurimasutalink']");
        // 定例売上マスタラベル表示
        this.$teireiurimasutalinklbl = $("*[id='teireiurimasutalinklbl']");
        // 定例売上マスタリンク
        this.$teireiurimasutalnkBtn = $("*[id='b0501w02-teireiurimasuta']");
        // 得意先設置サーバーマスタリンク表示
        this.$ttstupwsvmasutalink = $("*[id='ttstupwsvmasutalink']");
        // 得意先設置サーバーマスタラベル表示
        this.$ttstupwsvmasutalinklbl = $("*[id='ttstupwsvmasutalinklbl']");
        // 得意先設置サーバーマスタリンク
        this.$ttstupwsvmasutalnkBtn = $("*[id='b0501w02-ttstupwsvmasuta']");
        // 反映元得意先リンク表示
        this.$haneimootokuisakilink = $("*[id='haneimootokuisakilink']");
        // 反映元得意先ラベル表示
        this.$haneimootokuisakilbl = $("*[id='haneimootokuisakilbl']");
        // 反映元得意先リンク
        this.$refhaneimototokuisakicdBtn = $("*[id='b0501w02-refhaneimototokuisakicd']");
        // 反映元得意先コード
        this.$haneimototokuisakicd = $("*[id='baseBean.haneimototokuisakicd']");
        // 反映元得意先名称
        this.$haneimototokuisakinm = $("*[id='baseBean.haneimototokuisakinm']");
        // 請求先リンク表示
        this.$skysakicdlink = $("*[id='skysakicdlink']");
        // 請求先リンクラベル表示
        this.$skysakicdlbl = $("*[id='skysakicdlbl']");
        // 請求先コードリンク
        this.$refskysakicdBtn = $("*[id='b0501w02-refskysakicd']");
        // 請求先コード
        this.$skysakicd = $("*[id='baseBean.skysakicd']");
        // 請求先コード名称
        this.$skysakinmn1 = $("*[id='baseBean.skysakinmn1']");
        // 請求先マスタ修正・照会リンク表示
        this.$skysakimasutashuseishokailink = $("*[id='skysakimasutashuseishokailink']");
        // 請求先マスタ修正・照会ラベル表示
        this.$skysakimasutashuseishokailinklbl = $("*[id='skysakimasutashuseishokailinklbl']");
        // 得意先ショップ名称表示
        this.$tokuisakishoptext1 = $("*[id='tokuisakishoptext1']");
        // ショップの時のみ入力可能
        this.$tokuisakishoptext2 = $("*[id='tokuisakishoptext2']");
        // 得意先ショップコードリンク表示
        this.$tokshoplink = $("*[id='tokshoplink']");
        // 得意先ショップコードラベル表示
        this.$tokshoplinklbl = $("*[id='tokshoplinklbl']");
        // 得意先ショップコードリンク
        this.$refshopcdBtn = $("*[id='b0501w02-refshopcd']");
        // 得意先ショップコード
        this.$tokuisakishopcd = $("*[id='baseBean.tokuisakishopcd']");
        // 得意先ショップ名称
        this.$tokuisakishopnm = $("*[id='baseBean.tokuisakishopnm']");
        // 会員コードリンク表示
        this.$kaiincdlink = $("*[id='kaiincdlink']");
        // 会員コードラベル表示
        this.$kaiincdlbl = $("*[id='kaiincdlbl']");
        // 会員コードリンク
        this.$refkaiincdBtn = $("*[id='b0501w02-refkaiincd']");
        // 会員作成有無区分
        this.$kaiincdSakuseiKbn = $("*[id='baseBean.kaiincdSakuseiKbn']");
        // 会員コード
        this.$kaiincd = $("*[id='baseBean.kaiincd']");
        // 会員コード変更前
        this.$kaiincdbefore = $("*[id='baseBean.kaiincdbefore']");
        // 会員マスタリンク表示
        this.$kainmstsyokailink = $("*[id='kainmstsyokailink']");
        // 会員マスタラベル表示
        this.$kainmstsyokailbl = $("*[id='kainmstsyokailbl']");
        // 会員マスタリンク
        this.$refkaiincdlnkBtn = $("*[id='b0501w02-refkaiincd-lnk']");
        // 表示を更新リンク
        this.$linkiyakukinkshnBtn = $("*[id='b0501w02-linkiyakukinkshn']");
        // 請求先マスタ照会リンク
        this.$linkrenkeiskysakiBtn = $("*[id='b0501w02-linkrenkeiskysaki']");
        // 委託相手得意先コードリンク
        this.$itakuaitesakicdBtn = $("*[id='b0501w02-itakuaitesakicd']");
        // 委託相手AP基本コード
        this.$itakuaiteapkihoncd = $("*[id='baseBean.itakuaiteapkihoncd']");
        // 委託相手ショップコード
        this.$itakuaiteshopcd = $("*[id='baseBean.itakuaiteshopcd']");
        // 委託相手得意先コード
        this.$itakuaitetokuisakicd = $("*[id='baseBean.itakuaitetokuisakicd']");
        // 委託相手得意先名
        this.$itakuaitetokuisakinm = $("*[id='baseBean.itakuaitetokuisakinm']");
        // 郵便番号
        this.$yubinno = $("*[id='baseBean.yubinno']");
        // 住所1
        this.$adrsn1 = $("*[id='baseBean.adrsn1']");
        // 住所2
        this.$adrsn2 = $("*[id='baseBean.adrsn2']");
        // 住所3
        this.$adrsn3 = $("*[id='baseBean.adrsn3']");
        // 地方公共団体コード1
        this.$chihoupubdanticdn1 = $("*[id='baseBean.chihoupubdanticdn1']");
        // 地方公共団体コード2
        this.$chihoupubdanticdn2 = $("*[id='baseBean.chihoupubdanticdn2']");
        // 連絡先番号１
        this.$renrakusakinon1 = $("*[id='baseBean.renrakusakinon1']");
        // 連絡先番号２
        this.$renrakusakinon2 = $("*[id='baseBean.renrakusakinon2']");
        // 連絡先番号３
        this.$renrakusakinon3 = $("*[id='baseBean.renrakusakinon3']");
        // 連絡先番号４
        this.$renrakusakinon4 = $("*[id='baseBean.renrakusakinon4']");
        // 連絡先メモ1
        this.$renrakusakimemon1 = $("*[id='baseBean.renrakusakimemon1']");
        // 連絡先メモ2
        this.$renrakusakimemon2 = $("*[id='baseBean.renrakusakimemon2']");
        // 連絡先メモ3
        this.$renrakusakimemon3 = $("*[id='baseBean.renrakusakimemon3']");
        // 連絡先メモ4
        this.$renrakusakimemon4 = $("*[id='baseBean.renrakusakimemon4']");
        // メールアドレス
        this.$mailadrse = $("*[id='baseBean.mailadrse']");
        // プラン初回設定日
        this.$rknplnsetteidd = $("*[id='baseBean.rknplnsetteidd']");
        // プラン初回設定日変更前
        this.$rknplnsetteiddbefore = $("*[id='rknplnsetteiddbefore']");
        // プラン継続更新日
        this.$rknplnkeizokukshndd = $("*[id='baseBean.rknplnkeizokukshndd']");
        // プラン継続更新日変更前
        this.$rknplnkeizokukshnddbefore = $("*[id='rknplnkeizokukshnddbefore']");
        // 申込受付日
        this.$moshikomircptdd = $("*[id='baseBean.moshikomircptdd']");
        // 入会申し込みNo
        this.$nyukaimokoshono = $("*[id='baseBean.nyukaimokoshono']");
        // 入会日
        this.$nyukaidd = $("*[id='baseBean.nyukaidd']");
        // 会員申込No
        this.$kaiinmokoshono = $("*[id='baseBean.kaiinmokoshono']");        
        // 申込登録者コード
        this.$moshikomitoshacd = $("*[id='baseBean.moshikomitoshacd']");
        // 申込登録者名
        this.$toshamei5 = $("*[id='baseBean.toshamei5']");
        // 入会登録者コード
        this.$nyukaiddtoshacd = $("*[id='baseBean.nyukaiddtoshacd']");
        // 入会登録者名
        this.$toshamei6 = $("*[id='baseBean.toshamei6']");
        // 退会登録者コード
        this.$taikaiddtoshacd = $("*[id='baseBean.taikaiddtoshacd']");
        // 退会登録者名
        this.$toshamei7 = $("*[id='baseBean.toshamei7']");
        // サーバーレンタル料
        this.$wsvrtlrkn = $("*[id='baseBean.wsvrtlrkn']");
        // 初回設置日
        this.$shokaiwsvstupdd = $("*[id='baseBean.shokaiwsvstupdd']");
        // 紹介元得意先コードリンク
        this.$reftokuisakicdBtn = $("*[id='b0501w02-reftokuisakicd']");
        // 紹介元得意先コード
        this.$shoukaitokuisakicd = $("*[id='deliveryOrderBean.shoukaitokuisakicd']");
        // 紹介元得意先名
        this.$shoukaitokuisakimei = $("*[id='deliveryOrderBean.tokuisakimei']");
        // 違約金情報リンク表示
        this.$iyakukinjoholink = $("*[id='iyakukinjoholink']");
        // 違約金情報ラベル表示
        this.$iyakukinjoholinklbl = $("*[id='iyakukinjoholinklbl']");
        // 違約金情報リンク
        this.$linkiyakukinjohoBtn = $("*[id='b0501w02-linkiyakukinjoho']");
        // 得意先違約金金額
        this.$iyakukinkin = $("*[id='baseBean.iyakukinkin']");
        // サブレンタル倉庫リンク表示
        this.$sokocdlink = $("*[id='sokocdlink']");
        // サブレンタル倉庫リンクラベル表示
        this.$sokolinklbl = $("*[id='sokolinklbl']");
        // サブレンタル倉庫コードリンク
        this.$refsokocdBtn = $("*[id='b0501w02-refsokocd']");
        // サブレンタル倉庫コード
        this.$saburtlsokocd = $("*[id='baseBean.saburtlsokocd']");
        // サブレンタル倉庫名称
        this.$sokonmn1 = $("*[id='baseBean.sokonmn1']");
        // 設置日登録者コードリンク
        this.$refsecchitoshacdBtn = $("*[id='b0501w02-refsecchitoshacd']");
        // 設置日登録者コード
        this.$stupddtoshacd = $("*[id='baseBean.stupddtoshacd']");
        // 設置日登録者名称
        this.$toshamei1 = $("*[id='baseBean.toshamei1']");
        // 引上登録者コードリンク
        this.$refhikiagetoshacdBtn = $("*[id='b0501w02-refhikiagetoshacd']");
        // 引上登録者コード
        this.$hikiagetoshacd = $("*[id='baseBean.hikiagetoshacd']");
        // 引上登録者名称
        this.$toshamei3 = $("*[id='baseBean.toshamei3']");
        // 登録年月日
        this.$inptsystmymd = $("*[id='baseBean.inptsystmymd']");
        // 登録時間
        this.$inptsystmjikan = $("*[id='baseBean.inptsystmjikan']");
        // 登録者AP基本コード
        this.$toshaapkihoncd = $("*[id='baseBean.toshaapkihoncd']");
        // 登録者ショップコード
        this.$toshashopcd = $("*[id='baseBean.toshashopcd']");
        // 登録者コード
        this.$toshacd = $("*[id='baseBean.toshacd']");
        // 登録者名称
        this.$toshamei = $("*[id='baseBean.toshamei']");
        // 更新年月日
        this.$kshnsystmymd = $("*[id='baseBean.kshnsystmymd']");
        // 更新時間
        this.$kshnsystmjikan = $("*[id='baseBean.kshnsystmjikan']");
        // 更新者AP基本コード
        this.$koshaapkihoncd = $("*[id='baseBean.koshaapkihoncd']");
        // 更新者ショップコード
        this.$koshashopcd = $("*[id='baseBean.koshashopcd']");
        // 更新者コード
        this.$koshacd = $("*[id='baseBean.koshacd']");
        // 更新者名称
        this.$koshamei = $("*[id='baseBean.koshamei']");
        
        // 営業エリアコードリンク
        this.$refeigyoareacdBtn = $("*[id='b0501w02-refeigyoareacd']");
        // 営業エリアコード
        this.$eigyoareacd = $("*[id='deliveryOrderBean.eigyoareacd']");
        // 営業エリア名称
        this.$eigyoareanm = $("*[id='deliveryOrderBean.eigyoareanm']");
        // 営業担当者コードリンク
        this.$reftnshacdBtn = $("*[id='b0501w02-reftnshacd']");
        // 営業担当者コード
        this.$eigyotnshacd = $("*[id='deliveryOrderBean.eigyotnshacd']");
        // 営業担当者名称
        this.$eigyotnshanm = $("*[id='deliveryOrderBean.eigyotnshanm']");
        // 獲得担当者コード1リンク
        this.$reftnshacd1Btn = $("*[id='b0501w02-reftnshacd1']");
        // 獲得担当者コード
        this.$kakutokutnshacd = $("*[id='deliveryOrderBean.kakutokutnshacd']");
        // 獲得担当者名称
        this.$tnshamei3 = $("*[id='deliveryOrderBean.tnshamei3']");
        // 本部エージェントコードリンク
        this.$refejntcdBtn = $("*[id='b0501w02-refejntcd']");
        // 本部エージェントコード
        this.$honbuejntcd = $("*[id='deliveryOrderBean.honbuejntcd']");
        // 本部エージェント名称
        this.$ejntmein1 = $("*[id='deliveryOrderBean.ejntmein1']");
        // 獲得本部キャンペーンコードリンク
        this.$linkkakutokuhonbukynpncdBtn = $("*[id='b0501w02-linkkakutokuhonbukynpncd']");
        // 獲得本部キャンペーンコード
        this.$kakutokuhonbukynpncd = $("*[id='deliveryOrderBean.kakutokuhonbukynpncd']");
        // 獲得本部キャンペーン名称
        this.$kakutokuhonbukynpnnm = $("*[id='deliveryOrderBean.kakutokuhonbukynpnnm']");
        // 獲得エージェントコードリンク
        this.$refejntcd3Btn = $("*[id='b0501w02-refejntcd3']");
        // 獲得エージェントコード
        this.$kakutokuejntcd = $("*[id='deliveryOrderBean.kakutokuejntcd']");
        // 獲得エージェント名称
        this.$ejntmein2 = $("*[id='deliveryOrderBean.ejntmein2']");
        // エージェントマージン１
        this.$ejntmgnn1 = $("*[id='deliveryOrderBean.ejntmgnn1']");
        // エージェントマージン２
        this.$ejntmgnn2 = $("*[id='deliveryOrderBean.ejntmgnn2']");
        // 獲得キャンペーンコードリンク
        this.$refkakutokukynpncdBtn = $("*[id='b0501w02-refkakutokukynpncd']");
        // 獲得キャンペーンコード
        this.$kakutokukynpncd = $("*[id='deliveryOrderBean.kakutokukynpncd']");
        // 獲得キャンペーン名称
        this.$kakutokukynpnnm = $("*[id='deliveryOrderBean.kakutokukynpnnm']");
        // 販促詳細コードリンク
        this.$refhansokushscdBtn = $("*[id='b0501w02-refhansokushscd']");
        // 販促詳細コード
        this.$hansokushscd = $("*[id='deliveryOrderBean.hansokushscd']");
        // 販促詳細名称
        this.$hansokunm = $("*[id='deliveryOrderBean.hansokunm']");
        // 配達拠点コードリンク
        this.$refdlvrykyotenBtn = $("*[id='b0501w02-refdlvrykyoten']");
        // 配達管轄ａｐ基本コード
        this.$dlvrykankatsuapkihoncd = $("*[id='deliveryOrderBean.dlvrykankatsuapkihoncd']");
        // 配達管轄ａｐ名称
        this.$apnm2 = $("*[id='deliveryOrderBean.apnm2']");
        // 配達管轄ショップコード
        this.$dlvrykankatsushopcd = $("*[id='deliveryOrderBean.dlvrykankatsushopcd']");
        // 配達管轄ショップ名称
        this.$shopnm2 = $("*[id='deliveryOrderBean.shopnm2']");
        // 配達拠点名称
        this.$dlvrykankatsukyotencd = $("*[id='deliveryOrderBean.dlvrykankatsukyotencd']");
        // 配達拠点名称
        this.$kyotennm = $("*[id='deliveryOrderBean.kyotennm']");
        // 配達会社リンク表示
        this.$cmpnycdlink = $("*[id='b0501w02-cmpnycdlink']");
        // 配達会社リンクラベル表示
        this.$cmpnylinklbl = $("*[id='b0501w02-cmpnylinklbl']");
        // 配達会社コードリンク
        this.$refdlvrycmpnycdBtn = $("*[id='b0501w02-refdlvrycmpnycd']");
        // 配達会社コード
        this.$dlvrycmpnycd = $("*[id='deliveryOrderBean.dlvrycmpnycd']");
        // 配達会社名称
        this.$dlvrycmpnynm = $("*[id='deliveryOrderBean.dlvrycmpnynm']");
        // 配達エリアコードリンク
        this.$refdlvryareacdBtn = $("*[id='b0501w02-refdlvryareacd']");
        // 配達エリアコード
        this.$dlvryareacd = $("*[id='deliveryOrderBean.dlvryareacd']");
        // 配達エリア名称
        this.$dlvryareanm = $("*[id='deliveryOrderBean.dlvryareanm']");
        // 配達担当者リンク表示
        this.$dlvrytnshacdlink = $("*[id='b0501w02-dlvrytnshacdlink']");
        // 配達担当者リンクラベル表示
        this.$dlvrytnshalinklbl = $("*[id='b0501w02-dlvrytnshalinklbl']");
        // 配達担当者コード3リンク
        this.$reftnshacd3Btn = $("*[id='b0501w02-reftnshacd3']");
        // 配達担当者コード
        this.$dlvrytnshacd = $("*[id='deliveryOrderBean.dlvrytnshacd']");
        // 配達担当者名称
        this.$dlvrytnshamei = $("*[id='deliveryOrderBean.dlvrytnshamei']");
        // 配達順
        this.$dlvryjun = $("*[id='deliveryOrderBean.dlvryjun']");
        // 地図番号
        this.$chizuno = $("*[id='deliveryOrderBean.chizuno']");
        // 外部連携配達コード
        this.$gaiburenkeidlvrycd = $("*[id='deliveryOrderBean.gaiburenkeidlvrycd']");
        // 受注受付区分
        this.$acptrcptkbnnm = $("*[id='deliveryOrderBean.acptrcptkbnnm']");
        // 受注時間指定時刻開始
        this.$acptjikanshiteijikoku = $("*[id='deliveryOrderBean.acptjikanshiteijikoku']");
        // 受注時間指定時刻終了
        this.$acptjikanshiteijikokushry = $("*[id='deliveryOrderBean.acptjikanshiteijikokushry']");
        // 預かりボトル本数１
        this.$azukaribtlhnsn1 = $("*[id='deliveryOrderBean.azukaribtlhnsn1']");
        // 預かりボトル本数２
        this.$azukaribtlhnsn2 = $("*[id='deliveryOrderBean.azukaribtlhnsn2']");
        // 注意内容（グループ）
        this.$chuinaiyoaci = $("*[id='deliveryOrderBean.chuinaiyoaci']");
        // 注意内容（AP）
        this.$chuinaiyoap = $("*[id='deliveryOrderBean.chuinaiyoap']");
        // 連絡事項（公開）
        this.$renrakujikokokai = $("*[id='deliveryOrderBean.renrakujikokokai']");
        // 特記事項（非公開）
        this.$tokkijikohikokai = $("*[id='deliveryOrderBean.tokkijikohikokai']");
        // 印刷メモ１
        this.$prntmemon1 = $("*[id='deliveryOrderBean.prntmemon1']");
        // 印刷メモ２
        this.$prntmemon2 = $("*[id='deliveryOrderBean.prntmemon2']");
        // 印刷メモ３
        this.$prntmemon3 = $("*[id='deliveryOrderBean.prntmemon3']");
        // まとめ買い有料本数
        this.$mtmkiyuryohns = $("*[id='saleBean.mtmkiyuryohns']");
        // まとめ買い無料本数
        this.$mtmkimuryohns = $("*[id='saleBean.mtmkimuryohns']");
        // 休眠区分
        this.$kyuminkbnnm = $("*[id='saleBean.kyuminkbnnm']");
        // 現在残高
        this.$genzaizd = $("*[id='saleBean.genzaizd']");
        // 計上前受注金額
        this.$keijomaeacptkin = $("*[id='saleBean.keijomaeacptkin']");
        // 紹介者情報リンク表示
        this.$shoshakazulink = $("*[id='shoshakazulink']");
        // 紹介者情報ラベル表示
        this.$shoshakazulinklbl = $("*[id='shoshakazulinklbl']");
        // 紹介者情報リンク
        this.$refshoshakazu = $("*[id='b0501w02-refshoshakazu']");
        // 預かり書番号
        this.$azushono = $("*[id='saleBean.azushono']");
        // 保証金預かり日
        this.$hoshokinazukaridd = $("*[id='saleBean.hoshokinazukaridd']");
        // 保証金預かり金額
        this.$hoshokinazukarikin = $("*[id='saleBean.hoshokinazukarikin']");
        // 保証金返却日
        this.$hoshokinhenkyakudd = $("*[id='saleBean.hoshokinhenkyakudd']");
        // 保証金返却金額
        this.$hoshokinhenkyakukin = $("*[id='saleBean.hoshokinhenkyakukin']");
        // ワンショットコードリンク
        this.$sanshokensakuwanshottocdBtn = $("*[id='b0501w02-sanshokensakuwanshottocd']");
        // ワンショットコード
        this.$wanshottocd = $("*[id='saleBean.wanshottocd']");
        // マージン名称
        this.$mgnnm = $("*[id='saleBean.mgnnm']");
        // 商品コード1リンク
        this.$refshncd1Btn = $("*[id='b0501w02-refshncd1']");
        // 商品コード1
        this.$mgnyoshncdn1 = $("*[id='saleBean.mgnyoshncdn1']");
        // 商品名称1
        this.$shnnm1 = $("*[id='saleBean.shnnm1']");
        // マージンコード1リンク
        this.$refmgncd1Btn = $("*[id='b0501w02-refmgncd1']");
        // マージンコード1
        this.$mgncdn1 = $("*[id='saleBean.mgncdn1']");
        // マージン名称1
        this.$mgnnm1 = $("*[id='saleBean.mgnnm1']");
        // 商品コード２リンク
        this.$refshncd2Btn = $("*[id='b0501w02-refshncd2']");
        // 商品コード２
        this.$mgnyoshncdn2 = $("*[id='saleBean.mgnyoshncdn2']");
        // 商品名称２
        this.$shnnm2 = $("*[id='saleBean.shnnm2']");
        // マージンコード２リンク
        this.$refmgncd2Btn = $("*[id='b0501w02-refmgncd2']");
        // マージンコード２
        this.$mgncdn2 = $("*[id='saleBean.mgncdn2']");
        // マージン名称２
        this.$mgnnm2 = $("*[id='saleBean.mgnnm2']");
        // 商品コード３リンク
        this.$refshncd3Btn = $("*[id='b0501w02-refshncd3']");
        // 商品コード３
        this.$mgnyoshncdn3 = $("*[id='saleBean.mgnyoshncdn3']");
        // 商品名称３
        this.$shnnm3 = $("*[id='saleBean.shnnm3']");
        // マージンコード３リンク
        this.$refmgncd3Btn = $("*[id='b0501w02-refmgncd3']");
        // マージンコード３
        this.$mgncdn3 = $("*[id='saleBean.mgncdn3']");
        // マージン名称３
        this.$mgnnm3 = $("*[id='saleBean.mgnnm3']");
        // 商品コード４リンク
        this.$refshncd4Btn = $("*[id='b0501w02-refshncd4']");
        // 商品コード４
        this.$mgnyoshncdn4 = $("*[id='saleBean.mgnyoshncdn4']");
        // 商品名称４
        this.$shnnm4 = $("*[id='saleBean.shnnm4']");
        // マージンコード４リンク
        this.$refmgncd4Btn = $("*[id='b0501w02-refmgncd4']");
        // マージンコード４
        this.$mgncdn4 = $("*[id='saleBean.mgncdn4']");
        // マージン名称４
        this.$mgnnm4 = $("*[id='saleBean.mgnnm4']");
        // 自動値引コード１リンク
        this.$refjidonebikin1Btn = $("*[id='b0501w02-refjidonebikin1']");
        // 自動値引コード１
        this.$jidonebikicd1 = $("*[id='saleBean.jidonebikicd1']");
        // 自動値引商品コード１
        this.$shncd1 = $("*[id='saleBean.shncd1']");
        // 自動値引商品名称１
        this.$shnnmn1 = $("*[id='saleBean.shnnmn1']");
        // 自動値引名称１
        this.$jidonebikinm1 = $("*[id='saleBean.jidonebikinm1']");
        // 月間販売本数１
        this.$gekkanhanbaihns1 = $("*[id='saleBean.gekkanhanbaihns1']");
        // 自動値引種別１
        this.$jidonebikikind1 = $("*[id='saleBean.jidonebikikind1']");
        // 値引本数１
        this.$nebikihns1 = $("*[id='saleBean.nebikihns1']");
        // 値引金額１
        this.$nebikikin1 = $("*[id='saleBean.nebikikin1']");
        // 自動値引コード２リンク
        this.$refjidonebikin2Btn = $("*[id='b0501w02-refjidonebikin2']");
        // 自動値引コード２
        this.$jidonebikicd2 = $("*[id='saleBean.jidonebikicd2']");
        // 自動値引名称２
        this.$jidonebikinm2 = $("*[id='saleBean.jidonebikinm2']");
        // 自動値引商品コード２
        this.$shncd2 = $("*[id='saleBean.shncd2']");
        // 自動値引商品名称２
        this.$shnnmn2 = $("*[id='saleBean.shnnmn2']");
        // 月間販売本数２
        this.$gekkanhanbaihns2 = $("*[id='saleBean.gekkanhanbaihns2']");
        // 自動値引種別２
        this.$jidonebikikind2 = $("*[id='saleBean.jidonebikikind2']");
        // 値引本数２
        this.$nebikihns2 = $("*[id='saleBean.nebikihns2']");
        // 値引金額２
        this.$nebikikin2 = $("*[id='saleBean.nebikikin2']");
        // 自動値引コード３リンク
        this.$refjidonebikin3Btn = $("*[id='b0501w02-refjidonebikin3']");
        // 自動値引コード
        this.$jidonebikicd3 = $("*[id='saleBean.jidonebikicd3']");
        // 自動値引名称３
        this.$jidonebikinm3 = $("*[id='saleBean.jidonebikinm3']");
        // 自動値引商品コード３
        this.$shncd3 = $("*[id='saleBean.shncd3']");
        // 自動値引商品名称３
        this.$shnnmn3 = $("*[id='saleBean.shnnmn3']");
        // 月間販売本数３
        this.$gekkanhanbaihns3 = $("*[id='saleBean.gekkanhanbaihns3']");
        // 自動値引種別３
        this.$jidonebikikind3 = $("*[id='saleBean.jidonebikikind3']");
        // 値引本数３
        this.$nebikihns3 = $("*[id='saleBean.nebikihns3']");
        // 値引金額３
        this.$nebikikin3 = $("*[id='saleBean.nebikikin3']");
        // ポイント管理有無ラベル1
        this.$pointkbn1 = $("*[id='labelPointkbn1']");
        // ポイント管理有無ラベル2
        this.$pointkbn2 = $("*[id='labelPointkbn2']");
        // まとめ買い数量
        this.$mtmkisuryo = $("*[id='saleBean.mtmkisuryo']");
        // まとめ買いポイント数
        this.$mtmkipointsu = $("*[id='saleBean.mtmkipointsu']");
        // 入会特典ポイント数
        this.$nyukaitokutenpointsu = $("*[id='saleBean.nyukaitokutenpointsu']");
        // 勤務先郵便番号
        this.$kinmusakiyubinno = $("*[id='additionalBean.kinmusakiyubinno']");
        // 勤務先住所1
        this.$kinmusakiadrsn1 = $("*[id='additionalBean.kinmusakiadrsn1']");
        // 勤務先住所2
        this.$kinmusakiadrsn2 = $("*[id='additionalBean.kinmusakiadrsn2']");
        // 勤務先住所3
        this.$kinmusakiadrsn3 = $("*[id='additionalBean.kinmusakiadrsn3']");
        // 勤務先地方公共団体コード1
        this.$kinmusakichihoupubdanticdn1 = $("*[id='additionalBean.kinmusakichihoupubdanticdn1']");
        // 勤務先地方公共団体コード2
        this.$kinmusakichihoupubdanticdn2 = $("*[id='additionalBean.kinmusakichihoupubdanticdn2']");
        // 実績担当者リンク表示
        this.$jissekitnshacdlink = $("*[id='jissekitnshacdlink']");
        // 定例発生番号
        this.$teireihasseino = $("*[id='rentalBean.teireihasseino']");
        // 定例売上複数行用メッセージ
        this.$hukusugyoyomsg = $("*[id='rentalBean.hukusugyoyomsg']");
        // 計上日
        this.$keijodd = $("*[id='rentalBean.keijodd']");
        // 開始年月
        this.$strtdydm = $("*[id='rentalBean.strtdydm']");
        // 次回計上年月
        this.$jikaikeijodydm = $("*[id='rentalBean.jikaikeijodydm']");
        // 終了年月
        this.$shrydydm = $("*[id='rentalBean.shrydydm']");
        // 前回発生計上日
        this.$znkhasseikeijodd = $("*[id='rentalBean.znkhasseikeijodd']");
        // 売上計上区分ラベル
        this.$urikeijokbnnm = $("*[id='rentalBean.urikeijokbnnm']");
        // 数量
        this.$suryo = $("*[id='rentalBean.suryo']");
        // 標準価格
        this.$hyojunprice = $("*[id='rentalBean.hyojunprice']");
        // 単価
        this.$unt = $("*[id='rentalBean.unt']");
        // 単価合計金額
        this.$smkin = $("*[id='rentalBean.smkin']");
        // 単価消費税
        this.$shohitax = $("*[id='rentalBean.shohitax']");
        // 初回単価
        this.$shokaiunt = $("*[id='rentalBean.shokaiunt']");
        // 初回単価合計金額
        this.$smkin1 = $("*[id='rentalBean.smkin1']");
        // 初回単価消費税
        this.$shohitax1 = $("*[id='rentalBean.shohitax1']");
        // 明細備考
        this.$meisaibiko = $("*[id='rentalBean.meisaibiko']");
        // 実績担当者リンク表示
        this.$jissekitnshacdlink = $("*[id='jissekitnshacdlink']");
        // 実績担当者ラベル表示
        this.$jissekitnshacdlbl = $("*[id='jissekitnshacdlbl']");
        // 実績担当者コードリンク
        this.$linkjissekitnshacdBtn = $("*[id='b0501w02-linkjissekitnshacd']");
        // 実績担当者コード
        this.$jissekitnshacd = $("*[id='rentalBean.jissekitnshacd']");
        // 実績担当者名称
        this.$tnshamei = $("*[id='rentalBean.tnshamei']");
        // 定例売上登録年月日
        this.$inptsystmymd1 = $("*[id='rentalBean.inptsystmymd1']");
        // 定例売上登録時間
        this.$inptsystmjikan1 = $("*[id='rentalBean.inptsystmjikan1']");
        // 定例売上登録者AP基本コード
        this.$toshaapkihoncd1 = $("*[id='rentalBean.toshaapkihoncd1']");
        // 定例売上登録者ショップコード
        this.$toshashopcd1 = $("*[id='rentalBean.toshashopcd1']");
        // 定例売上登録者コード
        this.$toshacd1 = $("*[id='rentalBean.toshacd1']");
        // 定例売上登録者名称
        this.$toshamei2 = $("*[id='rentalBean.toshamei2']");
        // 定例売上更新年月日
        this.$kshnsystmymd1 = $("*[id='rentalBean.kshnsystmymd1']");
        // 定例売上更新時間
        this.$kshnsystmjikan1 = $("*[id='rentalBean.kshnsystmjikan1']");
        // 定例売上更新者AP基本コード
        this.$koshaapkihoncd1 = $("*[id='rentalBean.koshaapkihoncd1']");
        // 定例売上更新者ショップコード
        this.$koshashopcd1 = $("*[id='rentalBean.koshashopcd1']");
        // 定例売上更新者コード
        this.$koshacd1 = $("*[id='rentalBean.koshacd1']");
        // 定例売上更新者名称
        this.$koshamei1 = $("*[id='rentalBean.koshamei1']");
        // 得意先マスタ情報変更日
        this.$ttmasutachgdd = $("*[id='reservationBean.ttmasutachgdd']");
        // 得意先マスタ情報得意先名称11
        this.$tokuisakinmn11 = $("*[id='reservationBean.tokuisakinmn11']");
        // 得意先マスタ情報得意先名称21
        this.$tokuisakinmn21 = $("*[id='reservationBean.tokuisakinmn21']");
        // 得意先マスタ情報得意先名称カナ11
        this.$tokuisakinmkanan11 = $("*[id='reservationBean.tokuisakinmkanan11']");
        // 得意先マスタ情報得意先名称カナ21
        this.$tokuisakinmkanan21 = $("*[id='reservationBean.tokuisakinmkanan21']");
        // 得意先マスタ情報郵便番号1
        this.$yubinno1 = $("*[id='reservationBean.yubinno1']");
        // 得意先マスタ情報連絡先番号11
        this.$renrakusakinon11 = $("*[id='reservationBean.renrakusakinon11']");
        // 得意先マスタ情報連絡先メモ11
        this.$renrakusakimemon11 = $("*[id='reservationBean.renrakusakimemon11']");
        // 得意先マスタ情報連絡先番号21
        this.$renrakusakinon21 = $("*[id='reservationBean.renrakusakinon21']");
        // 得意先マスタ情報連絡先メモ21
        this.$renrakusakimemon21 = $("*[id='reservationBean.renrakusakimemon21']");
        // 得意先マスタ情報連絡先番号31
        this.$renrakusakinon31 = $("*[id='reservationBean.renrakusakinon31']");
        // 得意先マスタ情報連絡先メモ31
        this.$renrakusakimemon31 = $("*[id='reservationBean.renrakusakimemon31']");
        // 得意先マスタ情報連絡先番号41
        this.$renrakusakinon41 = $("*[id='reservationBean.renrakusakinon41']");
        // 得意先マスタ情報連絡先メモ41
        this.$renrakusakimemon41 = $("*[id='reservationBean.renrakusakimemon41']");
        // 得意先マスタ情報住所11
        this.$adrsn11 = $("*[id='reservationBean.adrsn11']");
        // 得意先マスタ情報住所21
        this.$adrsn21 = $("*[id='reservationBean.adrsn21']");
        // 得意先マスタ情報住所31
        this.$adrsn31 = $("*[id='reservationBean.adrsn31']");
        // 得意先マスタ情報地方公共団体コード1
        this.$tokuisakichihoupubdanticdn1 = $("*[id='reservationBean.tokuisakichihoupubdanticdn1']");
        // 得意先マスタ情報地方公共団体コード2
        this.$tokuisakichihoupubdanticdn2 = $("*[id='reservationBean.tokuisakichihoupubdanticdn2']");
        // 得意先マスタ情報印刷メモ11
        this.$prntmemon11 = $("*[id='reservationBean.prntmemon11']");
        // 得意先マスタ情報印刷メモ21
        this.$prntmemon21 = $("*[id='reservationBean.prntmemon21']");
        // 得意先マスタ情報印刷メモ31
        this.$prntmemon31 = $("*[id='reservationBean.prntmemon31']");
        // 得意先マスタ情報地図番号1
        this.$chizuno1 = $("*[id='reservationBean.chizuno1']");
        // 得意先マスタ情報配達順1
        this.$dlvryjun1 = $("*[id='reservationBean.dlvryjun1']");
        // 得意先マスタ情報営業エリアリンク表示
        this.$eigyoareacd1link = $("*[id='eigyoareacd1link']");
        // 得意先マスタ情報営業エリアリンクラベル表示
        this.$eigyoareacd1lbl = $("*[id='eigyoareacd1lbl']");
        // 得意先マスタ情報営業エリアコードリンク
        this.$refeigyoareacd1Btn = $("*[id='b0501w02-refeigyoareacd1']");
        // 得意先マスタ情報営業エリアコード
        this.$eigyoareacd1 = $("*[id='reservationBean.eigyoareacd1']");
        // 得意先マスタ情報営業エリア名称
        this.$eigyoareanm1 = $("*[id='reservationBean.eigyoareanm1']");
        // 得意先マスタ情報営業担当者リンク表示
        this.$eigyotnshacd1link = $("*[id='eigyotnshacd1link']");
        // 得意先マスタ情報営業担当者リンクラベル表示
        this.$eigyotnshacd1lbl = $("*[id='eigyotnshacd1lbl']");
        // 得意先マスタ情報営業担当者コード3リンク
        this.$reftnshacd2Btn = $("*[id='b0501w02-reftnshacd2']");
        // 得意先マスタ情報営業担当者コード
        this.$eigyotnshacd1 = $("*[id='reservationBean.eigyotnshacd1']");
        // 得意先マスタ情報営業担当者名称
        this.$eigyotnshanm1 = $("*[id='reservationBean.eigyotnshanm1']");
        // 得意先マスタ情報配達拠点リンク表示
        this.$dlvrykyoten1link = $("*[id='dlvrykyoten1link']");
        // 得意先マスタ情報配達拠点リンクラベル表示
        this.$dlvrykyoten1lbl = $("*[id='dlvrykyoten1lbl']");
        // 得意先マスタ情報配達拠点コードリンク
        this.$refdlvrykyoten1Btn = $("*[id='b0501w02-refdlvrykyoten1']");
        // 得意先マスタ情報配達管轄ＡＰ基本コード1
        this.$dlvrykankatsuapkihoncd1 = $("*[id='reservationBean.dlvrykankatsuapkihoncd1']");
        // 得意先マスタ情報配達管轄ＡＰ名称1
        this.$apnm1 = $("*[id='reservationBean.apnm1']");
        // 得意先マスタ情報配達管轄ショップコード１
        this.$dlvrykankatsushopcd1 = $("*[id='reservationBean.dlvrykankatsushopcd1']");
        // 得意先マスタ情報配達管轄ショップ名称１
        this.$shopnm1 = $("*[id='reservationBean.shopnm1']");
        // 得意先マスタ情報配達管轄拠点コード1
        this.$dlvrykankatsukyotencd1 = $("*[id='reservationBean.dlvrykankatsukyotencd1']");
        // 得意先マスタ情報配達管轄拠点名称１
        this.$kyotennm1 = $("*[id='reservationBean.kyotennm1']");
        // 得意先マスタ情報配達会社リンク表示
        this.$cmpnycdlink1 = $("*[id='b0501w02-cmpnycdlink1']");
        // 得意先マスタ情報配達会社リンクラベル表示
        this.$cmpnylinklbl1 = $("*[id='b0501w02-cmpnylinklbl1']");
        // 得意先マスタ情報配達会社コードリンク
        this.$refdlvrycmpnycd1Btn = $("*[id='b0501w02-refdlvrycmpnycd1']");
        // 得意先マスタ情報配達会社コード
        this.$dlvrycmpnycd1 = $("*[id='reservationBean.dlvrycmpnycd1']");
        // 得意先マスタ情報配達会社名称
        this.$dlvrycmpnynm1 = $("*[id='reservationBean.dlvrycmpnynm1']");
        // 得意先マスタ情報配達エリアリンク表示
        this.$dlvryareacd1link = $("*[id='dlvryareacd1link']");
        // 得意先マスタ情報配達エリアリンクラベル表示
        this.$dlvryareacd1lbl = $("*[id='dlvryareacd1lbl']");
        // 得意先マスタ情報配達エリアコードリンク
        this.$refdlvryareacd1Btn = $("*[id='b0501w02-refdlvryareacd1']");
        // 得意先マスタ情報配達エリアコード
        this.$dlvryareacd1 = $("*[id='reservationBean.dlvryareacd1']");
        // 得意先マスタ情報配達エリア名称
        this.$dlvryareanm1 = $("*[id='reservationBean.dlvryareanm1']");
        // 得意先マスタ情報配達担当者リンク表示
        this.$dlvrytnshacdlink1 = $("*[id='b0501w02-dlvrytnshacdlink1']");
        // 得意先マスタ情報配達担当者リンクラベル表示
        this.$dlvrytnshalinklbl1 = $("*[id='b0501w02-dlvrytnshalinklbl1']");
        // 得意先マスタ情報配達担当者コードリンク
        this.$reftnshacd4Btn = $("*[id='b0501w02-reftnshacd4']");
        // 得意先マスタ情報配達担当者コード
        this.$dlvrytnshacd1 = $("*[id='reservationBean.dlvrytnshacd1']");
        // 得意先マスタ情報配達担当者名称
        this.$dlvrytnshamei1 = $("*[id='reservationBean.dlvrytnshamei1']");
        // 得意先マスタ情報受注時間指定時刻開始
        this.$acptjikanshiteijikokustr = $("*[id='reservationBean.acptjikanshiteijikokustr']");
        // 得意先マスタ情報受注時間指定時刻終了
        this.$acptjikanshiteijikokushry1 = $("*[id='reservationBean.acptjikanshiteijikokushry1']");
        // 請求先マスタ情報変更日
        this.$skmhnkymd = $("*[id='reservationBean.skmhnkymd']");
        // 請求先マスタ情報請求先名称11
        this.$skysakinmn11 = $("*[id='reservationBean.skysakinmn11']");
        // 請求先マスタ情報請求先名称カナ
        this.$skysakinmkana = $("*[id='reservationBean.skysakinmkana']");
        // 請求先マスタ情報請求先名称2
        this.$skysakinmn2 = $("*[id='reservationBean.skysakinmn2']");
        // 請求先マスタ情報請求先名称略称
        this.$skysakinmryakusho = $("*[id='reservationBean.skysakinmryakusho']");
        // 請求先マスタ情報郵便番号2
        this.$yubinno2 = $("*[id='reservationBean.yubinno2']");
        // 請求先マスタ情報連絡先番号12
        this.$renrakusakinon12 = $("*[id='reservationBean.renrakusakinon12']");
        // 請求先マスタ情報連絡先番号22
        this.$renrakusakinon22 = $("*[id='reservationBean.renrakusakinon22']");
        // 請求先マスタ情報連絡先番号32
        this.$renrakusakinon32 = $("*[id='reservationBean.renrakusakinon32']");
        // 請求先マスタ情報連絡先番号42
        this.$renrakusakinon42 = $("*[id='reservationBean.renrakusakinon42']");
        // 請求先マスタ情報住所12
        this.$adrsn12 = $("*[id='reservationBean.adrsn12']");
        // 請求先マスタ情報住所22
        this.$adrsn22 = $("*[id='reservationBean.adrsn22']");
        // 請求先マスタ情報住所32
        this.$adrsn32 = $("*[id='reservationBean.adrsn32']");
        // 請求先マスタ情報営業担当者リンク表示
        this.$eigyotnshacd2link = $("*[id='eigyotnshacd2link']");
        // 請求先マスタ情報営業担当者リンクラベル表示
        this.$eigyotnshacd2lbl = $("*[id='eigyotnshacd2lbl']");
        // 請求先マスタ情報営業担当者コードリンク
        this.$reftnshacd5Btn = $("*[id='b0501w02-reftnshacd5']");
        // 請求先マスタ情報営業担当者コード
        this.$eigyotnshacd2 = $("*[id='reservationBean.eigyotnshacd2']");
        // 請求先マスタ情報営業担当者名称
        this.$eigyotnshanm2 = $("*[id='reservationBean.eigyotnshanm2']");
        // 請求先マスタ情報回収担当者リンク表示
        this.$kaishutnshacdlink = $("*[id='kaishutnshacdlink']");
        // 請求先マスタ情報回収担当者リンクラベル表示
        this.$kaishutnshacdlbl = $("*[id='kaishutnshacdlbl']");
        // 請求先マスタ情報回収担当者コードリンク
        this.$reftnshacd6Btn = $("*[id='b0501w02-reftnshacd6']");
        // 請求先マスタ情報回収担当者コード
        this.$kaishutnshacd = $("*[id='reservationBean.kaishutnshacd']");
        // 請求先マスタ情報回収担当者名称
        this.$kaishutnshamei = $("*[id='reservationBean.kaishutnshamei']");
        // ハイフン
        this.$hyphen1 = $("*[id='hyphen1']");
        this.$hyphen2 = $("*[id='hyphen2']");
        this.$hyphen3 = $("*[id='hyphen3']");
        this.$hyphen4 = $("*[id='hyphen4']");
        this.$hyphen5 = $("*[id='hyphen5']");
        this.$hyphen6 = $("*[id='hyphen6']");
        this.$hyphen7 = $("*[id='hyphen7']");
        this.$hyphen8 = $("*[id='hyphen8']");
        this.$hyphen9 = $("*[id='hyphen9']");
        this.$hyphen10 = $("*[id='hyphen10']");
        this.$hyphen11 = $("*[id='hyphen11']");
        this.$hyphen12 = $("*[id='hyphen12']");
        this.$hyphen13 = $("*[id='hyphen13']");
        this.$hyphen14 = $("*[id='hyphen14']");
        this.$hyphen15 = $("*[id='hyphen15']");
        this.$hyphen16 = $("*[id='hyphen16']");
        
        // セレクトボックス
        // 得意先区分
        this.$tokuisakikbn = $("*[id='baseBean.tokuisakikbn']");
        // 受託顧客配達区分
        this.$jutakudlvrycstkbn = $("*[id='baseBean.jutakudlvrycstkbn']");
        // コード種別
        this.$cdkindkbn = $("*[id='baseBean.cdkindkbn']");
        // 業種
        this.$gyoshukbn = $("*[id='baseBean.gyoshukbn']");
        // 業種詳細
        this.$gyoshushskbn = $("*[id='baseBean.gyoshushskbn']");
        // 利用サーバー区分
        this.$riyowsvkbn = $("*[id='baseBean.riyowsvkbn']");
        // 事業所コード
        this.$jigyoshocd = $("*[id='baseBean.jigyoshocd']");
        // 得意先敬称区分
        this.$tokuisakikeishokbn = $("*[id='baseBean.tokuisakikeishokbn']");
        // 連絡先番号区分1
        this.$renrakusakinokbnn1 = $("*[id='baseBean.renrakusakinokbnn1']");
        // 連絡先番号区分2
        this.$renrakusakinokbnn2 = $("*[id='baseBean.renrakusakinokbnn2']");
        // 連絡先番号区分3
        this.$renrakusakinokbnn3 = $("*[id='baseBean.renrakusakinokbnn3']");
        // 連絡先番号区分4
        this.$renrakusakinokbnn4 = $("*[id='baseBean.renrakusakinokbnn4']");
        // プラン
        this.$pln = $("*[id='baseBean.pln']");
        // 得意先区分受注区分情報定数
        this.tokuisakiJutakuKbn= {tokuisakikbn:this.$tokuisakikbn,
                                    jutakudlvrycstkbn:this.$jutakudlvrycstkbn};
        // 受付区分
        this.$rcptkbn = $("*[id='deliveryOrderBean.rcptkbn']");
        // 獲得区分
        this.$kakutokukbn = $("*[id='deliveryOrderBean.kakutokukbn']");
        // 媒体区分
        this.$baitaikbn = $("*[id='deliveryOrderBean.baitaikbn']");
        // 獲得本部キャンペーン景品区分
        this.$kakutokuhonbukynpnkeihinkbn = $("*[id='deliveryOrderBean.kakutokuhonbukynpnkeihinkbn']");
        // 獲得キャンペーン景品区分
        this.$kakutokukeihin = $("*[id='deliveryOrderBean.kakutokukeihin']");
        // 納品先情報1
        this.$nohinsakijohon1 = $("*[id='deliveryOrderBean.nohinsakijohon1']");
        // 納品先情報2
        this.$nohinsakijohon2 = $("*[id='deliveryOrderBean.nohinsakijohon2']");
        // 受注時間指定区分
        this.$acptjikanshiteikbn = $("*[id='deliveryOrderBean.acptjikanshiteikbn']");
        // 注意内容区分(グループ)
        this.$chuitokuisakikbnaci = $("*[id='deliveryOrderBean.chuitokuisakikbnaci']");
        // 注意内容区分(AP)
        this.$chuitokuisakikbnap = $("*[id='deliveryOrderBean.chuitokuisakikbnap']");
        // 商品名セレクトボックス
        this.$shnmei = $("*[id='rentalBean.shnmei']");
        // 得意先マスタ登録年月日
        this.$tsminptsystmymd = $("*[id='reservationBean.tsminptsystmymd']");
        // 得意先マスタ登録時間
        this.$tsminptsystmjikan = $("*[id='reservationBean.tsminptsystmjikan']");
        // 得意先マスタ登録者AP基本コード
        this.$tsmtoshaapkihoncd = $("*[id='reservationBean.tsmtoshaapkihoncd']");
        // 得意先マスタ登録者ショップコード
        this.$tsmtoshashopcd = $("*[id='reservationBean.tsmtoshashopcd']");
        // 得意先マスタ登録者コード
        this.$tsmtoshacd = $("*[id='reservationBean.tsmtoshacd']");
        // 得意先マスタ登録者名称
        this.$tsmtoshamei = $("*[id='reservationBean.tsmtoshamei']");
        // 得意先マスタ更新年月日
        this.$tsmkshnsystmymd = $("*[id='reservationBean.tsmkshnsystmymd']");
        // 得意先マスタ更新時間
        this.$kshnsystmjikan3 = $("*[id='reservationBean.kshnsystmjikan3']");
        // 得意先マスタ更新者AP基本コード
        this.$koshaapkihoncd2 = $("*[id='reservationBean.koshaapkihoncd2']");
        // 得意先マスタ更新者ショップコード
        this.$koshashopcd3 = $("*[id='reservationBean.koshashopcd3']");
        // 得意先マスタ更新者コード
        this.$koshacd3 = $("*[id='reservationBean.koshacd3']");
        // 得意先マスタ更新者名称
        this.$koshamei3 = $("*[id='reservationBean.koshamei3']");
        // 得意先マスタ前回変更日
        this.$ymd3 = $("*[id='reservationBean.ymd3']");
        // 得意先マスタ情報得意先敬称区分
        this.$tokuisakikeishokbn1 = $("*[id='reservationBean.tokuisakikeishokbn1']");
        // 得意先マスタ情報連絡先番号11
        this.$renrakusakinokbnn11 = $("*[id='reservationBean.renrakusakinokbnn11']");
        // 得意先マスタ情報連絡先番号21
        this.$renrakusakinokbnn21 = $("*[id='reservationBean.renrakusakinokbnn21']");
        // 得意先マスタ情報連絡先番号31
        this.$renrakusakinokbnn31 = $("*[id='reservationBean.renrakusakinokbnn31']");
        // 得意先マスタ情報連絡先番号41
        this.$renrakusakinokbnn41 = $("*[id='reservationBean.renrakusakinokbnn41']");
        // 得意先マスタ情報事業所コード1
        this.$jigyoshocd1 = $("*[id='reservationBean.jigyoshocd1']");
        // 得意先マスタ情報納品先情報11
        this.$nohinsakijohon11 = $("*[id='reservationBean.nohinsakijohon11']");
        // 得意先マスタ情報納品先情報21
        this.$nohinsakijohon21 = $("*[id='reservationBean.nohinsakijohon21']");
        // 得意先マスタ情報受注時間指定区分セレクトボックス
        this.$acptjikanshiteikbn1 = $("*[id='reservationBean.acptjikanshiteikbn1']");
        // 請求先マスタ登録年月日
        this.$inptsystmymd2 = $("*[id='reservationBean.inptsystmymd2']");
        // 請求先マスタ登録時間
        this.$inptsystmjikan2 = $("*[id='reservationBean.inptsystmjikan2']");
        // 請求先マスタ登録者AP基本コード
        this.$toshaapkihoncd2 = $("*[id='reservationBean.toshaapkihoncd2']");
        // 請求先マスタ登録者ショップコード
        this.$toshashopcd2 = $("*[id='reservationBean.toshashopcd2']");
        // 請求先マスタ登録者コード
        this.$toshacd2 = $("*[id='reservationBean.toshacd2']");
        // 請求先マスタ登録者名称
        this.$toshamei4 = $("*[id='reservationBean.toshamei4']");
        // 請求先マスタ更新年月日
        this.$kshnsystmymd2 = $("*[id='reservationBean.kshnsystmymd2']");
        // 請求先マスタ更新時間
        this.$kshnsystmjikan2 = $("*[id='reservationBean.kshnsystmjikan2']");
        // 請求先マスタ更新者AP基本コード
        this.$kshnapkihoncd = $("*[id='reservationBean.kshnapkihoncd']");
        // 請求先マスタ更新者ショップコード
        this.$koshashopcd2 = $("*[id='reservationBean.koshashopcd2']");
        // 請求先マスタ更新者コード
        this.$koshacd2 = $("*[id='reservationBean.koshacd2']");
        // 請求先マスタ更新者名称
        this.$koshamei2 = $("*[id='reservationBean.koshamei2']");
        // 請求先マスタ前回変更日
        this.$ymd2 = $("*[id='reservationBean.ymd2']");        
        // 請求先マスタ情報請求先敬称セレクトボックス
        this.$skysakikeisho = $("*[id='reservationBean.skysakikeisho']");
        // 請求先マスタ情報連絡先番号12
        this.$renrakusakinokbnn12 = $("*[id='reservationBean.renrakusakinokbnn12']");
        // 請求先マスタ情報連絡先番号22
        this.$renrakusakinokbnn22 = $("*[id='reservationBean.renrakusakinokbnn22']");
        // 請求先マスタ情報連絡先番号32
        this.$renrakusakinokbnn32 = $("*[id='reservationBean.renrakusakinokbnn32']");
        // 請求先マスタ情報連絡先番号42
        this.$renrakusakinokbnn42 = $("*[id='reservationBean.renrakusakinokbnn42']");
        // 請求先マスタ情報事業所コード1
        this.$jigyoshocd2 = $("*[id='reservationBean.jigyoshocd2']");
        // チェックボックス
        // 親子反映機能チェックボックス
        this.$oyakohaneikinoflg = $("*[name='baseBean.oyakohaneikinoflg']");
        // 再入会チェックボックス
        this.$sainyukaifrg = $("*[name='baseBean.sainyukaifrg']");
        // 初回違約金チェックボックス
        this.$plnshokaiiyakukinflg = $("*[name='baseBean.plnshokaiiyakukinflg']");
        // 継続違約金チェックボックス
        this.$plnkshniyakukinflg = $("*[name='baseBean.plnkshniyakukinflg']");
        // はじめてセットチェックボックス
        this.$startup = $("*[name='baseBean.startup']");
        // 定例売上設定チェックボックス
        this.$teireisetteikbn = $("*[name='rentalBean.teireisetteikbn']");
        // 得意先マスタ変更チェックボックス
        this.$tokuisakimasutahnkflg = $("*[name='reservationBean.tokuisakimasutahnkflg']");
        // 請求先マスタ変更チェックボックス
        this.$seikyusakimsthnkflg = $("*[name='reservationBean.seikyusakimsthnkflg']");
        // ラジオボタン
        // メルマガ区分ラジオボタン
        this.$merumagakbn = $("*[name='baseBean.merumagakbn']");
        // 特別単価区分ラジオボタン
        this.$rknkindtokubetsuuntkbn = $("*[name='baseBean.rknkindtokubetsuuntkbn']");
        // 申込書受付区分ラジオボタン
        this.$mokoshorcptkbn = $("*[name='baseBean.mokoshorcptkbn']");
        // 請求設定区分ラジオボタン
        this.$skysetteikbn = $("*[name='baseBean.skysetteikbn']");
        // 請求設定区分ラジオボタン変更前
        this.$skysetteikbnbefore = $("*[name='baseBean.skysetteikbnbefore']");
        // 会員コードラジオボタン
        this.$kaiincdkbn = $("*[name='baseBean.kaiincdkbn']");
        // 売上計上担当者区分ラジオボタン
        this.$urikeijotnshakbn = $("*[name='deliveryOrderBean.urikeijotnshakbn']");
        // 他社切替ラジオボタン
        this.$tashakirikae = $("*[name='deliveryOrderBean.tashakirikae']");
        // 配達区分ラジオボタン
        this.$dlvrykbn = $("*[name='deliveryOrderBean.dlvrykbn']");
        // 留守置き可能フラグラジオボタン
        this.$fzaiokikanoflg = $("*[name='deliveryOrderBean.fzaiokikanoflg']");
        // 納品金額印字ラジオボタン
        this.$kinotptnashiflg = $("*[name='deliveryOrderBean.kinotptnashiflg']");
        // まとめ買い区分ラジオボタン
        this.$mtmkisvkbn = $("*[name='saleBean.mtmkisvkbn']");
        // 保証金区分ラジオボタン
        this.$hoshokinkbn = $("*[name='saleBean.hoshokinkbn']");
        // 売上設定区分ラジオボタン
        this.$urisettei = $("*[name='rentalBean.urisettei']");
        // 発生間隔計上ラジオボタン
        this.$hasseikankakukeijoptn = $("*[name='rentalBean.hasseikankakukeijoptn']");
        // 売上発生ラジオボタン
        this.$urihasseiarinashiflg = $("*[name='rentalBean.urihasseiarinashiflg']");
        // 有効区分1ラジオボタン
        this.$yukokbn1 = $("*[name='rentalBean.yukokbn1']");
        // 得意先マスタ情報配達区分1ラジオボタン
        this.$dlvrykbn1 = $("*[name='reservationBean.dlvrykbn1']");
        // ポイント管理有無ラジオボタン
        this.$pointkbn = $("*[name='saleBean.pointkbn']");
        // まとめ買いポイント区分ラジオボタン
        this.$mtmkipointkbn = $("*[name='saleBean.mtmkipointkbn']");
        // 入会特典区分ラジオボタン
        this.$nyukaitokutenumu = $("*[name='saleBean.nyukaitokutenumu']");

        // 現在日
        this.$genzaidd = $("*[id='baseBean.genzaidd']");
        // 価格情報
        this.$priceinfo = $("*[id='rentalBean.priceinfo']");
        // 消費税計算基準日
        this.$taxCalcDate = $("*[id='taxCalcDate']");
        // 商品税区分
        this.$shntaxkbn = $("*[id='rentalBean.shntaxkbn']");
        // オブジェクト
        // 住所反映用定数
        this.adrsnObj = {yubinno:this.$yubinno,
                                adrsn1:this.$adrsn1,
                                adrsn2:this.$adrsn2,
                                adrsn3:this.$adrsn3,
                                chihoupubdanticdn1:this.$chihoupubdanticdn1,
                                chihoupubdanticdn2:this.$chihoupubdanticdn2,
                                name:this.$yubinno.attr('id'),
                                label:'郵便番号'};
        // 勤務先住所反映用定数
        this.kinmusakiAdrsnObj = {yubinno:this.$kinmusakiyubinno,
                                adrsn1:this.$kinmusakiadrsn1,
                                adrsn2:this.$kinmusakiadrsn2,
                                adrsn3:this.$kinmusakiadrsn3,
                                chihoupubdanticdn1:this.$kinmusakichihoupubdanticdn1,
                                chihoupubdanticdn2:this.$kinmusakichihoupubdanticdn2,
                                name:this.$kinmusakiyubinno.attr('id'),
                                label:'勤務先郵便番号'};
        // 変更得意先住所反映用定数
        this.tokuisakiAdrsnObj = {yubinno:this.$yubinno1,
                                adrsn1:this.$adrsn11,
                                adrsn2:this.$adrsn21,
                                adrsn3:this.$adrsn31,
                                chihoupubdanticdn1:this.$tokuisakichihoupubdanticdn1,
                                chihoupubdanticdn2:this.$tokuisakichihoupubdanticdn2,
                                name:this.$yubinno1.attr('id'),
                                label:'変更得意先郵便番号'};
        // 変更請求先住所反映用定数
        this.skysakiAdrsnObj = {yubinno:this.$yubinno2,
                                adrsn1:this.$adrsn12,
                                adrsn2:this.$adrsn22,
                                adrsn3:this.$adrsn32,
                                name:this.$yubinno2.attr('id'),
                                label:'変更請求先郵便番号'};
        // 自動値引１クリア用定数
        this.jidonebiki1Obj = {cd:this.$jidonebikicd1,
                                nm:this.$jidonebikinm1,
                                shncd:this.$shncd1,
                                shnnm:this.$shnnmn1,
                                hns:this.$gekkanhanbaihns1,
                                kind:this.$jidonebikikind1,
                                nebikihns:this.$nebikihns1,
                                nebikikin:this.$nebikikin1};
        // 自動値引２クリア用定数
        this.jidonebiki2Obj = {cd:this.$jidonebikicd2,
                                nm:this.$jidonebikinm2,
                                shncd:this.$shncd2,
                                shnnm:this.$shnnmn2,
                                hns:this.$gekkanhanbaihns2,
                                kind:this.$jidonebikikind2,
                                nebikihns:this.$nebikihns2,
                                nebikikin:this.$nebikikin2};
        // 自動値引３クリア用定数
        this.jidonebiki3Obj = {cd:this.$jidonebikicd3,
                                nm:this.$jidonebikinm3,
                                shncd:this.$shncd3,
                                shnnm:this.$shnnmn3,
                                hns:this.$gekkanhanbaihns3,
                                kind:this.$jidonebikikind3,
                                nebikihns:this.$nebikihns3,
                                nebikikin:this.$nebikikin3};
        this.shnCdNm1= {cd:this.$mgnyoshncdn1,
                        nm:this.$shnnm1};
        this.shnCdNm2= {cd:this.$mgnyoshncdn2,
                        nm:this.$shnnm2};
        this.shnCdNm3= {cd:this.$mgnyoshncdn3,
                        nm:this.$shnnm3};
        this.shnCdNm4= {cd:this.$mgnyoshncdn4,
                        nm:this.$shnnm4};
        this.mgnCdNm1= {cd:this.$mgncdn1,
                        nm:this.$mgnnm1};
        this.mgnCdNm2= {cd:this.$mgncdn2,
                        nm:this.$mgnnm2};
        this.mgnCdNm3= {cd:this.$mgncdn3,
                        nm:this.$mgnnm3};
        this.mgnCdNm4= {cd:this.$mgncdn4,
                        nm:this.$mgnnm4};
        // 定例売上マスタリンク制御用定数
        this.teireiurimasutaObj = {link:this.$teireiurimasutalink,
                                        linklbl:this.$teireiurimasutalinklbl};
        // 得意先設置サーバーマスタリンク制御用定数
        this.ttstupwsvmasutaObj = {link:this.$ttstupwsvmasutalink,
                                        linklbl:this.$ttstupwsvmasutalinklbl};
        // 反映元得意先リンク制御用定数
        this.haneimototokuisakiObj = {link:this.$haneimootokuisakilink,
                                        linklbl:this.$haneimootokuisakilbl,
                                        cd:this.$haneimototokuisakicd,
                                        nm:this.$haneimototokuisakinm};
        // 請求先マスタ修正・照会リンク制御用定数
        this.skysakimasutashuseishokaiObj = {link:this.$skysakimasutashuseishokailink,
                                        linklbl:this.$skysakimasutashuseishokailinklbl};
        // 得意先ショップリンク制御用定数
        this.tokuisakiShopObj = {link:this.$tokshoplink,
                                linklbl:this.$tokshoplinklbl,
                                cd:this.$tokuisakishopcd,
                                nm:this.$tokuisakishopnm};
        // サブレンタル倉庫リンク制御用定数
        this.sokoObj = {link:this.$sokocdlink,
                        linklbl:this.$sokolinklbl,
                        cd:this.$saburtlsokocd,
                        nm:this.$sokonmn1};
        // 配達会社リンク制御用定数
        this.dlvryCmpnyObj = {link:this.$cmpnycdlink,
                                linklbl:this.$cmpnylinklbl,
                                cd:this.$dlvrycmpnycd,
                                nm:this.$dlvrycmpnynm};
        // 配達担当者リンク制御用定数
        this.dlvryTnshaObj = {link:this.$dlvrytnshacdlink,
                                linklbl:this.$dlvrytnshalinklbl,
                                cd:this.$dlvrytnshacd,
                                nm:this.$dlvrytnshamei};
        // 得意先マスタ情報配達会社リンク制御用定数
        this.dlvryCmpny1Obj = {link:this.$cmpnycdlink1,
                                linklbl:this.$cmpnylinklbl1,
                                cd:this.$dlvrycmpnycd1,
                                nm:this.$dlvrycmpnynm1};
        // 得意先マスタ情報配達担当者リンク制御用定数
        this.dlvryTnsha1Obj = {link:this.$dlvrytnshacdlink1,
                                linklbl:this.$dlvrytnshalinklbl1,
                                cd:this.$dlvrytnshacd1,
                                nm:this.$dlvrytnshamei1};
        // 請求先リンク制御用定数
        this.skysakicdObj = {link:this.$skysakicdlink,
                                linklbl:this.$skysakicdlbl,
                                cd:this.$skysakicd,
                                nm:this.$skysakinmn1};
        // 会員リンク制御用定数
        this.kaiinObj= {link:this.$kaiincdlink,
                        linklbl:this.$kaiincdlbl,
                        cd:this.$kaiincd};
        // 会員マスタ照会リンク制御用定数
        this.kaiinMstObj = {link:this.$kainmstsyokailink,
                                        linklbl:this.$kainmstsyokailbl};
        // 違約金情報リンク制御用定数
        this.iyakukinjohoObj = {link:this.$iyakukinjoholink,
                                        linklbl:this.$iyakukinjoholinklbl};
        // 定期受注マスタリンク制御用定数
        this.teikiJuchuObj = {link:this.$teikiJuchulink,
                                        linklbl:this.$teikiJuchulinklbl};
        // 紹介者情報リンク制御用定数
        this.shoshakazuObj = {link:this.$shoshakazulink,
                                        linklbl:this.$shoshakazulinklbl};
        // 受注時間指定区分定数
        this.acptjikanshiteikbnObj= {select:this.$acptjikanshiteikbn,
                                        str:this.$acptjikanshiteijikoku,
                                        shry:this.$acptjikanshiteijikokushry};
        // 得意先マスタ受注時間指定区分定数
        this.acptjikanshiteikbn1Obj= {select:this.$acptjikanshiteikbn1,
                                        str:this.$acptjikanshiteijikokustr,
                                        shry:this.$acptjikanshiteijikokushry1};
        // プラン変更時用定数
        this.plnObj= {shokai:this.$rknplnsetteidd,
                        shokaiCheck:this.$plnshokaiiyakukinflg,
                        keizoku:this.$rknplnkeizokukshndd,
                        keizokuCheck:this.$plnkshniyakukinflg};
        // プラン初回設定日変更時用定数
        this.plnsetteiObj= {date:this.$rknplnsetteidd,
                      datebefore:this.$rknplnsetteiddbefore,
                        iyakukinCheck:this.$plnshokaiiyakukinflg};
        // プラン継続更新日変更時用定数
        this.plnkeizokuObj= {date:this.$rknplnkeizokukshndd,
                             datebefore:this.$rknplnkeizokukshnddbefore,
                             iyakukinCheck:this.$plnkshniyakukinflg};
        // 保証金区分変更時用定数
        this.hoshokinObj= {azushono:this.$azushono,
                            hoshokinazukaridd:this.$hoshokinazukaridd,
                            hoshokinazukarikin:this.$hoshokinazukarikin,
                            hoshokinhenkyakudd:this.$hoshokinhenkyakudd,
                            hoshokinhenkyakukin:this.$hoshokinhenkyakukin};
        // ポイント管理有無変更時用定数
        this.pointkbnObj= {pointkbn:this.$pointkbn,
                            pointkbn1:this.$pointkbn1,
                            pointkbn2:this.$pointkbn2};
        // まとめ買いポイント区分変更時用定数
        this.mtmkipointObj= {mtmkisuryo:this.$mtmkisuryo,
                            mtmkipointsu:this.$mtmkipointsu};
        // 入会特典区分変更時用定数
        this.nyukaitokutenObj= {nyukaitokutenpointsu:this.$nyukaitokutenpointsu};
        // 単価消費税計算用定数
        this.untTaxCalcObj = {suryo:this.$suryo,
                            unt:this.$unt,
                            smkin:this.$smkin,
                            shohitax:this.$shohitax,
                            keijodd:this.$keijodd,
                            dydm:this.$jikaikeijodydm}
        // 初回単価消費税計算用定数
        this.untShokaiTaxCalcObj = {suryo:this.$suryo,
                            unt:this.$shokaiunt,
                            smkin:this.$smkin1,
                            shohitax:this.$shohitax1,
                            keijodd:this.$keijodd,
                            dydm:this.$strtdydm}
        // 定例売上設定制御定数
        this.teireisetteiObj= {checkbox:this.$teireisetteikbn,
                            keijodd:this.$keijodd,
                            strtdydm:this.$strtdydm,
                            jikaikeijodydm:this.$jikaikeijodydm,
                            shrydydm:this.$shrydydm,
                            jissekitnshacd:this.$jissekitnshacd,
                            shnmei:this.$shnmei,
                            suryo:this.$suryo,
                            hyojunprice:this.$hyojunprice,
                            unt:this.$unt,
                            shokaiunt:this.$shokaiunt,
                            meisaibiko:this.$meisaibiko,
                            urisettei:this.$urisettei,
                            hasseikankakukeijoptn:this.$hasseikankakukeijoptn,
                            yukokbn1:this.$yukokbn1,
                            jissekitnshacdlink:this.$jissekitnshacdlink,
                            jissekitnshacdlbl:this.$jissekitnshacdlbl,
                            urihasseiarinashiflg:this.$urihasseiarinashiflg};
        // 得意先マスタ設定制御定数
        this.tokuisakiObj= {checkbox:this.$tokuisakimasutahnkflg,
                            ttmasutachgdd:this.$ttmasutachgdd,
                            tokuisakikeishokbn1:this.$tokuisakikeishokbn1,
                            tokuisakinmn11:this.$tokuisakinmn11,
                            tokuisakinmkanan11:this.$tokuisakinmkanan11,
                            tokuisakinmn21:this.$tokuisakinmn21,
                            tokuisakinmkanan21:this.$tokuisakinmkanan21,
                            yubinno1:this.$yubinno1,
                            btnyubinno3:this.$btnyubinno3,
                            renrakusakinokbnn11:this.$renrakusakinokbnn11,
                            renrakusakinokbnn21:this.$renrakusakinokbnn21,
                            renrakusakinokbnn31:this.$renrakusakinokbnn31,
                            renrakusakinokbnn41:this.$renrakusakinokbnn41,
                            renrakusakinon11:this.$renrakusakinon11,
                            renrakusakinon21:this.$renrakusakinon21,
                            renrakusakinon31:this.$renrakusakinon31,
                            renrakusakinon41:this.$renrakusakinon41,
                            renrakusakimemon11:this.$renrakusakimemon11,
                            renrakusakimemon21:this.$renrakusakimemon21,
                            renrakusakimemon31:this.$renrakusakimemon31,
                            renrakusakimemon41:this.$renrakusakimemon41,
                            adrsn11:this.$adrsn11,
                            adrsn21:this.$adrsn21,
                            adrsn31:this.$adrsn31,
                            jigyoshocd1:this.$jigyoshocd1,
                            eigyoareacd1link:this.$eigyoareacd1link,
                            eigyoareacd1lbl:this.$eigyoareacd1lbl,
                            eigyoareacd1:this.$eigyoareacd1,
                            eigyotnshacd1link:this.$eigyotnshacd1link,
                            eigyotnshacd1lbl:this.$eigyotnshacd1lbl,
                            eigyotnshacd1:this.$eigyotnshacd1,
                            dlvrykyoten1link:this.$dlvrykyoten1link,
                            dlvrykyoten1lbl:this.$dlvrykyoten1lbl,
                            dlvrykankatsuapkihoncd1:this.$dlvrykankatsuapkihoncd1,
                            dlvrykankatsushopcd1:this.$dlvrykankatsushopcd1,
                            dlvrykbn1:this.$dlvrykbn1,
                            cmpnycdlink1:this.$cmpnycdlink1,
                            cmpnylinklbl1:this.$cmpnylinklbl1,
                            dlvrycmpnycd1:this.$dlvrycmpnycd1,
                            dlvrykankatsukyotencd1:this.$dlvrykankatsukyotencd1,
                            dlvryareacd1link:this.$dlvryareacd1link,
                            dlvryareacd1lbl:this.$dlvryareacd1lbl,
                            dlvryareacd1:this.$dlvryareacd1,
                            dlvrytnshacdlink1:this.$dlvrytnshacdlink1,
                            dlvrytnshalinklbl1:this.$dlvrytnshalinklbl1,
                            dlvrytnshacd1:this.$dlvrytnshacd1,
                            prntmemon11:this.$prntmemon11,
                            prntmemon21:this.$prntmemon21,
                            prntmemon31:this.$prntmemon31,
                            chizuno1:this.$chizuno1,
                            dlvryjun1:this.$dlvryjun1,
                            nohinsakijohon11:this.$nohinsakijohon11,
                            nohinsakijohon21:this.$nohinsakijohon21,
                            acptjikanshiteikbn1:this.$acptjikanshiteikbn1,
                            acptjikanshiteijikokustr:this.$acptjikanshiteijikokustr,
                            acptjikanshiteijikokushry1:this.$acptjikanshiteijikokushry1};
        // 請求先マスタ設定制御定数
        this.skysakiObj= {checkbox:this.$seikyusakimsthnkflg,
                            skmhnkymd:this.$skmhnkymd,
                            skysakinmn11:this.$skysakinmn11,
                            skysakikeisho:this.$skysakikeisho,
                            skysakinmkana:this.$skysakinmkana,
                            skysakinmn2:this.$skysakinmn2,
                            skysakinmryakusho:this.$skysakinmryakusho,
                            yubinno2:this.$yubinno2,
                            btnyubinno4:this.$btnyubinno4,
                            renrakusakinokbnn12:this.$renrakusakinokbnn12,
                            renrakusakinokbnn22:this.$renrakusakinokbnn22,
                            renrakusakinokbnn32:this.$renrakusakinokbnn32,
                            renrakusakinokbnn42:this.$renrakusakinokbnn42,
                            renrakusakinon12:this.$renrakusakinon12,
                            renrakusakinon22:this.$renrakusakinon22,
                            renrakusakinon32:this.$renrakusakinon32,
                            renrakusakinon42:this.$renrakusakinon42,
                            adrsn12:this.$adrsn12,
                            adrsn22:this.$adrsn22,
                            adrsn32:this.$adrsn32,
                            jigyoshocd2:this.$jigyoshocd2,
                            eigyotnshacd2link:this.$eigyotnshacd2link,
                            eigyotnshacd2lbl:this.$eigyotnshacd2lbl,
                            eigyotnshacd2:this.$eigyotnshacd2,
                            kaishutnshacdlink:this.$kaishutnshacdlink,
                            kaishutnshacdlbl:this.$kaishutnshacdlbl,
                            kaishutnshacd:this.$kaishutnshacd};
        // 反映元得意先ロストフォーカス用定数
        this.dataHaneimotoTokuisaki = {apkihoncd:this.$apkihoncd,shopcd:this.$shopcd,cd:this.$haneimototokuisakicd,nm:this.$haneimototokuisakinm,len:6};
        // 紹介元得意先ロストフォーカス用定数
        this.dataShoukaimotoTokuisaki = {apkihoncd:this.$apkihoncd,shopcd:this.$shopcd,cd:this.$shoukaitokuisakicd,nm:this.$shoukaitokuisakimei,len:6};
        // 委託相手先得意先ロストフォーカス用定数
        this.dataItakuaiteTokuisaki = {apkihoncd:this.$itakuaiteapkihoncd,shopcd:this.$itakuaiteshopcd,cd:this.$itakuaitetokuisakicd,nm:this.$itakuaitetokuisakinm,len:6};
        // サブレンタル倉庫ロストフォーカス用定数
        this.dataSaburtlSoko = {apkihoncd:this.$apkihoncd,shopcd:this.$shopcd,cd:this.$saburtlsokocd,nm:this.$sokonmn1,len:6};
        // 請求先コードロストフォーカス用定数
        this.dataSkysaki = {apkihoncd:this.$apkihoncd,shopcd:this.$shopcd,cd:this.$skysakicd,oyacd:this.$oyatokuisakicd,nm:this.$skysakinmn1,len:6};
        // 得意先ショップコードロストフォーカス用定数
        this.dataShop = {apkihoncd:this.$apkihoncd,cd:this.$tokuisakishopcd,nm:this.$tokuisakishopnm,len:3};
        // 設置日登録者コードロストフォーカス用定数
        this.dataStupdd = {apkihoncd:this.$apkihoncd,shopcd:this.$shopcd,cd:this.$stupddtoshacd,nm:this.$toshamei1,len:6};
        // 引上登録者コードロストフォーカス用定数
        this.dataHikiage = {apkihoncd:this.$apkihoncd,shopcd:this.$shopcd,cd:this.$hikiagetoshacd,nm:this.$toshamei3,len:6};
        // 営業担当者コードロストフォーカス用定数
        this.dataEigyo = {apkihoncd:this.$apkihoncd,shopcd:this.$shopcd,cd:this.$eigyotnshacd,nm:this.$eigyotnshanm,len:6};
        // 得意先マスタ情報営業担当者コードロストフォーカス用定数
        this.dataTokuisakiEigyo = {apkihoncd:this.$apkihoncd,shopcd:this.$shopcd,cd:this.$eigyotnshacd1,nm:this.$eigyotnshanm1,len:6};
        // 請求先マスタ情報営業担当者コードロストフォーカス用定数
        this.dataSkysakiEigyo = {apkihoncd:this.$apkihoncd,shopcd:this.$shopcd,cd:this.$eigyotnshacd2,nm:this.$eigyotnshanm2,len:6};
        // 獲得担当者コードロストフォーカス用定数
        this.dataKakutoku = {apkihoncd:this.$apkihoncd,shopcd:this.$shopcd,cd:this.$kakutokutnshacd,nm:this.$tnshamei3,len:6};
        // 配達担当者コードロストフォーカス用定数
        this.dataDlvry = {apkihoncd:this.$apkihoncd,shopcd:this.$shopcd,cd:this.$dlvrytnshacd,nm:this.$dlvrytnshamei,len:6};
        // 得意先マスタ配達担当者コードロストフォーカス用定数
        this.dataTokuisakiDlvry = {apkihoncd:this.$apkihoncd,shopcd:this.$shopcd,cd:this.$dlvrytnshacd1,nm:this.$dlvrytnshamei1,len:6};
        // 実績担当者コードロストフォーカス用定数
        this.dataJisseki = {apkihoncd:this.$apkihoncd,shopcd:this.$shopcd,cd:this.$jissekitnshacd,nm:this.$tnshamei,len:6};
        // 回収担当者コードロストフォーカス用定数
        this.dataKaishu = {apkihoncd:this.$apkihoncd,shopcd:this.$shopcd,cd:this.$kaishutnshacd,nm:this.$kaishutnshamei,len:6};
        // 営業エリアコードロストフォーカス用定数
        this.dataEigyoArea = {apkihoncd:this.$apkihoncd,shopcd:this.$shopcd,cd:this.$eigyoareacd,nm:this.$eigyoareanm,len:5};
        // 得意先マスタ情報営業エリアコードロストフォーカス用定数
        this.dataTokuisakiEigyoArea = {apkihoncd:this.$apkihoncd,shopcd:this.$shopcd,cd:this.$eigyoareacd1,nm:this.$eigyoareanm1,len:5};
        // 配達管轄拠点コードロストフォーカス用定数
        this.dataDlvrykankatsuKyotencd = {apkihoncd:this.$dlvrykankatsuapkihoncd,shopcd:this.$dlvrykankatsushopcd,cd:this.$dlvrykankatsukyotencd,
                                            apnm:this.$apnm2,shopnm:this.$shopnm2,nm:this.$kyotennm,areacd:this.$dlvryareacd,areanm:this.$dlvryareanm,aplen:3,shoplen:3,len:2};
        // 得意先マスタ情報配達管轄拠点コードロストフォーカス用定数
        this.dataTokuisakiDlvrykankatsuKyotencd = {apkihoncd:this.$dlvrykankatsuapkihoncd1,shopcd:this.$dlvrykankatsushopcd1,cd:this.$dlvrykankatsukyotencd1,
                                            apnm:this.$apnm1,shopnm:this.$shopnm1,nm:this.$kyotennm1,areacd:this.$dlvryareacd1,areanm:this.$dlvryareanm1,aplen:3,shoplen:3,len:2};
        // 本部エージェントコードロストフォーカス用定数
        this.dataHonbuEjnt = {apkihoncd:'001',shopcd:'000',cd:this.$honbuejntcd,nm:this.$ejntmein1,len:6,honbuflg:'t'};
        // 獲得エージェントコードロストフォーカス用定数
        this.dataKakutokuEjnt = {apkihoncd:this.$apkihoncd,shopcd:this.$shopcd,cd:this.$kakutokuejntcd,nm:this.$ejntmein2,len:6,honbuflg:'f'};
        // 獲得本部キャンペーンコードロストフォーカス用定数
        this.dataKakutokuHonbuKynpn = {apkihoncd:'001',shopcd:'000',cd:this.$kakutokuhonbukynpncd,nm:this.$kakutokuhonbukynpnnm,kynpnkeihinkbn:this.$kakutokuhonbukynpnkeihinkbn,td:'td_kakutokuhonbukynpncd',inptkbn:'0',len:6};
        // 獲得キャンペーンコードロストフォーカス用定数
        this.dataKakutokuKynpn = {apkihoncd:this.$apkihoncd,shopcd:this.$shopcd,cd:this.$kakutokukynpncd,nm:this.$kakutokukynpnnm,kynpnkeihinkbn:this.$kakutokukeihin,td:'td_kakutokukynpncd',inptkbn:'1',len:6};
        // 配達会社コードロストフォーカス用定数
        this.dataDlvrycmpny = {apkihoncd:this.$apkihoncd,shopcd:this.$shopcd,cd:this.$dlvrycmpnycd,nm:this.$dlvrycmpnynm,len:3};
        // 得意先マスタ情報配達会社コードロストフォーカス用定数
        this.dataTokuisakiDlvrycmpny = {apkihoncd:this.$apkihoncd,shopcd:this.$shopcd,cd:this.$dlvrycmpnycd1,nm:this.$dlvrycmpnynm1,len:3};
        // 獲得詳細コードロストフォーカス用定数
        this.dataHansokushs = {apkihoncd:this.$apkihoncd,shopcd:this.$shopcd,cd:this.$hansokushscd,nm:this.$hansokunm,len:4};
        // ワンショットコードロストフォーカス用定数
        this.dataWanshottocd = {apkihoncd:this.$apkihoncd,shopcd:this.$shopcd,cd:this.$wanshottocd,nm:this.$mgnnm};
        // 配達エリアコードロストフォーカス用定数
        this.dataDlvryArea = {apkihoncd:this.$dlvrykankatsuapkihoncd,shopcd:this.$dlvrykankatsushopcd,cd:this.$dlvryareacd,dlvrykankatsukyotencd:this.$dlvrykankatsukyotencd,nm:this.$dlvryareanm,len:5};
        // 得意先マスタ情報配達エリアコードロストフォーカス用定数
        this.dataTokuisakiDlvryArea = {apkihoncd:this.$dlvrykankatsuapkihoncd1,shopcd:this.$dlvrykankatsushopcd1,cd:this.$dlvryareacd1,dlvrykankatsukyotencd:this.$dlvrykankatsukyotencd1,nm:this.$dlvryareanm1,len:5};
        // 参照画面表示用オブジェクト
        // 定例売上マスタリンク定数
        this.dataTeireiuriMasuta = {apkihoncd:this.$apkihoncd,shopcd:this.$shopcd,cd:this.$ptokuisakicd};
        // 得意先設置サーバーマスタリンク定数
        this.dataTtstupwsvMasuta = {apkihoncd:this.$apkihoncd,shopcd:this.$shopcd,cd:this.$ptokuisakicd};
        // 請求先マスタリンク定数
        this.dataSkysakiMasuta = {apkihoncd:this.$apkihoncd,shopcd:this.$shopcd,cd:this.$skysakicd,cdkensakuflg:'0'};
        // 会員マスタリンク定数
        this.dataKaiinMasuta = {cd:this.$kaiincd};
        // 違約金情報リンク定数
        this.dataIyakukin = {apkihoncd:this.$apkihoncd,shopcd:this.$shopcd,cd:this.$ptokuisakicd};
        // 紹介者情報リンク定数
        this.dataShokai = {apkihoncd:this.$apkihoncd,shopcd:this.$shopcd,cd:this.$ptokuisakicd};
        // 定期受注マスタリンク定数
        this.dataTeikiacpt = {apkihoncd:this.$apkihoncd,shopcd:this.$shopcd,cd:this.$ptokuisakicd};
    }

    /**
     * 更新ボタンクリック時に実行
     */
    #btnkshnClick = () => {
        this.#afterUpdateAction = AFTER_UPDATE_ACTION.NONE;
    }

    /**
     * 更新後に受注登録へボタンクリック時に実行
     */
    #btnacptinptClick = () => {
        this.#afterUpdateAction = AFTER_UPDATE_ACTION.JUCHU;
    }

    /**
     * 更新後に実行
     */
    onUpdated = (_e,data)=> {
        // フォーム情報を再設定する
        this.setFormDataAfterUpdate(data.data);
        
        // 活性制御の再設定のためモジュールロードイベントを発生させる
        $ACI_EVENT_ELE.trigger(EVENTS.MODULE_LOAD_COMPLETE);
        
        // 更新後共通処理
        this.updatedCommon();

        // 押下ボタンによる遷移先変更
        switch (this.#afterUpdateAction) {
        // 受注登録へ遷移
        case AFTER_UPDATE_ACTION.JUCHU:
            // 別機能を開いて、自身のウィンドウは閉じる
            openProcessSubWindowAndClose(`c0801w01/${this.$apkihoncd.val()}/${this.$shopcd.val()}/${this.$ptokuisakicd.val()}/b0501w02`, $.param({ "autoKakutei": "true" }), false);
            break;
        
        // 遷移なし
        case AFTER_UPDATE_ACTION.NONE:
        default:
            break;
        }
    }
    
    /**
     * 更新失敗後に実行
     */
    onUpdateFailed = (_e, _jqXHR)=> {
        let messageJson;
        try{
            messageJson = $.parseJSON(_jqXHR.responseText);
        }catch(e){
            console.error(e);
        }
        // エラー項目を取得
        const messageTargetItems = messageJson.fields;
        
        if (messageTargetItems) {
            const target = messageTargetItems[0];
            for (let i = 1; i < 7; i++) {
                // エラー項目が存在するタブを判定する
                const $searchResult = $("#tabcontents" + i).find("[name='" + target.name + "']").attr('name');
                // 該当のタブを選択する
                if ($searchResult !== undefined) {
                    $("#tab" + i).click();
                    return;
                }
            }
        }
    }
    
    /**
     * 会員情報を印刷後に実行
     */
    onPrinted = (_e, _data)=> {
        // フォーム情報取得
        const api = "/api/b0501w02/form";
        
        // apiコール
        $.get(api)
        .then((data) => {
            if (data) {
                // フォーム情報を再設定する
                this.setFormDataAfterUpdate(data);
                
                // 活性制御の再設定のためモジュールロードイベントを発生させる
                $ACI_EVENT_ELE.trigger(EVENTS.MODULE_LOAD_COMPLETE);
            }
        })
        
        // 更新後共通処理
        this.updatedCommon();
    }

    /**
     * 更新後共通処理
     */
    updatedCommon = () => {
        // 更新ボタンと更新後に会員情報を印刷ボタン、更新後に受注登録へボタンを非表示
        this.$btnkshn.hide();
        this.$btnprnt.hide();
        this.$btnacptinpt.hide();
        
        // 戻るボタンの属性を変更
        // ※検索画面で1ページ目を表示するための設定
        let attrClick = this.$btnmodoru.attr('onclick');
        this.$btnmodoru.attr('onclick', attrClick.replace('acir=1','acir=2'));
        
        // 基本情報タブを選択
        $('#tab1').click();
    }
        
    /**
     * 更新後にフォーム情報を再設定する。
     * @param data APIで取得した得意先情報
     */
    setFormDataAfterUpdate = (data) => {
        
        const $base = data.base_bean;
        const $deli = data.delivery_order_bean;
        const $sale = data.sale_bean;
        const $rent = data.rental_bean;
        const $resv = data.reservation_bean;

        // 基本情報
        this.$kshnFlag.val('t'); // 更新フラグ
        this.$genzaidd.val($base.genzaidd); // 現在日
        this.$tokuisakicd.text($base.tokuisakicd1); // 得意先コード
        this.$ptokuisakicd.val($base.ptokuisakicd); // hidden得意先コード
        this.$tokuisakinm.text($base.tokuisakinm); // 得意先名称
        this.$tokuisakijotainm.text($base.tokuisakijotainm); // 得意先状態
        this.$tokuisakijotai.val($base.tokuisakijotai); // hidden得意先状態
        this.$jotaistsnm.text($base.jotaistsnm); // 状態ステータス
        this.$jotaists.val($base.jotaists); // hidden状態ステータス
        this.$skysakicd.val($base.skysakicd); // 請求先コード
        this.$skysakinmn1.text($base.skysakinmn1); // 請求先名称
        // 請求設定区分
        if ($base.skysetteikbn == '0') {
            $("*[id='baseBean.skysetteikbn1']").prop('checked', true);
        } else if ($base.skysetteikbn == '1') {
            $("*[id='baseBean.skysetteikbn2']").prop('checked', true);
        } else if ($base.skysetteikbn == '2') {
            $("*[id='baseBean.skysetteikbn3']").prop('checked', true);
        }        
        this.$kaiincd.val($base.kaiincd); // 会員コード
        // 会員コード区分
        if ($base.kaiincdkbn == '0') {
            $("*[id='baseBean.kaiincdkbn1']").prop('checked', true);
        } else if ($base.kaiincdkbn == '1') {
            $("*[id='baseBean.kaiincdkbn2']").prop('checked', true);
        } else if ($base.kaiincdkbn == '2') {
            $("*[id='baseBean.kaiincdkbn3']").prop('checked', true);
        }
        this.$plnshokaiiyakukinflg.prop("checked", false); // 初回違約金チェックボックス
        this.$plnkshniyakukinflg.prop("checked", false); // 継続違約金チェックボックス
        this.$oyakohaneikinoflg.prop("checked", false); // 親子反映機能チェックボックス
        this.$iyakukinkin.text(separateWithComma($base.iyakukinkin)); // 得意先違約金金額
        this.$moshikomitoshacd.text($base.moshikomitoshacd); // 申込登録者コード
        this.$toshamei5.text($base.toshamei5); // 申込登録者名
        this.$nyukaiddtoshacd.text($base.nyukaiddtoshacd); // 入会登録者コード
        this.$toshamei6.text($base.toshamei6); // 入会登録者名
        this.$taikaiddtoshacd.text($base.taikaiddtoshacd); // 退会登録者コード
        this.$toshamei7.text($base.toshamei7); // 退会登録者名
        this.$wsvrtlrkn.text($base.wsvrtlrkn); // サーバーレンタル料
        this.$inptsystmymd.text($base.inptsystmymd); // 登録年月日
        this.$inptsystmjikan.text($base.inptsystmjikan); // 登録時間
        this.$toshaapkihoncd.text($base.toshaapkihoncd); // 登録者AP基本コード
        this.$toshashopcd.text($base.toshashopcd); // 登録者ショップコード
        this.$toshacd.text($base.toshacd); // 登録者コード
        this.$toshamei.text($base.toshamei); // 登録者名称
        this.$hyphen1.text('-');
        this.$hyphen2.text('-');
        this.$kshnsystmymd.text($base.kshnsystmymd); // 更新年月日
        this.$kshnsystmjikan.text($base.kshnsystmjikan); // 更新時間
        this.$koshaapkihoncd.text($base.koshaapkihoncd); // 更新者AP基本コード
        this.$koshashopcd.text($base.koshashopcd); // 更新者ショップコード
        this.$koshacd.text($base.koshacd); // 更新者コード
        this.$koshamei.text($base.koshamei); // 更新者名称
        this.$hyphen3.text('-');
        this.$hyphen4.text('-');
        
        // 受注情報
        this.$acptrcptkbnnm.text($deli.acptrcptkbnnm); // 受注受付区分
        this.$azukaribtlhnsn1.val($deli.azukaribtlhnsn1); // 預かりボトル本数１
        this.$azukaribtlhnsn2.val($deli.azukaribtlhnsn2); // 預かりボトル本数２
        
        // その他付帯情報
        this.$kyuminkbnnm.text($sale.kyuminkbnnm); // 休眠区分
        this.$genzaizd.text($sale.genzaizd); // 現在残高
        this.$keijomaeacptkin.text($sale.keijomaeacptkin); // 計上前受注金額
        
        // 定例売上情報
        this.$teireihasseino.val($rent.ttmasutachgdd); // 定例発生番号
        this.$hukusugyoyomsg.text($rent.hukusugyoyomsg); // 定例売上複数行用メッセージ
        this.$teireisetteikbn.prop("checked", false);　// 定例売上設定チェックボックス
        this.$znkhasseikeijodd.text($rent.znkhasseikeijodd); // 前回発生計上日
        this.$inptsystmymd1.text($rent.inptsystmymd1); // 定例売上登録年月日
        this.$inptsystmjikan1.text($rent.inptsystmjikan1); // 定例売上登録時間
        this.$toshaapkihoncd1.text($rent.toshaapkihoncd1); // 定例売上登録者AP基本コード
        this.$toshashopcd1.text($rent.toshashopcd1); // 定例売上登録者ショップコード
        this.$toshacd1.text($rent.toshacd1); // 定例売上登録者コード
        this.$toshamei2.text($rent.toshamei2); // 定例売上登録者名称
        if ($rent.inptsystmymd1 != null && $rent.inptsystmymd1 != '') {
            this.$hyphen5.text('-');
            this.$hyphen6.text('-');
        }
        this.$kshnsystmymd1.text($rent.kshnsystmymd1); // 定例売上更新年月日
        this.$kshnsystmjikan1.text($rent.kshnsystmjikan1); // 定例売上更新時間
        this.$koshaapkihoncd1.text($rent.koshaapkihoncd1); // 定例売上更新者AP基本コード
        this.$koshashopcd1.text($rent.koshashopcd1); // 定例売上更新者ショップコード
        this.$koshacd1.text($rent.koshacd1); // 定例売上更新者コード
        this.$koshamei1.text($rent.koshamei1); // 定例売上更新者名称
        if ($rent.kshnsystmymd1 != null && $rent.kshnsystmymd1 != '') {
            this.$hyphen7.text('-');
            this.$hyphen8.text('-');
        }
        
        // 得意先マスタ変更情報
        this.$tokuisakimasutahnkflg.prop("checked", false); // 得意先マスタ変更チェックボックス
        this.$ttmasutachgdd.val($resv.ttmasutachgdd); // 得意先マスタ情報変更日
        this.$tsminptsystmymd.text($resv.tsminptsystmymd); // 得意先マスタ登録年月日
        this.$tsminptsystmjikan.text($resv.tsminptsystmjikan); // 得意先マスタ登録時間
        this.$tsmtoshaapkihoncd.text($resv.tsmtoshaapkihoncd); // 得意先マスタ登録者AP基本コード
        this.$tsmtoshashopcd.text($resv.tsmtoshashopcd); // 得意先マスタ登録者ショップコード
        this.$tsmtoshacd.text($resv.tsmtoshacd); // 得意先マスタ登録者コード
        this.$tsmtoshamei.text($resv.tsmtoshamei); // 得意先マスタ登録者名称
        if ($resv.tsminptsystmymd != null && $resv.tsminptsystmymd != '') {
            this.$hyphen9.text('-');
            this.$hyphen10.text('-');
        } else {
            this.$hyphen9.text('');
            this.$hyphen10.text('');
        }
        this.$tsmkshnsystmymd.text($resv.tsmkshnsystmymd); // 得意先マスタ更新年月日
        this.$kshnsystmjikan3.text($resv.kshnsystmjikan3); // 得意先マスタ更新時間
        this.$koshaapkihoncd2.text($resv.koshaapkihoncd2); // 得意先マスタ更新者AP基本コード
        this.$koshashopcd3.text($resv.koshashopcd3); // 得意先マスタ更新者ショップコード
        this.$koshacd3.text($resv.koshacd3); // 得意先マスタ更新者コード
        this.$koshamei3.text($resv.koshamei3); // 得意先マスタ更新者名称
        if ($resv.tsmkshnsystmymd != null && $resv.tsmkshnsystmymd != '') {
            this.$hyphen11.text('-');
            this.$hyphen12.text('-');
        } else {
            this.$hyphen11.text('');
            this.$hyphen12.text('');
        }
        this.$ymd3.text($resv.ymd3); // 得意先マスタ前回変更日
        this.$tokuisakinmn11.val($resv.tokuisakinmn11); // 得意先名称11
        this.$tokuisakinmn21.val($resv.tokuisakinmn21); // 得意先名称21
        this.$tokuisakikeishokbn1.val($resv.tokuisakikeishokbn1); // 得意先敬称区分
        this.$tokuisakinmkanan11.val($resv.tokuisakinmkanan11); // 得意先名称カナ11
        this.$tokuisakinmkanan21.val($resv.tokuisakinmkanan21); // 得意先名称カナ21
        this.$yubinno1.val($resv.yubinno1); // 郵便番号1
        this.$renrakusakinokbnn11.val($resv.renrakusakinokbnn11); // 連絡先区分11
        this.$renrakusakinon11.val($resv.renrakusakinon11); // 連絡先番号11
        this.$renrakusakimemon11.val($resv.renrakusakimemon11); // 連絡先メモ11
        this.$renrakusakinokbnn21.val($resv.renrakusakinokbnn21); // 連絡先区分21
        this.$renrakusakinon21.val($resv.renrakusakinon21); // 連絡先番号21
        this.$renrakusakimemon21.val($resv.renrakusakimemon21); // 連絡先メモ21
        this.$renrakusakinokbnn31.val($resv.renrakusakinokbnn31); // 連絡先区分31
        this.$renrakusakinon31.val($resv.renrakusakinon31); // 連絡先番号31
        this.$renrakusakimemon31.val($resv.renrakusakimemon31); // 連絡先メモ31
        this.$renrakusakinokbnn41.val($resv.renrakusakinokbnn41); // 連絡先区分41
        this.$renrakusakinon41.val($resv.renrakusakinon41); // 連絡先番号41
        this.$renrakusakimemon41.val($resv.renrakusakimemon41); // 連絡先メモ41
        this.$jigyoshocd1.val($resv.jigyoshocd1); // 事業所コード1
        this.$adrsn11.val($resv.adrsn11); // 住所11
        this.$adrsn21.val($resv.adrsn21); // 住所21
        this.$adrsn31.val($resv.adrsn31); // 住所31
        this.$prntmemon11.val($resv.prntmemon11); // 印刷メモ11
        this.$prntmemon21.val($resv.prntmemon21); // 印刷メモ21
        this.$prntmemon31.val($resv.prntmemon31); // 印刷メモ31
        this.$chizuno1.val($resv.chizuno1); // 地図番号1
        this.$dlvryjun1.val($resv.dlvryjun1); // 配達順1
        this.$eigyoareacd1.val($resv.eigyoareacd1); // 営業エリアコード
        this.$eigyoareanm1.text($resv.eigyoareanm1); // 営業エリア名称
        this.$eigyotnshacd1.val($resv.eigyotnshacd1); // 営業担当者コード
        this.$eigyotnshanm1.text($resv.eigyotnshanm1); // 営業担当者名称
        this.$dlvrykankatsuapkihoncd1.val($resv.dlvrykankatsuapkihoncd1); // 配達管轄ＡＰ基本コード1
        this.$apnm1.text($resv.apnm1); // 配達管轄ＡＰ名称1
        this.$dlvrykankatsushopcd1.val($resv.dlvrykankatsushopcd1); // 配達管轄ショップコード１
        this.$shopnm1.text($resv.shopnm1); // 配達管轄ショップ名称１
        this.$dlvrykankatsukyotencd1.val($resv.dlvrykankatsukyotencd1); // 配達管轄拠点コード1
        this.$kyotennm1.text($resv.kyotennm1); // 配達管轄拠点名称１
        // 配達区分
        if ($resv.dlvrykbn1 == '1') {
            $("*[id='reservationBean.dlvrykbn11']").prop('checked', true);
        } else if ($resv.dlvrykbn1 == '2') {
            $("*[id='reservationBean.dlvrykbn12']").prop('checked', true);
        } else if ($resv.dlvrykbn1 == '3') {
            $("*[id='reservationBean.dlvrykbn13']").prop('checked', true);
        }
        this.$dlvrycmpnycd1.val($resv.dlvrycmpnycd1); // 配達会社コード
        this.$dlvrycmpnynm1.text($resv.dlvrycmpnynm1); // 配達会社名称
        this.$dlvryareacd1.val($resv.dlvryareacd1); // 配達エリアコード
        this.$dlvryareanm1.text($resv.dlvryareanm1); // 配達エリア名称
        this.$dlvrytnshacd1.val($resv.dlvrytnshacd1); // 配達担当者コード
        this.$dlvrytnshamei1.text($resv.dlvrytnshamei1); // 配達担当者名称
        this.$nohinsakijohon11.val($resv.nohinsakijohon11); // 納品先情報11
        this.$nohinsakijohon21.val($resv.nohinsakijohon21); // 納品先情報21
        this.$acptjikanshiteikbn1.val($resv.acptjikanshiteikbn1); // 受注時間指定区分
        this.$acptjikanshiteijikokustr.val($resv.acptjikanshiteijikokustr); // 受注時間指定時刻開始
        this.$acptjikanshiteijikokushry1.val($resv.acptjikanshiteijikokushry1); // 受注時間指定時刻終了
        
        // 請求先マスタ変更情報
        this.$seikyusakimsthnkflg.prop("checked", false); // 請求先マスタ変更チェックボックス
        this.$skmhnkymd.val($resv.skmhnkymd); // 請求先マスタ情報変更日
        this.$inptsystmymd2.text($resv.inptsystmymd2); // 請求先マスタ登録年月日
        this.$inptsystmjikan2.text($resv.inptsystmjikan2); // 請求先マスタ登録時間
        this.$toshaapkihoncd2.text($resv.toshaapkihoncd2); // 請求先マスタ登録者AP基本コード
        this.$toshashopcd2.text($resv.toshashopcd2); // 請求先マスタ登録者ショップコード
        this.$toshacd2.text($resv.toshacd2); // 請求先マスタ登録者コード
        this.$toshamei4.text($resv.toshamei4); // 請求先マスタ登録者名称
        if ($resv.inptsystmymd2 != null && $resv.inptsystmymd2 != '') {
            this.$hyphen13.text('-');
            this.$hyphen14.text('-');
        } else {
            this.$hyphen13.text('');
            this.$hyphen14.text('');
        }
        this.$kshnsystmymd2.text($resv.kshnsystmymd2); // 請求先マスタ更新年月日
        this.$kshnsystmjikan2.text($resv.kshnsystmjikan2); // 請求先マスタ更新時間
        this.$kshnapkihoncd.text($resv.kshnapkihoncd); // 請求先マスタ更新者AP基本コード
        this.$koshashopcd2.text($resv.koshashopcd2); // 請求先マスタ更新者ショップコード
        this.$koshacd2.text($resv.koshacd2); // 請求先マスタ更新者コード
        this.$koshamei2.text($resv.koshamei2); // 請求先マスタ更新者名称
        if ($resv.kshnsystmymd2 != null && $resv.kshnsystmymd2 != '') {
            this.$hyphen15.text('-');
            this.$hyphen16.text('-');
        } else {
            this.$hyphen15.text('');
            this.$hyphen16.text('');
        }
        this.$ymd2.text($resv.ymd2); // 請求先マスタ前回変更日
        this.$skysakinmn11.val($resv.skysakinmn11); // 請求先名称11
        this.$skysakinmkana.val($resv.skysakinmkana); // 請求先名称カナ
        this.$skysakinmn2.val($resv.skysakinmn2); // 請求先名称2
        this.$skysakikeisho.val($resv.skysakikeisho); // 請求先敬称         
        this.$skysakinmryakusho.val($resv.skysakinmryakusho); // 請求先名称略称
        this.$yubinno2.val($resv.yubinno2); // 郵便番号2
        this.$renrakusakinokbnn12.val($resv.renrakusakinokbnn12); // 連絡先区分12
        this.$renrakusakinon12.val($resv.renrakusakinon12); // 連絡先番号12
        this.$renrakusakinokbnn22.val($resv.renrakusakinokbnn22); // 連絡先区分22
        this.$renrakusakinon22.val($resv.renrakusakinon22); // 連絡先番号22
        this.$renrakusakinokbnn32.val($resv.renrakusakinokbnn32); // 連絡先区分32
        this.$renrakusakinon32.val($resv.renrakusakinon32); // 連絡先番号32
        this.$renrakusakinokbnn42.val($resv.renrakusakinokbnn42); // 連絡先区分42
        this.$renrakusakinon42.val($resv.renrakusakinon42); // 連絡先番号42
        this.$adrsn12.val($resv.adrsn12); // 住所12
        this.$adrsn22.val($resv.adrsn22); // 住所22
        this.$adrsn32.val($resv.adrsn32); // 住所32
        this.$eigyotnshacd2.val($resv.eigyotnshacd2); // 営業担当者コード
        this.$eigyotnshanm2.text($resv.eigyotnshanm2); // 営業担当者名称
        this.$kaishutnshacd.val($resv.kaishutnshacd); // 回収担当者コード
        this.$kaishutnshamei.text($resv.kaishutnshamei); // 回収担当者名称
        this.$jigyoshocd2.val($resv.jigyoshocd2); // 事業所コード1
    }
    
    /**
     * 表示を更新リンククリック時処理
     */
    #clickLinkiyakukinkshnBtn = (e)=> {

        e.stopPropagation();
        e.preventDefault();
        
        // 新規から遷移時は処理終了
        if (this.$kshnFlag.val() =='f') {
            return;
        }
        
        const apkihoncd = this.$apkihoncd.val();
        const shopcd = this.$shopcd.val();
        const tokuisakicd = this.$ptokuisakicd.val();
        
        // 違約金金額取得
        const api = "/api/b0501w02/iyakukin/"
            + apkihoncd + "/"
            + shopcd + "/"
            + tokuisakicd;
        // apiコール
        $.get(api)
        .then((data) => {
            if (data) {
                this.$iyakukinkin.text(separateWithComma(data));
            }
        })
    }
    
    /**
     * 得意先マスタ子画面呼び出し処理
     */
    #clickTokuisakicdBtn = (e) => {
        
        e.stopPropagation();
        e.preventDefault();
        
        // clickされた要素の取得
        const currentTarget = e.currentTarget;
        const id = currentTarget.id;
        // コードを定義
        let apkihoncd;
        let shopcd;
        const jigyoshokanri = this.$jigyoshokanri.val();
        // 適用する値設定用関数の選定
        let callbackFunction;
        // 渡すparamを定義
        let params;
        switch (id) {
            case "b0501w02-refhaneimototokuisakicd":
                callbackFunction = this.setHaneitokuisakicd;
                apkihoncd = this.$apkihoncd.val();
                shopcd = this.$shopcd.val();
                params = {
                    "kinoid":"b0501w02",
                    "apkihoncd":apkihoncd,
                    "shopcd":shopcd,
                    "jigyoshokanri":jigyoshokanri,
                    "cdkensakuflg":"1"
                };
                break;
            case "b0501w02-itakuaitesakicd":
                callbackFunction = this.setItakuaitesakicd;
                apkihoncd = this.$itakuaiteapkihoncd.val();
                shopcd = this.$itakuaiteshopcd.val();
                params = {
                    "kinoid":"b0501w02",
                    "apkihoncd":apkihoncd,
                    "shopcd":shopcd,
                    "jigyoshokanri":jigyoshokanri,
                    "cdkensakuflg":"1"
                };
                break;
            case "b0501w02-reftokuisakicd":
                callbackFunction = this.setShoukaitokuisakicd;
                apkihoncd = this.$apkihoncd.val();
                shopcd = this.$shopcd.val();
                params = {
                    "kinoid":"b0501w02",
                    "apkihoncd":apkihoncd,
                    "shopcd":shopcd,
                    "jigyoshokanri":jigyoshokanri,
                    "cdkensakuflg":"1"
                };
                break;
            // 想定外ケース
            default:
                // 空文字を設定し検索結果を無しとする
                callbackFunction = this.setException;
                params = {
                    "kinoid":"b0501w02",
                    "apkihoncd":"",
                    "shopcd":"",
                    "jigyoshokanri":"",
                    "cdkensakuflg":""
                };
                break;
        }
        
        const queryParams = $.param(params);
        
        // 委託相手コード検索の場合
        if (id == "b0501w02-itakuaitesakicd") {   
            // 委託相手チェック
            const api = "/api/b0501w02/checkitakuaite";
            
            // apiコール
            $.get(api, {apkihoncd:apkihoncd, shopcd:shopcd})
            .then((data) => {
                if (data) {
                    // 子ウィンドウの呼び出し
                    openDataSelectWindow(callbackFunction,"/b0501w03?" + queryParams);
                }
            })
        // 上記以外の場合
        } else {
            // 子ウィンドウの呼び出し
            openDataSelectWindow(callbackFunction,"/b0501w03?" + queryParams);
        }
   }
   
    setHaneitokuisakicd = (data) => {
        this.$haneimototokuisakicd.val(data.tokuisakicd);
        this.$haneimototokuisakinm.text(data.tokuisakinm);
    }
    setItakuaitesakicd = (data) => {
        this.$itakuaitetokuisakicd.val(data.tokuisakicd);
        this.$itakuaitetokuisakinm.text(data.tokuisakinm);
    }
    setShoukaitokuisakicd = (data) => {
        this.$shoukaitokuisakicd.val(data.tokuisakicd);
        this.$shoukaitokuisakimei.text(data.tokuisakinm);
    }
    
    /**
     * 請求先マスタ子画面呼び出し処理
     */
    #clickSkysakicdBtn = (e) => {
        
        e.stopPropagation();
        e.preventDefault();

        // コードの取得
        const apkihoncd = this.$apkihoncd.val();
        const shopcd = this.$shopcd.val();
        const jigyoshokanri = this.$jigyoshokanri.val();
        // 適用する値設定用関数の選定
        const callbackFunction = this.setSkysakicd;
        const params = {
                "kinoid":"b0501w02",
                "apkihoncd":apkihoncd,
                "shopcd":shopcd,
                "jigyoshokanri":jigyoshokanri,
                "cdkensakuflg":"1"
                };
        
        const queryParams = $.param(params);
        
        // 子ウィンドウの呼び出し
        openDataSelectWindow(callbackFunction,"/b0503w03?" + queryParams);
   }
   
    setSkysakicd = (data) => {
        this.$skysakicd.val(data.skysakicd);
        this.$skysakinmn1.text(data.skysakinmn1);
    }
    
    /**
     * APショップヘッダ子画面呼び出し処理
     */
    #clickShopcdBtn = (e) => {
        
        e.stopPropagation();
        e.preventDefault();
        
        // コードの取得
        const apkihoncd = this.$apkihoncd.val();
        const callbackFunction = this.setShopcd;
        const params = {
            "kinoid":"b0501w02",
            "apkihoncd":apkihoncd,
            "cdkensakuflg":"1"
        };
        const queryParams = $.param(params);
        
        // 子ウィンドウの呼び出し
        openDataSelectWindow(callbackFunction,"/z0103w01?" + queryParams);
   }
   
    setShopcd = (data) => {
        this.$tokuisakishopcd.val(data.shopcd);
        this.$tokuisakishopnm.text(data.shopnm);
    }
    
    /**
     * 会員コード子画面呼び出し処理
     */
    #clickKaiincdBtn = (e) => {
        
        e.stopPropagation();
        e.preventDefault();
               
        // パラメータの取得
        const renrakusakinon1 = this.$renrakusakinon1.val();
        const renrakusakinon2 = this.$renrakusakinon2.val();
        const renrakusakinon3 = this.$renrakusakinon3.val();
        const renrakusakinon4 = this.$renrakusakinon4.val();
        
        // 連絡先番号1～4全て未入力の場合、エラー
        if (renrakusakinon1.trim() == '' &&
            renrakusakinon2.trim() == '' &&
            renrakusakinon3.trim() == '' &&
            renrakusakinon4.trim() == '') {
            const messageId = "W113";
            const $dialog = new ErrorMessageDialog({
                "param": ["連絡先番号"],
                "messageId": messageId,
            });        
            $dialog.open();
            return;
        }
        
        const callbackFunction = this.setKaiincd;
        const params = {
            "kinoid":"b0501w02",
            "renrakusakinon1":renrakusakinon1,
            "renrakusakinon2":renrakusakinon2,
            "renrakusakinon3":renrakusakinon3,
            "renrakusakinon4":renrakusakinon4,
            "cdkensakuflg":"1"
        };
        const queryParams = $.param(params);
        
        // 子ウィンドウの呼び出し
        openDataSelectWindow(callbackFunction,"/z0513w01?" + queryParams);
    }

    setKaiincd = (data) => {
        this.$kaiincd.val(data.kaiincd);
    }
   
    /**
     * 倉庫マスタ子画面呼び出し処理
     */
    #clickSokocdBtn = (e) => {
       
        e.stopPropagation();
        e.preventDefault();
        
        // コードの取得
        const apkihoncd = this.$apkihoncd.val();
        const shopcd = this.$shopcd.val();
        const callbackFunction = this.setSokocd;
        const params = {
            "kinoid":"b0501w02",
            "apkihoncd":apkihoncd,
            "shopcd":shopcd,
            "cdkensakuflg":"1",
            "sokokbn":"3"
        };
        const queryParams = $.param(params);
        
        // 子ウィンドウの呼び出し
        openDataSelectWindow(callbackFunction,"/a2201w03?" + queryParams);
   }
   
    setSokocd = (data) => {
        this.$saburtlsokocd.val(data.sokocd);
        this.$sokonmn1.text(data.sokonm);
    }
    
    /**
     * 担当者マスタ子画面呼び出し処理
     */
    #clickTnshacdBtn = (e) => {

        e.stopPropagation();
        e.preventDefault();
        
        // clickされた要素の取得
        const currentTarget = e.currentTarget;
        const id = currentTarget.id;
        // コードの取得
        const apkihoncd = this.$apkihoncd.val();
        const shopcd = this.$shopcd.val();
        const jigyoshokanri = this.$jigyoshokanri.val();
        let flgVal;
        if (this.$akshanikbn.val() === '4') {
            flgVal = '1';
        } else {
            flgVal = '2';
        }
        // 適用する値設定用関数の選定
        let callbackFunction;
        // 渡すparamを定義
        let params;
        switch (id) {
            case "b0501w02-refsecchitoshacd":
                callbackFunction = this.setSecchitoshacd;
                // kinoidは必須（画面IDを定義する）
                params = {
                    "kinoid":"b0501w02",
                    "apkihoncd":apkihoncd,
                    "shopcd":shopcd,
                    "jigyoshokanri":'',
                    "eigyotnshakbn":'',
                    "jimutnshakbn":'',
                    "dlvrytnshakbn":'',
                    "cdkensakuflg":"2"
                };
                break;
            case "b0501w02-refhikiagetoshacd":
                callbackFunction = this.setHikiagetoshacd;
                params = {
                    "kinoid":"b0501w02",
                    "apkihoncd":apkihoncd,
                    "shopcd":shopcd,
                    "jigyoshokanri":'',
                    "eigyotnshakbn":'',
                    "jimutnshakbn":'',
                    "dlvrytnshakbn":'',
                    "cdkensakuflg":"2"
                };
                break;
            case "b0501w02-linkjissekitnshacd":
                callbackFunction = this.setJissekitnshacd;
                params = {
                    "kinoid":"b0501w02",
                    "apkihoncd":apkihoncd,
                    "shopcd":shopcd,
                    "jigyoshokanri":jigyoshokanri,
                    "eigyotnshakbn":"",
                    "jimutnshakbn":"",
                    "dlvrytnshakbn":"",
                    "cdkensakuflg":flgVal
                };
                break;
            case "b0501w02-reftnshacd":
                callbackFunction = this.setTnshacd;
                params = {
                    "kinoid":"b0501w02",
                    "apkihoncd":apkihoncd,
                    "shopcd":shopcd,
                    "jigyoshokanri":jigyoshokanri,
                    "eigyotnshakbn":"1",
                    "jimutnshakbn":"",
                    "dlvrytnshakbn":"",
                    "cdkensakuflg":flgVal
                };
                break;
            case "b0501w02-reftnshacd1":
                callbackFunction = this.setTnshacd1;
                params = {
                    "kinoid":"b0501w02",
                    "apkihoncd":apkihoncd,
                    "shopcd":shopcd,
                    "jigyoshokanri":"",
                    "eigyotnshakbn":"",
                    "jimutnshakbn":"",
                    "dlvrytnshakbn":"",
                    "cdkensakuflg":"2"
                };
                break;
            case "b0501w02-reftnshacd2":
                callbackFunction = this.setTnshacd2;
                params = {
                    "kinoid":"b0501w02",
                    "apkihoncd":apkihoncd,
                    "shopcd":shopcd,
                    "jigyoshokanri":jigyoshokanri,
                    "eigyotnshakbn":"1",
                    "jimutnshakbn":"",
                    "dlvrytnshakbn":"",
                    "cdkensakuflg":flgVal
                };
                break;
            case "b0501w02-reftnshacd3":
                callbackFunction = this.setTnshacd3;
                params = {
                    "kinoid":"b0501w02",
                    "apkihoncd":apkihoncd,
                    "shopcd":shopcd,
                    "jigyoshokanri":jigyoshokanri,
                    "eigyotnshakbn":"",
                    "jimutnshakbn":"",
                    "dlvrytnshakbn":"1",
                    "cdkensakuflg":flgVal
                };
                break;
            case "b0501w02-reftnshacd4":
                callbackFunction = this.setTnshacd4;
                params = {
                    "kinoid":"b0501w02",
                    "apkihoncd":apkihoncd,
                    "shopcd":shopcd,
                    "jigyoshokanri":jigyoshokanri,
                    "eigyotnshakbn":"",
                    "jimutnshakbn":"",
                    "dlvrytnshakbn":"1",
                    "cdkensakuflg":flgVal
                };
                break;
            case "b0501w02-reftnshacd5":
                callbackFunction = this.setTnshacd5;
                params = {
                    "kinoid":"b0501w02",
                    "apkihoncd":apkihoncd,
                    "shopcd":shopcd,
                    "jigyoshokanri":'',
                    "eigyotnshakbn":"1",
                    "jimutnshakbn":"0",
                    "dlvrytnshakbn":"0",
                    "cdkensakuflg":"2"
                };
                break;
            case "b0501w02-reftnshacd6":
                callbackFunction = this.setTnshacd6;
                params = {
                    "kinoid":"b0501w02",
                    "apkihoncd":apkihoncd,
                    "shopcd":shopcd,
                    "jigyoshokanri":'',
                    "eigyotnshakbn":"0",
                    "jimutnshakbn":"0",
                    "dlvrytnshakbn":"0",
                    "cdkensakuflg":"2"
                };
                break;
            // 想定外ケース
            default:
                // 空文字を設定し子画面での検索結果を無しとする。
                callbackFunction = this.setException;
                params = {
                    "kinoid":"b0501w02",
                    "apkihoncd":"",
                    "shopcd":"",
                    "jigyoshokanri":"",
                    "eigyotnshakbn":"",
                    "jimutnshakbn":"",
                    "dlvrytnshakbn":"",
                    "cdkensakuflg":"1"
                };
                break;
        }
        
        const queryParams = $.param(params);
        
        // 子ウィンドウの呼び出し
        openDataSelectWindow(callbackFunction,"/a0102w03?" + queryParams);
   }
   
    setSecchitoshacd = (data) => {
        this.$stupddtoshacd.val(data.tnshacd);
        this.$toshamei1.text(data.tnshanm);
    }
    setHikiagetoshacd = (data) => {
        this.$hikiagetoshacd.val(data.tnshacd);
        this.$toshamei3.text(data.tnshanm);
    }
    setJissekitnshacd = (data) => {
        this.$jissekitnshacd.val(data.tnshacd);
        this.$tnshamei.text(data.tnshanm);
    }
    setTnshacd = (data) => {
        this.$eigyotnshacd.val(data.tnshacd);
        this.$eigyotnshanm.text(data.tnshanm);
    }
    setTnshacd1 = (data) => {
        this.$kakutokutnshacd.val(data.tnshacd);
        this.$tnshamei3.text(data.tnshanm);
    }
    setTnshacd2 = (data) => {
        this.$eigyotnshacd1.val(data.tnshacd);
        this.$eigyotnshanm1.text(data.tnshanm);
    }
    setTnshacd3 = (data) => {
        this.$dlvrytnshacd.val(data.tnshacd);
        this.$dlvrytnshamei.text(data.tnshanm);
    }
    setTnshacd4 = (data) => {
        this.$dlvrytnshacd1.val(data.tnshacd);
        this.$dlvrytnshamei1.text(data.tnshanm);
    }
    setTnshacd5 = (data) => {
        this.$eigyotnshacd2.val(data.tnshacd);
        this.$eigyotnshanm2.text(data.tnshanm);
    }
    setTnshacd6 = (data) => {
        this.$kaishutnshacd.val(data.tnshacd);
        this.$kaishutnshamei.text(data.tnshanm);
    }
    
    /**
     * 営業エリアマスタ子画面呼び出し処理
     */
    #clickEigyoareacdBtn = (e) => {
        
        e.stopPropagation();
        e.preventDefault();
        // clickされた要素の取得
        const currentTarget = e.currentTarget;
        const id = currentTarget.id;
        // コードの取得
        const apkihoncd = this.$apkihoncd.val();
        const shopcd = this.$shopcd.val();
        const params = {
                    "kinoid":"b0501w02",
                    "apkihoncd":apkihoncd,
                    "shopcd":shopcd,
                    "cdkensakuflg":"1"
                };
        // 適用する値設定用関数の選定
        let callbackFunction;
        switch (id) {
            case "b0501w02-refeigyoareacd":
                callbackFunction = this.setEigyoareacd;
                break;
            case "b0501w02-refeigyoareacd1":
                callbackFunction = this.setEigyoareacd1;
                break;
            // 例外ケース
            default:
                callbackFunction = this.setException;
                break;
        }
        
        const queryParams = $.param(params);
        
        // 子ウィンドウの呼び出し
        openDataSelectWindow(callbackFunction,"/a0105w03?" + queryParams);
   }
   
    setEigyoareacd = (data) => {
        this.$eigyoareacd.val(data.eigyoareacd);
        this.$eigyoareanm.text(data.eigyoareanm);
    }
    setEigyoareacd1 = (data) => {
        this.$eigyoareacd1.val(data.eigyoareacd);
        this.$eigyoareanm1.text(data.eigyoareanm);
    }
    
    /**
     * キャンペーンマスタ子画面呼び出し処理
     */
    #clickKakutokukynpncdBtn = (e) => {
        
        e.stopPropagation();
        e.preventDefault();
        
        // clickされた要素の取得
        const currentTarget = e.currentTarget;
        const id = currentTarget.id;
        // コードの取得
        const apkihoncd = this.$apkihoncd.val();
        const shopcd = this.$shopcd.val();
        let params;
        // 適用する値設定用関数の選定
        let callbackFunction;
        switch (id) {
            case "b0501w02-linkkakutokuhonbukynpncd":
                callbackFunction = this.setKakutokukynpncd;
                params = {
                    "kinoid":"b0501w02",
                    "apkihoncd":"001",
                    "shopcd":"000",
                    "inptkbn":"0",
                    "cdkensakuflg":"1"
                };
                break;
            case "b0501w02-refkakutokukynpncd":
                callbackFunction = this.setKakutokukynpncd1;
                params = {
                    "kinoid":"b0501w02",
                    "apkihoncd":apkihoncd,
                    "shopcd":shopcd,
                    "inptkbn":"1",
                    "cdkensakuflg":"1"
                };
                break;
            // 想定外ケース
            default:
                // パラメータに空文字を設定し検索結果を無しとする
                callbackFunction = this.setException;
                params = {
                    "kinoid":"b0501w02",
                    "apkihoncd":"",
                    "shopcd":"",
                    "inptkbn":"",
                    "cdkensakuflg":"1"
                };
                break;
        }
        
        const queryParams = $.param(params);
        
        // 子ウィンドウの呼び出し
        openDataSelectWindow(callbackFunction,"/a0112w03?" + queryParams);
   }
    setKakutokukynpncd = (data) => {
        this.$kakutokuhonbukynpncd.val(data.kynpncd);
        this.$kakutokuhonbukynpnnm.text(data.kynpnnm);
        // 景品プルダウン初期化
        this.getKynpnkeihinkbnPulldown({data:this.dataKakutokuHonbuKynpn});
    }
    setKakutokukynpncd1 = (data) => {
        this.$kakutokukynpncd.val(data.kynpncd);
        this.$kakutokukynpnnm.text(data.kynpnnm);
        // 景品プルダウン初期化
        this.getKynpnkeihinkbnPulldown({data:this.dataKakutokuKynpn});
    }
    
    /**
     * 得意先獲得販促詳細マスタ子画面呼び出し処理
     */
    #clickHansokushscdBtn = (e) => {
        
        e.stopPropagation();
        e.preventDefault();
        
        // コードの取得
        const apkihoncd = this.$apkihoncd.val();
        const shopcd = this.$shopcd.val();
        const params = {
                    "kinoid":"b0501w02",
                    "apkihoncd":apkihoncd,
                    "shopcd":shopcd,
                    "cdkensakuflg":"1"
                    };
        // 適用する値設定用関数の選定
        const callbackFunction = this.setHansokushscdcd;
        
        const queryParams = $.param(params);
        
        // 子ウィンドウの呼び出し
        openDataSelectWindow(callbackFunction,"/a0501w03?" + queryParams);
   }
    setHansokushscdcd = (data) => {
        this.$hansokushscd.val(data.hansokushscd);
        this.$hansokunm.text(data.hansokunm);
    }
    
    /**
     * エージェントマスタ子画面呼び出し処理
     */
    #clickEjntcdBtn = (e) => {
        
        e.stopPropagation();
        e.preventDefault();
        
        // clickされた要素の取得
        const currentTarget = e.currentTarget;
        const id = currentTarget.id;
        // コードの取得
        const apkihoncd = this.$apkihoncd.val();
        const shopcd = this.$shopcd.val();
        let params;
        // 適用する値設定用関数の選定
        let callbackFunction;
        switch (id) {
            case "b0501w02-refejntcd":
                callbackFunction = this.setEjntcd;
                params = {
                    "kinoid":"b0501w02",
                    "apkihoncd":"001",
                    "shopcd":"000",
                    "cdkensakuflg":"1"
                };
                break;
            case "b0501w02-refejntcd3":
                callbackFunction = this.setEjntcd1;
                params = {
                    "kinoid":"b0501w02",
                    "apkihoncd":apkihoncd,
                    "shopcd":shopcd,
                    "cdkensakuflg":"1"
                };
                break;
            // 想定外ケース
            default:
                // パラメータに空文字を設定し検索結果を無しとする
                callbackFunction = this.setException;
                params = {
                    "kinoid":"b0501w02",
                    "apkihoncd":"",
                    "shopcd":"",
                    "cdkensakuflg":"1"
                };
                break;
        }
        
        const queryParams = $.param(params);
        
        // 子ウィンドウの呼び出し
        openDataSelectWindow(callbackFunction,"/a0201w03?" + queryParams);
   }
    setEjntcd = (data) => {
        this.$honbuejntcd.val(data.ejntcd);
        this.$ejntmein1.text(data.ejntmein1);
    }
    setEjntcd1 = (data) => {
        this.$kakutokuejntcd.val(data.ejntcd);
        this.$ejntmein2.text(data.ejntmein1);
        // 社内担当者コードが存在する場合のみ社内担当者名を取得
        if (data.shanaitnshacd) {
            // 社内担当者名名取得
            const api = "/api/parts/s02m003t/unique/"
                + this.$apkihoncd.val() + "/"
                + this.$shopcd.val() + "/"
                + data.shanaitnshacd;
            // apiコール
            $.get(api)
            .then((data) => {
                if (data) {
                    this.$ejntmgnn2.text(data);
                }
            })
            this.$ejntmgnn1.text(data.shanaitnshacd);
        }
    }
    
    /**
     * 配達拠点マスタ子画面呼び出し処理
     */
    #clickDlvrykyotencdBtn = (e) => {
        
        e.stopPropagation();
        e.preventDefault();
        
        // clickされた要素の取得
        const currentTarget = e.currentTarget;
        const id = currentTarget.id;
        // 子画面呼び出しに必要な定数
        let params;
        let apkihoncd;
        let shopcd;
        let jutakukbn;
        // 適用する値設定用関数の選定
        let callbackFunction;
        switch (id) {
            case "b0501w02-refdlvrykyoten":
                apkihoncd = this.$dlvrykankatsuapkihoncd.val() ? this.$dlvrykankatsuapkihoncd.val() : this.$apkihoncd.val();
                shopcd = this.$dlvrykankatsushopcd.val() ? this.$dlvrykankatsushopcd.val() : this.$shopcd.val();
                this.beforeApkihoncd = this.$dlvrykankatsuapkihoncd.val();
                this.beforeApkihonnm = this.$apnm2.text();
                this.beforeShopcd = this.$dlvrykankatsushopcd.val();
                this.beforeShopnm = this.$shopnm2.text();
                callbackFunction = this.setDlvrykyotencd;
                break;
            case "b0501w02-refdlvrykyoten1":
                apkihoncd = this.$dlvrykankatsuapkihoncd1.val() ? this.$dlvrykankatsuapkihoncd1.val() : this.$apkihoncd.val();
                shopcd = this.$dlvrykankatsushopcd1.val() ? this.$dlvrykankatsushopcd1.val() : this.$shopcd.val();
                this.beforeApkihoncd = this.$dlvrykankatsuapkihoncd1.val();
                this.beforeApkihonnm = this.$apnm1.text();
                this.beforeShopcd = this.$dlvrykankatsushopcd1.val();
                this.beforeShopnm = this.$shopnm1.text();
                callbackFunction = this.setDlvrykyotencd1;
                break;
            // 想定外ケース
            default:
                callbackFunction = this.setException;
                break;
        }
        
        jutakukbn = this.$apkihoncd.val() == apkihoncd && this.$shopcd.val() == shopcd ? '' : '1';
        
        params = {
            "kinoid":"b0501w02",
            "apkihoncd":apkihoncd,
            "shopcd":shopcd,
            "jutakukbn":jutakukbn,
            "cdkensakuflg":"1"
        };
        const queryParams = $.param(params);

        // 子ウィンドウの呼び出し
        openDataSelectWindow(callbackFunction,"/a0106w03?" + queryParams);
   }
   
    setDlvrykyotencd = (data) => {
        if (this.$dlvrykankatsukyotencd.val() !== data.dlvrykyotencd) {
            this.$dlvryareacd.val("");
            this.$dlvryareanm.text("");
        }
        
        this.$dlvrykankatsukyotencd.val(data.dlvrykyotencd);
        this.$kyotennm.text(data.dlvrykyotennm);
        
        this.$dlvrykankatsuapkihoncd.val(this.beforeApkihoncd ? this.beforeApkihoncd : this.$apkihoncd.val());
        this.$apnm2.text(this.beforeApkihonnm);
        if (!this.beforeApkihoncd) {
            this.dataDlvrykankatsuKyotencd.apkihoncd.trigger("change");
        }
        this.$dlvrykankatsushopcd.val(this.beforeShopcd ? this.beforeShopcd : this.$shopcd.val());
        this.$shopnm2.text(this.beforeShopnm);
        if (!this.beforeShopcd) {
            this.dataDlvrykankatsuKyotencd.shopcd.trigger("change");
        }
    }
    setDlvrykyotencd1 = (data) => {
        if (this.$dlvrykankatsukyotencd1.val() !== data.dlvrykyotencd) {
            this.$dlvryareacd1.val("");
            this.$dlvryareanm1.text("");
        }
        
        this.$dlvrykankatsukyotencd1.val(data.dlvrykyotencd);
        this.$kyotennm1.text(data.dlvrykyotennm);
        
        this.$dlvrykankatsuapkihoncd1.val(this.beforeApkihoncd ? this.beforeApkihoncd : this.$apkihoncd.val());
        this.$apnm1.text(this.beforeApkihonnm);
        if (!this.beforeApkihoncd) {
            this.dataTokuisakiDlvrykankatsuKyotencd.apkihoncd.trigger("change");
        }
        this.$dlvrykankatsushopcd1.val(this.beforeShopcd ? this.beforeShopcd : this.$shopcd.val());
        this.$shopnm1.text(this.beforeShopnm);
        if (!this.beforeShopcd) {
            this.dataTokuisakiDlvrykankatsuKyotencd.shopcd.trigger("change");
        }

    }
    
    /**
     * 配達会社マスタ子画面呼び出し処理
     */
    #clickDlvrycmpnycdBtn = (e) => {
        e.stopPropagation();
        e.preventDefault();
        // clickされた要素の取得
        const currentTarget = e.currentTarget;
        const id = currentTarget.id;
        // コードの取得
        const apkihoncd = this.$apkihoncd.val();
        const shopcd = this.$shopcd.val();
        const params = {
                    "kinoid":"b0501w02",
                    "apkihoncd":apkihoncd,
                    "shopcd":shopcd,
                    "cdkensakuflg":"1"
                };
        // 適用する値設定用関数の選定
        let callbackFunction;
        switch (id) {
            case "b0501w02-refdlvrycmpnycd":
                callbackFunction = this.setDlvrycmpnycd;
                break;
            case "b0501w02-refdlvrycmpnycd1":
                callbackFunction = this.setDlvrycmpnycd1;
                break;
            // 想定外ケース
            default:
                callbackFunction = this.setException;
                break;
        }
        
        const queryParams = $.param(params);
        
        // 子ウィンドウの呼び出し
        openDataSelectWindow(callbackFunction,"/a0109w03?" + queryParams);
   }
   
    setDlvrycmpnycd = (data) => {
        this.$dlvrycmpnycd.val(data.dlvrycmpnycd);
        this.$dlvrycmpnynm.text(data.dlvrycmpnynm);
    }
    setDlvrycmpnycd1 = (data) => {
        this.$dlvrycmpnycd1.val(data.dlvrycmpnycd);
        this.$dlvrycmpnynm1.text(data.dlvrycmpnynm);
    }
    
    /**
     * 配達エリアマスタ子画面呼び出し処理
     */
    #clickDlvryareacdBtn = (e) => {
        e.stopPropagation();
        e.preventDefault();
        // clickされた要素の取得
        const currentTarget = e.currentTarget;
        const id = currentTarget.id;
        // 子画面呼び出しに必要な定数
        let params;
        let apkihoncd;
        let shopcd;
        let kyotencd;
        // 適用する値設定用関数の選定
        let callbackFunction;
        switch (id) {
            case "b0501w02-refdlvryareacd":
                // コードの取得
                apkihoncd = this.$dlvrykankatsuapkihoncd.val();
                shopcd = this.$dlvrykankatsushopcd.val();
                kyotencd = this.$dlvrykankatsukyotencd.val();
                params = {
                            "kinoid":"b0501w02",
                            "apkihoncd":apkihoncd,
                            "shopcd":shopcd,
                            "dlvrykyotencd":kyotencd,
                            "cdkensakuflg":"1"
                        };
                callbackFunction = this.setDlvryareacd;
                break;
            case "b0501w02-refdlvryareacd1":
                // コードの取得
                apkihoncd = this.$dlvrykankatsuapkihoncd1.val();
                shopcd = this.$dlvrykankatsushopcd1.val();
                kyotencd = this.$dlvrykankatsukyotencd1.val();
                params = {
                            "kinoid":"b0501w02",
                            "apkihoncd":apkihoncd,
                            "shopcd":shopcd,
                            "dlvrykyotencd":kyotencd,
                            "cdkensakuflg":"1"
                        };
                callbackFunction = this.setDlvryareacd1;
                break;
            // 想定外ケース
            default:
                // パラメータに空文字を設定し検索結果を無しとする
                params = {
                            "kinoid":"b0501w02",
                            "apkihoncd":"",
                            "shopcd":"",
                            "dlvrykyotencd":"",
                            "cdkensakuflg":"1"
                        };
                callbackFunction = this.setException;
                break;
        }
        const queryParams = $.param(params);
        
        // 子ウィンドウの呼び出し
        openDataSelectWindow(callbackFunction,"/a0108w03?" + queryParams);
   }
   
    setDlvryareacd = (data) => {
        this.$dlvryareacd.val(data.dlvryareacd);
        this.$dlvryareanm.text(data.dlvryareanm);
    }
    setDlvryareacd1 = (data) => {
        this.$dlvryareacd1.val(data.dlvryareacd);
        this.$dlvryareanm1.text(data.dlvryareanm);
    }
    
    /**
     * マージンマスタ子画面呼び出し処理
     */
    #clickMgncdBtn = (e) => {
        
        e.stopPropagation();
        e.preventDefault();
        
        // clickされた要素の取得
        const currentTarget = e.currentTarget;
        const id = currentTarget.id;
        
        let params;
        // コードの取得
        const apkihoncd = this.$apkihoncd.val();
        const shopcd = this.$shopcd.val();
        // 適用する値設定用関数の選定
        let callbackFunction;
        switch (id) {
            case "b0501w02-sanshokensakuwanshottocd":
                params = {
                            "kinoid":"b0501w02",
                            "apkihoncd":apkihoncd,
                            "shopcd":shopcd,
                            "mgntrgtkbn":"1",
                            "cdkensakuflg":"1"
                        };
                callbackFunction = this.setWanshottocd;
                break;
            case "b0501w02-refmgncd1":
                params = {
                            "kinoid":"b0501w02",
                            "apkihoncd":apkihoncd,
                            "shopcd":shopcd,
                            "mgntrgtkbn":"2",
                            "cdkensakuflg":"1"
                        };
                callbackFunction = this.setMgncd1;
                break;
            case "b0501w02-refmgncd2":
                params = {
                            "kinoid":"b0501w02",
                            "apkihoncd":apkihoncd,
                            "shopcd":shopcd,
                            "mgntrgtkbn":"2",
                            "cdkensakuflg":"1"
                        };
                callbackFunction = this.setMgncd2;
                break;
            case "b0501w02-refmgncd3":
                params = {
                            "kinoid":"b0501w02",
                            "apkihoncd":apkihoncd,
                            "shopcd":shopcd,
                            "mgntrgtkbn":"2",
                            "cdkensakuflg":"1"
                        };
                callbackFunction = this.setMgncd3;
                break;
            case "b0501w02-refmgncd4":
                params = {
                            "kinoid":"b0501w02",
                            "apkihoncd":apkihoncd,
                            "shopcd":shopcd,
                            "mgntrgtkbn":"2",
                            "cdkensakuflg":"1"
                        };
                callbackFunction = this.setMgncd4;
                break;
            // 想定外ケース
            default:
                // パラメータに空文字を設定し検索結果を無しとする
                params = {
                            "kinoid":"b0501w02",
                            "apkihoncd":"",
                            "shopcd":"",
                            "mgntrgtkbn":"",
                            "cdkensakuflg":"1"
                        };
                callbackFunction = this.setException;
                break;
        }
        
        const queryParams = $.param(params);
        
        // 子ウィンドウの呼び出し
        openDataSelectWindow(callbackFunction,"/a0202w03?" + queryParams);
   }
   
    setWanshottocd = (data) => {
        this.$wanshottocd.val(data.mgncd);
        this.$mgnnm.text(data.mgnnm);
    }
    setMgncd1 = (data) => {
        this.$mgncdn1.val(data.mgncd);
        this.$mgnnm1.text(data.mgnnm);
    }
    setMgncd2 = (data) => {
        this.$mgncdn2.val(data.mgncd);
        this.$mgnnm2.text(data.mgnnm);
    }
    setMgncd3 = (data) => {
        this.$mgncdn3.val(data.mgncd);
        this.$mgnnm3.text(data.mgnnm);
    }
    setMgncd4 = (data) => {
        this.$mgncdn4.val(data.mgncd);
        this.$mgnnm4.text(data.mgnnm);
    }
    
    /**
     * 商品マスタ子画面呼び出し処理
     */
    #clickShncdBtn = (e) => {
        
        e.stopPropagation();
        e.preventDefault();
        
        // clickされた要素の取得
        const currentTarget = e.currentTarget;
        const id = currentTarget.id;
        // コードの取得
        const apkihoncd = this.$apkihoncd.val();
        const shopcd = this.$shopcd.val();
        const params = {"kinoid":"b0501w02",
                        "apkihoncd":apkihoncd,
                        "shopcd":shopcd,
                        "cdkensakuflg":"2"};
        // 適用する値設定用関数の選定
        let callbackFunction;
        switch (id) {
            case "b0501w02-refshncd1":
                callbackFunction = this.setShncd1;
                break;
            case "b0501w02-refshncd2":
                callbackFunction = this.setShncd2;
                break;
            case "b0501w02-refshncd3":
                callbackFunction = this.setShncd3;
                break;
            case "b0501w02-refshncd4":
                callbackFunction = this.setShncd4;
                break;
            // 想定外ケース
            default:
                // パラメータに空文字を設定し検索結果を無しとする
                callbackFunction = this.setException;
                break;
        }
        
        const queryParams = $.param(params);
        
        // 子ウィンドウの呼び出し
        openDataSelectWindow(callbackFunction,"/a0301w03?" + queryParams);
   }
   
    setShncd1 = (data) => {
        this.$mgnyoshncdn1.val(data.shncd);
        this.$shnnm1.text(data.shnnm);
    }
    setShncd2 = (data) => {
        this.$mgnyoshncdn2.val(data.shncd);
        this.$shnnm2.text(data.shnnm);
    }
    setShncd3 = (data) => {
        this.$mgnyoshncdn3.val(data.shncd);
        this.$shnnm3.text(data.shnnm);
    }
    setShncd4 = (data) => {
        this.$mgnyoshncdn4.val(data.shncd);
        this.$shnnm4.text(data.shnnm);
    }
    
    /**
     * 自動値引マスタ子画面呼び出し処理
     */
    #clickJidonebikicdBtn = (e) => {
        e.stopPropagation();
        e.preventDefault();
        
        // 特別値引区分=なしの場合、エラー
        if ($("*[name='baseBean.rknkindtokubetsunebikiflg']:checked").val() === '0') {
            const messageId = "W098";
            const $dialog = new ErrorMessageDialog({
                "param": ["値引のできる料金プラン"],
                "messageId": messageId,
            });        
            $dialog.open();
            return;
        }
        
        // clickされた要素の取得
        const currentTarget = e.currentTarget;
        const id = currentTarget.id;
        // コードの取得
        const apkihoncd = this.$apkihoncd.val();
        const shopcd = this.$shopcd.val();
        const params = {"kinoid":"b0501w02",
                        "apkihoncd":apkihoncd,
                        "shopcd":shopcd,
                        "cdkensakuflg":"1"
                        };
                        
        // 適用する値設定用関数の選定
        let callbackFunction;
        switch (id) {
            case "b0501w02-refjidonebikin1":
                callbackFunction = this.setJidonebikicd1;
                break;
            case "b0501w02-refjidonebikin2":
                callbackFunction = this.setJidonebikicd2;
                break;
            case "b0501w02-refjidonebikin3":
                callbackFunction = this.setJidonebikicd3;
                break;
            // 想定外ケース
            default:
                callbackFunction = this.setException;
                break;
        }
        
        const queryParams = $.param(params);
        
        // 子ウィンドウの呼び出し
        openDataSelectWindow(callbackFunction,"/a0502w03?" + queryParams);
    }
   
    setJidonebikicd1 = (data) => {
        this.$jidonebikicd1.val(data.jidonebikicd);
        this.$jidonebikinm1.text(data.jidonebikinm);
        this.$shncd1.text(data.shncd);
        this.$shnnmn1.text(data.shnnm);
        this.$gekkanhanbaihns1.text(separateWithComma(data.gekkanhanbaihns));
        this.$nebikihns1.text(separateWithComma(data.nebikibtlhns));
        this.$nebikikin1.text(separateWithComma(data.nebikikin));
        this.$jidonebikikind1.text(data.jidonebikikindnm);
    }
    setJidonebikicd2 = (data) => {
        this.$jidonebikicd2.val(data.jidonebikicd);
        this.$jidonebikinm2.text(data.jidonebikinm);
        this.$shncd2.text(data.shncd);
        this.$shnnmn2.text(data.shnnm);
        this.$gekkanhanbaihns2.text(separateWithComma(data.gekkanhanbaihns));
        this.$nebikihns2.text(separateWithComma(data.nebikibtlhns));
        this.$nebikikin2.text(separateWithComma(data.nebikikin));
        this.$jidonebikikind2.text(data.jidonebikikindnm);
    }
    setJidonebikicd3 = (data) => {
        this.$jidonebikicd3.val(data.jidonebikicd);
        this.$jidonebikinm3.text(data.jidonebikinm);
        this.$shncd3.text(data.shncd);
        this.$shnnmn3.text(data.shnnm);
        this.$gekkanhanbaihns3.text(separateWithComma(data.gekkanhanbaihns));
        this.$nebikihns3.text(separateWithComma(data.nebikibtlhns));
        this.$nebikikin3.text(separateWithComma(data.nebikikin));
        this.$jidonebikikind3.text(data.jidonebikikindnm);
    }
    
    // 想定外ケース用例外コールバック関数
    setException = (_data) => {
    }
    
    /**
     * クリアボタン押下時に該当コードと該当ラベル項目をクリアする
     */
    kuriaCdAndNm = (e) => {
        
        e.stopPropagation();
        e.preventDefault();
        
        // 値クリア処理
        $.each(e['data'],(_i, v) => {
            if (v.is("input")) {
                v.val('');
            } else if (v.is("span")) {
                v.text('');
            }
        });
    }
    
    /**
     * 得意先ショップコード変更時にフックし、ショップ名称を表示する
     */
    showTokuisakiShopNm = (_e) => {
        
        // 名称を表示し入力可能文言を非表示
        this.$tokuisakishoptext1.show();
        this.$tokuisakishoptext2.hide();
    }
    
    /**
     * プルダウン変更時イベントにフックする
     */
    changedPullDown = (e, isScriptCall) => {
        // プルダウン生成時の場合
        if (isScriptCall) {
            // プルダウンを1行目を強制選択させる
            this.selectFirstIndex(e);
        }
    }
    
    /**
     * プルダウンの1行目を選択する
     */
    selectFirstIndex = (e) => {
        const $currentTarget = $(e.currentTarget);
        // 値がNULLの場合
        if ((e.type == EVENTS.TEMPLATE_LOAD_COMPLETE &&
                $currentTarget.attr("data-default") == null) ||
            (e.type == 'change' && $currentTarget.val() == null)) {
            // プルダウンを1行目を強制選択させる
            const firstRowVal = $currentTarget.children('option:first-child').val();
            $currentTarget.val(firstRowVal);
        }
    }
    
    /**
     * 得意先区分セレクトボックス変更時イベントにフックする
     */
    changedTokuisakiKbn = (e, isScriptCall) => {
        // プルダウン生成時の場合
        if (isScriptCall) { 
            return;
        }
        
        // 得意先ショップコードを制御する
        this.changedTokuisakiJutakuKbn(e);
        // サブレンタル倉庫コードを制御する
        this.changedTokuisakiJutakuKbnSoko(e);
        // ポイント管理有無を制御する
        this.changedTokuisakiJutakuKbnPoint(e);
    }
    
    /**
     * 顧客区分セレクトボックス変更時イベントにフックする
     */
    changedJutakuKbn = (e, isScriptCall) => {
        
        // 変更された要素の取得
        const currentTarget = e.currentTarget;
        
        // プルダウン生成時の場合
        if (isScriptCall) { 
            // 得意先区分に連動したイベントの場合、顧客区分プルダウンを1行目を強制選択させる
            this.selectFirstIndex(currentTarget);
            // ポイント管理有無を制御する
            this.changedTokuisakiJutakuKbnPoint(e);
            return;
        }
        
        // 得意先ショップコードを制御する
        this.changedTokuisakiJutakuKbn(e);
        // サブレンタル倉庫コードを制御する
        this.changedTokuisakiJutakuKbnSoko(e);
        // ポイント管理有無を制御する
        this.changedTokuisakiJutakuKbnPoint(e);
    }
    
    /**
     * 得意先区分、顧客区分セレクトボックス変更時イベントにフックする
     */
    changedTokuisakiJutakuKbn = (_e) => {
        
        // セレクトボックスの値を取得
        const tokuisakikbnVal = this.$tokuisakikbn.val();
        const jutakudlvrycstkbnVal = this.$jutakudlvrycstkbn.val();

        // 得意先区分が顧客
        if (tokuisakikbnVal === '1') {
            // 顧客区分がショップ顧客時
            if (jutakudlvrycstkbnVal === '2') {
                // リンク、コード制御処理を呼び出し得意先ショップコードを活性化
                this.switchLinkLabel(this.tokuisakiShopObj, true);
                // 名称を表示とし入力可能文言を非表示
                this.$tokuisakishoptext1.show();
                this.$tokuisakishoptext2.hide();
            // 顧客区分がショップ顧客以外
            } else if (jutakudlvrycstkbnVal !== '2') {
                // リンク、コード制御処理を呼び出し得意先ショップコードを非活性化
                this.switchLinkLabel(this.tokuisakiShopObj, false, true);
                // 名称を非表示とし入力可能文言を表示
                this.$tokuisakishoptext1.hide();
                this.$tokuisakishoptext2.show();
            }
        // 得意先区分がショップ
        } else if (tokuisakikbnVal === '2') {
            // リンク、コード制御処理を呼び出し得意先ショップコードを活性化
            this.switchLinkLabel(this.tokuisakiShopObj, true);
            // 名称を表示とし入力可能文言を非表示
            this.$tokuisakishoptext1.show();
            this.$tokuisakishoptext2.hide();
        // 得意先区分が顧客またはショップ顧客以外
        } else {
            // リンク、コード制御処理を呼び出し得意先ショップコードを非活性化
            this.switchLinkLabel(this.tokuisakiShopObj, false, true);
            // 名称を非表示とし入力可能文言を表示
            this.$tokuisakishoptext1.hide();
            this.$tokuisakishoptext2.show();
        }
        
        // ボタン反映委託相手を制御する処理
        // 得意先区分がNA受託または配達受託かつ新規ボタンから遷移した際にのみボタンを活性化する
        if (tokuisakikbnVal !== '5' && tokuisakikbnVal !== '6' && this.$kshnFlag.val() == 'f'
            || this.$kshnFlag.val() =='t') {
            this.$btnhneiitakuaite.prop('disabled', true);
        } else {
            this.$btnhneiitakuaite.prop('disabled', false);
        }
    }
    
    /**
     * 得意先区分、顧客区分セレクトボックス変更時にフックし、サブレンタル倉庫コードを制御する
     */
    changedTokuisakiJutakuKbnSoko = (_e) => {
        
        // セレクトボックスの値を取得
        const tokuisakikbnVal = this.$tokuisakikbn.val();
        const jutakudlvrycstkbnVal = this.$jutakudlvrycstkbn.val();
        
        // ショップ区分が「１」：ショップ
        if (this.$shopkbn.val() === '1') {
            // リンク、コード制御処理を呼び出しサブレンタル倉庫コードを非活性化し処理終了
            this.switchLinkLabel(this.sokoObj, false, true);
            return;
        }
        // ショップ区分が「２」：AP直売
        // 得意先区分が顧客
        if (tokuisakikbnVal === '1') {
            // 顧客区分がショップ顧客時
            if (jutakudlvrycstkbnVal === '2') {
                // リンク、コード制御処理を呼び出しサブレンタル倉庫コードを活性化
                this.switchLinkLabel(this.sokoObj, true);
            // 顧客区分がショップ顧客以外
            } else if (jutakudlvrycstkbnVal !== '2') {
                // リンク、コード制御処理を呼び出しサブレンタル倉庫コードを非活性化
                this.switchLinkLabel(this.sokoObj, false, true);
            }
        // 得意先区分がショップ
        } else if (tokuisakikbnVal === '2') {
            // リンク、コード制御処理を呼び出しサブレンタル倉庫コードを活性化
            this.switchLinkLabel(this.sokoObj, true);
        // 得意先区分が顧客またはショップ顧客以外
        } else {
            // リンク、コード制御処理を呼び出しサブレンタル倉庫コードを非活性化
            this.switchLinkLabel(this.sokoObj, false, true);
        }
    }
    
    /**
     * 得意先区分、顧客区分セレクトボックス変更時イベントにフックし、ポイント管理有無を制御する
     */
    changedTokuisakiJutakuKbnPoint = (_e) => {
        
        // セレクトボックスの値を取得
        const tokuisakikbnVal = this.$tokuisakikbn.val();
        const jutakudlvrycstkbnVal = this.$jutakudlvrycstkbn.val();

        // 得意先区分が顧客
        if (tokuisakikbnVal === '1') {
            // 顧客区分が直売顧客、委託顧客
            if (jutakudlvrycstkbnVal === '1' || jutakudlvrycstkbnVal === '4' || jutakudlvrycstkbnVal === 'a'){
                // ポイント管理有無を活性化
                this.setReadOnly(this.$pointkbn, false);
                this.setReadOnly(this.$pointkbn1, false);
                this.setReadOnly(this.$pointkbn2, false);
            } else {
                // ポイント管理有無の値を「なし」にして非活性化
                $("input[name='saleBean.pointkbn'][value='0']").prop("checked", true);
                this.setReadOnly(this.$pointkbn, true);
                this.setReadOnly(this.$pointkbn1, true);
                this.setReadOnly(this.$pointkbn2, true);
            }
        // 顧客区分が直売顧客以外
        } else {
            // ポイント管理有無の値を「なし」にして非活性化
            $("input[name='saleBean.pointkbn'][value='0']").prop("checked", true);
            this.setReadOnly(this.$pointkbn, true);
            this.setReadOnly(this.$pointkbn1, true);
            this.setReadOnly(this.$pointkbn2, true);
        }
    }
    
    /**
     * 得意先区分セレクトボックス変更時イベントにフックする
     */
    changeUrikeijoChuiAci = (e, isScriptCall) => {
        
        // セレクトボックスの値を取得
        const tokuisakikbnVal = this.$tokuisakikbn.val();
        
        // 得意先区分が「顧客」
        if (tokuisakikbnVal === '1') {
            if (!isScriptCall && e.type == 'change') {
                // 注意内容セレクトボックスの一行目を選択状態とし活性化
                this.$chuitokuisakikbnaci.val('');
            }
            this.$chuitokuisakikbnaci.prop('disabled', false);
            // 売上計上区分ラベルに「売上」を設定
            this.$urikeijokbnnm.text('売上');
        // 得意先区分が「経費以外
        } else if (tokuisakikbnVal === '9') {
            if (!isScriptCall && e.type == 'change') {
                // 注意内容セレクトボックスを「設定なし」とし非活性化
                this.$chuitokuisakikbnaci.val('0');
            }
            this.$chuitokuisakikbnaci.prop('disabled', true);
            // 売上計上区分ラベルに「経費」を設定
            this.$urikeijokbnnm.text('経費');
        // 得意先区分が上記以外
        } else {
            if (!isScriptCall && e.type == 'change') {
                // 注意内容セレクトボックスを「設定なし」とし非活性化
                this.$chuitokuisakikbnaci.val('0');
            }
            this.$chuitokuisakikbnaci.prop('disabled', true);
            // 売上計上区分ラベルに「売上」を設定
            this.$urikeijokbnnm.text('売上');
        }
    }
    
    /**
     * 顧客区分セレクトボックス変更時イベントにフックし、納品金額印字ラジオボタンを制御する
     */
    changeKinotptnashiflg = (_e, isScriptCall) => {
        // プルダウン生成時の場合、スキップ
        if (isScriptCall) { 
            return;
        }
        
        // 顧客区分が「ショップ顧客」以外では処理終了
        if (this.$jutakudlvrycstkbn.val() !== '2') {
            return;
        }
        // 顧客区分が「ショップ顧客」
        // 納品金額印字ラジオボタンを「印字しない」に設定
        this.$kinotptnashiflg.val(['1']);
    }
    
    /**
     * プランセレクトボックス変更時にフックし、初回設定日と更新日を制御
     */
    changedPln = (e) => {
        
        e.stopPropagation();
        // プランセレクトボックスの値を取得
        const selectVal = this.$pln.val();
        // プランが「基本プラン」の場合、初回設定日と継続更新日の項目を非活性化
        if (selectVal === '000') {
            $.each(this.plnObj,(_i, v) => {
                if (v.is(":checkbox")) {
                    v.prop('checked', false);
                    v.prop('disabled', true);
                } else {
                    v.val('');
                    this.setReadOnly(v, true);
                }
            });
        // プランが「子育てアクアプラン」または「２年割プラン」の場合、初回設定日と継続更新日の項目を活性化
        } else {
            $.each(this.plnObj,(_i, v) => {
                if (v.is(":checkbox")) {
                    v.prop('disabled', false);
                } else {
                    this.setReadOnly(v, false);
                }
            });
        }
    }
    
    /**
     * 受注時間指定区分セレクトボックス変更時イベントにフックする
     */
    changedAcptjikanshiteikbn = (e) => {
        
        e.stopPropagation();
        // セレクトボックスの値を取得
        const selectVal =this.$acptjikanshiteikbn.val();
        // 受注時刻開始終了制御処理を呼び出す
        this.propAcptjikan(this.acptjikanshiteikbnObj, selectVal)
    }
    
    /**
     * 得意先マスタ情報受注時間指定区分セレクトボックス変更時イベントにフックする
     */
    changedAcptjikanshiteikbn1 = (e) => {
        
        if (e) {
            e.stopPropagation();
        }
        // セレクトボックスの値を取得
        const selectVal =this.$acptjikanshiteikbn1.val();
        // 受注時刻開始終了制御処理を呼び出す
        this.propAcptjikan(this.acptjikanshiteikbn1Obj, selectVal)
    }
    
    /**
     * 受注時刻開始終了制御処理
     */
    propAcptjikan = (_e, value) => {
        
        // 受注時間指定区分が「指定-頃」または「指定-以降」
        if (value === '5' || value === '6') {
            // 時間指定時刻開始を活性とし時間指定時刻終了を非活性
            _e['str'].prop('disabled', false);
            _e['shry'].prop('disabled', true);
            _e['shry'].val('');
        // 受注時間指定区分が「指定」
        } else if (value === '8') {
            // 時間指定時刻開始と時間指定時刻終了を活性
            _e['str'].prop('disabled', false);
            _e['shry'].prop('disabled', false);
        // 受注時間指定区分が上記以外
        } else {
            // 時間指定時刻開始と時間指定時刻終了を非活性
            _e['str'].prop('disabled', true);
            _e['shry'].prop('disabled', true);
            _e['str'].val('');
            _e['shry'].val('');
        }
        
    }
    
    /**
     * 特別単価区分ラジオボタンの値により親子反映機能チェックボックスを制御する
     */
    changedRknkindtokubetsuuntkbn = (e) => {
        
        e.stopPropagation();
        // 特別単価区分が「あり」
        if ($("*[name='baseBean.rknkindtokubetsuuntkbn']:checked").val() === '1') {
            
            // 請求先コードが未入力または新規登録時
            if (!this.$skysakicd.val() || isNaN(this.$skysakicd.val()) || this.$kshnFlag.val() =='f') {
                // 親子反映機能チェックボックスを活性化し処理終了
                this.$oyakohaneikinoflg.prop('disabled', false);
                return;
            }
            
            // 請求先コードが入力済
            // 親得意先コードを初期化
            this.$oyatokuisakicd.val('');
            // 親得意先コード取得用url定義
            const api = "/api/b0501w02/skysaki/"
                + this.$apkihoncd.val() + "/"
                + this.$shopcd.val() + "/"
                + this.$skysakicd.val();
             
            // apiコール
            $.get(api)
            .then((data) => {
                if (data) {                    
                    this.$oyatokuisakicd.val(data[1]);
                }
                // 請求先マスタ.親得意先コード=画面.得意先コードの場合非活性のまま処理終了
                if (this.$oyatokuisakicd.val() === this.$ptokuisakicd.val()) {
                    // 親子反映機能チェックボックスを非活性
                    this.$oyakohaneikinoflg.prop('disabled', true);
                    this.$oyakohaneikinoflg.prop('checked', false);
                    return;
                }
                // 親子反映機能チェックボックスを活性
                this.$oyakohaneikinoflg.prop('disabled', false);
            })
        // 特別単価区分が「なし」
        } else {
            // 親子反映機能チェックボックスを非活性
            this.$oyakohaneikinoflg.prop('disabled', true);
            this.$oyakohaneikinoflg.prop('checked', false);
        }
    }
    
    /**
     * 請求設定区分ラジオボタンの値により請求先項目を制御する
     */
    changedSkysetteikbn = (e) => {
        
        e.stopPropagation();
        // 請求設定区分が「コード指定」
        if ($("*[name='baseBean.skysetteikbn']:checked").val() === '1') {
            // 請求先コードとリンクを活性化
            this.switchLinkLabel(this.skysakicdObj, true);
        // 請求設定区分が「コード指定」以外
        } else {
            // 請求先コードとリンクを非活性
            this.switchLinkLabel(this.skysakicdObj, false, true);
        }
    }
    
    /**
     * 会員作成有無区分、会員コードの値により会員項目を制御する
     */
    changedKaiincdSakuseiKbn = (e) => {
        e.stopPropagation();
        
        // 得意先反映時にコールされた場合、処理を抜ける
        if (this.isHaneiTokuisaki) {
            this.isHaneiTokuisaki = false;
            return;
        }
        
        // 会員コード、リンク非活性
        this.$kaiincdlink.hide();
        this.$kaiincdlbl.show();
        this.setReadOnly(this.$kaiincd, true);
        
        // 会員作成有無区分ありの場合
        if (this.$kaiincdSakuseiKbn.val()) {
            // 会員コードありの場合
            if (this.$kaiincd.val()) {
                // 会員コード区分非活性
                this.$kaiincdkbn.prop('disabled', true);                
            // 会員コードなしの場合
            } else {
                // 会員コード区分活性
                this.$kaiincdkbn.prop('disabled', false);   
            }
            // 会員マスタ照会(リンク)活性
            this.switchLinkLabel(this.kaiinMstObj, true);
        // 会員作成有無区分なしの場合
        } else {
            // 会員コード区分非活性
            this.$kaiincdkbn.prop('disabled', true);
            // 会員マスタ照会(リンク)非活性
            this.switchLinkLabel(this.kaiinMstObj, false);
        }
    }
    
    /**
     * 会員コード区分ラジオボタンの値により会員項目を制御する
     */
    changedKaiincdkbn = (e) => {

        e.stopPropagation();

        // 会員コード区分が「コード指定」
        if ($("*[name='baseBean.kaiincdkbn']:checked").val() === '1') {
            // 会員コードとリンクを活性化
            this.switchLinkLabel(this.kaiinObj, true, false);
        // 会員コード区分が「コード指定」以外または選択ボタンから遷移時
        } else {
            // 会員コードとリンクを非活性
            this.switchLinkLabel(this.kaiinObj, false, true);
        }
    }
    
    /**
     * 配達区分ラジオボタンの値により配達会社項目を制御する
     */
    changedDlvrykbn = (e) => {
        
        e.stopPropagation();
        // ラジオボタンの値を取得
        const radioVal = $("*[name='deliveryOrderBean.dlvrykbn']:checked").val();
        // 配達会社配達担当者制御処理を呼び出す
        this.propDeliveryTnshaAndCmpny(this.dlvryCmpnyObj, this.dlvryTnshaObj, radioVal);
    }
    
    /**
     * 得意先マスタ情報配達区分ラジオボタンの値により配達担当者項目を制御する
     */
    changedDlvrykbn1 = (e) => {
        
        if (e) {
            e.stopPropagation();
        }
        // ラジオボタンの値を取得
        const radioVal = $("*[name='reservationBean.dlvrykbn1']:checked").val();
        // 配達会社と配達担当者制御処理を呼び出す
        this.propDeliveryTnshaAndCmpny(this.dlvryCmpny1Obj, this.dlvryTnsha1Obj, radioVal);
    }
    
    /**
     * 配達区分ラジオボタンの値に基づく配達会社配達担当者制御処理
     */
    propDeliveryTnshaAndCmpny = (_e, _v, value) => {
        // 配達区分が「他社便」
        if (value === '2') {
            // リンク、コード制御処理を呼び出し配達担当者コードを活性化
            this.switchLinkLabel(_v, true);
            // リンク、コード制御処理を呼び出し配達会社コードを活性化
            this.switchLinkLabel(_e, true);
        // 配達区分が「自社」
        } else if (value === '1') {
            // リンク、コード制御処理を呼び出し配達担当者コードを活性化
            this.switchLinkLabel(_v, true);
            // リンク、コード制御処理を呼び出し配達会社コードを非活性化
            this.switchLinkLabel(_e, false, true);
        // 配達区分が「持ち帰り」
        } else {
            // リンク、コード制御処理を呼び出し配達担当者コードを非活性化
            this.switchLinkLabel(_v, false, true);
            // リンク、コード制御処理を呼び出し配達会社コードを非活性化
            this.switchLinkLabel(_e, false, true);
        }
    }
    
    /**
     * 保証金区分ラジオボタンの値により保証金項目を制御する
     */
    changedHoshokinkbn = (e) => {
        
        e.stopPropagation();
        // 保証金区分が「あり」
        if ($("*[name='saleBean.hoshokinkbn']:checked").val() === '1') {
            // 保証金項目を活性化
            $.each(this.hoshokinObj,(_i, v) => {
                v.prop('disabled', false);
            });
        // 保証金区分が「なし」
        } else {
            // 保証金項目を非活性とし値をクリア
            $.each(this.hoshokinObj,(_i, v) => {
                v.prop('disabled', true);
                v.val('');
            });
        }
    }
    
    /**
     * 売上設定ラジオボタンの値により金額項目を制御する
     */
    changedUrisettei = (e) => {

        if (e) {
            e.stopPropagation();
        }
        
        // 売上設定が「定例売上マスタ」
        if ($("*[name='rentalBean.urisettei']:checked").val() === '0') {
            // 数量を活性化してクリア
            this.$suryo.prop('disabled', false);
            this.$suryo.val('');
            // 単価を活性化し標準価格の値を設定
            this.$unt.prop('disabled', false);
            this.$unt.val(this.$hyojunprice.text());
            // 初回単価を活性化し標準価格の値を設定
            this.$shokaiunt.prop('disabled', false);
            this.$shokaiunt.val(this.$hyojunprice.text());
        // 売上設定が「得意先設置サーバーマスタ」
        } else {
            // 数量を非活性とし０を設定
            this.$suryo.prop('disabled', true);
            this.$suryo.val('0');
            // 単価を非活性とし０を設定
            this.$unt.prop('disabled', true);
            this.$unt.val('0');
            // 初回単価を非活性とし０を設定
            this.$shokaiunt.prop('disabled', true);
            this.$shokaiunt.val('0');
            // 合計金額、消費税に０を設定
            this.$smkin.text('0');
            this.$shohitax.text('0');
            this.$smkin1.text('0');
            this.$shohitax1.text('0');
        }
    }
    
    /**
     * まとめ買い区分ラジオボタンの値により本数を制御する
     */
    changedMtmkisvkbn = (e) => {
        
        e.stopPropagation();
        // まとめ買い区分が「あり」
        if ($("*[name='saleBean.mtmkisvkbn']:checked").val() === '1') {
            // まとめ買い本数を活性化
            this.$mtmkiyuryohns.prop('disabled', false);
            this.$mtmkimuryohns.prop('disabled', false);
        // まとめ買い区分が「なし」
        } else {
            // まとめ買い本数を非活性とし値をクリア
            this.$mtmkiyuryohns.prop('disabled', true);
            this.$mtmkimuryohns.prop('disabled', true);
            this.$mtmkiyuryohns.val('')
            this.$mtmkimuryohns.val('')
        }
    }

     /**
     * まとめ買いポイント区分ラジオボタンの値により「まとめ買い数量」、「まとめ買いポイント数」を制御する
     */
    changedMtmkipointkbn = (e) => {
        
        e.stopPropagation();
        // まとめ買いポイント区分が「あり」
        if ($("*[name='saleBean.mtmkipointkbn']:checked").val() === '1') {
            // 「まとめ買い数量」、「まとめ買いポイント数」項目を活性化
            $.each(this.mtmkipointObj,(_i, v) => {
                v.prop('disabled', false);
            });
        // 保証金区分が「なし」
        } else {
            // 「まとめ買い数量」、「まとめ買いポイント数」項目を非活性とし値をクリア
            $.each(this.mtmkipointObj,(_i, v) => {
                v.prop('disabled', true);
                v.val('');
            });
        }
    }

    /**
     * 入会時特典区分ラジオボタンの値により入会時特典ポイント数を制御する
     */
    changedNyukaitokutenumu = (e) => {
        
        e.stopPropagation();
        // 入会時特典区分が「あり」
        if ($("*[name='saleBean.nyukaitokutenumu']:checked").val() === '1') {
            // 「入会特典ポイント数」項目を活性化
            $.each(this.nyukaitokutenObj,(_i, v) => {
                v.prop('disabled', false);
            });
        // 入会時特典区分が「なし」
        } else {
            // 「入会特典ポイント数」項目を非活性とし値をクリア
            $.each(this.nyukaitokutenObj,(_i, v) => {
                v.prop('disabled', true);
                v.val('');
            });
        }
    }

    /**
     * 更新フラグ変更時イベントにフックする
     */
    changedKshnFlag = (e) => {
        
        e.stopPropagation();
        
        // 新規登録時、更新時でリンクの活性制御を行う
        const isLink = this.$kshnFlag.val() =='f' ? false : true;

        // リンク、コード制御
        this.switchLinkLabel(this.teireiurimasutaObj, isLink); // 定例売上マスタ
        this.switchLinkLabel(this.ttstupwsvmasutaObj, isLink); // 得意先設置サーバーマスタ
        this.switchLinkLabel(this.skysakimasutashuseishokaiObj, isLink); // 請求先マスタ修正・照会
        this.switchLinkLabel(this.iyakukinjohoObj, isLink); // 違約金情報
        this.switchLinkLabel(this.teikiJuchuObj, isLink); // 定期受注登録・照会
        this.switchLinkLabel(this.shoshakazuObj, isLink); // 紹介者数

    }
        
    /**
     * 再入会チェックボックス変更時イベントにフックする
     */
    changedSainyukaifrg = (e) => {
        
        e.stopPropagation();
        // 新規から遷移時は処理終了
        if (this.$kshnFlag.val() =='f') {
            return;
        }
        
        // 再入会チェックボックスがチェックONの場合
        if (this.$sainyukaifrg.is(':checked')) {
            // リンク、コード制御処理を呼び出し反映元得意先コードを活性化
            this.switchLinkLabel(this.haneimototokuisakiObj, true);
            this.$btnhaneitokuisaki.prop('disabled', false);
        // 再入会チェックボックスがチェックOFFの場合
        } else {
            // リンク、コード制御処理を呼び出し反映元得意先コードを非活性化
            this.switchLinkLabel(this.haneimototokuisakiObj, false, true);
            this.$btnhaneitokuisaki.prop('disabled', true);
        }
    }
    
    /**
     * 請求先マスタチェックボックス変更時イベントにフックし、画面項目の活性状態を一括で制御する
     */
    changedSkysakiHnkCheckbox = (e) => {
        
        e.stopPropagation();
        // 新規登録時、全項目を非活性化し処理終了
        if (e.type == EVENTS.MODULE_LOAD_COMPLETE && (
            this.$kshnFlag.val() =='f' || this.$skysakicd.val() == '')) {
            e['data']['checkbox'].prop('disabled', true);
            this.changeMassively(e['data']);
            return;
        }
        
        if (e['data']['checkbox'].is(':checked')) {
            // 一括活性化処理を呼び出す
            this.changeMassively(e['data'], true);
        } else {
            // 一括非活性化処理を呼び出す
            this.changeMassively(e['data']);
        }
    }
    
    /**
     * 定例売上設定チェックボックス変更時イベントにフックし、画面項目の活性状態を一括で制御する
     */
    changedTeireiHnkCheckbox = (e) => {

        e.stopPropagation();
        if (e['data']['checkbox'].is(':checked')) {
            // 一括活性化処理を呼び出す
            this.changeMassively(e['data'], true);
            // 売上設定の値が「得意先設置サーバ管理マスタ」の場合
            if ($("*[name='rentalBean.urisettei']:checked").val() === '1') {
                // 数量、単価、初回単価を非活性にする
                this.$suryo.prop('disabled', true);
                this.$unt.prop('disabled', true);
                this.$shokaiunt.prop('disabled', true);
            }
        } else {
            // 一括非活性化処理を呼び出す
            this.changeMassively(e['data']);
        }
        
    }
    
    /**
     * 得意先マスタ変更チェックボックス変更時イベントにフックし、画面項目の活性状態を一括で制御する
     */
    changedTokuisakiHnkCheckbox = (e) => {
        
        e.stopPropagation();
        // 新規登録時、全項目を非活性化し処理終了
        if (this.$kshnFlag.val() =='f') {
            e['data']['checkbox'].prop('disabled', true);
            this.changeMassively(e['data']);
            return;
        }
        
        if (e['data']['checkbox'].is(':checked')) {
            // 一括活性化処理を呼び出す
            this.changeMassively(e['data'], true);
            // 受注時間指定時刻区分と配達区分による制御処理を呼び出す
            this.changedAcptjikanshiteikbn1();
            this.changedDlvrykbn1();
        } else {
            // 一括非活性化処理を呼び出す
            this.changeMassively(e['data']);
        }
    }
    
    /**
     * 画面項目の活性状態を一括で切り替える処理
     */
    changeMassively = (e, isChecked) => {
        
        // チェックボックスがチェックOFFの場合、入力不可とする
        if (!isChecked) {
            $.each(e,(_i, v) => {
                // 入力項目を非活性とする
                if (v.is(":radio") || v.is(":text") || v.is('select') || v.is('button')) {
                    v.prop('disabled', true);
                // リンクを非活性化する
                } else if (v.attr('class') == 'ref-link') {
                    v.hide();
                } else if (v.attr('class') == 'ref-label') {
                    v.show();
                }
            });
        // チェックボックスがチェックONの場合、入力可とする
        } else {
            $.each(e,(_i, v) => {
                // 入力項目を活性とする
                if (v.is(":radio") || v.is(":text") || v.is('select')|| v.is('button')) {
                    v.prop('disabled', false);
                // リンクを活性化する
                } else if (v.attr('class') == 'ref-link') {
                    v.show();
                } else if (v.attr('class') == 'ref-label') {
                    v.hide();
                }
            });
        }
    }
    
    /**
     * リンク、ラベル表示を切り替える処理
     */
    switchLinkLabel = (_e, isLink, readOnlyMode = false) => {
        
        if (isLink) {
            // リンクを表示しコードを活性化
            _e['link'].show();
            _e['linklbl'].hide();
            if (_e['cd']) {
                this.setReadOnly( _e['cd'], readOnlyMode);
            }
        } else {
            // リンクを非表示しコードを非活性化し値をクリア
            _e['link'].hide();
            _e['linklbl'].show();
            if (_e['cd']) {
                this.setReadOnly( _e['cd'], readOnlyMode);
                _e['cd'].val('');
            }
            if (_e['nm']) {
                _e['nm'].text('');
            }
        }
    }
    
    /**
     * 得意先コードにより得意先マスタから得意先名を取得し、設定する。
     * @param e イベントオブジェクト
     */
     getTokuisakiNm = (e) => {
        
        const $apKihonCd = e.data.apkihoncd;
        const $shopCd = e.data.shopcd;
        const $tokuisakiCd = e.data.cd;
        const $tokuisakiNm = e.data.nm;
        const len = e.data.len;
        
        // 得意先名を初期化
        $tokuisakiNm.text("");

        // 得意先コードが未入力の場合、処理終了
        if (!$tokuisakiCd.val().trim() ||
            isNaN($tokuisakiCd.val())) {
            return;
        }
        
        // 得意先コード0埋め(6文字)
        const tmpTokuisakiCd = $tokuisakiCd.val().trim().padStart(len, '0');
        $tokuisakiCd.val(tmpTokuisakiCd);
        
        // 得意先名取得
        const api = "/api/b0501w02/tokuisakinm/"
            + $apKihonCd.val() + "/"
            + $shopCd.val() + "/"
            + $tokuisakiCd.val();
        // apiコール
        $.get(api)
        .then((data) => {
            if (data) {
                $tokuisakiNm.text(data);
            }
        })
    }
    
    
    /**
     * 委託相手得意先コードにより得意先マスタから委託得意先名を取得し、設定する。
     * @param e イベントオブジェクト
     */
     getItakuTokuisakiNm = (e) => {
        
        const $apKihonCd = e.data.apkihoncd;
        const $shopCd = e.data.shopcd;
        const $tokuisakiCd = e.data.cd;
        const $tokuisakiNm = e.data.nm;
        
        // 得意先名を初期化
        $tokuisakiNm.text("");

        // AP基本コードまたはショップコードまたは得意先コードが未入力の場合、処理終了
        if (!$apKihonCd.val().trim() || !$shopCd.val().trim() || !$tokuisakiCd.val().trim() ||
            isNaN($apKihonCd.val()) || isNaN($shopCd.val()) || isNaN($tokuisakiCd.val())) {
            return;
        }
        
        // AP基本コード0埋め(3文字)
        const tmpApkihonCd = $apKihonCd.val().trim().padStart(3, '0');
        $apKihonCd.val(tmpApkihonCd);
        // ショップコード0埋め(3文字)
        const tmpShopCd = $shopCd.val().trim().padStart(3, '0');
        $shopCd.val(tmpShopCd);
        // 得意先コード0埋め(6文字)
        const tmpTokuisakiCd = $tokuisakiCd.val().trim().padStart(6, '0');
        $tokuisakiCd.val(tmpTokuisakiCd);
        
        // 得意先名取得
        const api = "/api/b0501w02/tokuisakinm/"
            + $apKihonCd.val() + "/"
            + $shopCd.val() + "/"
            + $tokuisakiCd.val();
        // apiコール
        $.get(api)
        .then((data) => {
            if (data) {
                $tokuisakiNm.text(data);
            }
        })
    }
    
    
    /**
     * 倉庫コードにより倉庫マスタから倉庫名を取得し、設定する。
     * @param e イベントオブジェクト
     */
     getSokoNm = (e) => {
        
        const $apKihonCd = e.data.apkihoncd;
        const $shopCd = e.data.shopcd;
        const $sokoCd = e.data.cd;
        const $sokoNm = e.data.nm;
        const len = e.data.len;
        
        // 倉庫名を初期化
        $sokoNm.text("");

        // サブレンタル倉庫コードが未入力の場合、処理終了
        if (!$sokoCd.val().trim() ||
            isNaN($sokoCd.val())) {
            return;
        }
        
        // サブレンタル倉庫コード0埋め(6文字)
        const tmpSokoCd = $sokoCd.val().trim().padStart(len, '0');
        $sokoCd.val(tmpSokoCd);
        
        // 倉庫名取得
        const api = "/api/b0501w02/soko/"
            + $apKihonCd.val() + "/"
            + $shopCd.val() + "/"
            + $sokoCd.val();
        // apiコール
        $.get(api)
        .then((data) => {
            if (data) {
                $sokoNm.text(data);
            }
        })
    }
    
    /**
     * 請求先コードにより請求先マスタから請求先名を取得し、設定する。
     * @param e イベントオブジェクト
     */
    getSkysakiNm = (e) => {
        
        const $apkihoncd = e.data.apkihoncd;
        const $shopcd = e.data.shopcd;
        const $skysakiCd = e.data.cd;
        const $skysakiNm = e.data.nm;
        const $oyaTokuisakiCd = e.data.oyacd;
        const len = e.data.len;
        
        // 請求先名と親得意先コードを初期化
        $skysakiNm.text("");
        $oyaTokuisakiCd.val("");

        // 請求先コードが未入力の場合、処理終了
        if (!$skysakiCd.val().trim() ||
            isNaN($skysakiCd.val())) {
            return;
        }

        // 請求先コード0埋め(6文字)
        const tmpSkysakicd = $skysakiCd.val().trim().padStart(len, '0');
        $skysakiCd.val(tmpSkysakicd);

        // 請求先名取得
        const api = "/api/b0501w02/skysaki/"
            + $apkihoncd.val() + "/"
            + $shopcd.val() + "/"
            + $skysakiCd.val();

        // apiコール
        $.get(api)
        .then((data) => {
            if (data) {
                $skysakiNm.text(data[0]);
                $oyaTokuisakiCd.val(data[1]);
            }
        })
    }
    
    /**
     * 得意先ショップコードによりショップマスタからショップ名を取得し、設定する。
     * @param e イベントオブジェクト
     */
    getShopNm = (e) => {
        
        const $apKihonCd = e.data.apkihoncd;
        const $shopCd = e.data.cd;
        const $shopNm = e.data.nm;
        const len = e.data.len;
        // ショップ名を初期化
        $shopNm.text("");

        // 得意先ショップコードが未入力の場合、処理終了
        if (!$shopCd.val().trim() ||
            isNaN($shopCd.val())) {
            return;
        }
        
        // ショップコード0埋め(3文字)
        const tmpShopCd = $shopCd.val().trim().padStart(len, '0');
        $shopCd.val(tmpShopCd);
        
        // ショップ名取得
        const api = "/api/b0501w02/shop/"
            + $apKihonCd.val() + "/"
            + $shopCd.val();
        // apiコール
        $.get(api, {shopkbn: 1})
        .then((data) => {
            if (data) {
                $shopNm.text(data);
            }
        })
    }
    
    /**
     * 担当者コードから担当者名を取得し、設定する。
     * @param e イベントオブジェクト
     */
     getTnshaNm = (e) => {
        
        const $apKihonCd = e.data.apkihoncd;
        const $shopCd = e.data.shopcd;
        const $tnshaCd = e.data.cd;
        const $tnshaNm = e.data.nm;
        const len = e.data.len;
        
        // 担当者名を初期化
        $tnshaNm.text('');

        // 担当者コードが未入力の場合、処理終了
        if (!$tnshaCd.val().trim() ||
            isNaN($tnshaCd.val())) {
            return;
        }
        
        // 担当者コード0埋め(6文字)
        const tmpTnshaCd = $tnshaCd.val().trim().padStart(len, '0');
        $tnshaCd.val(tmpTnshaCd);
        
        // 担当者名取得
        const api = "/api/b0501w02/tnsha/"
            + $apKihonCd.val() + "/"
            + $shopCd.val() + "/"
            + $tnshaCd.val();
        // apiコール
        $.get(api)
        .then((data) => {
            if (data) {
                $tnshaNm.text(data);
            }
        })
    }
    
    /**
     * 営業エリアコードにより営業エリアマスタから営業エリア名を取得し、設定する。
     * @param e イベントオブジェクト
     */
    getEigyoAreaNm = (e) => {
        
        const $apKihonCd = e.data.apkihoncd;
        const $shopCd = e.data.shopcd;
        const $eigyoAreaCd = e.data.cd;
        const $eigyoAreaNm = e.data.nm;
        const len = e.data.len;
        
        // 営業エリア名を初期化
        $eigyoAreaNm.text("");

        // 営業エリアコードが未入力の場合、処理終了
        if (!$eigyoAreaCd.val().trim() ||
            isNaN($eigyoAreaCd.val())) {
            return;
        }
        
        // 営業エリアコード0埋め(5文字)
        const tmpEigyoAreaCd = $eigyoAreaCd.val().trim().padStart(len, '0');
        $eigyoAreaCd.val(tmpEigyoAreaCd);
        
        // 営業エリア名取得
        const api = "/api/b0501w02/eigyoarea/"
            + $apKihonCd.val() + "/"
            + $shopCd.val() + "/"
            + $eigyoAreaCd.val();
        // apiコール
        $.get(api)
        .then((data) => {
            if (data) {
                $eigyoAreaNm.text(data);
            }
        })
    }
    
    /**
     * 配達管轄AP基本コード変更時に該当名称を取得し、設定する。
     * @param e イベントオブジェクト
     */
    getDlvryKyotenApNm = (e) => {
        
        const $apkihonCd = e.data.apkihoncd;
        const $shopCd = e.data.shopcd;
        const $dlvryKyotenCd = e.data.cd;
        const $apNm = e.data.apnm;
        const $shopNm = e.data.shopnm;
        const $dlvryKyotenNm = e.data.nm;
        const len = e.data.aplen;
        // 配達管轄AP名称を初期化
        $apNm.text("");

        // 配達管轄AP基本コードが未入力の場合、名称をクリアし処理終了
        if (!$apkihonCd.val().trim() ||
            isNaN($apkihonCd.val())) {
            $shopNm.text("");
            $dlvryKyotenNm.text("");
            return;
        }
        // AP基本コード0埋め(3文字)
        const tmpDlvrykankatsuApCd = $apkihonCd.val().trim().padStart(len, '0');
        $apkihonCd.val(tmpDlvrykankatsuApCd);
        
        // AP基本名取得
        const api = "/api/b0501w02/ap/"
            + $apkihonCd.val();
        // apiコール
        $.get(api)
        .then((data) => {
            if (data) {
                $apNm.text(data);
            }
        })
        
        // 配達管轄ショップコードが入力済の場合、ショップマスタからショップ名称を取得する
        if ($shopCd.val().trim() &&
            !isNaN($shopCd.val())) {
            // ショップ名を初期化
            $shopNm.text("");
            // ショップ名取得
            const api = "/api/b0501w02/shop/"
                + $apkihonCd.val() + "/"
                + $shopCd.val();
            // apiコール
            $.get(api)
            .then((data) => {
                if (data) {
                    $shopNm.text(data);
                }
            })
        }
        
        // 配達管轄ショップコードと配達拠点コードが入力済の場合、配達拠点マスタから配達拠点名称を取得する
        if ($shopCd.val().trim() && $dlvryKyotenCd.val().trim() &&
            !isNaN($shopCd.val()) && !isNaN($dlvryKyotenCd.val())) {
            // 配達拠点名を初期化
            $dlvryKyotenNm.text("");
            // 拠点名取得
            const api = "/api/b0501w02/dlvrykyoten/"
                + $apkihonCd.val() + "/"
                + $shopCd.val() + "/"
                + $dlvryKyotenCd.val();
            // apiコール
            $.get(api)
            .then((data) => {
                if (data) {
                    $dlvryKyotenNm.text(data);
                }
            })
        }
    }
    
    /**
     * 配達管轄ショップコードによりショップマスタからショップ名称を取得し、設定する。
     * @param e イベントオブジェクト
     */
    getDlvryKyotenShopNm = (e) => {
        
        const $apkihonCd = e.data.apkihoncd;
        const $shopCd = e.data.shopcd;
        const $dlvryKyotenCd = e.data.cd;
        const $shopNm = e.data.shopnm;
        const $dlvryKyotenNm = e.data.nm;
        const len = e.data.shoplen;
        // 配達管轄ショップ名称を初期化
        $shopNm.text("");

        // 配達管轄AP基本コードまたは配達管轄ショップコードが未入力の場合、名称をクリアし処理終了
        if (!$apkihonCd.val().trim() || !$shopCd.val().trim() ||
            isNaN($apkihonCd.val()) || isNaN($shopCd.val())) {
            $dlvryKyotenNm.text("");
            return;
        }
        // ショップコード0埋め(3文字)
        const tmpDlvrykankatsuShopCd = $shopCd.val().trim().padStart(len, '0');
        $shopCd.val(tmpDlvrykankatsuShopCd);
        
        // ショップ名取得
        const api = "/api/b0501w02/shop/"
            + $apkihonCd.val() + "/"
            + $shopCd.val();
        // apiコール
        $.get(api)
        .then((data) => {
            if (data) {
                $shopNm.text(data);
            }
        })
        // 配達拠点コードが入力済の場合、配達拠点マスタから配達拠点名称を取得する
        if ($dlvryKyotenCd.val().trim() &&
            !isNaN($dlvryKyotenCd.val())) {
            // 配達拠点名を初期化
            $dlvryKyotenNm.text("");
            // 拠点名取得
            const api = "/api/b0501w02/dlvrykyoten/"
                + $apkihonCd.val() + "/"
                + $shopCd.val() + "/"
                + $dlvryKyotenCd.val();
            // apiコール
            $.get(api)
            .then((data) => {
                if (data) {
                    $dlvryKyotenNm.text(data);
                }
            })
        }
    }
    
    /**
     * 配達管轄拠点コードにより配達拠点マスタから拠点名称を取得し、設定する。
     * @param e イベントオブジェクト
     */
    getDlvryKyotenNm = (e) => {
        
        const $apKihonCd = e.data.apkihoncd;
        const $shopCd = e.data.shopcd;
        const $dlvryKyotenCd = e.data.cd;
        const $dlvryKyotenNm = e.data.nm;
        const $dlvryareaCd = e.data.areacd;
        const $dlvryareaNm = e.data.areanm;
        const len = e.data.len;
        // 配達拠点名、配達エリアコード、配達エリア名称を初期化
        $dlvryKyotenNm.text("");
        $dlvryareaCd.val("");
        $dlvryareaNm.text("");        

        // 配達管轄AP基本コード、配達管轄ショップコード、配達拠点コードが未入力の場合、処理終了
        if (!$apKihonCd.val().trim() || !$shopCd.val().trim() || !$dlvryKyotenCd.val().trim() ||
            isNaN($apKihonCd.val()) || isNaN($shopCd.val()) || isNaN($dlvryKyotenCd.val())) {
            return;
        }
        
        // 配達拠点コード0埋め(2文字)
        const tmpKyotenCd = $dlvryKyotenCd.val().trim().padStart(len, '0');
        $dlvryKyotenCd.val(tmpKyotenCd);
        
        // 拠点名取得
        const api = "/api/b0501w02/dlvrykyoten/"
            + $apKihonCd.val() + "/"
            + $shopCd.val() + "/"
            + $dlvryKyotenCd.val();
        // apiコール
        $.get(api)
        .then((data) => {
            if (data) {
                $dlvryKyotenNm.text(data);
            }
        })
    }
    
    /**
     * エージェントコードによりエージェントマスタからエージェント名を取得し、設定する。
     * @param e イベントオブジェクト
     */
    getEjntNm = (e) => {
        
        let apKihonCd;
        let shopCd;
        const $ejntCd = e.data.cd;
        const $ejntNm = e.data.nm;
        const len = e.data.len;
        const honbuflg = e.data.honbuflg;
        
        // 本部エージェント名取得の際は固定値が渡されてるくるためそのまま定数に設定する
        if (e.data.honbuflg == 't') {
            apKihonCd = e.data.apkihoncd
            shopCd = e.data.shopcd
        } else {
            apKihonCd = e.data.apkihoncd.val();
            shopCd = e.data.shopcd.val();
        }
        
        // エージェント名を初期化
        $ejntNm.text("");
        if (e.data.honbuflg == 'f') {
            this.$ejntmgnn1.text('');
            this.$ejntmgnn2.text('');
        }

        // エージェントコードが未入力の場合、処理終了
        if (!$ejntCd.val().trim() ||
            isNaN($ejntCd.val())) {
            return;
        }
        
        // エージェントコード0埋め(6文字)
        const tmpEjntCd = $ejntCd.val().trim().padStart(len, '0');
        $ejntCd.val(tmpEjntCd);
        
        // エージェント名取得
        const api = "/api/b0501w02/ejnt/"
            + apKihonCd + "/"
            + shopCd + "/"
            + $ejntCd.val();
        // apiコール
        $.get(api, {honbuflg: honbuflg})
        .then((data) => {
            if (data) {
                $ejntNm.text(data[0]);
                // 獲得エージェント名取得の際は社内担当者コードも画面に設定する
                if (honbuflg == 'f') {
                    this.$ejntmgnn1.text(data[1]);
                    this.$ejntmgnn2.text(data[2]);
                }
            }
        })
    }
    
    /**
     * キャンペーンコードによりキャンペーンマスタからキャンペーン名を取得し、設定する。
     * @param e イベントオブジェクト
     */
    getKynpnNm = (e) => {
        
        let apKihonCd;
        let shopCd;
        const $kynpnCd = e.data.cd;
        const $kynpnNm = e.data.nm;
        
        // 獲得本部キャンペーン名取得の際は固定値が渡されてるくるためそのまま定数に設定する
        if (e.data.inptkbn === '0') {
            apKihonCd = e.data.apkihoncd
            shopCd = e.data.shopcd
        } else {
            apKihonCd = e.data.apkihoncd.val();
            shopCd = e.data.shopcd.val();
        }
        
        // キャンペーン名を初期化
        $kynpnNm.text("");

        // キャンペーンコードが未入力の場合、処理終了
        if (!$kynpnCd.val().trim() ||
            isNaN($kynpnCd.val())) {
            return;
        }
        
        // キャンペーン名取得
        const api = "/api/b0501w02/kynpn/"
            + apKihonCd + "/"
            + shopCd + "/"
            + $kynpnCd.val();
            
        // apiコール
        $.get(api)
        .then((data) => {
            if (data) {
                $kynpnNm.text(data);
            }
        })
    }
    
    /**
     * 景品区分プルダウンを取得し、設定する。
     * @param e イベントオブジェクト
     */
    getKynpnkeihinkbnPulldown = (e) => {
        let apKihonCd;
        let shopCd;
        const $kynpnCd = e.data.cd;
        const $kynpnkeihinkbn = e.data.kynpnkeihinkbn;
        const td = e.data.td;
      
        // 景品区分プルダウンをクリアする
        $kynpnkeihinkbn.children().remove();
        
        if (!$kynpnCd.val() || isNaN($kynpnCd.val())) {
            return;
        }
        
        // 獲得本部キャンペーン名取得の際は固定値が渡されてるくるためそのまま定数に設定する
        if (e.data.inptkbn === '0') {
            apKihonCd = e.data.apkihoncd;
            shopCd = e.data.shopcd;
        } else {
            apKihonCd = e.data.apkihoncd.val();
            shopCd = e.data.shopcd.val();
        }
        
        $kynpnkeihinkbn.attr('class', 'aci-ajax-parts');
        
        const dataApi = "/api/parts/s02m088t/" + 
            apKihonCd + "/" + 
            shopCd + "/" + 
            $kynpnCd.val();
            
        // api属性設定
        $kynpnkeihinkbn.attr("data-api", dataApi);

        // キャッシュのapi属性を変更
        // ※変更しないと古いapi定義のままコールされてしまう
        $kynpnkeihinkbn.data("api", dataApi);
        
        // プルダウン初期化
        new AciAjaxParts().init($('#' + td));
    }
    
    /**
     * 景品区分プルダウン変更時のイベントにフックする
     */
    changedKynpnkeihinkbn = (e, isScriptCall) => {
        
        // 初期表示時の場合、スキップする
        if (isScriptCall) {
            return;
        }
        
        const $target = $(e.currentTarget);
        
        // キャッシュのdefault属性を変更
        $target.data("default", $target.val());
    }
    
    /**
     * 配達会社コードにより配達会社マスタから配達会社名を取得し、設定する。
     * @param e イベントオブジェクト
     */
    getDlvryCmpnyNm = (e) => {
        
        const $apKihonCd = e.data.apkihoncd;
        const $shopCd = e.data.shopcd;
        const $dlvryCmpnyCd = e.data.cd;
        const $dlvryCmpnyNm = e.data.nm;
        const len = e.data.len;
        
        // 配達会社名を初期化
        $dlvryCmpnyNm.text("");

        // 配達会社コードが未入力の場合、処理終了
        if (!$dlvryCmpnyCd.val().trim() ||
            isNaN($dlvryCmpnyCd.val())) {
            return;
        }
        
        // 配達会社コード0埋め(3文字)
        const tmpDlvryCmpnyCd = $dlvryCmpnyCd.val().trim().padStart(len, '0');
        $dlvryCmpnyCd.val(tmpDlvryCmpnyCd);
        
        // 配達会社名取得
        const api = "/api/b0501w02/dlvrycmpny/"
            + $apKihonCd.val() + "/"
            + $shopCd.val() + "/"
            + $dlvryCmpnyCd.val();
        // apiコール
        $.get(api)
        .then((data) => {
            if (data) {
                $dlvryCmpnyNm.text(data);
            }
        })
    }
    
    /**
     * 獲得詳細コードにより得意先獲得詳細マスタから獲得詳細名を取得し、設定する。
     * @param e イベントオブジェクト
     */
    getHansokuShsNm = (e) => {
        
        const $apKihonCd = e.data.apkihoncd;
        const $shopCd = e.data.shopcd;
        const $hansokuShsCd = e.data.cd;
        const $hansokuShsNm = e.data.nm;
        const len = e.data.len;
        // 獲得詳細名を初期化
        $hansokuShsNm.text("");

        // 獲得詳細コードが未入力の場合、処理終了
        if (!$hansokuShsCd.val().trim() ||
            isNaN($hansokuShsCd.val())) {
            return;
        }
        
        // 獲得詳細コード0埋め(4文字)
        const tmpHansokuShsCd = $hansokuShsCd.val().trim().padStart(len, '0');
        $hansokuShsCd.val(tmpHansokuShsCd);
        
        // 獲得詳細名取得
        const api = "/api/b0501w02/hansokushs/"
            + $apKihonCd.val() + "/"
            + $shopCd.val() + "/"
            + $hansokuShsCd.val();
        // apiコール
        $.get(api)
        .then((data) => {
            if (data) {
                $hansokuShsNm.text(data);
            }
        })
    }
    
    /**
     * 配達エリアコードにより配達エリアマスタから配達エリア名を取得し、設定する。
     * @param e イベントオブジェクト
     */
    getDlvryAreaNm = (e) => {
        
        const $apKihonCd = e.data.apkihoncd;
        const $shopCd = e.data.shopcd;
        const $dlvryKankatsuKyotenCd = e.data.dlvrykankatsukyotencd;
        const $dlvryAreaCd = e.data.cd;
        const $dlvryAreaNm = e.data.nm;
        const len = e.data.len;
        
        // 配達エリア名を初期化
        $dlvryAreaNm.text("");

        // 配達拠点AP基本コード、配達拠点ショップコード、配達エリアコード、配達管轄拠点コードが未入力の場合、処理終了
        if (!$apKihonCd.val().trim() || !$shopCd.val().trim()
            || !$dlvryAreaCd.val().trim() || !$dlvryKankatsuKyotenCd.val().trim() ||
            isNaN($apKihonCd.val()) || isNaN($shopCd.val()) ||
            isNaN($dlvryAreaCd.val()) || isNaN($dlvryKankatsuKyotenCd.val())) {
            return;
        }
        
        // 配達エリアコード0埋め(5文字)
        const tmpDlvryAreaCd = $dlvryAreaCd.val().trim().padStart(len, '0');
        $dlvryAreaCd.val(tmpDlvryAreaCd);
        
        // 配達エリア名取得
        const api = "/api/b0501w02/dlvryarea/"
            + $apKihonCd.val() + "/"
            + $shopCd.val() + "/"
            + $dlvryKankatsuKyotenCd.val() + "/"
            + $dlvryAreaCd.val();
        // apiコール
        $.get(api)
        .then((data) => {
            if (data) {
                $dlvryAreaNm.text(data);
            }
        })
    }
    
    /**
     * ワンショットコードによりマージンマスタからマージン名を取得し、設定する。
     * @param e イベントオブジェクト
     */
    getMgnNm = (e) => {
        
        const $apKihonCd = e.data.apkihoncd;
        const $shopCd = e.data.shopcd;
        const $mgnCd = e.data.cd;
        const $mgnNm = e.data.nm;
        
        // 配達エリア名を初期化
        $mgnNm.text("");

        // ワンショットコードが未入力の場合、処理終了
        if (!$mgnCd.val().trim() ||
            !$mgnCd.val().match(/^[A-Za-z0-9]*$/)) {
            return;
        }
        
        // 配達エリア名取得
        const api = "/api/b0501w02/mgncd/"
            + $apKihonCd.val() + "/"
            + $shopCd.val() + "/"
            + $mgnCd.val();
        // apiコール
        $.get(api)
        .then((data) => {
            if (data) {
                $mgnNm.text(data);
            }
        })
    }
    
    /**
     * 定例売上マスタ（検索画面）を表示する
     * @param e イベントオブジェクト
     */
    clickTeireiuriMasuta = (e) => {
        
        e.stopPropagation();
        e.preventDefault();
        
        // 表示する画面情報を取得
        const $target = $(e.currentTarget);
        const urlId = $target.data('url');
        // パラメーターの値を取得
        const apkihoncd = e.data.apkihoncd.val();
        const shopcd = e.data.shopcd.val();
        const cd = e.data.cd.val();
        const data = {apkihoncd:apkihoncd, shopcd:shopcd, tokuisakicd:cd};
        // 画面表示処理をコール
        this.openWindow(null, urlId, cd ? data : null);
    }
    
    /**
     * 得意先マスタ（検索画面）を表示する
     * @param e イベントオブジェクト
     */
    clickTtstupwsvMasuta = (e) => {
        
        e.stopPropagation();
        e.preventDefault();
        
        // 表示する画面情報を取得
        const $target = $(e.currentTarget);
        const urlId = $target.data('url');
        // パラメーターの値を取得
        const apkihoncd = e.data.apkihoncd.val();
        const shopcd = e.data.shopcd.val();
        const cd = e.data.cd.val();
        const jigyoshokanri = this.$jigyoshokanri.val();
        const data = {apkihoncd:apkihoncd, shopcd:shopcd,kinoid:'b0501w02',jigyoshokanri:jigyoshokanri,kinoflag:'1',tokuisakicd:cd};
        // 画面表示処理をコール
        this.openWindow(null, urlId, cd ? data : null);
    }
    
    /**
     * 請求先マスタ（検索画面）を表示する
     * @param e イベントオブジェクト
     */
    clickSkysakiMasuta = (e) => {
        
        e.stopPropagation();
        e.preventDefault();
        
        // 表示する画面情報を取得
        const $target = $(e.currentTarget);
        const urlId = $target.data('url');
        // パラメーターの値を取得
        const apkihoncd = e.data.apkihoncd.val();
        const shopcd = e.data.shopcd.val();
        const cd = e.data.cd.val();
        const data = {apkihoncd:apkihoncd, shopcd:shopcd, skysakicd:cd};
        // 画面表示処理をコール
        this.openWindow(null, urlId, cd ? data : null);
    }
    
    /**
     * 会員マスタ照会画面を表示する
     * @param e イベントオブジェクト
     */
    clickKaiinMasuta = (e) => {
        
        e.stopPropagation();
        e.preventDefault();
        
        // 表示する画面情報を取得
        const $target = $(e.currentTarget);
        const urlId = $target.data('url');
        // パラメーターの値を取得
        const cd = e.data.cd.val();
        // 表示画面は未作成
        const param = {kaiincd:cd};
        
        // 会員コードチェック
        const api = "/api/b0501w02/checkkaiin";
        
        // apiコール
        $.get(api, {kaiincd:cd})
        .then((data) => {
            if (data) {
                // 画面表示処理をコール
                this.openWindow(param, urlId + '/' + cd);
            }
        }) 
    }
    
    /**
     * 得意先違約金設定画面を表示する
     * @param e イベントオブジェクト
     */
    clickIyakukinJoho = (e) => {
        
        e.stopPropagation();
        e.preventDefault();
        
        // 表示する画面情報を取得
        const $target = $(e.currentTarget);
        const urlId = $target.data('url');
        // パラメーターの値を取得
        const apkihoncd = e.data.apkihoncd.val();
        const shopcd = e.data.shopcd.val();
        const cd = e.data.cd.val();
        const data = {apkihoncd:apkihoncd, shopcd:shopcd, tokuisakicd:cd};
        // 画面表示処理をコール
        this.openWindow(null, urlId, cd ? data : null);
    }
    
    /**
     * 紹介者情報画面を表示する
     * @param e イベントオブジェクト
     */
    clickShoshaJoho = (e) => {
        
        e.stopPropagation();
        e.preventDefault();
        
        // 表示する画面情報を取得
        const $target = $(e.currentTarget);
        const urlId = $target.data('url');
        // パラメーターの値を取得
        const apkihoncd = e.data.apkihoncd.val();
        const shopcd = e.data.shopcd.val();
        const cd = e.data.cd.val();
        let aciFlg = this.$b0501w02kihonkbn.val() === '1' ? true :false
        const data = {aciflg: aciFlg};
        // エンドポイントとして使用するデータをオブジェクトとして定義
        const urlData = {mode:'4',apkihoncd:apkihoncd, shopcd:shopcd, tokuisakicd:cd};
        // 画面表示処理をコール
        this.openWindow(data, urlId, urlData);
    }
    
    /**
     * 定期受注マスタ（検索画面）を表示する
     * @param e イベントオブジェクト
     */
    clickTeikiacptMasuta = (e) => {
        
        e.stopPropagation();
        e.preventDefault();
        
        // 表示する画面情報を取得
        const $target = $(e.currentTarget);
        const urlId = $target.data('url');
        // パラメーターの値を取得
        const apkihoncd = e.data.apkihoncd.val();
        const shopcd = e.data.shopcd.val();
        const cd = e.data.cd.val();
        const data = {apkihoncd:apkihoncd, shopcd:shopcd, tokuisakicd:cd};
        // 画面表示処理をコール
        this.openWindow(null, urlId, cd ? data : null);
    }
    
    /**
     * windowを作成し、画面を表示する。
     * @param param リクエストパラメータ
     * @param id url情報
     * @param urlObj エンドポイント情報
     */
    openWindow = (param, id, urlObj) => {        
        // URLを生成
        let parseUrl = id;
        // エンドポイントとして使用するデータが存在する場合、urlに追加
        if (urlObj) {
            $.each(urlObj,(_i, v) => {
                parseUrl = parseUrl +'/' + v;
            });
        }

        return openProcessSubWindow(parseUrl, $.param(param));
    }
    
    /**
     * 事業所プルダウンロード時のイベントにフックする
     * @param _e イベントオブジェクト
     * @param _data プルダウンのデータ
     */
    jigyoshocdLoadComplete = (_e,_data) => {
        const $target = _e.target;
        // 事業所管理ありのショップの場合
        if (this.$jigyoshokanri.val() == '1') {
            // アクセス範囲区分≠4（事業所以外）の場合
            if (this.$akshanikbn.val() != '4') {
                // 事業所プルダウンの1行目の空白を削除する
                $($("*[id='" + $target.id + "']")).children('option:first-child').remove();
            }
        // 事業所管理なしのショップの場合
        } else {
            // 事業所コード：先頭ブランク行の値に00を設定する
            $($("*[id='" + $target.id + "']")).children('option:first-child').val('00');
        }
    }
    
     /**
     * 商品プルダウンロード時のイベントにフックする
     * @param _e イベントオブジェクト
     * @param _data プルダウンのデータ
     */
    shncdLoadComplete = (_e,_data) => {
        if (!this.shnloaded && this.$teireihasseino.val() == '') {
            // 標準価格を取得する
            this.getHyojunprice();
            this.shnloaded = true;
        }        
    }
    
     /**
     * 商品プルダウン変更時のイベントにフックする
     * @param _e イベントオブジェクト
     */
    shnmeiChanged = (_e,isScriptCall) => {
        // 初期表示時でない場合
        if (!isScriptCall) {
            // 標準価格を取得する
            this.getHyojunprice();
        }
    }
    
     /**
     * 標準価格を取得する
     */
    getHyojunprice = () => {
        
        const apKihonCd = this.$apkihoncd.val();
        const shopCd = this.$shopcd.val();
        const shnCd = this.$shnmei.val();
     
        // 標準価格取得
        const api = "/api/b0501w02/hyojunprice/"
            + apKihonCd + "/"
            + shopCd + "/"
            + shnCd;
        
        // apiコール
        $.get(api)
        .then((data) => {            
            if (data !== null && data.length > 0) {
                const price = separateWithComma(data[0]);
                this.$shntaxkbn.val(data[1]); // 税区分
                this.$hyojunprice.text(price); // 標準価格
                this.$shokaiunt.val(price); // 初回単価
                // 売上設定＝0(定例売上マスタ)の場合
                if ($("*[name='rentalBean.urisettei']:checked").val() === '0') {
                    this.$unt.val(price); // 単価    
                    // 単価の合計金額、消費税額を設定する
                    this.calcKingaku(this.untTaxCalcObj);
                    // 初回単価の合計金額、消費税額を設定する
                    this.calcKingaku(this.untShokaiTaxCalcObj);
                }
            }
        })
    }
    
    /**
     * 郵便番号反映ボタン押下時のイベントにフックする
     */
    clickYubinnoHanei = (e) => {
        
        e.stopPropagation();
        e.preventDefault();
        
        // 住所情報取得
        const api = "/api/b0501w02/address";
        
        // apiコール
        $.get(api, {yubinno: e.data.yubinno.val(), name: e.data.name, label: e.data.label})
        .then((data) => {            
            if (data) {
                // APIで取得した件数が1件の場合
                if (data.length == 1) {
                    // 取得した内容を反映
                    e.data.adrsn1.val(data[0].tdoufknmei + data[0].shikutyosonmei);
                    e.data.adrsn2.val(data[0].tyoikimei);
                    e.data.adrsn3.val([]);
                    if (e.data.chihoupubdanticdn1 !== undefined) {
                        e.data.chihoupubdanticdn1.val(data[0].chihoupubdanticdn1);
                        e.data.chihoupubdanticdn2.val(data[0].chihoupubdanticdn2);
                    }
                // APIで取得した件数が2件以上の場合
                } else if (data.length > 1) {
                    // 郵便番号検索画面をコールする
                    const params = {
                        "kinoid":"b0501w02",
                        "cdkensakuflg":"1",
                        "yubinno":e.data.yubinno.val()
                        };

                    // 適用する値設定用関数
                    const callbackFunction = function(data2) {
                        e.data.adrsn1.val(data2.tdoufkn + data2.shikutyoson);
                        e.data.adrsn2.val(data2.tyoiki);
                        e.data.adrsn3.val([])
                        if (e.data.chihoupubdanticdn1 !== undefined) {
                            e.data.chihoupubdanticdn1.val(data2.chihoupubdanticdn1);
                            e.data.chihoupubdanticdn2.val(data2.chihoupubdanticdn2);
                        }
                    };
                    const queryParams = $.param(params);
            
                    openDataSelectWindow(callbackFunction,"/z0512w01?" + queryParams);
                }
            }
        })      
    }
    
    /**
     * プラン日付オンフォーカスイベントにフックする。
     * @param e イベントオブジェクト
     */
    focusPlandd = (e) => {
        e.stopPropagation();
        e.preventDefault();
            
        const $date = e.data.date;
        const $datebefore = e.data.datebefore;
        
        // 変更前日付を退避
        $datebefore.val($date.val().trim());
    }
    
    /**
     * プラン日付変更イベントにフックする。
     * @param e イベントオブジェクト
     */
    changePlandd = (e) => {
        
        const $date = e.data.date;
        const $datebefore = e.data.datebefore;
        const $iyakukinCheck = e.data.iyakukinCheck;

        // 変更前が未入力の場合
        if ($datebefore.val() == '' &&
            $date.val().trim().length > 0) {
            // 違約金チェックボックスをONにする
            $iyakukinCheck.prop("checked", true);
        }
    }
    
    /**
     * 開始年月ロストフォーカスイベントにフックする。
     * @param e イベントオブジェクト
     */
    blurStrtdydm = (_e) => {
        // 開始年月を入力し、次回計上年月が空欄の場合
        if (this.$strtdydm.val().trim() != '' &&
            this.$jikaikeijodydm.val().trim() == '') {
            // 次回計上年月に開始年月の値を設定する
            this.$jikaikeijodydm.val(this.$strtdydm.val());
        }
    }
    
    /**
     * 単価、数量、計上日付を元に再計算する
     * @param e イベントオブジェクト
     */
    recalcKingaku = (e) => {
        this.calcKingaku(e.data);
    }
    
    /**
     * 単価、数量、計上日付を元に合計金額、消費税を計算する
     * @param data 消費税計算用のデータ
     */
    calcKingaku = (data) => {
        
        const suryo = data.suryo.val().replaceAll(",","");
        const unt = data.unt.val().replaceAll(",","");
        const keijodd = data.keijodd.val();
        const dydm = data.dydm.val().trim().replaceAll("/","");
        const $smkin = data.smkin;
        const $shohitax = data.shohitax;
 
        // 数値が入力されていない場合
        if (suryo == '' || unt == '' || isNaN(suryo) || isNaN(unt)) {
            // 合計金額、消費税をクリアする
            $smkin.text([]);
            $shohitax.text([]);
            return;
        }
   
        // 計上年月、計上日が未入力の場合
        if (keijodd == '' || dydm == '') {
            // 計算基準日に運用日付を設定
            this.$taxCalcDate.val(this.$genzaidd.val());
        // 入力ありの場合
        } else {
            const date = dydm + keijodd;
            // 入力日付が不正の場合
            if (isNaN(date)) {
                // 計算基準日に運用日付を設定
                this.$taxCalcDate.val(this.$genzaidd.val());
            } else {
                // 計算基準日に計上年月＋計上日を設定
                this.$taxCalcDate.val(date);                
            }
        }

        // 価格情報初期化
        const price_obj = new CalcPrice('taxCalcDate', this.$priceinfo.val());

        // 合計金額
        let zeinuki = price_obj.kinRound(parseFloat((unt * suryo).toFixed(2)));
        $smkin.text(separateWithComma(zeinuki));       

        // 消費税
        let tax = this.calcTax(price_obj, unt, suryo, zeinuki);
        $shohitax.text(separateWithComma(tax));
    }
    
    /**
     * 消費税を計算する
     * @param price_obj 価格情報
     * @param unt 単価
     * @param suryo 数量
     * @param kin 税抜き金額
     * @returns　消費税額     
     */
    calcTax = (price_obj, unt, suryo, kin) => {
        
        const taxkbn = this.$shntaxkbn.val();

        // 商品税区分が2:非課税 3:不課税の場合は0円で返却する
        // 売上(税設定しない場合は商品税区分が空白なので税計算を行う)
        if (price_obj.info.type == '1') {
            if (taxkbn == '2' || taxkbn == '3') {
                return 0;
            }
        }

        // 単価が0円の場合は金額から消費税を計算する
        if (unt == null || unt == 0) {
            return price_obj.calcTaxEx(kin, taxkbn);
        }

        // 外税の場合
        if (this.getKeisanKbn(price_obj) == '2') {
            return price_obj.calcTaxEx(kin, taxkbn);
        // 内税の場合
        } else {
            // 単価消費税計算
            let tax = price_obj.calcTaxEx(unt, taxkbn);

            // 税込金額（（単価 ＋ 単品消費税） × 数量）
            let komikin = price_obj.kinRound(parseFloat((calc_add_impl(unt, tax) * suryo).toFixed(2)));

            // 内税の場合は（単品単価＋単品消費税）×数量より単価×数量を引いたものを消費税金額とし、
            // 税込金額-消費税金額を金額とする
            return komikin - kin;
        }
    }
    
    /**
     * 計算区分を取得する
     * @param price_obj 価格情報
     * @returns　計算区分     
     */
    getKeisanKbn = (price_obj) => {
        
        const tokuisakikbn = this.$tokuisakikbn.val();
        
        // 売上仕入区分＝1(売上)の場合
        if (price_obj.info.type == '1') {
            // 卸の場合
            if (checkOroshi(tokuisakikbn)) {
                return '2'; // 税計算区分＝2(外税)
            } else {
                return getCurrent(
                    (price_obj.dateItm == null) ? "" : getItmValue(price_obj.dateItm),
                     price_obj.info.taxkeisankbnurikirikaedd,
                     price_obj.info.genztaxkeisankbnuri,
                     price_obj.info.shintaxkeisankbnuri);
             }
        } else {
            return price_obj.info.taxkeisankbn;
        }
    }
    
    /**
     * 得意先反映ボタンクリックイベントにフックする。
     * @param e イベントオブジェクト
     */
    #clickHaneiTokuisakiBtn = (_e) => {
        
        const $apKihonCd = this.$apkihoncd;
        const $shopCd = this.$shopcd;
        const $haneimototokuisakicd = this.$haneimototokuisakicd;
       
        // ショップ名取得
        const api = "/api/b0501w02/haneitokuisaki/"
            + $apKihonCd.val() + "/"
            + $shopCd.val()
            ;
        // apiコール
        $.get(api, {tokuisakicd: $haneimototokuisakicd.val()})
        .then((data) => {
            if (data) {
                this.isHaneiTokuisaki = true;
                // 得意先情報を画面に設定する
                this.setHaneimotoTokuisakiInfo(data);
                // 活性制御の再設定のためモジュールロードイベントを発生させる
                $ACI_EVENT_ELE.trigger(EVENTS.MODULE_LOAD_COMPLETE);

            }
        })
    }
    
    /**
     * 反映元得意先情報を設定する。
     * @param data APIで取得した得意先情報
     */
    setHaneimotoTokuisakiInfo = (data) => {
        
        const $base = data.base_bean;
        const $deli = data.delivery_order_bean;
        
        // 基本情報
        this.$tokuisakikbn.val($base.tokuisakikbn); // 得意先区分
        this.$jutakudlvrycstkbn.val($base.jutakudlvrycstkbn); // 顧客区分
        this.$jutakudlvrycstkbn.attr
            ("data-default", $base.jutakudlvrycstkbn).trigger(EVENTS.ACI_LOAD);
        this.$cdkindkbn.val($base.cdkindkbn); // コード種別
        this.$cdkindkbn.attr("data-default", $base.cdkindkbn).trigger(EVENTS.ACI_LOAD);
        this.$gyoshukbn.val($base.gyoshukbn); // 業種区分
        this.$gyoshukbn.attr("data-default", $base.gyoshukbn).trigger(EVENTS.ACI_LOAD);
        this.$gyoshushskbn.val($base.gyoshushskbn); // 業種詳細区分
        this.$gyoshushskbn.attr("data-default", $base.gyoshushskbn).trigger(EVENTS.ACI_LOAD);
        this.$riyowsvkbn.val($base.riyowsvkbn); // 利用サーバー区分
        this.$jigyoshocd.val($base.jigyoshocd); // 事業所コード
        this.$jigyoshocd.attr("data-default", $base.jigyoshocd).trigger(EVENTS.ACI_LOAD);
        this.$yubinno.val($base.yubinno); // 郵便番号
        this.$adrsn1.val($base.adrsn1);　// 住所１
        this.$adrsn2.val($base.adrsn2); // 住所２
        this.$adrsn3.val($base.adrsn3); // 住所３
        this.$chihoupubdanticdn1.val($base.chihoupubdanticdn1); // 地方公共団体コード１
        this.$chihoupubdanticdn2.val($base.chihoupubdanticdn2); // 地方公共団体コード２

        // 営業管理情報
        this.$eigyoareacd.val($deli.eigyoareacd); // 営業エリアコード
        this.$eigyoareanm.text($deli.eigyoareanm); // 営業エリア名称
        this.$eigyotnshacd.val($deli.eigyotnshacd); // 営業担当者コード
        this.$eigyotnshanm.text($deli.eigyotnshanm); // 営業担当者名称
        // 売上計上担当者区分
        if ($deli.urikeijotnshakbn == '0') {
            $("*[id='deliveryOrderBean.urikeijotnshakbn1']").prop('checked', true);
        } else if ($deli.urikeijotnshakbn == '1') {
            $("*[id='deliveryOrderBean.urikeijotnshakbn2']").prop('checked', true);
        } else if ($deli.urikeijotnshakbn == '2') {
            $("*[id='deliveryOrderBean.urikeijotnshakbn3']").prop('checked', true);
        }
        
        // 配達情報
        this.$dlvrykankatsuapkihoncd.val($deli.dlvrykankatsuapkihoncd); // 配達拠点AP基本コード
        this.$dlvrykankatsushopcd.val($deli.dlvrykankatsushopcd); // 配達拠点ショップコード
        this.$apnm2.text($deli.apnm2); // 配達拠点ＡＰ名称
        this.$shopnm2.text($deli.shopnm2); // 配達拠点ショップ名称
        this.$dlvrykankatsukyotencd.val($deli.dlvrykankatsukyotencd); // 配達管轄拠点コード
        this.$kyotennm.text($deli.kyotennm); // 配達管轄拠点名称
        // 配達区分
        if ($deli.dlvrykbn == '1') {
            $("*[id='deliveryOrderBean.dlvrykbn1']").prop('checked', true);
        } else if ($deli.dlvrykbn == '2') {
            $("*[id='deliveryOrderBean.dlvrykbn2']").prop('checked', true);
        } else if ($deli.dlvrykbn == '3') {
            $("*[id='deliveryOrderBean.dlvrykbn3']").prop('checked', true);
        }
        this.$dlvrycmpnycd.val($deli.dlvrycmpnycd); // 配達会社コード
        this.$dlvrycmpnynm.text($deli.dlvrycmpnynm); // 配達会社名称
        this.$dlvryareacd.val($deli.dlvryareacd); // 配達エリアコード
        this.$dlvryareanm.text($deli.dlvryareanm); // 配達エリア名称
        this.$dlvrytnshacd.val($deli.dlvrytnshacd); // 配達担当者コード
        this.$dlvrytnshamei.text($deli.dlvrytnshamei); // 配達担当者名
        this.$dlvryjun.val($deli.dlvryjun); // 配達順
        this.$chizuno.val($deli.chizuno); // 地図番号
        this.$gaiburenkeidlvrycd.val($deli.gaiburenkeidlvrycd); // 外部連携配達コード
            
        // 再入会チェックボックスがチェックONの場合
        if (this.$sainyukaifrg.is(':checked')) {
            // 基本情報
            this.$skysakicd.val($base.skysakicd); // 請求先コード
            this.$skysakinmn1.text($base.skysakinmn1); // 請求先名称
            // 請求設定区分
            if ($base.skysetteikbn == '0') {
                $("*[id='baseBean.skysetteikbn1']").prop('checked', true);
            } else if ($base.skysetteikbn == '1') {
                $("*[id='baseBean.skysetteikbn2']").prop('checked', true);
            } else if ($base.skysetteikbn == '2') {
                $("*[id='baseBean.skysetteikbn3']").prop('checked', true);
            }
            this.$tokuisakishopcd.val($base.tokuisakishopcd); // 得意先ショップコード
            this.$tokuisakishopnm.text($base.tokuisakishopnm); // 得意先ショップ名称
            this.$kaiincd.val($base.kaiincd); // 会員コード
            // 会員コード区分
            if ($base.kaiincdkbn == '0') {
                $("*[id='baseBean.kaiincdkbn1']").prop('checked', true);
            } else if ($base.kaiincdkbn == '1') {
                $("*[id='baseBean.kaiincdkbn2']").prop('checked', true);
            } else if ($base.kaiincdkbn == '2') {
                $("*[id='baseBean.kaiincdkbn3']").prop('checked', true);
            }
            if (!this.$ptokuisakicd.val()) {
                this.$kaiincdkbn.trigger("change");    
            }           
            this.$tokuisakinmn1.val($base.tokuisakinmn1); // 得意先名称１
            this.$tokuisakinmn2.val($base.tokuisakinmn2); // 得意先名称２
            this.$tokuisakikeishokbn.val($base.tokuisakikeishokbn); // 得意先敬称区分
            this.$tokuisakinmkanan1.val($base.tokuisakinmkanan1); // 得意先名カナ（半角）1
            this.$tokuisakinmkanan2.val($base.tokuisakinmkanan2); // 得意先名カナ（半角）2
            this.$renrakusakinokbnn1.val($base.renrakusakinokbnn1); // 連絡先区分１
            this.$renrakusakinon1.val($base.renrakusakinon1); // 連絡先番号１
            this.$renrakusakimemon1.val($base.renrakusakimemon1); // 連絡先メモ１
            this.$renrakusakinokbnn2.val($base.renrakusakinokbnn2); // 連絡先区分２
            this.$renrakusakinon2.val($base.renrakusakinon2); // 連絡先番号２
            this.$renrakusakimemon2.val($base.renrakusakimemon2); // 連絡先メモ２
            this.$renrakusakinokbnn3.val($base.renrakusakinokbnn3); // 連絡先区分３
            this.$renrakusakinon3.val($base.renrakusakinon3); // 連絡先番号３
            this.$renrakusakimemon3.val($base.renrakusakimemon3); // 連絡先メモ３
            this.$renrakusakinokbnn4.val($base.renrakusakinokbnn4); // 連絡先区分４
            this.$renrakusakinon4.val($base.renrakusakinon4); // 連絡先番号４
            this.$renrakusakimemon4.val($base.renrakusakimemon4); // 連絡先メモ４
            // メルマガ区分
            if ($base.merumagakbn == '1') {
                $("*[id='baseBean.merumagakbn1']").prop('checked', true);
            } else if ($base.merumagakbn == '0') {
                $("*[id='baseBean.merumagakbn2']").prop('checked', true);
            }
            this.$mailadrse.val($base.mailadrse); // メールアドレス
        
            // 営業管理情報
            this.$rcptkbn.val($deli.rcptkbn); // 受付区分
            this.$kakutokukbn.val($deli.kakutokukbn); // 獲得区分
            this.$baitaikbn.val($deli.baitaikbn); // 媒体区分
            this.$kakutokutnshacd.val($deli.kakutokutnshacd); // 獲得担当者コード
            this.$tnshamei3.text($deli.tnshamei3); // 獲得担当者名
            // 他社切替
            if ($deli.tashakirikae == '0') {
                $("*[id='deliveryOrderBean.tashakirikae1']").prop('checked', true);
            } else if ($deli.tashakirikae == '1') {
                $("*[id='deliveryOrderBean.tashakirikae2']").prop('checked', true);
            }
            
            // 配達情報
            // 留守置き可能フラグ
            if ($deli.fzaiokikanoflg == '0') {
                $("*[id='deliveryOrderBean.fzaiokikanoflg1']").prop('checked', true);
            } else if ($deli.fzaiokikanoflg == '1') {
                $("*[id='deliveryOrderBean.fzaiokikanoflg2']").prop('checked', true);
            }
            this.$nohinsakijohon1.val($deli.nohinsakijohon1); // 納品先情報1
            this.$nohinsakijohon2.val($deli.nohinsakijohon2); // 納品先情報2
            
            // 受注情報
            this.$acptjikanshiteikbn.val($deli.acptjikanshiteikbn); // 時間指定区分
            this.$acptjikanshiteijikoku.val($deli.acptjikanshiteijikoku); // 時間指定時刻開始
            this.$acptjikanshiteijikokushry.val($deli.acptjikanshiteijikokushry); // 時間指定時刻終了
            // 納品金額印字
            if ($deli.kinotptnashiflg == '0') {
                $("*[id='deliveryOrderBean.kinotptnashiflg1']").prop('checked', true);
            } else if ($deli.kinotptnashiflg == '1') {
                $("*[id='deliveryOrderBean.kinotptnashiflg2']").prop('checked', true);
            }
            this.$chuitokuisakikbnaci.val($deli.chuitokuisakikbnaci); // 注意内容（グループ）区分
            this.$chuinaiyoaci.val($deli.chuinaiyoaci); // 注意内容（グループ）
            this.$chuitokuisakikbnap.val($deli.chuitokuisakikbnap); // 注意内容（AP）区分
            this.$chuinaiyoap.val($deli.chuinaiyoap); // 注意内容（AP）
            this.$renrakujikokokai.val($deli.renrakujikokokai); // 連絡事項（公開）
            this.$tokkijikohikokai.val($deli.tokkijikohikokai); // 特記事項（非公開）
            this.$prntmemon1.val($deli.prntmemon1); // 印刷メモ１
            this.$prntmemon2.val($deli.prntmemon2); // 印刷メモ２
            this.$prntmemon3.val($deli.prntmemon3); // 印刷メモ３
        }
    }
    
    /**
     * 委託相手反映ボタンクリックイベントにフックする。
     * @param e イベントオブジェクト
     */
    #clickHaneiItakuaiteBtn = (_e) => {
        
        const apKihonCd = this.$itakuaiteapkihoncd.val();
        const shopCd = this.$itakuaiteshopcd.val();
        const tokuisakicd = this.$itakuaitetokuisakicd.val();
        const jutakudlvrycstkbn = this.$jutakudlvrycstkbn.val();
       
        // ショップ名取得
        const api = "/api/b0501w02/haneiitakuaite/"
               + jutakudlvrycstkbn
            ;
        // apiコール
        $.get(api, {apkihoncd:apKihonCd
                   ,shopcd:shopCd
                   ,tokuisakicd: tokuisakicd})
        .then((data) => {
            if (data) {
                // 委託相手情報を画面に設定する
                this.setHaneimotoItakuaiteInfo(data);
                // 活性制御の再設定のためモジュールロードイベントを発生させる
                $ACI_EVENT_ELE.trigger(EVENTS.MODULE_LOAD_COMPLETE);
            }
        })
    }
    
    /**
     * 反映元委託相手情報を設定する。
     * @param data APIで取得した得意先情報
     */
    setHaneimotoItakuaiteInfo = (data) => {
        
        const $base = data.base_bean;
        const $deli = data.delivery_order_bean;
        
        // 基本情報
        this.$cdkindkbn.val($base.cdkindkbn); // コード種別
        this.$cdkindkbn.attr("data-default", $base.cdkindkbn).trigger(EVENTS.ACI_LOAD);
        this.$gyoshukbn.val($base.gyoshukbn); // 業種区分
        this.$gyoshukbn.attr("data-default", $base.gyoshukbn).trigger(EVENTS.ACI_LOAD);
        this.$gyoshushskbn.val($base.gyoshushskbn); // 業種詳細区分
        this.$gyoshushskbn.attr("data-default", $base.gyoshushskbn).trigger(EVENTS.ACI_LOAD);
        this.$riyowsvkbn.val($base.riyowsvkbn); // 利用サーバー区分
        this.$tokuisakinmn1.val($base.tokuisakinmn1); // 得意先名称１
        this.$tokuisakinmn2.val($base.tokuisakinmn2); // 得意先名称２
        this.$tokuisakikeishokbn.val($base.tokuisakikeishokbn); // 得意先敬称区分
        this.$tokuisakinmkanan1.val($base.tokuisakinmkanan1); // 得意先名カナ（半角）1
        this.$tokuisakinmkanan2.val($base.tokuisakinmkanan2); // 得意先名カナ（半角）2
        this.$yubinno.val($base.yubinno); // 郵便番号
        this.$adrsn1.val($base.adrsn1);　// 住所１
        this.$adrsn2.val($base.adrsn2); // 住所２
        this.$adrsn3.val($base.adrsn3); // 住所３
        this.$chihoupubdanticdn1.val($base.chihoupubdanticdn1); // 地方公共団体コード１
        this.$chihoupubdanticdn2.val($base.chihoupubdanticdn2); // 地方公共団体コード２
        this.$renrakusakinokbnn1.val($base.renrakusakinokbnn1); // 連絡先区分１
        this.$renrakusakinon1.val($base.renrakusakinon1); // 連絡先番号１
        this.$renrakusakimemon1.val($base.renrakusakimemon1); // 連絡先メモ１
        this.$renrakusakinokbnn2.val($base.renrakusakinokbnn2); // 連絡先区分２
        this.$renrakusakinon2.val($base.renrakusakinon2); // 連絡先番号２
        this.$renrakusakimemon2.val($base.renrakusakimemon2); // 連絡先メモ２
        this.$renrakusakinokbnn3.val($base.renrakusakinokbnn3); // 連絡先区分３
        this.$renrakusakinon3.val($base.renrakusakinon3); // 連絡先番号３
        this.$renrakusakimemon3.val($base.renrakusakimemon3); // 連絡先メモ３
        this.$renrakusakinokbnn4.val($base.renrakusakinokbnn4); // 連絡先区分４
        this.$renrakusakinon4.val($base.renrakusakinon4); // 連絡先番号４
        this.$renrakusakimemon4.val($base.renrakusakimemon4); // 連絡先メモ４
        this.$mailadrse.val($base.mailadrse); // メールアドレス
            
        // 申込・サーバー設置情報
        this.$moshikomircptdd.val($base.moshikomircptdd); // 申込受付日
        this.$startup.prop('checked', $base.startup == '1' ? true : false); // はじめてｾｯﾄ
        this.$nyukaimokoshono.val($base.nyukaimokoshono); // 入会申し込みNo
        this.$nyukaidd.val($base.nyukaidd); // 入会日
        // 申込書受付区分
        if ($base.mokoshorcptkbn == '1') {
            $("*[id='baseBean.mokoshorcptkbn1']").prop('checked', true);
        } else if ($base.mokoshorcptkbn == '0') {
            $("*[id='baseBean.mokoshorcptkbn2']").prop('checked', true);
        }
        this.$kaiinmokoshono.val($base.kaiinmokoshono); // 会員申込No
        this.$shokaiwsvstupdd.val($base.shokaiwsvstupdd); // 初回設置日
        this.$stupddtoshacd.val($base.stupddtoshacd); // 設置日登録者コード
        this.$toshamei1.text($base.toshamei1); // 設置日登録者名
        
        // 配達情報
        this.$dlvrykankatsuapkihoncd.val($deli.dlvrykankatsuapkihoncd); // 配達拠点AP基本コード
        this.$dlvrykankatsushopcd.val($deli.dlvrykankatsushopcd); // 配達拠点ショップコード
        this.$apnm2.text($deli.apnm2); // 配達拠点ＡＰ名称
        this.$shopnm2.text($deli.shopnm2); // 配達拠点ショップ名称
        this.$dlvrykankatsukyotencd.val($deli.dlvrykankatsukyotencd); // 配達管轄拠点コード
        this.$kyotennm.text($deli.kyotennm); // 配達管轄拠点名称      
        this.$dlvryareacd.val($deli.dlvryareacd); // 配達エリアコード
        this.$dlvryareanm.text($deli.dlvryareanm); // 配達エリア名称
    }
    
    /**
     * テキストボックスのreadonlyを切り替える
     * @param obj オブジェクト
     * @param readonly true:読み取り専用, false:入力可能
    */
    setReadOnly = (obj, readonly) => {
        if (readonly) {
            obj.prop('readonly', true);
            obj.prop('tabindex', '-1');
            obj.attr('style', 'color: gray;pointer-events: none;')
        } else {
            obj.prop('readonly', false);
            obj.prop('tabindex', '');
            obj.attr('style', 'color: black;')
        }
    }
    
    /**
     * 画面アンロード時のイベントにフックする。
     * @param e イベントオブジェクト
     */
    #onBeforeUnload = (_e)=> {
        // セッションクリア
        // ACCIS2023-6738対応でデッドロック回避のため削除
        //this.clearSession();
    }

    /**
     * サーバのセッションで保持する情報をクリアする。
     */
    clearSession = () => {
        // APIコール
        $.ajax({
            url: "/api/b0501w02/clearsession",
            type: "PUT"
        }).then((_res, status) => {
            console.debug("ajax:" + status);
        }).catch((v)=>{
            console.error("ajax error:" + v.statusText);
            console.error(v);
        });
    }

    /**
     * 戻るボタンの非表示制御
     */
    hideBackButton = () => {
        // 作業予約から遷移した場合は戻るボタンを非表示にする
        if (this.$kinoid?.val() === "b1701w02") {
            this.$btnmodoru.hide();
        }
    }
}