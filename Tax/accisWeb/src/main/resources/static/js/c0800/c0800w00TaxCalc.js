/*
   システム名:ACCIS2023
*/
var TYPE_URIAGE = '1';
var TYPE_SHIIRE = '2';
var KBN_TOKUISAKI_TOKUISAKI = '1';
var KBN_TOKUISAKI_SHOP = '2';
var KBN_TOKUISAKI_AP = '3';
var KBN_TOKUISAKI_ACI = '4';
var KBN_TOKUISAKI_NAJUTAKU = '5';
var KBN_TOKUISAKI_DLVRYJUTAKU = '6';
var KBN_TOKUISAKI_KEIHI = '9';
var KBN_KEIJYO_URIAGE = '01';
var KBN_KEIJYO_CAMPAIGN = '02';
var KBN_KEIJYO_KEIHI = '08';
var KBN_TAX_KAZEI = '1';
var KBN_TAX_KEIGEN = '4';
var KBN_TAX_KOMEISAI = '5';
var KBN_TAXKEISAN_IN = '1';
var KBN_TAXKEISAN_EX = '2';
var KBN_KEISANHOHO_MEISAI = '1';
var KBN_KEISANHOHO_DENPYO = '2';
var KBN_KEISANHOHO_NONE = '3';
var KBN_PRICE_HYOJUN = '1';
var CD_DLVRY_MOCHIKAERI = 'M';
var DEBUG_CALCUTIL = true;

function dispDebugMsg(_msg) {
    if (DEBUG_CALCUTIL) alert(_msg);
}

/* **********************************************
 * 価格情報　ハンドリングクラス
 *    （変数priceinfo_strにJASON形式の価格情報を画面上で保持しておく必要がある）
 *  _date：切替対象日
 * ********************************************** */
