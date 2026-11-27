/**
   システム名:ACCIS2023
   機能名:受注明細予約登録
*/
import { $ACI_EVENT_ELE, EVENTS, addFilter, FILTERS } from "../aciCore.js";

var price_obj;
var acpt_obj;

export default class C0802W01 {

    constructor() {
        this.#setFieldVariables();
        this.#setEvents();
        this.#setFilters();
    }

    #setFilters() {
        addFilter(FILTERS.ACI_LOAD_AFTER_MAPPING, "c0802w01_meisai_list", this.meisaiBindFilter);
    }

    #setEvents() {
        // AP/ショップ選択時
        $ACI_EVENT_ELE.on(EVENTS.Z0101W01_LOAD_COMPLETE, this.apShopSelected);
        // 得意先共通ヘッダの得意先コードの入力値が変更された時
        $("input[id='kttcd']").on("change", this.apShopSelected);
        // 得意先共通ヘッダのリンククリックにより得意先検索子画面を表示し、得意先を選択した際に発火するイベント
        $ACI_EVENT_ELE.on(EVENTS.Z0501W01_KTTCD_SELECTED, _e => $("input[id='kttcd']").trigger("change"));
        // 得意先共通ヘッダの得意先確定イベント
        $ACI_EVENT_ELE.on(EVENTS.Z0501W01_KTTCD_KAKUTEI_COMPLETE, this.tokuisakiKakutei);
        // このモジュールがロード完了した時
        $ACI_EVENT_ELE.on(EVENTS.MODULE_LOAD_COMPLETE, this.shokiSearch);
        // 明細部のロードが完了した時
        $ACI_EVENT_ELE.on(EVENTS.TEMPLATE_LOAD_COMPLETE + "-c0802w01_meisai_list", this.#loadcomplete);
        // 更新ボタンクリックによるAPI呼び出しが正常終了した時
        this.$btnkshn.on(EVENTS.AJAX_TRIGGER_SUCCESS, this.afterUpdate);
        // 明細備考のクリックイベント
        $(document).on('click', [this.$btnnc].join(', '), this.#clickLinkBtn);
        // 明細行入力項目の変更イベント
        this.$loadtarget.on('change', ".katasiki", this.partsChanged);
        this.$loadtarget.on('change', ".keijokbn", this.partsChanged);
        this.$loadtarget.on(EVENTS.AMOUNT_FORMAT_COMPLETE, ".acptsuryo", this.changeRow);
        this.$loadtarget.on(EVENTS.AMOUNT_FORMAT_COMPLETE, ".acptunt", this.changeRow);
        this.$loadtarget.on(EVENTS.AMOUNT_FORMAT_COMPLETE, ".soukaisumei", this.changeRow);
        this.$loadtarget.on(EVENTS.AMOUNT_FORMAT_COMPLETE, ".zankaisumei", this.changeRow);
        this.$loadtarget.on('change', ".meisaibiko", this.changeRow);
    }

    #setFieldVariables() {
        this.$btnmeisaikensaku = $("#btnmeisaikensaku");
        this.$btnmheadkensaku = $("#btnmheadkensaku");
        this.$tokuiInfo = $("#tokuiinfo");
        this.$display = $("#display");
        this.$display2 = $("#display2");
        this.$btnkshn = $("#btnkshn");
        this.$btnkensaku = $("#btnkensaku");
        this.$meisailist = $('#c0802w01_meisai_list');
        this.$btnnc = ':button:not([disabled]):not([data-code-type="cod"]):not([data-code-type="cog"])';
        this.$mbiko = 0;
        this.$btnokini = $("#btnokini");
        this.$btngyofukusha = $("#btngyofukusha");
        this.$btngyosonyu = $("#btngyosonyu");
        this.$btnmodoru = $("#btnmodoru");
        this.$loadtarget = $("#c0802w01_meisai_list");
        // くちばしメニューから遷移してきた場合を識別するためのフラグを設定
        this.fromBeakMenu = $("input[id='baseBean.kinoid']").val() === "z0501w01";
        // 初回表示かどうかを示すフラグを設定
        this.firstDisplay = true;
    }

    meisaiBindFilter = (row, html) => {
        const $html = $(html);
        if (row.acptuntcntlkbn == "0") {
            // 非活性
            $html.find(".acptunt").css("pointer-events", "none");
            $html.find(".acptunt").css("color", "gray");
        }
        return $html;
    }
    /**
     * ap/shop選択時のイベントにフックする
     * @param {Event} _e イベントオブジェクト
     * @param {object} shop 選択したショップ情報
     */
    apShopSelected = (_e, shop) => {
        this.$display2.addClass("display-none");
        this.$display.removeClass("display-none").show();
        //得意先リストの表示
        this.$tokuiInfo.removeClass("display-none").show();
        //得意先リストの表示
        this.$tokuiInfo.show();
        //得意先ヘッダーの初期化
        $ACI_EVENT_ELE.trigger(EVENTS.Z0501W01_SHOPCD_INIT, [shop]);
        // くちばしメニューから遷移し、画面を初期表示する場合は、得意先確定イベントを発火する（得意先共通ヘッダは固定表示されていないため）
        // ※未配達受注データ確認・修正から遷移してきたときは得意先共通ヘッダは固定表示しているため確定イベントも自動的に発火する
        if (this.firstDisplay === true && this.fromBeakMenu === true) {
            $("#btnkttkaktei").trigger('click');
        }
    }

    bodyBindFilter = (row, html) => {
        const $html = $(html);
        if (row.fzaiokikanoflg == "1") {
            // 更新対象
            $html.find(".sfzaiokikanoflg").attr('checked', 'checked');
        }

        return $html;
    }

    /**
     * 得意先選択時のイベントにフックする
     * @param e イベントオブジェクト
     * @param shop 選択したショップ情報
     */
    tokuisakiKakutei = (_e, kttcd) => {

        // くちばしメニューから遷移した場合かつ、初回表示の際はフラグを解除
        if (this.firstDisplay === true) {
            this.firstDisplay = false;
        }
        let acptdnpyno = "00";
        let edano = "00";
        if ($("input[id='baseBean.kinoid']").val() === "c0806w01") {
            // 未配達受注データ確認/修正からの遷移時は伝票まで特定されている
            kttcd = $("input[id='baseBean.tokuisakicd']").val();
            $("input[id='z0501w01-kttcd']").val(kttcd);
            acptdnpyno = $("input[id='baseBean.acptdnpyno']").val();
            edano = $("input[id='baseBean.edano']").val();
        }
        // 遷移元機能IDが指定されていなかった場合は「00」を設定
        const kinoid = $("input[id='baseBean.kinoid']").val() === "" ?
            "00" : $("input[id='baseBean.kinoid']").val(); 
        $("input[id='baseBean.tokuisakicd']").val(kttcd);
        const data = "";
        const apkihoncd = $('#apkihoncd').val();
        const shopcd = $('#shopcd').val();
        const urlSegments = [
            "/api/c0802w01/search",
            apkihoncd,
            shopcd,
            kttcd,
            acptdnpyno,
            edano,
            kinoid
        ];
        const url = urlSegments.join("/");

        $.ajax({
            url: url,
            type: "GET",
            data: data,
            dataType: 'json'
        }).then((data, status) => {
            //成功した場合の処理
            console.debug("ajax" + status);
            if (data.tokuisakidata == "0") {
                // 得意先情報が取得できてない場合
                this.$display2.addClass("display-none").show();
            } else {
                $("span[id='dlvrykyotennm']").text(data.dlvrykyotennm);
                $("span[id='dlvryareanm']").text(data.dlvryareanm);
                $("span[id='dlvrycmpnynm']").text(data.dlvrycmpnynm);
                $("span[id='acptdnpyno']").text(data.acptdnpyno + data.acptdnpynodelim + data.edano);
                $("span[id='dlvryptnnm']").text(data.dlvryptnnm);
                $("span[id='dlvrytnshamei']").text(data.dlvrytnshacd + " " + data.dlvrytnshamei);
                $("span[id='datsksmotokbnnm']").text(data.datsksmotokbnnm);
                $("span[id='nohinsakijohon1']").text(data.nohinsakijohon1 + " " + data.nohinsakijohon2);
                // 処理区分ラジオボタン制御
                switch (data.prcskbn) {
                    // 新規：新規を選択状態にし、新規以外は非活性
                    case "1":
                        $("*[id='baseBean.prcskbn1']").prop("checked", true);
                        $("*[id='baseBean.prcskbn1']").prop("disabled", false);
                        $("*[id='baseBean.prcskbn2']").prop("disabled", true);
                        $("*[id='baseBean.prcskbn3']").prop("disabled", true);
                        break;
                    // 更新：更新を選択状態にし、新規は非活性
                    case "2":
                        $("*[id='baseBean.prcskbn2']").prop("checked", true);
                        $("*[id='baseBean.prcskbn1']").prop("disabled", true);
                        $("*[id='baseBean.prcskbn2']").prop("disabled", false);
                        $("*[id='baseBean.prcskbn3']").prop("disabled", false);
                        break;
                    // 削除：削除を選択状態にする（初期表示で削除が選択されることはない）
                    case "3":
                        $("*[id='baseBean.prcskbn3']").prop("checked", true);
                        break;
                }
                // 留守置きチェックボックス
                if (data.fzaiokikanoflg == "1") {
                    $("*[id='fzaiokikanoflg']").attr('checked', 'checked');
                }

                $("input[id='baseBean.skysakicd']").val(data.skysakicd);
                $("input[id='baseBean.dlvrykbn']").val(data.dlvrykbn);
                $("input[id='baseBean.dlvrykyotencd']").val(data.dlvrykyotencd);
                $("input[id='baseBean.dlvrykyotennm']").val(data.dlvrykyotennm);
                $("input[id='baseBean.dlvrykankatsuapkihoncd']").val(data.dlvrykankatsuapkihoncd);
                $("input[id='baseBean.dlvrykankatsushopcd']").val(data.dlvrykankatsushopcd);
                $("input[id='baseBean.dlvryareacd']").val(data.dlvryareacd);
                $("input[id='baseBean.dlvryareanm']").val(data.dlvryareanm);
                $("input[id='baseBean.dlvrycmpnycd']").val(data.dlvrycmpnycd);
                $("input[id='baseBean.dlvrycmpnynm']").val(data.dlvrycmpnynm);
                $("input[id='baseBean.mochikaeriflg']").val(data.mochikaeriflg);
                $("input[id='baseBean.acptdnpyno']").val(data.acptdnpyno);
                $("input[id='baseBean.acptdnpynodelim']").val(data.acptdnpynodelim);
                $("input[id='baseBean.edano']").val(data.edano);
                $("input[id='baseBean.dlvrytnshacd']").val(data.dlvrytnshacd);
                $("input[id='baseBean.dlvrytnshamei']").val(data.dlvrytnshamei);
                $("input[id='baseBean.datsksmotokbnnm']").val(data.datsksmotokbnnm);
                $("input[id='baseBean.nohinsakijohon1']").val(data.nohinsakijohon1);
                $("input[id='baseBean.nohinsakijohon2']").val(data.nohinsakijohon2);
                $("input[id='baseBean.tokuisakijotai']").val(data.tokuisakijotai);
                $("input[id='baseBean.tokuisakikbn']").val(data.tokuisakikbn);
                $("input[id='baseBean.fzaiokikanoflg']").val(data.fzaiokikanoflg);
                $("input[id='baseBean.prntmemon1']").val(data.prntmemon1);
                $("input[id='baseBean.prntmemon2']").val(data.prntmemon2);
                $("input[id='baseBean.prntmemon3']").val(data.prntmemon3);
                $("input[id='baseBean.dlvryjun']").val(data.dlvryjun);
                $("input[id='baseBean.acptrcptkeirokbn']").val(data.acptrcptkeirokbn);
                $("input[id='baseBean.acptdd']").val(data.acptdd);
                $("input[id='baseBean.jsshohitaxvo']").val(data.jsshohitaxvo);
                // 価格情報初期化
                price_obj = new CalcPrice('baseBean.acptdd', data.priceinfo);
                acpt_obj = new SellPrice(
                    'baseBean.acptdd',
                    'shoppricekirikaedd',
                    'genzshopprice',
                    'shinshopprice',
                    'genzshopmochikaeriprice',
                    'shinshopmochikaeriprice',
                    'dlvryytidd',
                    'baseBean.acptdd',
                    'hansokustrtdd',
                    'hansokushrydd',
                    'hansokuprice',
                    'hansokumochikaeriprice',
                    'dlvryytidd',
                    'baseBean.acptdd',
                    'ttpricestrtdd',
                    'ttpriceshrydd',
                    'ttprice',
                    'ttmochikaeriprice',
                    'acptunt',
                    'shokidsplyprice',
                    'keijokbn',
                    'pricekbn',
                    'tokuisakikbn',
                    'tanpinmeisaiflg',
                    'baseBean.mochikaeriflg');

                this.$btnmeisaikensaku.click();

                this.$display2.removeClass("display-none").show();
            }
        }).catch((v) => {
            //失敗した場合の処理
            console.error("ajax error:" + v.statusText);
            console.error(v);
        });

    }

    /**
    * 明細のパーツ(プルダウン、ラジオボタン)の変更時にフックする
    */
    partsChanged = (_e, isScriptCall) => {
        _e.stopPropagation();

        // 初期表示時でない場合
        if (!isScriptCall) {
            // 変更行の情報をサーバに送信する
            this.changeRow(_e);
        }
    }

    /**
     * 初期表示で検索ボタンを疑似押下
     */
    shokiSearch = () => {
        if ($("input[id='baseBean.kinoid']").val() === "c0806w01") {
            this.tokuisakiKakutei();
        }
    }

    afterUpdate = (_shop) => {

        if ($("input[id='baseBean.kinoid']").val() === "c0806w01") {
            this.$btnmodoru.click();
        } else {
            $("input[id='kttcd']").val("").trigger("change");
            this.$display2.addClass("display-none");

        }

    }

    // 明細備考検索子画面呼び出し処理
    #clickLinkBtn = (e) => {
        e.stopPropagation();
        e.preventDefault();

        // clickされた要素の取得
        const currentTarget = e.currentTarget;
        const id = currentTarget.id;

        // 備考検索
        if (id.indexOf("btnlink") > 0) {
            const tmp = id.replace("btnlink", "detailBeanList");
            this.$mbiko = tmp.replace("]", "].meisaibiko");

            // kinoidは必須（画面IDを定義する）
            const params = {
                "kinoid": "c0802w01",
                "apkihoncd": $("*[name='baseBean.apkihoncd']").val(),
                "shopcd": $("*[name='baseBean.shopcd']").val(),
                "cdkensakuflg": "1"
            };

            const queryParams = $.param(params);

            // 子ウィンドウの呼び出し
            openDataSelectWindow(this.setMbiko, "/z0806w01?" + queryParams);
        }
        // 行挿入・行追加
        if (id.indexOf("btngyosonyu") > 0 || id.indexOf("btngyotuika") == 0) {

            if (id.indexOf("btngyotuika") == 0) {
                $("*[name='baseBean.meisaiindex']").val('-1');
            } else {
                let indexTemp = id.replace("btngyosonyu[", "");
                let index = indexTemp.replace("]", "");
                $("*[name='baseBean.meisaiindex']").val(index);
            }
            const params = {
                "kinoid": "c0802w01",
                "apkihoncd": $("*[name='baseBean.apkihoncd']").val(),
                "shopcd": $("*[name='baseBean.shopcd']").val(),
                "cdkensakuflg": "1"
            };

            const queryParams = $.param(params);

            // 子ウィンドウの呼び出し
            openDataSelectWindow(this.gyosonyu, "/a0301w03?" + queryParams);

        }

        // 行複写
        if (id.indexOf("btngyofukusha") > 0) {
            //let index = id.replace(/[a-z\[\]]/g, "");
            let indexTemp = id.replace("btngyofukusha[", "");
            let index = indexTemp.replace("]", "");
            $("*[name='baseBean.meisaiindex']").val(index);
            this.$btngyofukusha.click();
        }

    }

    gyosonyu = (data) => {
        $("*[name='baseBean.renkeishncd']").val(data.shncd);
        this.$btngyosonyu.click();
    }

    setMbiko = (data) => {
        $("*[name=" + this.$mbiko + "]").val(data.mojisetteiatai);
    }

    /**
     * 入力値を変更した明細行の情報をサーバに送信する
     */
    changeRow = (_e) => {
        _e.stopPropagation();
        _e.preventDefault();
        const $currentTarget = $(_e.currentTarget);
        const index = $currentTarget.data("tablerow");

        var itemName = _e.currentTarget.name.replace(/.+[/.]|[0-9\[\]]/g, '');
        if (itemName) {
            switch (itemName) {
                //ここに項目IDのcase文を追加し、固有処理を記述します。
                case "acptsuryo":

                    //総数の再計算
                    calc_multiply('acptsuryo', 'uriirisu', 'sokazu', index);
                    $(".sokazu-label" + index).text($("input[id='detailBeanList[" + index + "].sokazu']").val());

                    //消費税、金額、合計金額の再計算
                    this.#recalculate(index);
                    break;

                case "keijokbn":

                    //販売価格のセット
                    acpt_obj.changeValue(index, true);

                    //消費税、金額、合計金額の再計算
                    this.#recalculate(index);
                    break;

                case "acptunt":

                    //消費税、金額、合計金額の再計算
                    this.#recalculate(index);
                    break;

                default:
                    break;
            }
        }
        
        const parent = 'detailBeanList' + '[' + index + ']';
        let data = {};

        // 変更した明細行の項目を抽出
        $.each($('form').serializeArray(), function(_, kv) {
            if (kv.name.indexOf(parent) == 0) {
                const child = kv.name.replace(parent + '.', '');
                data[child] = kv.value;
            }
        });

        // APIコール
        $.ajax({
            url: "/api/c0802w01/changerow/" + index,
            dataType: 'json',
            contentType: "application/json",
            type: "PUT",
            data: JSON.stringify(data)
        }).then((_res, status) => {
            console.debug("ajax:" + status);
        }).catch((v) => {
            console.error("ajax error:" + v.statusText);
            console.error(v);
        });
        
    }

    #loadcomplete = (_e) => {
        //消費税、金額、合計金額の再計算
        this.#recalculate();
    }

    /**
     * 金額再計算処理
     * @param mno 明細一覧行数
     */
    #recalculate = (mno) => {
        var noTaxFg = $('input[name="baseBean.tokuisakikbn"]').val() == KBN_TOKUISAKI_KEIHI;

        if (mno == null) {
            var msize = $('#c0802w01_meisai_list').children().length;
            for (var i = 0; i < msize; i++) {
                if ($("input[name='detailBeanList[" + i + "].taxkbn'").val() != '5') {
                    price_obj.calcLine(i, 'acptunt', 'acptsuryo', 'kin', 'acptuntshohitaxkin', 'acptshohitaxkin', 'taxkbn', 'baseBean.tokuisakikbn', noTaxFg);
                }
            }

        } else {
            if ($("input[name='detailBeanList[" + mno + "].taxkbn'").val() != '5') {
                price_obj.calcLine(mno, 'acptunt', 'acptsuryo', 'kin', 'acptuntshohitaxkin', 'acptshohitaxkin', 'taxkbn', 'baseBean.tokuisakikbn', noTaxFg);
            }
        }
        price_obj.calcAll(".aci-sticky-table", 'kin', 'acptkin', 'acptshohitaxkin', 'shohitaxkin', 'sm', 'baseBean.tokuisakikbn', noTaxFg, 'taxkbn');
        this.#addComma();
    }

    //既存データの初期表示及びデータ更新時に対象クラスの値をカンマ区切りにする。
    #addComma = () => {
        $(document).find($(".num_label")).each((_index, element) => {
            const $element = $(element);
            const text = $element.text();
            if (text) {
                $element.text(separateWithComma(text));
            }
            if (element.value) {
                element.value = separateWithComma(element.value);
            }
        });
    }
}