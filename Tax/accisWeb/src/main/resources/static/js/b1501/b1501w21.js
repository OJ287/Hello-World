/*
   システム名:ACCIS2023
   機能名:請求入金照会（詳細）
 */
import {$ACI_EVENT_ELE,EVENTS, addFilter, FILTERS} from "../aciCore.js";

export default class B1501W21{

    constructor(){
        this.#setFieldVariables();
        this.#setEvents();
        this.#setFilters();
    }

    #setFilters(){
        addFilter(FILTERS.ACI_LOAD_AFTER_MAPPING,"b1501w21_m1_meisai_body",this.meisaiBindFilter1);
        addFilter(FILTERS.ACI_LOAD_AFTER_MAPPING,"b1501w21_m2_meisai_body",this.meisaiBindFilter2);
    }

    #setEvents(){
        $ACI_EVENT_ELE.on(EVENTS.MODULE_LOAD_COMPLETE,this.init);
        $ACI_EVENT_ELE.on(EVENTS.Z0101W01_LOAD_COMPLETE,this.apShopSelected);
        $("input[id='ksscd']").on("change", this.apShopSelected);
        this.$meisaiBody.on(EVENTS.TEMPLATE_LOAD_ERROR, this.selectError);
        $ACI_EVENT_ELE.on(EVENTS.Z0502W01_KSSCD_KAKUTEI_COMPLATE,this.seikyusakiKakutei)
        $ACI_EVENT_ELE.on(EVENTS.TEMPLATE_LOAD_COMPLETE + "-b1501w21_m1_meisai_body",this.m2MeisaiLoad);
        $ACI_EVENT_ELE.on(EVENTS.TEMPLATE_LOAD_COMPLETE + "-b1501w21_m2_meisai_body",this.footerLoad);
        // 選択チェックボックス変更イベント
        $(document).on("change", ".sentakumei", this.sentakuChange);
        // 伝票クリックイベント
        this.$meisaiBody.on("click", ".dnpyonomei", this.#onClickDnpyonomei);
        // 特典ポイントボタンクリックイベント
        $("*[id='tokutenpoint']").on("click", this.#onClickTokutenpoint);
        // 画面から移動する前のイベント
        $ACI_EVENT_ELE.on('beforeunload', this.#onBeforeUnload);
    }

    #setFieldVariables(){
        this.$display = $("#display");
        this.$seikyusakiinfo = $("#seikyusakiinfo");
        this.$meisaiBody = $("#b1501w21_m2_meisai_body");
        this.$btninit = $("#btninit");
        this.$shopnm = $("*[id='baseBean.shopnm']");
    }

    meisaiBindFilter1 = (row,html) => {
        const $html = $(html);
        if (row.yukokbn != "1") {
            $html.children("td").addClass("muko");
        }

        if (row.sentakumei == "1") {
            $html.find(".sentakumei").prop("checked", true);
        }

        if (row.index == 0) {
            $html.find(".sentakumei").css("display", "none");
        }
        return $html;
    }

    meisaiBindFilter2 = (row,html) => {
        const $html = $(html);
        // 請求書出力有無区分による明細部背景色制御
        if (row.skyshootptarinashikbnmei == "1") {
            $html.children("td").addClass("cancel_line");
            $html.find(".taxrate").removeClass("cancel_line");
        }

        // 売上/入金取消フラグによる明細部背景色制御
        if (row.urinykntrksflag == "1") {
            $html.children("td").addClass("cancel_line");
            $html.find(".taxrate").removeClass("cancel_line");
        }
        // 消費税率の%記号、★記号の表示制御
        if (row.taxflag == "1") {
            // 消費税用行の場合
            $html.children("td").addClass("tax_line");
            // 税率の%を非表示
            $html.find(".taxratemei_percent").hide();
            // 税率の★を非表示
            $html.find(".taxratemei_star").hide();
        } else {
            // 通常行の場合
            // 税率の%と★の表示制御
            if (row.taxratemei != "" && row.taxratemei != null) {
                // 税率が設定されている場合
                // %を表示
                $html.find(".taxratemei_percent").show();
                // ★を非表示
                $html.find(".taxratemei_star").hide();
            } else {
                // 税率が設定されていない場合
                // %を非表示
                $html.find(".taxratemei_percent").hide();
                if (row.shohitaxkin2mei != "" && row.shohitaxkin2mei != null) {
                    // 消費税額が設定されている場合
                    // ★を表示
                    $html.find(".taxratemei_star").show();
                } else {
                    // 消費税額が設定されていない場合
                    // ★を非表示
                    $html.find(".taxratemei_star").hide();
                }
            }
        }
        // 登録者情報の表示制御
        if (row.toshacdmei == "" || row.toshacdmei == null) {
            // 登録者コードが空の場合
            // 登録者情報を非表示
            $html.find(".toshainfo").hide();
        } else {
            // 登録者コードが空ではない場合
            // 登録者情報を表示
            $html.find(".toshainfo").show();
        }

        return $html;
    }

    /**
     * ap/shop選択時のイベントにフックする
     * @param e イベントオブジェクト
     * @param shop 選択したショップ情報
     */
    apShopSelected = (_e,shop) => {
        this.$display.removeClass("display-none").show();

        const dydmdd = $("*[id='baseBean.dydm']").val();
        // 請求先ヘッダの表示
        this.$seikyusakiinfo.show();
        // 請求先ヘッダの初期化
        $ACI_EVENT_ELE.trigger(EVENTS.Z0502W01_SHOPCD_INIT, [shop]);

        $ACI_EVENT_ELE.trigger(EVENTS.ACI_SEARCH_RESET);

        // 初期表示用の隠しボタンを実行
        $('#btninit').click();
    }

    /**
     * 検索エラー処理
     */
    selectError = (e,v) => {
        // バブリングされたイベントだったらスルー
        if (e.currentTarget != e.target) {
            return;
        }
        const status = v.status;
        if (status === 404) {
            // 0件時の処理
            $("*[id='baseBean.shokihyojiflag']").val("0");
            $ACI_EVENT_ELE.trigger(EVENTS.ACI_SEARCH_RESET);
        } else if (status === 409) {
            $("*[id='baseBean.shokihyojiflag']").val("0");
            $ACI_EVENT_ELE.trigger(EVENTS.ACI_SEARCH_RESET);
        }
    }

    /**
     * セッションクリアAPI呼出処理
     */
     clearSession = () => {
        // APIコール
        $.ajax({
            url: "/api/b1501w21/clearsession",
            type: "PUT"
        }).then((res,status) => {
            console.debug("ajax:" + status);
        }).catch((v) => {
            console.error("ajax error:" + v.statusText);
            console.error(v);
        });
    }

    /**
     * 初期表示時イベント
     * @param e イベントオブジェクト
     */
    init = (_e) => {
        // ショップ名称
        this.$shopnm.val(document.getElementById('subheadershopname').innerText);
    }

    /**
     * 請求先選択時のイベントにフックする
     * @param e イベントオブジェクト
     * @param shop 選択したショップ情報
     */
    seikyusakiKakutei = (_e,ksscd) => {
        this.$meisaiBody.trigger(EVENTS.ACI_LOAD,[{
            "bindData":{"ksscd":ksscd},
            "complete":function(_data,_status){
                /* do something */
            },
            "error":function(_v){/* do something */},
            "finally":function(){/* do something */}
         }]);
   }

    /**
     * 明細エリア読み込み後イベント
     */
    m2MeisaiLoad = (_e,data) => {
        // 明細データが1件以下の場合は印刷ボタン利用フラグに2を設定(ボタン制御は検索結果エリア読み込み時に実施)
        if (data.total_elements <= 1) {
            $("*[id='footerBean.insatable']").val("2");
        }
        // 初期表示フラグに1を設定
        $("*[id='baseBean.shokihyojiflag']").val("1");
        // 検索ボタンを疑似押下
        $("#btnkensaku").trigger("click");
    }

    /**
     * 検索結果一覧エリア読み込み後イベント
     */
     footerLoad = (_e,data) => {
        const n8peruri = data.content[0].n8peruri;
        const n8pershohzei = data.content[0].n8pershohzei;
        const n10peruri = data.content[0].n10peruri;
        const n10pershohzei = data.content[0].n10pershohzei;
        const kinsm1 = data.content[0].kinsm1;
        const kinsm2 = data.content[0].kinsm2;
        const kinsm3 = data.content[0].kinsm3;
        const pointsm = data.content[0].pointsm;
        const insatable = data.content[0].insatable;
        const $insatable = $("*[if='footerBean.insatflag']").val();
        const $shokihyojiflag = $("*[id='baseBean.shokihyojiflag']").val();
        // フッターエリアにデータ設定
        $("*[id='footerBean.n8peruri']").text(separateWithComma(n8peruri));
        $("*[id='footerBean.n8pershohzei']").text(separateWithComma(n8pershohzei));
        $("*[id='footerBean.n10peruri']").text(separateWithComma(n10peruri));
        $("*[id='footerBean.n10pershohzei']").text(separateWithComma(n10pershohzei));
        $("*[id='footerBean.kinsm1']").text(separateWithComma(kinsm1));
        $("*[id='footerBean.kinsm2']").text(separateWithComma(kinsm2));
        $("*[id='footerBean.kinsm3']").text(separateWithComma(kinsm3));
        $("*[id='footerBean.pointsm']").text(separateWithComma(pointsm));
        $("*[id='footerBean.insatable']").val(insatable);
        // 初期表示として検索ボタンを押下した場合
        if ($shokihyojiflag == '1') {
            // 初期表示フラグに0を設定
            $("*[id='baseBean.shokihyojiflag']").val("0");
            // 明細エリア、検索結果一覧エリア共にデータが取得出来ている場合
            if ($insatable != '2' && insatable == '1') {
                // 印刷ボタンを活性
                $("*[id='btnprint']").prop('disabled', false);
            }
            // 通常の検索ボタン押下時
        } else {
            // 処理フラグに1を設定
            $("*[id='footerBean.insatflag']").val("1");
        }

        //「明細欄」と「合計欄」の横スクロールバーの同期
        var scrollmeisai = $(".scrollmeisai");
        var scrollsummary = $('.scrollsummary');
        //「明細欄」の横スクロールバーに対する「合計欄」の横スクロールバーの同期
        scrollmeisai.scroll(function() {
            scrollsummary.scrollLeft(scrollmeisai.scrollLeft());
        });

    }

    /**
     * 選択チェックボックス変更時イベント
     */
    sentakuChange = (e) => {
        e.stopPropagation();
        e.preventDefault();
        const $currentTarget = $(e.currentTarget);
        const index = $currentTarget.data("tablerow");
        const parent = 'm1MeisaiBeanList[' + index + ']';
        let data = {};

        // 変更した明細行の項目を抽出
        $.each($('form').serializeArray(), function(_, kv) {
            if(kv.name.indexOf(parent) == 0) {
                const child = kv.name.replace(parent + '.', '');
                data[child] = kv.value;
            }
        });

        // APIコール
        $.ajax({
            url: "/api/b1501w21/changerow/" + index,
            dataType: 'json',
            contentType: "application/json",
            type: "PUT",
            data: JSON.stringify(data)
        }).then((res,status) => {
            console.debug("ajax:" + status);
        }).catch((v) => {
            console.error("ajax error:" + v.statusText);
            console.error(v);
        });
    }

    /**
     * 伝票番号リンク押下時処理
     * @param e イベントオブジェクト
     */
    #onClickDnpyonomei = (e) => {
        const $currentTarget = $(e.currentTarget);

        const apkihoncd = $("*[id='baseBean.apkihoncd']").val();
        const shopcd = $("*[id='baseBean.shopcd']").val();
        const urikinmei = $currentTarget.data("urikinmei");
        const nyknkin2mei = $currentTarget.data("nyknkin2mei");
        const dnpynomei = $currentTarget.data("dnpynomei");
        const skysakicd = $("*[id='ksscd']").val();
        let url = "";
        // 売上金額か入金金額のどちらかが必ず入ってくるため、呼び出し先の判定には売上金額と入金金額を用いる
        if (urikinmei !== "") {
            // 売上金額が設定されている場合
            // 呼び出し先URLをZ1001W01に指定
            url = "z1001w01/" + apkihoncd + "/" + shopcd + "/" + dnpynomei;
        } else {
            if (nyknkin2mei !== "") {
                // 入金金額が設定されている場合
                // 呼び出し先URLをZ1504W01に指定
                url = "z1504w01/" + apkihoncd + "/" + shopcd + "/" + dnpynomei + "/" + skysakicd;
            } else {
                // どちらも設定されていない場合、何もしない
                return;
            }
        }
        // 指定されたURLをポップアップで呼び出す
        modalOpen(url, null, 1300, 650);
    }

    /**
     * 特典ポイントボタン押下時処理
     * @param e イベントオブジェクト
     */
    #onClickTokutenpoint = (e) => {
        const apkihoncd = $("*[id='baseBean.apkihoncd']").val();
        const shopcd = $("*[id='baseBean.shopcd']").val();
        const skysakicd = $("*[id='ksscd']").val();

        let url = "z2402w01/" + apkihoncd + "/" + shopcd + "/" + skysakicd + "/" + "1";
        console.log("url:" + url);
        // 指定されたURLをポップアップで呼び出す
        modalOpen(url, null, 1300, 650);
    }

    #onBeforeUnload = (e) => {
        // セッションクリア
        // ACCIS2023-6738対応でデッドロック回避のため削除
        //this.clearSession();
    }
}