function CalcPrice(_date, _jason) {

    eval("this.info = " + _jason);
    this.dateItm = _date;

    let jsShohiTaxVo = $("input[id*='jsshohitaxvo']").val();
    var jsShohiTaxList;
    eval("jsShohiTaxList = [" + jsShohiTaxVo + "]");
    // 価格計算（行単位）
    // mno：行番号
    // tankaItm：単価項目名
    // suryoItm：数量項目名
    // kinItm：金額項目名
    // taxItm：単価消費税項目名
    // taxKinItm：消費税金額項目名
    // shntaxkbnItm：商品税区分項目名
    // tokuisakiKbnItm：得意先区分項目名
    // noTaxFg：消費税計算対象外フラグ
    this.calcLine = function(mno, tankaItm, suryoItm, kinItm, taxItm, taxKinItm, shntaxkbnItm, tokuisakiKbnItm, noTaxFg) {
        var tanka = getItmValue(tankaItm, mno);
        var suryo = getItmValue(suryoItm, mno);

        var taxkbn;

        if (shntaxkbnItm != null) {
            taxkbn = getItmValue(shntaxkbnItm, mno);
        }
        if ((taxkbn == null || taxkbn == "" || taxkbn == KBN_TAX_KAZEI || taxkbn == KBN_TAX_KEIGEN)
            && this.info.keisanhoho == KBN_KEISANHOHO_MEISAI && noTaxFg != true) {
            //金額・消費税金額計算
            //内税の場合
            if (this.getKeisanKbn(tokuisakiKbnItm) == KBN_TAXKEISAN_IN) {

                //単品消費税計算
                var tax = this.calcTaxEx(tanka, taxkbn);
                setItmValue(taxItm, tax, mno);

                //内税の場合は（単品単価＋単品消費税）×数量より単価×数量を引いたものを消費税金額とし、
                //税込金額－消費税金額を金額とする
                var komikin = this.kinRound(calc_multiply_impl(calc_add_impl(tanka, tax), suryo));
                var kin = this.kinRound(calc_multiply_impl(tanka, suryo));

                //消費税金額
                setItmValue(taxKinItm, komikin - kin, mno);

                //金額
                setItmValue(kinItm, kin, mno);

                //外税の場合
            } else {

                //外税の場合は単品単価×数量を金額とし、それより消費税金額を求める
                //単品消費税は算出しない
                setItmValue(taxItm, "0", mno);
                var kin = this.kinRound(calc_multiply_impl(tanka, suryo));
                var taxkin = this.calcTaxEx(kin, taxkbn);

                //消費税金額
                setItmValue(taxKinItm, taxkin, mno);

                //金額
                setItmValue(kinItm, kin, mno);
            }

            //課税対象では無い場合
        } else {

            //消費税金額
            if (this.info.keisanhoho != KBN_KEISANHOHO_NONE) {
                setItmValue(taxItm, "0", mno);
                setItmValue(taxKinItm, "0", mno);
            }

            //金額
            setItmValue(kinItm, this.kinRound(calc_multiply_impl(tanka, suryo)), mno);
        }
    }

    // 価格計算（全体）
    // mno：明細群番号
    // kinItm：金額項目名
    // kinAllItm：合計金額項目名
    // taxItm：消費税金額項目名
    // taxAllItm：合計消費税金額項目名
    // sumItm：合計税込み金額項目名
    // tokuisakiKbnItm：得意先区分項目名
    // noTaxFg：消費税計算対象外フラグ
    // shntaxkbnItm：商品税区分項目名
    this.calcAll = function(mid, kinItm, kinAllItm, taxItm, taxAllItm, sumItm, tokuisakiKbnItm, noTaxFg, shntaxkbnItm) {
        //金額合計処理
        calc_sum(mid, kinItm, kinAllItm);

        var kinAll = getItmValue(kinAllItm);

        if (this.info.keisanhoho != KBN_KEISANHOHO_NONE && noTaxFg != true) {
            //消費税合計処理（明細単位）
            if (this.info.keisanhoho == KBN_KEISANHOHO_MEISAI) {
                calc_sum(mid, taxItm, taxAllItm);

                //消費税合計処理（伝票単位）仕入のみ
            } else if (this.info.keisanhoho == KBN_KEISANHOHO_DENPYO) {
                var n8sumKin = 0;
                var n8sumTax = 0;
                var n10sumKin = 0;
                var n10sumTax = 0;
                var msize = $(mid).children('tbody').children().length;
                for (var i = 0; i < msize; i++) {

                    var taxkbn;
                    if (shntaxkbnItm != null) {
                        //税区分を取得
                        taxkbn = getItmValue(shntaxkbnItm, i);
                    }
                    if ((taxkbn == KBN_TAX_KAZEI || taxkbn == KBN_TAX_KEIGEN)) {
                        //税率が８％の商品金
                        if (8 == getRate(taxkbn)) {
                            n8sum += isNaN(getItmValue(kinItm, i)) ? 0 : Number(getItmValue(kinItm, i));
                            //税率が10％の商品金
                        } else if (10 == getRate(taxkbn)) {
                            n10sum += isNaN(getItmValue(kinItm, i)) ? 0 : Number(getItmValue(kinItm, i));
                        }
                    }
                }
                //税率が８％の消費税
                n8sumTax = isNaN(n8sum) ? 0 : this.taxRound(8 / 100 * Number(n8sum));
                //税率が10％の消費税
                n10sumTax = isNaN(n10sum) ? 0 : this.taxRound(10 / 100 * Number(n10sum));

                setItmValue(taxAllItm, n8sumTax + n10sumTax);
            }
        } else if (this.info.keisanhoho != KBN_KEISANHOHO_NONE) {
            setItmValue(taxAllItm, "0");
        }

        //税込み合計処理
        calc_add(kinAllItm, taxAllItm, sumItm);
    }

    // 外税計算
    this.calcTaxEx = function(val, shohinTaxkbn) {
        try {
            return isNaN(val) ? 0 : this.taxRound(this.getRate(shohinTaxkbn) / 100 * Number(val));
        } catch (e) {
            dispDebugMsg("ex::" + e.description);
            return 0;
        }
    }
    //月末日チェック
    this.checkLastDayOfMonth = function(targetYMD) {

        var strYYYY = targetYMD.substring(0, 4);
        var strYYYYMM = targetYMD.substring(0, 6);
        var strMMDD = targetYMD.substring(4, 8);
        var inYYYY = parseInt(strYYYY, 10);
        var bNG = false;
        if ("0230" == strMMDD || "0231" == strMMDD
            || "0431" == strMMDD
            || "0631" == strMMDD
            || "0931" == strMMDD
            || "1131" == strMMDD) {
            bNG = true;
        }

        // 閏年ではない2月29日
        if (!(inYYYY % 4 == 0 && (inYYYY % 400 == 0 || inYYYY % 100 != 0)) && "0229" == strMMDD) {
            bNG = true;
        }

        return bNG;
    }
    //正しい月末日取得
    this.getLastDayOfMonth = function(strYYYY, strMM) {
        var lastYYYYMMDD = new Date(strYYYY, strMM, 0);
        var strLastYYYY = lastYYYYMMDD.getFullYear();
        var strLastMM = lastYYYYMMDD.getMonth() + 1;
        var strLastDD = lastYYYYMMDD.getDate();
        return [
            strLastYYYY,
            ('0' + strLastMM).slice(-2),
            strLastDD
        ].join('');
    }
    // 税率取得
    this.getRate = function(shohinTaxkbn) {
        var date = (this.dateItm == null) ? "" : getItmValue(this.dateItm);
        if (this.checkLastDayOfMonth(date)) {
            var strYYYY = date.substring(0, 4);
            var strMM = date.substring(4, 6);
            date = this.getLastDayOfMonth(strYYYY, strMM);
        }
        for (var j = 0; j < jsShohiTaxList.length; j++) {
            var taxkbn = jsShohiTaxList[j].taxkbn;
            var tekiyostrtdd = jsShohiTaxList[j].tekiyostrtdd;
            var tekiyoshrydd = jsShohiTaxList[j].tekiyoshrydd;
            var shohitax = 0;
            if (shohinTaxkbn == taxkbn && date >= tekiyostrtdd && date <= tekiyoshrydd) {
                shohitax = jsShohiTaxList[j].shohitax;
                break;
            }
        }
        return shohitax;
    }

    // 税計算区分取得
    this.getKeisanKbn = function(tokuisakiKbnItm) {
        if (this.info.type == TYPE_URIAGE) {
            if (checkOroshi(getItmValue(tokuisakiKbnItm))) {
                return KBN_TAXKEISAN_EX;
            } else {
                return getCurrent(
                    (this.dateItm == null) ? "" : getItmValue(this.dateItm),
                    this.info.taxkeisankbnurikirikaedd,
                    this.info.genztaxkeisankbnuri,
                    this.info.shintaxkeisankbnuri);
            }
        } else {
            return this.info.taxkeisankbn;
        }
    }

    // 税端数計算
    this.taxRound = function(value) {
        return calc_round(value, this.info.taxhasukeisankbn);
    }

    // 金額端数計算
    this.kinRound = function(value) {
        return calc_round(value, this.info.urikinhasukeisankbn);
    }
}

/**
 * 丸め値取得
 * @param value 明細番号
 * @param hasukbn 端数計算区分
 */
function calc_round(value, hasukbn) {
    switch (hasukbn) {
        case 2:
            return Number(value) < 0 ? Math.ceil(value) : Math.floor(value);
        case 3:
            return Number(value) < 0 ? Math.floor(value) : Math.ceil(value);
        default:
            // マイナスの場合、五捨六入になってしまうので、0.01引いて計算する。
            return Number(value) < 0 ? Math.round(value - 0.01) : Math.round(value);
    }
}
/**
 * 計算処理（掛け算）
 * @param arg1 計算項目名１
 * @param arg2 計算項目名２
 * @param arg3 結果表示項目名
 * @param mno 明細番号
 */
