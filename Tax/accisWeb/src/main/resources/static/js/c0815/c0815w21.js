/*
   システム名:ACCIS2023
   機能名:配達実績登録（個別入力）
 */
import { $ACI_EVENT_ELE, EVENTS, addFilter, FILTERS } from "../aciCore.js";

let priceinfo_str = $("input[id='baseBean.priceinfo']").val();
let price_obj;
let hyujyun_obj;
let acpt_obj;
const ID_KEIJOKBN2 = 'keijokbn2';
const ID_HANBAIPRICEMEI = 'hanbaiprice';
const ID_KEIJOKBN = 'keijokbn';

export default class C0815W21 {

    constructor() {
        this.#setFieldVariables();
        this.#setFilters();
        this.#setEvents();
    }

    #setFilters() {
        addFilter(FILTERS.ACI_LOAD_AFTER_MAPPING, "c0815w21_meisai_body", this.meisaiBindFilter);
    }

    #setEvents() {
        this.$reftnshacdLink.on("click", this.#clickReftnshacdLink);
        this.$linkrenkeihmk1.on("click", this.#clicklinkrenkeihmkLink);
        this.$linkrenkeihmk2.on("click", this.#clicklinkrenkeihmkLink);
        this.$linkrenkeihmk3.on("click", this.#clicklinkrenkeihmkLink);
        this.$linkrenkeihmk4.on("click", this.#clicklinkrenkeihmkLink);
        const dataDlvrytnshatnsha = { cd: $("*[id='baseBean.dlvrytnshacd']"), nm: $("*[id='baseBean.tnshamei']"), len: 6 }
        this.$dlvrytnshacd.on("change", dataDlvrytnshatnsha, this.getTnshNm);
        this.$btnAddrowCheck.on(EVENTS.AJAX_TRIGGER_SUCCESS, this.#onAddRowCheck);
        this.$btnAddrow.on(EVENTS.AJAX_TRIGGER_SUCCESS, this.#onAddRow);
        this.$btnUpdate.on(EVENTS.AJAX_TRIGGER_SUCCESS, this.#onUpdated);
        this.$btnBack.on(EVENTS.AJAX_TRIGGER_COMPLETE, this.#onUpdated);
        $ACI_EVENT_ELE.on(EVENTS.TEMPLATE_LOAD_COMPLETE + "-subheadershoptel", this.apShopSelected);
        $ACI_EVENT_ELE.on(EVENTS.TEMPLATE_LOAD_COMPLETE + "-baseBean.dlvrycmpnynm", this.addOption);
        $ACI_EVENT_ELE.on(EVENTS.TEMPLATE_LOAD_FINALLY + "-c0815w21_meisai_body", this.onMeisaiLoadFinally);

        /*changeイベント*/
        const txt = ':text:not([readonly]):not([disabled]):not(.aci-amount-format)',
            tx2 = 'textarea:not([readonly]):not([disabled]):not(.aci-amount-format)',
            pwd = ':password:not([readonly]):not([disabled]):not(.aci-amount-format)',
            drl = 'select:not([multiple]):not(.aci-amount-format)',
            lst = 'select[multiple]:not(.aci-amount-format)',
            fup = ':file:not(.aci-amount-format)';
        $(document).on('change', [txt, tx2, pwd, drl, lst, fup].join(', '), this.#onChange);
        $(document).on(EVENTS.AMOUNT_FORMAT_COMPLETE, ".aci-amount-format", this.#onChange);
    }

    #setFieldVariables() {
        this.$display = $("#display");
        // 価格情報初期化
        price_obj = new CalcPrice('baseBean.dlvrydd', priceinfo_str);
        hyujyun_obj = new GenerationPrice(
            'baseBean.dlvrydd',
            'hyojunpricekirikaedd',
            'genzhyojunprice',
            'shinhyojunprice',
            'genzhyojunmochikaeriprice',
            'shinhyojunmochikaeriprice',
            'baseBean.dlvrycmpnynm',
            'hyojunpricemei');
        acpt_obj = new SellPrice(
            'baseBean.dlvrydd',
            'shoppricekirikaedd',
            'genzshopprice',
            'shinshopprice',
            'genzshopmochikaeriprice',
            'shinshopmochikaeriprice',
            'baseBean.dlvrydd',
            'baseBean.dlvrydd',
            'hansokustrtdd',
            'hansokushrydd',
            'hansokuprice',
            'hansokumochikaeriprice',
            'baseBean.dlvrydd',
            'baseBean.dlvrydd',
            'ttpricestrtdd',
            'ttpriceshrydd',
            'ttprice',
            'ttmochikaeriprice',
            'hanbaiprice',
            'shokidsplyprice',
            'keijokbn2',
            'pricekbn',
            'baseBean.tokuisakikbn',
            'tanpinmeisaiflg',
            'baseBean.dlvrycmpnynm');
        this.$loadtarget = $("#c0815w21_meisai_body");
        this.$btnmeisai = $("#btnmeisai");
        this.$reftnshacdLink = $("#reftnshacd");
        this.$dlvrytnshacd = $("*[id='baseBean.dlvrytnshacd']");
        this.$tnshamei = $("*[id='baseBean.tnshamei']");
        this.$dlvrycmpnynm = $("select[id='baseBean.dlvrycmpnynm']");
        this.$apkihoncd = $("#apkihoncd");
        this.$shopcd = $("#shopcd");
        this.$tokuisakiKbn = $("*[id='baseBean.tokuisakikbn']");
        this.$linkrenkeihmk1 = $("#linkrenkeihmk1");
        this.$haifubutsucd1 = $("*[id='footerBean.haifubutsucd1']");
        this.$haifubutsunm1 = $("*[id='footerBean.haifubutsunm1']");
        this.$haifuflg1 = $("*[name='footerBean.haifuflg1']");
        this.$linkrenkeihmk2 = $("#linkrenkeihmk2");
        this.$haifubutsucd2 = $("*[id='footerBean.haifubutsucd2']");
        this.$haifubutsunm2 = $("*[id='footerBean.haifubutsunm2']");
        this.$haifuflg2 = $("*[name='footerBean.haifuflg2']");
        this.$linkrenkeihmk3 = $("#linkrenkeihmk3");
        this.$haifubutsucd3 = $("*[id='footerBean.haifubutsucd3']");
        this.$haifubutsunm3 = $("*[id='footerBean.haifubutsunm3']");
        this.$haifuflg3 = $("*[name='footerBean.haifuflg3']");
        this.$linkrenkeihmk4 = $("#linkrenkeihmk4");
        this.$haifubutsucd4 = $("*[id='footerBean.haifubutsucd4']");
        this.$haifubutsunm4 = $("*[id='footerBean.haifubutsunm4']");
        this.$haifuflg4 = $("*[name='footerBean.haifuflg4']");
        this.$tokuiinfo = $("#tokuiinfo");
        this.$btnModoru = $("#btnmodoru");
        this.$btnBack = $("#btnback");
        this.$btnUpdate = $("#btnkshn");
        this.$btnAddrowCheck = $("#btnAddrowCheck");
        this.$btnAddrow = $("#btnAddrow");
        this.$hdnshuseihuka = $("*[id='baseBean.hdnshuseihuka']");
        this.$dlvrydd = $("*[id='baseBean.dlvrydd']");
        this.$aciAmountFormat = $(".aci-amount-format");
        this.$dlvrykbn = $('[id="baseBean.dlvrykbn"]');
    }

    meisaiBindFilter = (row, html) => {
        const $html = $(html);
        this.$aciAmountFormat.on(EVENTS.AMOUNT_FORMAT_COMPLETE, this.#onChange);
        if (this.$hdnshuseihuka.val() == "1") {
            // 数量
            $html.find(".suryomei").prop('readonly', true);
            $html.find(".suryomei").prop('tabindex', "-1");
            $html.find(".suryomei").addClass("mode_disabled");
            // 販売価格
            $html.find(".hanbaipricemei").prop('readonly', true);
            $html.find(".hanbaipricemei").prop('tabindex', "-1");
            $html.find(".hanbaipricemei").addClass("mode_disabled");
        }
        if (row.base_bean.authflag == "t") {
            // 型式
            $html.find(".katasikimei").addClass("mode_disabled");
            // 計上区分
            $html.find(".keijokbn").addClass("mode_disabled");
            // 数量
            $html.find(".suryomei").prop('readonly', true);
            $html.find(".suryomei").addClass("mode_disabled");
            $html.find(".suryomei").prop('tabindex', "-1");
            $html.find(".suryomei").addClass("num_label input_label");
            // 販売価格
            $html.find(".hanbaipricemei").prop('readonly', true);
            $html.find(".hanbaipricemei").addClass("mode_disabled");
            $html.find(".hanbaipricemei").prop('tabindex', "-1");
            $html.find(".hanbaipricemei").addClass("num_label input_label");
            $html.find(".hanbaipricemei").prop("style", "color:#000000;");
            // 明細備考
            $html.find(".meisaibikomei").prop('tabindex', "-1");
            $html.find(".meisaibikomei").addClass("num_label input_label");
        } else {
            // 数量
            $html.find(".suryomei").addClass("num");
            // 販売価格
            $html.find(".hanbaipricemei").addClass("num");
        }
        if (row.addgyoflg == "1") {
            $html.find(".keijokbnnmmei").remove();
        } else {
            $html.find(".keijokbn").remove();
        }
        if (row.tanpinmeisaiflg != "1") {
            // 販売価格
            $html.find(".hanbaipricemei").prop('readonly', true);
            $html.find(".hanbaipricemei").addClass("mode_disabled");
            $html.find(".hanbaipricemei").prop('tabindex', "-1");
        }

        return $html;
    }

    /**
   * ap/shop選択時のイベントにフックする
   * @param e イベントオブジェクト
   * @param shop 選択したショップ情報
   */
    apShopSelected = (_e, _shop) => {
        if (this.$hdnshuseihuka.val() == "1") {
            // 配達日
            this.$dlvrydd.prop('readonly', true);
            this.$dlvrydd.prop('tabindex', "-1");
            this.$dlvrydd.addClass("mode_disabled");
        }
        //得意先ヘッダーの初期化
        $ACI_EVENT_ELE.trigger(EVENTS.Z0501W01_SHOPCD_INIT, [_shop]);
        this.$btnmeisai.click();
        //得意先リストの表示
        this.$tokuiinfo.show();
        this.$display.removeClass("display-none").show();
        $("#meisaiarea").show();
    }

    /**
     * チェンジ処理
     * @param ev イベント
     */
    #onChange = (ev, isScriptCall) => {
        ev.stopPropagation();
        ev.preventDefault();

        const Q_EventTarget = ev.type === EVENTS.AMOUNT_FORMAT_COMPLETE ? ev.target : ev.currentTarget;
        const itemName = Q_EventTarget.id.replace(/.+[/.]|[0-9\[\]]/g, '');
        if (itemName && isScriptCall !== true) {
            let mno;
            switch (itemName.toUpperCase()) {
                case "SURYOMEI":
                    mno = Q_EventTarget.id.replace(/[a-z\[\]]/g, '');

                    //総数の再計算
                    calc_multiply('suryomei', 'irisumei', 'sokazumei', mno);

                    //消費税、金額、合計金額の再計算
                    if (this.$tokuisakiKbn.val() == "9") {
                        this.#keihirecalculate();
                    } else {
                        this.#recalculate();
                    }

                    break;

                case "HANBAIPRICE":
                    //消費税、金額、合計金額の再計算
                    if (this.$tokuisakiKbn.val() == "9") {
                        this.#keihirecalculate();
                    } else {
                        this.#recalculate();
                    }

                    break;

                case "KEIJOKBN":
                    mno = Q_EventTarget.id.replace(/[a-z\[\]]/g, '');
                    const keijokbnnm = $("select[name='meisaiSummaryBeanList[" + mno + "].keijokbn'] option:selected").text();
                    $('*[id="' + ID_KEIJOKBN + '[' + mno + ']' + '"]').val($('[id="' + ID_KEIJOKBN + '[' + mno + ']' + '"]').val());
                    $('*[id="' + ID_KEIJOKBN2 + '[' + mno + ']' + '"]').val($('[id="' + ID_KEIJOKBN + '[' + mno + ']' + '"]').val());
                    $("input[name='meisaiSummaryBeanList[" + mno + "].keijokbnnmmei']").val(keijokbnnm);

                    //販売価格のセット
                    acpt_obj.changeValue(mno, true);

                    if (this.$tokuisakiKbn.val() == "9") {
                        this.#keihirecalculate(mno);
                    } else {
                        this.#recalculate(mno);
                    }
                    break;

                case "DLVRYCMPNYNM":
                    // ACCIS2023-7136 配達会社変更時に配達区分を変更することで
                    // 配達会社変更後に行追加した際の単価設定が正しく行われるようにする
                    if (this.$dlvrycmpnynm.val() === 'M') {
                        this.$dlvrykbn.val('3');
                    } else if (this.$dlvrycmpnynm.val() === 'J') {
                        this.$dlvrykbn.val('1');
                    } else {
                        this.$dlvrykbn.val('2');
                    }
                    
                    //標準価格のセット
                    hyujyun_obj.changeAllValue();

                    //販売価格のセット
                    acpt_obj.changeAllValue();

                    //消費税、金額、合計金額の再計算
                    if (this.$tokuisakiKbn.val() == "9") {
                        this.#keihirecalculate(mno);
                    } else {
                        this.#recalculate(mno);
                    }
                    break;
                case "DLVRYDD":
                    //標準価格のセット
                    hyujyun_obj.changeAllValue();

                    break;
                default:
                    break;
            }
        }
    }

    //経費得意先用計算処理
    #keihirecalculate = (mno) => {
        if (mno == null) {
            let msize = $('.aci-sticky-table').children('tbody').children().length;
            for (let i = 0; i < msize; i++) {
                if (getItmValue('shntaxkbn', i) != '5') {
                    price_obj.calcLine(i, 'hanbaiprice', 'suryomei', 'kinmei', 'untshohitaxkin', 'shohitaxkin', 'shntaxkbn', 'baseBean.tokuisakikbn', true);
                    $('*[id="hanbaipricemei[' + i + ']' + '"]').val($('*[id="' + ID_HANBAIPRICEMEI + '[' + i + ']' + '"]').val());
                }
            }
        } else {
            if (getItmValue('shntaxkbn', mno) != '5') {
                price_obj.calcLine(mno, 'hanbaiprice', 'suryomei', 'kinmei', 'untshohitaxkin', 'shohitaxkin', 'shntaxkbn', 'baseBean.tokuisakikbn', true);
                $('*[id="hanbaipricemei[' + mno + ']' + '"]').val($('*[id="' + ID_HANBAIPRICEMEI + '[' + mno + ']' + '"]').val());
            }
        }
        price_obj.calcAll(".aci-sticky-table", 'kinmei', 'footerBean.sm', 'shohitaxkin', 'footerBean.uchishohitaxkin', 'footerBean.kinsm', 'baseBean.tokuisakikbn', false, 'shntaxkbn');
        this.#addComma();
    }

    //金額再計算処理
    #recalculate = (mno) => {
        if (mno == null) {
            let msize = $('.aci-sticky-table').children('tbody').children().length;
            for (let i = 0; i < msize; i++) {
                if (getItmValue('shntaxkbn', i) != '5') {
                    price_obj.calcLine(i, 'hanbaiprice', 'suryomei', 'kinmei', 'untshohitaxkin', 'shohitaxkin', 'shntaxkbn', 'baseBean.tokuisakikbn');
                    $('*[id="hanbaipricemei[' + i + ']' + '"]').val($('*[id="' + ID_HANBAIPRICEMEI + '[' + i + ']' + '"]').val());
                }
            }
        } else {
            if (getItmValue('shntaxkbn', mno) != '5') {
                price_obj.calcLine(mno, 'hanbaiprice', 'suryomei', 'kinmei', 'untshohitaxkin', 'shohitaxkin', 'shntaxkbn', 'baseBean.tokuisakikbn');
                $('*[id="hanbaipricemei[' + mno + ']' + '"]').val($('*[id="' + ID_HANBAIPRICEMEI + '[' + mno + ']' + '"]').val());
            }
        }
        price_obj.calcAll(".aci-sticky-table", 'kinmei', 'footerBean.sm', 'shohitaxkin', 'footerBean.uchishohitaxkin', 'footerBean.kinsm', 'baseBean.tokuisakikbn', false, 'shntaxkbn');
        this.#addComma();
    }

    // 担当者マスタ子画面呼び出し処理
    #clickReftnshacdLink = (e) => {
        e.stopPropagation();
        e.preventDefault();

        // kinoidは必須（画面IDを定義する）
        const params = {
            "kinoid": "c0815w21",
            "apkihoncd": this.$apkihoncd.val(),
            "shopcd": this.$shopcd.val(),
            "jigyoshokanri": null,
            "eigyotnshakbn": null,
            "jimutnshakbn": null,
            "dlvrytnshakbn": "1",
            "cdkensakuflg": "1"
        };

        const queryParams = $.param(params);

        // 子ウィンドウの呼び出し
        openDataSelectWindow(this.setDlvrytnshacd, "/a0102w03?" + queryParams);
    }

    setDlvrytnshacd = (data) => {
        this.$dlvrytnshacd.val(data.tnshacd);
        this.$tnshamei.val(data.tnshanm);
    }

    // 明細備考検索子画面呼び出し処理
    #clickbtnlinkBtn = (e) => {
        e.stopPropagation();
        e.preventDefault();

        // clickされた要素の取得
        const currentTarget = e.currentTarget;

        $('#btnlinkindex').val(currentTarget.id.replaceAll(/.*\[|\].*/g, ''));

        // kinoidは必須（画面IDを定義する）
        const params = {
            "kinoid": "c0815w21",
            "apkihoncd": this.$apkihoncd.val(),
            "shopcd": this.$shopcd.val(),
            "cdkensakuflg": "1"
        };

        const queryParams = $.param(params);

        // 子ウィンドウの呼び出し
        openDataSelectWindow(this.setMeisaibikomei, "/z0806w01?" + queryParams);
    }

    setMeisaibikomei = (data) => {
        const index = $('#btnlinkindex').val();
        const meisaibikomei = "meisaibikomei[" + index + "]";
        $("input[id='" + meisaibikomei + "']").val(data.mojisetteiatai);
    }

    // 配布物マスタ子画面呼び出し処理
    #clicklinkrenkeihmkLink = (e) => {
        e.stopPropagation();
        e.preventDefault();

        // clickされた要素の取得
        const currentTarget = e.currentTarget;
        const id = currentTarget.id;

        // 適用する値設定用関数の選定
        let callbackFunction = null;

        if (id == "linkrenkeihmk1") {
            callbackFunction = this.setHaifubutsucd1
        } else if (id == "linkrenkeihmk2") {
            callbackFunction = this.setHaifubutsucd2
        } else if (id == "linkrenkeihmk3") {
            callbackFunction = this.setHaifubutsucd3
        } else {
            callbackFunction = this.setHaifubutsucd4
        }

        // kinoidは必須（画面IDを定義する）
        const params = {
            "kinoid": "c0815w21",
            "apkihoncd": this.$apkihoncd.val(),
            "shopcd": this.$shopcd.val(),
            "cdkensakuflg": "1"
        };

        const queryParams = $.param(params);

        // 子ウィンドウの呼び出し
        openDataSelectWindow(callbackFunction, "/a0401w03?" + queryParams);
    }

    setHaifubutsucd1 = (data) => {
        this.$haifubutsucd1.val(data.haifubutsucd);
        this.$haifubutsunm1.val(data.haifubutsunm);
        $("*[id='footerBean.haifubutsuapkihoncd1']").val(this.$apkihoncd.val());
        $("*[id='footerBean.haifubutsushopcd1']").val(this.$shopcd.val());
        this.$haifuflg1.prop("disabled", false);
        this.$haifuflg1.prop("checked", true);
        this.$haifuflg1.attr("checked", "checked");
    }

    setHaifubutsucd2 = (data) => {
        this.$haifubutsucd2.val(data.haifubutsucd);
        this.$haifubutsunm2.val(data.haifubutsunm);
        $("*[id='footerBean.haifubutsuapkihoncd2']").val(this.$apkihoncd.val());
        $("*[id='footerBean.haifubutsushopcd2']").val(this.$shopcd.val());
        this.$haifuflg2.prop("disabled", false)
        this.$haifuflg2.prop("checked", true);
        this.$haifuflg2.attr("checked", "checked");
    }

    setHaifubutsucd3 = (data) => {
        this.$haifubutsucd3.val(data.haifubutsucd);
        this.$haifubutsunm3.val(data.haifubutsunm);
        $("*[id='footerBean.haifubutsuapkihoncd3']").val(this.$apkihoncd.val());
        $("*[id='footerBean.haifubutsushopcd3']").val(this.$shopcd.val());
        this.$haifuflg3.prop("disabled", false)
        this.$haifuflg3.prop("checked", true);
        this.$haifuflg3.attr("checked", "checked");
    }

    setHaifubutsucd4 = (data) => {
        this.$haifubutsucd4.val(data.haifubutsucd);
        this.$haifubutsunm4.val(data.haifubutsunm);
        $("*[id='footerBean.haifubutsuapkihoncd4']").val(this.$apkihoncd.val());
        $("*[id='footerBean.haifubutsushopcd4']").val(this.$shopcd.val());
        this.$haifuflg4.prop("disabled", false)
        this.$haifuflg4.prop("checked", true);
        this.$haifuflg4.attr("checked", "checked");
    }

    //既存データの初期表示及びデータ更新時に対象クラスの値をカンマ区切りにする。
    #addComma = () => {
        $(document).find($(".num")).each((_index, element) => {
            if (element.value) {
                element.value = separateWithComma(element.value);
            }
        });
        $(document).find($(".num_label")).each((_index, element) => {
            if (element.value) {
                element.value = separateWithComma(element.value);
            }
        });
    }

    /**
     * 担当者コードから担当者名を取得し、設定する。
     * @param e イベントオブジェクト
     */
    getTnshNm = (e) => {

        const apKihonCd = this.$apkihoncd.val();
        const shopCd = this.$shopcd.val();
        const $tnshCd = e.data.cd;
        const $tnshNm = e.data.nm;
        const len = e.data.len;

        // 担当者名を初期化
        $tnshNm.val("");

        // 担当者コードが未入力の場合、処理終了
        if (!this.isValidCode($tnshCd.val().trim())) {
            return;
        }

        // 担当者コード0埋め(6文字)
        const tmpTnshCd = $tnshCd.val().trim().padStart(len, '0');
        $tnshCd.val(tmpTnshCd);

        // 担当者名取得
        const api = "/api/parts/s02m003t/unique/"
            + apKihonCd + "/"
            + shopCd + "/"
            + $tnshCd.val();

        // apiコール
        $.get(api)
            .then((data) => {
                if (data) {
                    $tnshNm.val(data);
                }
            })
    }

    onMeisaiLoadFinally = (_e, _shop) => {
        //標準価格のセット
        hyujyun_obj.changeAllValue();

        if (this.$tokuisakiKbn.val() == "9") {
            this.#keihirecalculate();
        } else {
            this.#recalculate();
        }

        $("button[id*='btnlink']").on("click", this.#clickbtnlinkBtn);
        //受注単価プロテクト処理
        acpt_obj.changeAllDisabled();
    }

    #onUpdated = (_e, _data, _status) => {
        this.$btnModoru.click();
    }

    #onAddRowCheck = (_e, _data, _status) => {
        _e.stopPropagation();
        _e.preventDefault();

        // kinoidは必須（画面IDを定義する）
        const params = {
            "kinoid": "c0815w21",
            "apkihoncd": this.$apkihoncd.val(),
            "shopcd": this.$shopcd.val(),
            "cdkensakuflg": "1"
        };

        const queryParams = $.param(params);

        // 子ウィンドウの呼び出し
        openDataSelectWindow(this.setAddgyo, "/a0301w03?" + queryParams);
    }

    setAddgyo = (data) => {
        // 行追加フラグON
        $("*[id='baseBean.addgyoflag']").val('1');
        $("*[id='meisaiHeaderBean.shncdadd']").val(data.shncd);
        this.$btnAddrow.click();
    }

    #onAddRow = (_e, _data, _status) => {
        // 行追加フラグOFF
        $("*[id='baseBean.addgyoflag']").val([]);
        $("*[id='meisaiHeaderBean.shncdadd']").val([]);
        this.$btnmeisai.click();
    }

    addOption = (_e) => {
        // 配達会社コードプルダウンに自社と持ち帰りを追加
        this.$dlvrycmpnynm.prepend(new Option("自社", "J"));
        this.$dlvrycmpnynm.append(new Option("持ち帰り", "M"));
        // 配達区分に応じた配達会社コード設定
        if ("3" == this.$dlvrykbn.val()) {
            // 持ち帰りの場合
            this.$dlvrycmpnynm.val("M");
        } else if ("1" === this.$dlvrykbn.val()) {
            // 自社の場合
            this.$dlvrycmpnynm.val("J");
        }
    }

    /**
     * 値が数字のみで構成されているかを判定して返す
     *
     * @param {any} val 値
     * @return {boolean} 値が数字のみであればtrue
     */
    isValidCode = val => val != null && val !== "" && /^[0-9]*$/.test(val)
}