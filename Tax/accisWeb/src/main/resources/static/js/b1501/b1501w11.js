/*
   システム名:ACCIS2023
   機能名:請求入金照会

 */
import {$ACI_EVENT_ELE,EVENTS, addFilter, FILTERS} from "../aciCore.js";

export default class B1501W11{

    constructor(){
        this.#setFieldVariables();
        this.#setFilters();
        this.#setEvents();
    }

    #setFilters(){
        addFilter(FILTERS.ACI_LOAD_AFTER_MAPPING,"b1501w11_meisai_body",this.meisaiBindFilter);
    }

    #setEvents(){
        $ACI_EVENT_ELE.on(EVENTS.MODULE_LOAD_COMPLETE, this.init);
        $ACI_EVENT_ELE.on(EVENTS.Z0101W01_LOAD_COMPLETE,this.apShopSelected);
        $ACI_EVENT_ELE.on(EVENTS.TEMPLATE_LOAD_COMPLETE + "-b1501w11_meisai_body",this.searchComplete);
        $("input[id='ksscd']").on("change",this.changeKsscd);
        $ACI_EVENT_ELE.on(EVENTS.Z0502W01_KSSCD_SELECTED,this.changeKsscd);
        $ACI_EVENT_ELE.on(EVENTS.Z0502W01_KSSCD_KAKUTEI_COMPLETE,this.seikyusakiKakutei);
    }

    #setFieldVariables(){
        this.$display = $("#display");
        this.$pageNum = $("#aci-page_current");
        this.$seikyusakiinfo = $("#seikyusakiinfo");
        this.$loadtarget = $("#b1501w11_meisai_body");

        // 明細部
        this.$b1501w11M1 = $("*[id='b1501w11_m1']");
        // 検索条件部
        this.$seikyu = $("*[id='seikyu']");
        // AP基本コード
        this.$apkihoncd = $("*[id='baseBean.apkihoncd']");
        // ショップコード
        this.$shopcd = $("*[id='baseBean.shopcd']");
        // 請求先コード
        this.$ksscd = $("*[id='ksscd']");
        // 年月(入力項目)
        this.$dydm1 = $("*[id='baseBean.dydm1']");
        // 検索ボタン
        this.$btnkensaku = $("#btnkensaku");
        // 明細部年月日（リンクオフ)
        //this.$dydmmeioff = $("#dydmmeioff");
        // 画面ボディ部（リンクオン)
        this.$dydmmeion = $("#dydmmeion");
        // 編集フラグ
        this.$initflg = $("*[id='baseBean.initflg']");
        // 初期表示フラグ
        this.$shokiflg = $("*[id='shokiflg']");
    }

    meisaiBindFilter = (row,html) => {
        const $html = $(html);
        // 年月設定処理
        //$html.find(".dydmmei").text(row['dydmmei']);
        return $html;
    }

    /**
     * 初期表示時にフックする
     */
    init = () => {
        const currentURL = new URL(window.location.href);
        const query = currentURL.searchParams;
        this.restoreFlag = query.get("acir");
        this.restoreApshopSelected = false;
    }
       
    /**
     * 検索完了時にフックする
     */
    searchComplete = () => {
        // 検索時に取得するshohitaxkinmeiの値により、明細部thの表示名を変更
        if ($("*[id='meisaiBeanList[0].shohitaxkbnmei']").val() === '1') {
            $("*[id='shohitax-th']").text('内消費税額')
        } else {
            $("*[id='shohitax-th']").text('消費税額')
        }

        // リンククリックイベントを関連付ける
        $(".dydmmei").on("click", this.#clickDydmmei);
    }

    /**
     * ap/shop選択時のイベントにフックする
     * @param e イベントオブジェクト
     * @param shop 選択したショップ情報
     */
    apShopSelected = (_e,shop) => {

        // AP/ショップ選択後に画面を表示する
        this.$display.removeClass("display-none").show();

        //請求先ヘッダの表示
        this.$seikyusakiinfo.show();
        //請求先ヘッダの初期化
        $ACI_EVENT_ELE.trigger(EVENTS.Z0502W01_SHOPCD_INIT,[shop]);

        // 検索条件部と明細部を非表示
        this.$seikyu.hide();
        this.$b1501w11M1.hide();

        // 明細部を空にする
        $('#b1501w11_meisai_body').empty();

        // 初期表示でない場合の処理
        if (this.$shokiflg.val() === '1') {
            // 検索条件を空にする
            this.$ksscd.val(''); // 請求先コード
            this.$dydm1.val(''); // 年月(入力項目)
            
            // 詳細画面から戻った後、APショップを変更した場合
            if (this.restoreFlag) {
                this.restoreApshopSelected = true;
            }
        }
        this.$shokiflg.val('1');
        
        // ACCIS2023-6863 対応
        // 他画面からの遷移時、請求先共通ヘッダを固定表示とせず入力可能とする要望への対応
        if (
            this.$initflg.val() === 'f' // 編集フラグの値が「f（他画面からの遷移）」
            && !this.restoreFlag        // 戻るボタンによる遷移以外
            && this.$ksscd.val() !== '' // 請求先コードの入力値が空ではない
        ) {
            // 請求先ヘッダの確定ボタンクリックイベントを発火する
            $("#btnksskaktei").trigger('click');
        }
    }

    /**
     * 請求先選択時のイベントにフックする
     * @param e イベントオブジェクト
     * @param ksscd 請求先コード
     */
    seikyusakiKakutei = (_e,ksscd) => {

        // 検索条件部と明細部を表示
        this.$seikyu.show();
        this.$b1501w11M1.show();

        // 画面項目制御
        this.$dydm1.prop("disabled", false); // 年月活性化
        this.$btnkensaku.prop("disabled", false); // 検索ボタン活性化
        // 明細部年月リンク活性化
        $(".dydmmeioff").removeClass("display-none").hide();
        $(".dydmmeion").removeClass("display-none").show();
               
        // 詳細画面から戻ってきた場合を除き、初期検索を行う
        if (!this.restoreFlag || this.restoreApshopSelected) {
            // 検索ボタンクリックイベントキック
            $('#btnkensaku').click();
        }
    }

    /**
     * 請求先コード変更時のイベントにフックする
     */
    changeKsscd = () => {
        // 請求先コード変更時にrestoreFlagを初期化
        //  (詳細画面から戻ってきた場合でも、請求先コード変更した場合は確定ボタン押下にて検索処理を実施させるため初期化する)
        this.restoreFlag = null;
        // 画面項目制御
        this.$dydm1.prop("disabled", true); // 年月非活性化
        this.$btnkensaku.prop("disabled", true); // 検索ボタン非活性化
        // 明細部年月リンク非活性化
        $(".dydmmeioff").removeClass("display-none").show();
        $(".dydmmeion").removeClass("display-none").hide();

    }

    /**
     * 年月日リンク押下時処理
     * @param e イベントオブジェクト
     */
    #clickDydmmei = (e) => {
        const $dataParams = $(e.currentTarget);
        const apkihoncd = this.$apkihoncd.val(); // AP基本コード
        const shopcd = this.$shopcd.val(); // ショップコード
        const ksscd = this.$ksscd.val(); // 請求先コード
        const initflg = $("*[id='baseBean.initflg']").val(); // 表示フラグ
        const kaishuhohokbn = $dataParams.data("kaishuhohokbn"); // 回収方法区分
        const dydmmei = $dataParams.data("dydmmei"); // 年月日
        const renban = $dataParams.data("renban"); // 連番

        // 遷移先URL生成
        let url = "/b1501w21/" + apkihoncd + "/" + shopcd + "/" + ksscd // 固定URL部分

        // 年月項目が「現在」以外の場合、年月日と連番のパラメータ文字列を付加する
        if (dydmmei != "現在") {
            url += "?kaishuhohokbn=" + kaishuhohokbn + "&dydmmei=" + dydmmei.replaceAll('/','') + "&renbanmei=" + renban + "&initflg=" + initflg;
        } else {
            url += "?initflg=" + initflg + "&kaishuhohokbn=" + kaishuhohokbn;
        }

        window.location.href = url;
    }
}