function calc_multiply(arg1, arg2, arg3, mno) {
    var val1 = getItmValue(arg1, mno);
    var val2 = getItmValue(arg2, mno);
    setItmValue(arg3, calc_multiply_impl(val1, val2), mno);
}
function calc_multiply_impl(val1, val2) {
    if (!isNaN(val1) && !isNaN(val2)) {
        return Number(val1) * Number(val2);
    } else {
        return 0;
    }
}

/**
 * 計算処理（足し算）
 * @param arg1 計算項目名１
 * @param arg2 計算項目名２
 * @param arg3 結果表示項目名
 * @param mno 明細番号
 */
function calc_add(arg1, arg2, arg3, mno) {
    var val1 = getItmValue(arg1, mno);
    var val2 = getItmValue(arg2, mno);
    setItmValue(arg3, calc_add_impl(val1, val2), mno);
}
function calc_add_impl(val1, val2) {
    if (!isNaN(val1) && !isNaN(val2)) {
        return Number(val1) + Number(val2);
    } else if (!isNaN(val1)) {
        return Number(val1);
    } else if (!isNaN(val2)) {
        return Number(val2);
    } else {
        return 0;
    }
}
/**
 * 計算処理（明細合計）
 * @param mid 明細ID
 * @param arg1 計算項目名１
 * @param arg2 結果表示項目名
 */
function calc_sum(mid, arg1, arg2) {
    var sum = 0;
    var msize = $(mid).children('tbody').children().length;
    for (var i = 0; i < msize; i++) {
        sum += isNaN(getItmValue(arg1, i)) ? 0 : Number(getItmValue(arg1, i));
    }
    setItmValue(arg2, sum);
}

/**
 * 現／新項目取得
 * @param _target 対象日
 * @param _center 基準日
 * @param _gen 現項目
 * @param _shin 新項目
 */
function getCurrent(_target, _center, _gen, _shin) {
    return (_target < _center) ? _gen : _shin;
}

/**
 * 卸判定
 * @param _tokuisakikbn 得意先区分
 */
function checkOroshi(_tokuisakikbn) {
    return _tokuisakikbn == KBN_TOKUISAKI_SHOP
        || _tokuisakikbn == KBN_TOKUISAKI_AP
        || _tokuisakikbn == KBN_TOKUISAKI_ACI
        || _tokuisakikbn == KBN_TOKUISAKI_NAJUTAKU
        || _tokuisakikbn == KBN_TOKUISAKI_DLVRYJUTAKU;
}

/**
 * 項目値セット
 * @param _itm 項目名
 * @param _value 値
 * @param _mno 明細行番号（省略可）
 */
function setItmValue(_itm, _value, _mno) {
    if (_itm != null) {
        // var itemInfo = formUtil.getFormItemInfoByFormItemId(_itm); 現時点ではitemInfoに該当するコンポーネントは実装していないようなので一旦コメントアウト。
        // var parseVal = formUtil.parseByItemInfo(itemInfo, _value); 
        // 一旦、数字,ハイフン,ピリオドだけを残すように処理を実装する
        var parseVal = String(_value).replace(/[^\d^.^-]/g, '');
        // itemInfoに関するコンポーネントが移植されていないため、一旦、すべて整数として整形されるように設定
        var value = qqu_GetDecimalCut(String(parseVal), 0, null);
        const itemSelector = this.getElementsByFormItemId(_itm, _mno);
        $(this.getElementsByFormItemId(_itm, _mno)).val(value);
        $(this.getElementsByFormItemId(_itm, _mno)).text(value);
    }
}

/**
 * 項目値取得
 * @param _itm 項目名
 * @param _mno 明細行番号（省略可）
 */
function getItmValue(_itm, _mno) {
    //var itemInfo = formUtil.getFormItemInfoByFormItemId(_itm); 現時点ではitemInfoに該当するコンポーネントは実装していないようなので一旦コメントアウト。
    var formatVal = $(this.getElementsByFormItemId(_itm, _mno)).val();
    if (!formatVal) {
        formatVal = $(this.getElementsByFormItemId(_itm, _mno)).text();
    }
    //var parseVal = formUtil.parseByItemInfo(itemInfo, formatVal);
    // 一旦、数字,ハイフン,ピリオドだけを残すように処理を実装する
    var parseVal = String(formatVal).replace(/[^\d^.^-]/g, '');
    return parseVal;
}

function qqu_GetDecimalCut(iStr, iDeciNum, iCutMode) {
    var Q_IntS = "";
    var Q_DecS = "";
    var Q_DecIdx = -1;
    var Q_4s5sS = "";
    if (iCutMode == 3) {
        return iStr;
    }
    Q_DecIdx = iStr.indexOf(".");
    if (iStr.match("^[\\.]{1}") != null) {
        Q_DecS = iStr.substr(1, iStr.length - 1);
        Q_IntS = "0";
    } else if (iStr.match("^[\\+|\\-]{1}[\\.]{1}") != null) {
        Q_DecS = iStr.substr(Q_DecIdx + 1, iStr.length - Q_DecIdx - 1);
        Q_IntS = iStr.substr(0, 1) + "0";
    } else if (Q_DecIdx >= 1) {
        Q_DecS = iStr.substr(Q_DecIdx + 1, iStr.length - Q_DecIdx - 1);
        Q_IntS = iStr.substr(0, Q_DecIdx);
    } else {
        return iStr;
    }
    if (Q_DecS.length <= iDeciNum) {
        return iStr;
    }
    Q_4s5sS = Q_DecS.substr(iDeciNum, 1);
    if ((Q_4s5sS < 5) && (iCutMode == 0 || iCutMode == null) || iCutMode == 1) {
        Q_DecS = Q_DecS.substr(0, iDeciNum);
    } else {
        if (iDeciNum == 0) {
            Q_IntS = Q_IntS - 1 + 2;
            Q_DecS = "";
        } else {
            Q_DecS = Q_DecS.substr(0, iDeciNum);
            Q_DecS = (Q_DecS - 1 + 2).toString();
            while (Q_DecS.length < iDeciNum) {
                Q_DecS = "0" + Q_DecS;
            }
            if (Q_DecS.length > iDeciNum) {
                Q_DecS = Q_DecS.substr(1, iDeciNum);
                Q_IntS = Q_IntS - 1 + 2;
            }
        }
    }
    while (Q_DecS != "") {
        if (Q_DecS.lastIndexOf("0") == Q_DecS.length - 1) {
            Q_DecS = Q_DecS.substr(0, Q_DecS.length - 1);
        } else {
            break;
        }
    }
    if (Q_DecS.length == 0 || Q_DecS == 0) {
        return Q_IntS.toString();
    } else {
        return Q_IntS + "." + Q_DecS;
    }
}

function getElementsByFormItemId(formItemId, rowIndex) {
    /* 
    現行と作りが異なるため、formに関する処理を一旦コメントアウト。
    カード項目の値を取得必要がある際は対応する
    if ($.type(formId) !== 'string' || $.type(formItemId) !== 'string') {
        return [];
    }
    formId = formId.toLowerCase();
    formItemId = formItemId.toLowerCase();
    id = formId + '-' + formItemId;
    formItemInfo = formUtil.getFormItemInfoByFormItemId(formItemId);
    if (!formItemInfo) {
        return [];
    }
    if (!formItemInfo.listId) {
        // カード項目
        if (formItemInfo.controlId === 'chl' || formItemInfo.controlId === 'rdo') {
            selector = '[name="' + id + '"]';
        } else {
            selector = '#' + id;
        }
        return $(selector).toArray();
    }
    */

    var selector;
    var selectorResult;
    // 明細以外の項目
    selector = "*[name='" + formItemId + "']";
    selectorResult = $(selector).toArray();
    if (selectorResult.length == 0) {
        // nameで取得されない場合、idで項目取得を行う
        selector = "*[id='" + formItemId + "']";
        selectorResult = $(selector).toArray();
    }
    if (selectorResult.length == 0) {
        if ($.isNumeric(rowIndex)) {
            // 明細の特定行の項目
            selector = "*[name$='[" + rowIndex + '].' + formItemId + "']";
            /*
                構造が異なるため一旦、分岐しない形で実装
            if (formItemInfo.controlId === 'chl' || formItemInfo.controlId === 'rdo') {
                selector = '[name="' + id + '"]';
            } else {
                selector = '#' + id;
            }
            */
            selectorResult = $(selector).toArray();
        }
        if (selectorResult.length == 0) {
            // 明細の全ての行の項目
            selector = "*[name$=']." + formItemId + "']";
            selectorResult = $(selector).filter(function() {
                return (/^\w+\[+\d+\]?$/).test($(this).attr('name'));
            }).toArray();
        }
    }
    return selectorResult;
}

/* **********************************************
 * 世代有り価格　ハンドリングクラス
 *    （引数は全て画面上の項目名をセットする。全てQuiQで定義された項目であること。）
 *  引数０：切替対象日
 *  引数１：切替基準日
 *  引数２：現配達価格
 *  引数３：新配達価格
 *  引数４：現持ち帰り価格
 *  引数５：新持ち帰り価格
 *  引数６：配達区分（省略可　持ち帰りの場合は"M"）
 *  引数７：表示用項目（省略可）
 *  引数８：初期値退避用項目（省略可）
 *  引数９：明細ＩＤ（省略可）
 * ********************************************** */
function GenerationPrice() {
    this.targetDate = arguments[0];
    this.centerDate = arguments[1];
    this.genDelvItm = arguments[2];
    this.shnDelvItm = arguments[3];
    this.genMochItm = arguments[4];
    this.shnMochItm = arguments[5];
    this.delvKbn = arguments[6];
    this.dispPrc = arguments[7];
    this.initPrc = arguments[8];
    this.mid = (arguments[9] == null) ? ".aci-sticky-table" : arguments[9];

    //有効期間、配達区分より判断される価格を取得する
    this.getCurrentValue = function(_itmNo) {
        return getCurrent(
            getItmValue(this.targetDate),
            getItmValue(this.centerDate, _itmNo),
            getItmValue(this.getGenItm(), _itmNo),
            getItmValue(this.getShnItm(), _itmNo));
    }

    //指定行を判断価格に変更する
    this.changeValue = function(_itmNo) {
        if (this.dispPrc != null) {
            var newVal = this.getCurrentValue(_itmNo);
            setItmValue(this.dispPrc, newVal, _itmNo);
            if (this.initPrc != null) {
                setItmValue(this.initPrc, newVal, _itmNo);
            }
        }
    }

    //明細全行を判断価格に変更する
    this.changeAllValue = function() {
        let msize = $(this.mid).children('tbody').children().length;
        for (var i = 0; i < msize; i++) {
            this.changeValue(i);
        }
    }

    //配達区分より判断される現項目名を取得する
    this.getGenItm = function() {
        var dlv = (this.delvKbn == null) ? true : checkDelivery(qid$(this.delvKbn).val());
        return dlv ? this.genDelvItm : this.genMochItm;
    }

    //配達区分より判断される新項目名を取得する
    this.getShnItm = function() {
        var dlv = (this.delvKbn == null) ? true : checkDelivery(qid$(this.delvKbn).val());
        return dlv ? this.shnDelvItm : this.shnMochItm;
    }
}

/* **********************************************
 * 販売価格　ハンドリングクラス
 *    （引数は全て画面上の項目名をセットする。全てQuiQで定義された項目であること。）
 *  引数０　：ショップ価格　切替対象日
 *  引数１　：ショップ価格　切替基準日
 *  引数２　：ショップ価格　現配達価格
 *  引数３　：ショップ価格　新配達価格
 *  引数４　：ショップ価格　現持ち帰り価格
 *  引数５　：ショップ価格　新持ち帰り価格
 *  引数６　：販促価格　　　有効開始の対象日
 *  引数７　：販促価格　　　有効終了の対象日
 *  引数８　：販促価格　　　有効開始基準日
 *  引数９　：販促価格　　　有効終了基準日
 *  引数１０：販促価格　　　配達価格
 *  引数１１：販促価格　　　持ち帰り価格
 *  引数１２：得意先価格　　有効開始の対象日
 *  引数１３：得意先価格　　有効終了の対象日
 *  引数１４：得意先価格　　有効開始基準日
 *  引数１５：得意先価格　　有効終了基準日
 *  引数１６：得意先価格　　配達価格
 *  引数１７：得意先価格　　持ち帰り価格
 *  引数１８：表示用項目
 *  引数１９：初期値退避用項目
 *  引数２０：計上区分
 *  引数２１：価格区分（標準価格の場合は表示用項目をプロテクトし、値の変更を行わない　※１）
 *  引数２２：得意先区分（※１　卸（ショップ・他ＡＰ・ＡＣＩ）の場合は表示用項目を常に入力とする）
 *  引数２３：単品明細フラグ
 *  引数２４：配達区分（省略可　持ち帰りの場合は"M"）
 *  引数２５：明細ＩＤ（省略可）
 * ********************************************** */
function SellPrice() {
    this.normalPrc = new GenerationPrice(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[24], arguments[18], arguments[19]);
    this.campaignPrc = new PeriodPrice(arguments[6], arguments[7], arguments[8], arguments[9], arguments[10], arguments[11], arguments[24]);
    this.tokuisakiPrc = new PeriodPrice(arguments[12], arguments[13], arguments[14], arguments[15], arguments[16], arguments[17], arguments[24]);
    this.dispPrc = arguments[18];
    this.initPrc = arguments[19];
    this.keijoKbn = arguments[20];
    this.pricekbn = arguments[21];
    this.tokuisakiKbn = arguments[22];
    this.tanpinFg = arguments[23];
    this.mid = (arguments[25] == null) ? ".aci-sticky-table" : arguments[25];

    //ショップ・販促・得意先別商品別価格より判断される価格を取得する
    this.getCurrentValue = function(_itmNo) {
        var newVal;

        switch (qid$(this.keijoKbn, _itmNo).val()) {
            case KBN_KEIJYO_URIAGE:
                newVal = this.tokuisakiPrc.getCurrentValue(_itmNo);
                if (newVal == null || newVal == '') {
                    newVal = this.normalPrc.getCurrentValue(_itmNo);
                }
                break;
            case KBN_KEIJYO_CAMPAIGN:
                newVal = this.campaignPrc.getCurrentValue(_itmNo);
                newVal = (newVal == null || newVal == '') ? 0 : newVal;
                break;
            case KBN_KEIJYO_KEIHI:
                newVal = getItmValue(this.dispPrc, _itmNo);
                break;
            default:
                newVal = 0;
                break;
        }

        return newVal;
    }

    //指定した行を判断価格に変更する
    this.changeValue = function(_itmNo, _compulsoryFlg) {
        var goFlg = (_compulsoryFlg == true) ? true : !this.hasChanged(_itmNo);

        //プロテクト処理を行い、値が変更可能な場合のみ判断価格に変更する
        if (this.changeDisabled(_itmNo, goFlg)) {
            var newVal = this.getCurrentValue(_itmNo);
            setItmValue(this.dispPrc, newVal, _itmNo);
            setItmValue(this.initPrc, newVal, _itmNo);
        }
    }

    //全行を判断価格に変更する
    this.changeAllValue = function() {
        let msize = $(this.mid).children('tbody').children().length;
        for (var i = 0; i < msize; i++) {
            this.changeValue(i, false);
        }
    }


    this.changeDisabled = function(_itmNo, _goFlg) {
        var price = $("*[name$='[" + _itmNo + '].' + this.pricekbn + "']").val();
        var keijo = $("*[name$='[" + _itmNo + '].' + this.keijoKbn + "']").val();
        var tokuisaki = $('*[name="' + this.tokuisakiKbn + '"]').val();
        var tanpin = $("*[name$='[" + _itmNo + '].' + this.tanpinFg + "']").val();
        var dispObj = $("*[name$='[" + _itmNo + '].' + this.dispPrc + "']");
        var changeable = true;

        //セット商品の時は使用不可とする
        if (tanpin != '1') {
            dispObj.css({
                'pointer-events': 'none',
                'cursor': 'default',
                'background-color': 'rgba(239, 239, 239, 0.3)',
                'color': 'rgb(128, 128, 128)',
                'border-color': 'rgba(118, 118, 118, 0.3)'
            });
            dispObj.prop("tabindex", "-1");
            dispObj.prop('readonly', true);

            //得意先区分＝経費・ショップ・他ＡＰ・ＡＣＩの時は使用可とする
        } else if (tokuisaki == KBN_TOKUISAKI_KEIHI || checkOroshi(tokuisaki)) {

            //計上区分≠経費・売上・キャンペーンの時は使用不可とする
            if (keijo == KBN_KEIJYO_KEIHI || keijo == KBN_KEIJYO_URIAGE || keijo == KBN_KEIJYO_CAMPAIGN) {
                dispObj.css({
                    'pointer-events': '',
                    'cursor': '',
                    'background-color': '',
                    'color': '',
                    'border-color': ''
                });
                dispObj.prop("tabindex", "");
                dispObj.prop('readonly', false);
                if (keijo == KBN_KEIJYO_KEIHI || !_goFlg) {
                    changeable = false;
                }
            } else {
                dispObj.css({
                    'pointer-events': 'none',
                    'cursor': 'default',
                    'background-color': 'rgba(239, 239, 239, 0.3)',
                    'color': 'rgb(128, 128, 128)',
                    'border-color': 'rgba(118, 118, 118, 0.3)'
                });
                dispObj.prop("tabindex", "-1");
                dispObj.prop('readonly', true);
                setItmValue(this.dispPrc, "0", _itmNo);
                setItmValue(this.initPrc, "0", _itmNo);
                changeable = false;
            }

            //価格区分＝標準の時は使用不可とする
        } else if (price == KBN_PRICE_HYOJUN) {
            dispObj.css({
                'pointer-events': 'none',
                'cursor': 'default',
                'background-color': 'rgba(239, 239, 239, 0.3)',
                'color': 'rgb(128, 128, 128)',
                'border-color': 'rgba(118, 118, 118, 0.3)'
            });
            dispObj.prop("tabindex", "-1");
            dispObj.prop('readonly', true);

        } else {

            //計上区分≠売上・キャンペーンの時は使用不可とする
            if (keijo == KBN_KEIJYO_URIAGE || keijo == KBN_KEIJYO_CAMPAIGN) {
                dispObj.css({
                    'pointer-events': '',
                    'cursor': '',
                    'background-color': '',
                    'color': '',
                    'border-color': ''
                });
                dispObj.prop("tabindex", "");
                dispObj.prop('readonly', false);

                if (!_goFlg) {
                    changeable = false;
                }
            } else {
                dispObj.css({
                    'pointer-events': 'none',
                    'cursor': 'default',
                    'background-color': 'rgba(239, 239, 239, 0.3)',
                    'color': 'rgb(128, 128, 128)',
                    'border-color': 'rgba(118, 118, 118, 0.3)'
                });
                dispObj.prop("tabindex", "-1");
                dispObj.prop('readonly', true);
                setItmValue(this.dispPrc, "0", _itmNo);
                setItmValue(this.initPrc, "0", _itmNo);
                changeable = false;
            }
        }

        return changeable;
    }

    //全行のプロテクトを変更する
    this.changeAllDisabled = function() {
        let msize = $(this.mid).children('tbody').children().length;
        for (var i = 0; i < msize; i++) {
            this.changeDisabled(i);
        }
    }

    //全行のプロテクトを解除する
    this.unprotectAll = function() {
        let msize = $(this.mid).children('tbody').children().length;
        for (var i = 0; i < msize; i++) {
            $(formUtil.getElementsByFormItemId(this.dispPrc, i))[0].disabled = false;
        }
    }

    //手入力されたかどうかを判断する
    this.hasChanged = function(_itmNo) {
        return (getItmValue(this.dispPrc, _itmNo) != getItmValue(this.initPrc, _itmNo));
    }
}

/*
 * **********************************************
 * 有効期間付き価格　ハンドリングクラス
 *    （引数は全て画面上の項目名をセットする。全てQuiQで定義された項目であること。）
 *  引数０：有効開始の対象日
 *  引数１：有効終了の対象日
 *  引数２：有効開始基準日
 *  引数３：有効終了基準日
 *  引数４：配達価格
 *  引数５：持ち帰り価格
 *  引数６：配達区分（省略可　持ち帰りの場合は"M"）
 * ********************************************** */
function PeriodPrice() {
    this.startCheckDate = arguments[0];
    this.endCheckDate = arguments[1];
    this.startDate = arguments[2];
    this.endDate = arguments[3];
    this.delvItm = arguments[4];
    this.mochItm = arguments[5];
    this.delvKbn = arguments[6];

    //有効期間、配達区分より判断される価格を取得する
    this.getCurrentValue = function(_itmNo) {
        return checkPeriod(
            getItmValue(this.startCheckDate),
            getItmValue(this.endCheckDate),
            getItmValue(this.startDate, _itmNo),
            getItmValue(this.endDate, _itmNo)) ? getItmValue(this.getCurrentItm(), _itmNo) : null;
    }

    //配達区分より判断される項目名を取得する
    this.getCurrentItm = function() {
        var dlv = (this.delvKbn == null) ? true : checkDelivery(qid$(this.delvKbn).val());
        return dlv ? this.delvItm : this.mochItm;
    }
}

/*
 **
 * 現行で使用していたが現時点で使用していないソース
    */

// 内税計算
this.calcTaxIn = function(val) {
    try {
        return isNaN(val) ? 0 : Number(val) - this.taxRound(Number(val) / (1 + this.getRate(shohinTaxkbn) / 100));
    } catch (e) {
        dispDebugMsg("in::" + e.description);
        return 0;
    }
}

/**
 * 計算処理（明細合計）削除時の対応
 * @param mid 明細ID
 * @param arg1 計算項目名１
 * @param arg2 結果表示項目名
 */
function calc_subtract(mid, arg1, arg2) {
    var sum = 0;
    let msize = $(this.mid).children('tbody').children().length;
    for (var i = 0; i < msize; i++) {
        sum -= isNaN(getItmValue(arg1, i)) ? 0 : Number(getItmValue(arg1, i));
    }
    setItmValue(arg2, sum);
}

/**
 * 項目取得
 * @param arg1 項目名
 * @param arg2 明細番号（省略可）
 */
var qid$ = function(_itmNm, _itmNo) {
    if (arguments.length == 2) {
        return $("*[name$='[" + _itmNo + '].' + _itmNm + "']");
    } else {
        return $("*[name$='" + _itmNm + "']");
    }
}

/**
* 有効期間チェック
* @param _startCheck 開始チェック日
* @param _endCheck 終了チェック日
* @param _start 開始基準日
* @param _end 終了基準日
*/
function checkPeriod(_startCheck, _endCheck, _start, _end) {
    return (_start <= _startCheck && _startCheck <= _end)
        || (_start <= _endCheck && _endCheck <= _end);
}

/**
 * 配達／持ち帰り判定
    * @param _kbn 配達会社コード
        */
function checkDelivery(_dlvry) {
    return (_dlvry != CD_DLVRY_MOCHIKAERI);
}

/***
 * 項目テキスト取得
    * @param _itm 項目名
        * @param _mno 明細行番号（省略可）
 */
function getItmText(_itm, _mno) {
    var itemInfo = formUtil.getFormItemInfoByFormItemId(_itm);
    var formatVal = $(window.fujitsu.intarfrm.kit.actions.getElementsByFormItemId(formUtil.formId, _itm, _mno)).text();
    var parseVal = formUtil.parseByItemInfo(itemInfo, formatVal);
    return parseVal;
}

// 価格計算（行単位）仕入、売上対応版(20070824)
// mno：行番号
// tankaItm：単価項目名
// suryoItm：数量項目名
// kinItm：金額項目名
// taxItm：単価消費税項目名
// taxKinItm：消費税金額項目名
// shntaxkbnItm：商品税区分項目名
// tokuisakiKbnItm：得意先区分項目名
// noTaxFg：消費税計算対象外フラグ
this.calcLine2 = function(mno, tankaItm, suryoItm, kinItm, taxItm, taxKinItm, shntaxkbnItm, tokuisakiKbnItm, noTaxFg) {
    var tanka = getItmValue(tankaItm, mno);

    var suryo = getItmValue(suryoItm, mno);

    var taxkbn;

    if (shntaxkbnItm != null) {
        taxkbn = getItmValue(shntaxkbnItm, mno);
    }
    if ((taxkbn == null || taxkbn == KBN_TAX_KAZEI || taxkbn == KBN_TAX_KEIGEN)
        && this.info.keisanhoho == KBN_KEISANHOHO_MEISAI && noTaxFg != true) {
        //金額・消費税金額計算
        //内税の場合
        if (this.getKeisanKbn(tokuisakiKbnItm) == KBN_TAXKEISAN_IN) {

            //単品消費税計算
            var tax = this.calcTaxEx(tanka, taxkbn);
            setItmValue(taxItm, tax, mno);

            //税込金額－消費税金額を金額とする
            //内税の場合は（単品単価＋単品消費税）×数量より単価×数量を引いたものを消費税金額とし、
            if (tanka == null || tanka == 0) {

                var tax1 = this.calcTaxEx(getItmValue(kinItm, mno), taxkbn);

                var kin = getItmValue(kinItm, mno);
                var komikin = Number(kin) + Number(tax1);


            } else {

                var kin = this.kinRound(calc_multiply_impl(tanka, suryo));
                var komikin = this.kinRound(calc_multiply_impl(calc_add_impl(tanka, tax), suryo));

            }

            //消費税金額
            setItmValue(taxKinItm, komikin - kin, mno);

            //金額
            setItmValue(kinItm, kin, mno);

            //外税の場合
        } else {

            //外税の場合は単品単価×数量を金額とし、それより消費税金額を求める
            //単品消費税は算出しない
            setItmValue(taxItm, "0", mno);

            if (tanka == null || tanka == 0) {
                var kin = getItmValue(kinItm, mno);
                var taxkin = this.calcTaxEx(kin, taxkbn);
            } else {
                var kin = this.kinRound(calc_multiply_impl(tanka, suryo));
                var taxkin = this.calcTaxEx(kin, taxkbn);
            }
            //消費税金額
            setItmValue(taxKinItm, taxkin, mno);

            //金額
            setItmValue(kinItm, kin, mno);
        }

        //課税対象では無い場合
    } else {

        //消費税金額
        if (this.info.keisanhoho != KBN_KEISANHOHO_NONE) {
            setItmValue(taxItm, "0", mno);
            setItmValue(taxKinItm, "0", mno);
        }

        //金額
        if (tanka == null || tanka == 0) {
        } else {
            setItmValue(kinItm, this.kinRound(calc_multiply_impl(tanka, suryo)), mno);
        }
    }
}
// 価格計算:売上対応版(売上のcalcLine2は廃止)
// mno：行番号
// tankaItm：単価項目名
// suryoItm：数量項目名
// kinItm：金額項目名
// taxItm：単価消費税項目名
// taxKinItm：消費税金額項目名
// shntaxkbnItm：商品税区分項目名
// tokuisakiKbnItm：得意先区分項目名
// noTaxFg：消費税計算対象外フラグ
// pricekbnItm：価格区分項目名
// kinUpdFg：金額修正フラグ
this.calcLine3 = function(mno, tankaItm, suryoItm, kinItm, taxItm, taxKinItm, shntaxkbnItm, tokuisakiKbnItm, noTaxFg, pricekbnItm, kinUpdFg) {
    var tanka = getItmValue(tankaItm, mno);

    var suryo = getItmValue(suryoItm, mno);
    var taxkbn;
    if (shntaxkbnItm != null) {
        taxkbn = getItmValue(shntaxkbnItm, mno);
    }

    if ((taxkbn == null || taxkbn == KBN_TAX_KAZEI || taxkbn == KBN_TAX_KEIGEN)
        && this.info.keisanhoho == KBN_KEISANHOHO_MEISAI && noTaxFg != true) {

        // 金額・消費税金額計算
        // 内税の場合
        if (this.getKeisanKbn(tokuisakiKbnItm) == KBN_TAXKEISAN_IN) {

            //単品消費税計算
            var tax = this.calcTaxEx(tanka, taxkbn);
            setItmValue(taxItm, tax, mno);

            //税込金額－消費税金額を金額とする
            //内税の場合は（単品単価＋単品消費税）×数量より単価×数量を引いたものを消費税金額とし、
            // オープン価格で単価が無いもしくは0で金額修正した場合
            var pricekbn = getItmValue(pricekbnItm, mno);
            if ((tanka == null || tanka == 0) && pricekbn == "2" && kinUpdFg != false) {
                //軽減税率対応 JS ryu 2019-05-24  Start
                //                  var tax1 = this.calcTaxEx(getItmValue(kinItm, mno));
                var tax1 = this.calcTaxEx(getItmValue(kinItm, mno), taxkbn);
                //軽減税率対応 JS ryu 2019-05-24  End
                var kin = getItmValue(kinItm, mno);
                var komikin = Number(kin) + Number(tax1);

            } else {

                var kin = this.kinRound(calc_multiply_impl(tanka, suryo));
                var komikin = this.kinRound(calc_multiply_impl(calc_add_impl(tanka, tax), suryo));

            }

            //消費税金額
            setItmValue(taxKinItm, komikin - kin, mno);

            //金額
            setItmValue(kinItm, kin, mno);

            //外税の場合
        } else {

            //外税の場合は単品単価×数量を金額とし、それより消費税金額を求める
            //単品消費税は算出しない
            setItmValue(taxItm, "0", mno);

            // オープン価格で単価が無いもしくは0で金額修正した場合
            var pricekbn = getItmValue(pricekbnItm, mno);
            if ((tanka == null || tanka == 0) && pricekbn == "2" && kinUpdFg != false) {
                var kin = getItmValue(kinItm, mno);
                var taxkin = this.calcTaxEx(kin, taxkbn);
            } else {
                var kin = this.kinRound(calc_multiply_impl(tanka, suryo));
                var taxkin = this.calcTaxEx(kin, taxkbn);
            }
            //消費税金額
            setItmValue(taxKinItm, taxkin, mno);

            //金額
            setItmValue(kinItm, kin, mno);
        }

        //課税対象では無い場合
    } else {

        //消費税金額
        if (this.info.keisanhoho != KBN_KEISANHOHO_NONE) {
            setItmValue(taxItm, "0", mno);
            setItmValue(taxKinItm, "0", mno);
        }

        //金額
        if (tanka == null || tanka == 0) {
        } else {
            setItmValue(kinItm, this.kinRound(calc_multiply_impl(tanka, suryo)), mno);
        }
    }
}

// apkihoncdItm :ａｐ基本コード
// shopcdItm :ショップコード
// mno：行番号
// taxItm：単価消費税項目名
// taxKinItm：消費税金額項目名
// kinItm：金額項目名
// syohinCdItm : 商品コード
// suryoItm：数量項目名
// tokuisakiItm：得意先コード
// keijokbnItm：計上区分
this.calcLine4 = function(apkihoncdItm, shopcdItm, mno, singleTaxItm, taxKinItm, kinItm, syohinCdItm, suryoItm, tokuisaki, keijokbnItm) {
    var apkihoncd = ('000' + getItmValue(apkihoncdItm)).slice(-3);
    var shopcd = ('000' + getItmValue(shopcdItm)).slice(-3);
    var syohinCd = ('000000' + getItmValue(syohinCdItm, mno)).slice(-6);
    if (syohinCd == '000000') {
        // 商品コードのコントローラーがラベルである場合
        syohinCd = ('000000' + getItmText(syohinCdItm, mno)).slice(-6);
    }
    var suryo = getItmValue(suryoItm, mno);
    var tokuisakiCd = ('000000' + tokuisaki).slice(-6);
    var keijokbn = getItmValue(keijokbnItm, mno);
    var targetDate = getItmValue(this.dateItm);

    if (suryo == null || suryo == "" || suryo == 0 || keijokbn == KBN_KEIJYO_CAMPAIGN) {
        //単価消費税
        setItmValue(singleTaxItm, "0", mno);
        //消費税
        setItmValue(taxKinItm, "0", mno);
        //金額
        setItmValue(kinItm, "0", mno);
        return;
    }

    $.ajax({
        type: 'POST',
        url: '../s01KouseihinServlet',
        data: {
            apkihoncd: apkihoncd,
            shopcd: shopcd,
            syohinCd: syohinCd,
            suryo: suryo,
            tokuisakiCd: tokuisakiCd,
            keijokbn: keijokbn,
            targetDate: targetDate
        },
        async: false,           // true:非同期(デフォルト), false:同期
        dataType: 'json',           // データをjson形式で飛ばす
        timeout: 3000,              // タイムアウト時間：３秒
        success: function(data) {   // ↓フォームの送信に成功した場合の処理
            //                  alert("OK");
            //                  alert(data["singlShohiTaxkinSetShohi"]);
            //                  alert(data["shohitaxkinSetShohi"]);
            //                  alert(data["shohikinSetShohi"]);

            try {
                //単価消費税
                setItmValue(singleTaxItm, data["singlShohiTaxkinSetShohi"], mno);
                //消費税
                setItmValue(taxKinItm, data["shohitaxkinSetShohi"], mno);
                //金額
                setItmValue(kinItm, data["shohikinSetShohi"], mno);
            } catch (e) {
                alert("商品(商品CD：" + syohinCd + ")の消費税計算にエラーが発生した。");
            }
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {  // ↓フォームの送信に失敗した場合の処理
            //XMLHttpRequest.status HTTPリクエストのステータス
            //textStatus タイムアウト、パースエラー等のエラー情報
            //errorThrown.message 例外情報
            alert("商品(商品CD：" + syohinCd + ")の消費税計算にエラーが発生した。" +
                "\n  ステータス：" + XMLHttpRequest.status +
                "\n  エラー情報：" + textStatus +
                "\n  例外情報：" + errorThrown.message);
        }
    });
}

/**
* プロテクト処理
* @param _itmObj 項目オブジェクト
    *  @param _fg プロテクトする場合はtrue
        */
function setItmProtect(_itmObj, _fg) {
    if (!_itmObj.prop('readonly')) {
        _itmObj.prop('disabled', _fg);
    }
}
