import * as Util from "./c0801w02usefulfunctions.js";

const FLG = {
    ON : '1',
    OFF: '0'
};

const TOKUISAKIKBN = {
    KOKYAKU     : '1',
    SHOP        : '2',
    AP          : '3',
    ACI         : '4',
    NAJUTAKU    : '5',
    DLVRYJUTAKU : '6',
    KEIHI       : '9'
};

const OROSHI = [
    TOKUISAKIKBN.SHOP,
    TOKUISAKIKBN.AP,
    TOKUISAKIKBN.ACI,
    TOKUISAKIKBN.NAJUTAKU,
    TOKUISAKIKBN.DLVRYJUTAKU,
];

const KEISANKBN = {
    IN: '1',
    EX: '2' 
};

const TAXKBN = {
    KAZEI    : '1',
    KEIGEN   : '4',
    KOMEISAI : '5'
};

const KEISANHOHO = {
    MEISAI : '1',
    DENPYO : '2',
    NONE   : '3'
};

const HASUKEISANKBN = {
    KIRISUTE : '2',
    KIRIAGE  : '3'
};

const PRICEKBN = {
    HYOJUN : '1',
    OPEN   : '2'
};

const DLVRYCMPNY = {
    JISHA      : 'J',
    MOCHIKAERI : 'M'
};

const KEIJOKBN = {
    URIAGE: '01',
    CAMPAIGN: '02',
    KEIHI: '08'
};

/**
 * 受注登録（受注登録）_受注金額計算機クラス
 */
export default class OrderAmountCalculator {
    
    /**
     * コンストラクタ
     *
     * @param {object[]} taxInfoList 消費税率情報リスト
     * @param {string} tokuisakikbn 得意先区分
     * @param {string} dlvryytidd 配達予定日
     * @param {string} dlvrycmpnycd 配達会社コード
     * @param {string} invoicshikoymd インボイス施行日
     */
    constructor(taxInfoList, tokuisakikbn, dlvryytidd, dlvrycmpnycd, invoicshikoymd) {
        this._taxInfoList = taxInfoList;
        this._tokuisakikbn = tokuisakikbn;
        this.dlvryytidd = dlvryytidd;
        this._invoicshikoymd = invoicshikoymd;
        this.dlvrycmpnycd = dlvrycmpnycd;
        this._detailList = [];
    }

    get dlvryytiDate () {
        return toDate(this.dlvryytidd);
    }
    
    get tokuisakikbn() {
        return this._tokuisakikbn;
    }
    
    get invoicshikoymd () {
        return this._invoicshikoymd;
    }
    
    get detailList() {
        return this._detailList;
    }
    
    set detailList(list) {
        const value = list === void 0 ? [] : list;
        this._detailList = value;
    }
    
    /**
     * 受注配達明細オブジェクトを取得する
     *
     * @param {number} [idx] 行番号
     * @return {Array|object} 受注配達明細オブジェクトまたは受注配達明細リスト 
     */
    getDetail = (idx) => idx === void 0 ? this.detailList : this.detailList[idx];
    
    /**
     * 標準価格を取得する
     * 
     * @param {object} detailObj 受注配達明細オブジェクト
     * @return {number} 標準価格
     */
    getHyojunPrice = (detailObj) => Util.parseFloat(this.getSwichablePrice(detailObj.hyojunprice_bean));
    
    /**
     * ショップ価格を取得する
     */
    getShopPrice = (detailObj) => Util.parseFloat(this.getSwichablePrice(this.getShopPriceInfo(detailObj)));
    
    /**
     * 得意先別特別商品価格を取得する
     *
     * @param {object} detailObj 受注配達明細オブジェクト
     * @return {?number} 得意先別特別商品価格（適用されない場合はnull）
     */
    getTokubetsuPrice = (detailObj) => {
        // 料金種別特別単価フラグがONの場合
        if (FLG.ON === detailObj.rknkindtokubetsuuntflg) {
            // 明細から特別商品価格リストを取得し適用期間に該当する商品価格情報を返す
            const tokubetsupriceInfo = detailObj.tokubetsuprice_bean_list.find((info) => 
                Util.isDateBetween(this.dlvryytidd, info.strtdd, info.shrydd)
            );
            return this.getRangePrice(tokubetsupriceInfo);
        }
        return null;
    }
    
    /**
     * 販促商品価格を取得する
     *
     * @param {object} detailObj 受注配達明細オブジェクト
     * @return {?number} 販促商品価格（適用されない場合はnull）
     */
    getCampaignPrice = (detailObj) => {
        // 販促区分がONの場合
        if (FLG.ON === detailObj.hansokukbn) {
            return this.getRangePrice(detailObj.hansokuprice_bean);
        }
        return null;
    }
    
    /**
     * 適用期間に応じた価格を取得する
     *
     * @param {object} rangePriceInfo 適用期間が設定された価格情報
     * @return {?number} 価格（適用期間外の場合はnull）
     */
    getRangePrice = (rangePriceInfo) =>  {
        if (rangePriceInfo) {
            return Util.isDateBetween(this.dlvryytidd, rangePriceInfo.strtdd, rangePriceInfo.shrydd)
                ? this.getPriceByDlvrycmpny(rangePriceInfo.price, rangePriceInfo.mochikaeriprice)
                : null;    
        }
        return null;
    }
    
    /**
     * 現新判定を行い価格を取得する
     *
     * @param {object} switchablePriceInfo 現新価格情報
     */
    getSwichablePrice = (switchablePriceInfo) => {
        const price = Util.getCurrentValue(
            switchablePriceInfo.genzprice,
            switchablePriceInfo.shinprice,
            switchablePriceInfo.pricekirikaedd,
            this.dlvryytidd
        );
        const mochikaeriprice = Util.getCurrentValue(
            switchablePriceInfo.genzmochikaeriprice,
            switchablePriceInfo.shinmochikaeriprice,
            switchablePriceInfo.pricekirikaedd,
            this.dlvryytidd
        );
        return this.getPriceByDlvrycmpny(price, mochikaeriprice);
    }
    
    /**
     * 配達会社に応じた価格を取得する
     *
     * @param {number} price 価格
     * @param {number} mochikaeriprice 持ち帰り価格
     *
     */
    getPriceByDlvrycmpny = (price, mochikaeriprice) =>
        DLVRYCMPNY.MOCHIKAERI === this.dlvrycmpnycd ? mochikaeriprice : price;
    
    /**
     * 明細のショップ商品価格情報を取得する.
     *
     * @param {object} detailObj 受注配達明細オブジェクト
     * @return {object} ショップ商品価格情報
     */
    getShopPriceInfo = (detailObj) => 
        PRICEKBN.HYOJUN === this.getPricekbn(detailObj)
            ? detailObj.hyojunprice_bean
            : detailObj.openprice_bean;

    /**
     * 価格区分を取得する
     * 
     * @param {object} detailObj 受注配達明細オブジェクト
     * @param {string} 価格区分
     */
    getPricekbn = (detailObj) => 
        Util.getCurrentValue(
            detailObj.genzpricekbn,
            detailObj.shinpricekbn,
            detailObj.hyojunprice_bean.pricekirikaedd,
            this.dlvryytidd);
    
    /**
     * 明細の販売価格を取得する
     *
     * @param {object} detailObj 受注配達明細オブジェクト
     * @return {number} 販売価格
     */
    getSalesPrice = (detailObj) => {
        console.groupCollapsed(`販売価格取得 … 現在の販売価格 = ${detailObj.acptunt}`);
        console.debug(`配達予定日 = ${this.dlvryytidd}`);
        console.debug(`配達会社コード = ${this.dlvrycmpnycd}`);
        console.debug(`単品明細フラグ = ${detailObj.tanpinmeisaiflg}`);
        console.debug(`計上区分 = ${detailObj.keijokbn}`);
        console.debug(`料金種別特別単価フラグ = ${detailObj.rknkindtokubetsuuntflg}`);
        console.groupCollapsed(`得意先別特別商品価格情報リスト`);
        console.debug(detailObj.tokubetsuprice_bean_list);
        console.debug(`特別価格 = ${this.getTokubetsuPrice(detailObj)}`);
        console.groupEnd();
        console.groupCollapsed(`販促商品価格情報`);
        console.debug(detailObj.hansokuprice_bean);
        console.debug(`販促価格 = ${this.getCampaignPrice(detailObj)}`);
        console.groupEnd();
        console.groupCollapsed(`ショップ商品価格情報`);
        console.debug(this.getShopPriceInfo(detailObj));
        console.debug(`ショップ価格 = ${this.getShopPrice(detailObj)}`);
        console.groupEnd();
        console.debug(`販促優先区分 = ${detailObj.hansokuyusenkbn}`);
        console.debug(`販促区分 = ${detailObj.hansokukbn}`);
        console.debug(`価格区分 = ${this.getPricekbn(detailObj)}`);
        console.groupEnd();
        const tokubetsuprice = this.getTokubetsuPrice(detailObj);
        const campaignprice = this.getCampaignPrice(detailObj);
        switch (detailObj.keijokbn) {
            // 計上区分が経費の場合、販売価格はそのまま（実際には在庫単価）
            case KEIJOKBN.KEIHI:
                return detailObj.acptunt;
            // 計上区分が売上の場合
            case KEIJOKBN.URIAGE:
                // 特別価格適用期間内の場合
                if (tokubetsuprice !== void 0 && tokubetsuprice !== null) {
                    // 特別価格を販売価格とする
                    console.debug(`特別価格を適用 : ${tokubetsuprice}`);
                    return Util.parseFloat(tokubetsuprice);
                }
                // 条件を満たさない場合はショップ価格を販売価格とする
                console.debug(`ショップ価格を適用 : ${this.getShopPrice(detailObj)}`);
                return this.getShopPrice(detailObj);
            // 計上区分がキャンペーンの場合
            case KEIJOKBN.CAMPAIGN:
                // 販促価格適用期間内の場合
                if (campaignprice !== void 0 && campaignprice !== null) {
                    // 販促価格を販売価格とする
                    console.debug(`販促価格を適用 : ${campaignprice}`);
                    return Util.parseFloat(campaignprice);
                }
                // 条件を満たさない場合はショップ価格を販売価格とする
                console.debug(`販促価格適用外 : 0`);
                return 0;
            default:
                // 計上区分がその他なら販売価格は0になる
                console.debug(`計上区分がその他 : 0`);
                return 0;
        }
    }
    
    /**
     * 販売価格の入力可否と変更可否をチェックする
     *
     * @param {object} detailObj 受注配達明細オブジェクト
     * @param {boolean} [force] 強制的に変更可能とする場合はtrueを指定する
     * @return {Array} 販売価格の入力可否と変更可否の配列
     */
    checkSalesPrice = (detailObj, force = false) => {
        const isOverride = force || (!this.hasManualChanged(detailObj) && !detailObj.unt_changed_before);
        console.groupCollapsed(`販売価格の入力可否/変更可否チェック (LINE = ${detailObj.index})`);
        console.debug(`強制変更フラグ = ${force}`);
        console.debug(`手入力有無 = ${this.hasManualChanged(detailObj)}`);
        console.debug(`以前の受注登録で手入力があったか？ = ${detailObj.unt_changed_before}`);
        console.debug(`販売価格を上書きするか = ${isOverride}`);
        console.debug(`単品明細フラグ = ${detailObj.tanpinmeisaiflg}`);
        console.debug(`得意先区分 = ${this.tokuisakikbn}`);
        console.debug(`価格区分 = ${this.getPricekbn(detailObj)}`);
        console.debug(`計上区分 = ${detailObj.keijokbn}`);
        if (FLG.OFF === detailObj.tanpinmeisaiflg) {
            console.debug("セット商品 … 入力不可、変更可");
            console.groupEnd();
            return [true, true];
        }
        if (TOKUISAKIKBN.KOKYAKU === this.tokuisakikbn) {
            if (PRICEKBN.HYOJUN == this.getPricekbn(detailObj)) {
                console.debug("得意先：顧客、価格区分：標準 … 入力不可、変更可");
                console.groupEnd();
                return [true, true];
            } else {
                if (KEIJOKBN.URIAGE === detailObj.keijokbn
                    || KEIJOKBN.CAMPAIGN === detailObj.keijokbn) {
                    console.debug(`得意先：顧客、価格区分：オープン、計上区分：売上またはキャンペーン … 入力可、変更可否：${isOverride}`);
                    console.groupEnd();
                    return [false, isOverride];
                } else {
                    console.debug(`得意先：顧客、価格区分：オープン、計上区分：売上、キャンペーン以外 … 入力不可、変更不可`);
                    console.groupEnd();
                    return [true, false];
                }
            }
        } else {
            if (KEIJOKBN.KEIHI === detailObj.keijokbn) {
                console.debug(`得意先：顧客以外、計上区分：経費 … 入力可、変更不可`);
                console.groupEnd();
                return [false, false];
            } else if (KEIJOKBN.URIAGE === detailObj.keijokbn
                || KEIJOKBN.CAMPAIGN === detailObj.keijokbn) {
                console.debug(`得意先：顧客以外、計上区分：売上またはキャンペーン … 入力可、変更可否：${isOverride}`);
                console.groupEnd();
                return [false, isOverride];
            } else {
                console.debug(`得意先：顧客以外、計上区分：経費、売上、キャンペーン以外 … 入力不可、変更不可`);
                console.groupEnd();
                return [true, false];
            }
        }
    }
    
    /**
     * 販売価格が手入力されたかをチェックする
     *
     * @param {object} detailObj 受注配達明細オブジェクト
     * @return {boolean} 手入力されている場合はtrue
     */
    hasManualChanged = (detailObj) =>  Util.parseFloat(detailObj.shokidsplyprice) !== Util.parseFloat(detailObj.acptunt);
    
    /**
     * 明細の単価を設定する
     *
     * @param {object} detailObj 受注配達明細オブジェクト
     * @param {boolean} [force] 手入力の有無に関係なく単価設定を行うか
     */
    setUnt = (detailObj, force = false) => {
        // 標準単価設定
        detailObj.hyojununt = this.getHyojunPrice(detailObj);
        // 受注単価（販売価格）設定
        const [disabled, changeable] = this.checkSalesPrice(detailObj, force);
        const salesprice = this.getSalesPrice(detailObj);
        // 販売価格入力欄の活性/非活性を設定する
        detailObj.disabled_acpt_unt = disabled;
        if (disabled && !changeable) {
            // 入力不可かつ変更不可の場合は販売価格（受注単価）と初期表示価格を0にする
            detailObj.shokidsplyprice = 0;
            detailObj.acptunt = 0;
        } else if (changeable) {
            // 変更可能な場合は取得した価格を初期表示価格と販売価格（受注単価）に設定
            detailObj.shokidsplyprice = salesprice;
            detailObj.acptunt = salesprice;
        } else {
            // 上記以外の場合、販売価格も初期表示金額も変更しない
            detailObj.shokidsplyprice = detailObj.shokidsplyprice;
            detailObj.acptunt = detailObj.acptunt;
        }
    }
    
    /**
     * 受注金額の計算を行う
     *
     * @param {number} [idx] 行番号（省略時は全行を対象とする）
     */
    calculate = (idx) => {
        console.groupCollapsed(`Call OrderAmountCalculator#calculate (LINE = ${idx === void 0 ? "ALL": idx})`);
        // 行番号が指定されている場合はその行を対象に、そうでない場合は全明細行を計算する
        if (idx !== void 0) {
            this._calculateRow(this.getDetail(idx));
        } else {
            this.detailList.forEach((detailObj, _index) => {
                this._calculateRow(detailObj);
            });
        }
        this._calculateDnpy();
        console.groupEnd();
    }
    
    /**
     * 受注配達伝票の金額計算を行う
     */
    _calculateDnpy = () => {
        console.groupCollapsed("受注伝票 … 金額計算");
        let acptkin = 0;
        let sm = 0;
        let shohitaxkin = 0;
        if (this.detailList.length > 0) {
            // 明細の金額をサマリ
            acptkin = this.detailList
                .map(detailObj => Util.parseFloat(detailObj.kin))
                .reduce((previous, current) => previous + current, 0);
            const keisanhoho = this.detailList[0].keisanhoho;
            const taxhasukeisankbn = this.detailList[0].taxhasukeisankbn;
            console.debug(`計算方法 = ${keisanhoho}`);
            console.debug(`得意先区分 = ${this.tokuisakikbn}`);
            // 消費税額計算（得意先区分が経費以外の場合に計算する）
            if (TOKUISAKIKBN.KEIHI !== this.tokuisakikbn) {
                if (KEISANHOHO.MEISAI === keisanhoho) {
                    // 計算方法が明細の場合は明細の受注消費税金額をサマリ
                    shohitaxkin = this.detailList
                        .map(detailObj => Util.parseFloat(detailObj.acptshohitaxkin))
                        .reduce((previous, current) => previous + current, 0);
                } else if (KEISANHOHO.DENPYO === keisanhoho) {
                    // 計算方法が伝票単位の場合(仕入のみ？)、商品の税率ごとに金額をサマリし
                    // それぞれに税率を元に税額を算出し、それらを加算して消費税額とする
                    // 商品の税率ごとに配列をグルーピング（配列の配列になる）
                    const groupingDetailListByTaxRate = Util.scatter(this.detailList, detailObj => this.getTaxRate(detailObj.shntaxkbn) * 100);
                    // 税率ごとにグルーピングされた明細の配列それぞれをもとに消費税額を集計
                    shohitaxkin = groupingDetailListByTaxRate.map((detailList, taxRateKey) => {
                        // 税率ごとの金額サマリ
                        const summary = detailList.map(detailObj => Util.parseFloat(detailObj.kin)).reduce((previous, current) => previous + current, 0);
                        // 金額サマリと税率から消費税額を算出
                        const tax = calculateTaxEx(summary, taxRateKey / 100, taxhasukeisankbn);
                        return tax;
                    })
                    // 税率別の消費税額をサマリ
                    .reduce((previous, current) => previous + current, 0);
                }
            }
            // 税込金額算出
            sm = acptkin + shohitaxkin;
        }
        
        console.debug(`受注金額 = ${acptkin}`);
        console.debug(`合計 = ${sm}`);
        console.debug(`消費税額 = ${shohitaxkin}`);
        
        // プロパティ更新
        this.acptkin = acptkin;
        this.shohitaxkin = shohitaxkin;
        this.sm = sm;
        console.groupEnd();
    }
    
    /**
     * 受注配達明細の金額計算を行う
     *
     * @param {object} detailObj 受注配達明細オブジェクト
     */
    _calculateRow = (detailObj) => {
        console.groupCollapsed(`${detailObj.index}行目の受注配達明細 … 金額計算`);
        console.debug(`商品税区分 = ${detailObj.shntaxkbn}`);
        console.debug(`計算方法 = ${detailObj.keisanhoho}`);
        console.debug(`税端数計算区分(2:切捨/3:切上/その他:四捨五入) = ${detailObj.taxhasukeisankbn}`);
        console.debug(`売上端数計算区分(2:切捨/3:切上/その他:四捨五入) = ${detailObj.urikinhasukeisankbn}`);
        console.debug(`得意先区分 = ${this.tokuisakikbn}`);
        console.debug(`インボイス施行日 = ${this.invoicshikoymd}`);
        console.debug(`配達予定日 = ${this.dlvryytidd}`);
        console.groupCollapsed("計算前の各種項目値");
        console.debug(`受注数量 = ${detailObj.acptsuryo}`);
        console.debug(`販売価格 = ${detailObj.acptunt}`);
        console.debug(`総数 = ${detailObj.sokazu}`);
        console.debug(`金額 = ${detailObj.kin}`);
        console.debug(`受注単価消費税金額 = ${detailObj.acptuntshohitaxkin}`);
        console.debug(`受注消費税金額 = ${detailObj.acptshohitaxkin}`);
        console.groupEnd();
        // 数量または販売金額が数値として有効でない場合は0とみなして処理する
        const acptsuryo = Util.isNumber(detailObj.acptsuryo) ? detailObj.acptsuryo : 0;
        const acptunt = Util.isNumber(detailObj.acptunt) ? detailObj.acptunt : 0;
        // 数量が0の場合
        if (Util.isZero(acptsuryo)) {
            detailObj.sokazu = '';
            detailObj.kin = detailObj.acptunt * 0;
            detailObj.acptuntshohitaxkin = 0;
            detailObj.acptshohitaxkin = 0;
        } else {
            // 総数
            detailObj.sokazu = Util.parseFloat(detailObj.uriirisu) * Util.parseFloat(acptsuryo);
            // 金額（税抜）
            detailObj.kin = rounding(
                Util.parseFloat(acptunt) * Util.parseFloat(acptsuryo),
                detailObj.urikinhasukeisankbn);
            // 課税対象かを判定
            if (this.isTaxable(detailObj)) {
                // 計算区分取得
                const keisankbn = this.getKeisankbn(detailObj);
                console.debug(`計算区分(1:内税/2:外税) = ${keisankbn}`);
                // 消費税率取得（配達予定日が日付として妥当でない場合、税率は0で計算）
                const taxRate = this.dlvryytiDate ? this.getTaxRate(detailObj.shntaxkbn) : 0;
                console.debug(`消費税率 = ${taxRate}`);
                // 計算区分（内税/外税）の判定
                if (KEISANKBN.IN === keisankbn) {
                    // 内税の場合
                    // 販売価格（単価）に対する消費税（受注単価消費税金額）を算出
                    detailObj.acptuntshohitaxkin = calculateTaxEx(
                        Util.parseFloat(detailObj.acptunt), taxRate, detailObj.taxhasukeisankbn);
                    // 販売価格と単価消費税を加算し単価税込金額を算出
                    const untWithTax = rounding(
                        Util.parseFloat(detailObj.acptunt) + Util.parseFloat(detailObj.acptuntshohitaxkin),
                        detailObj.urikinhasukeisankbn);
                    // 単価税込金額に受注数量を乗じて税込金額を算出
                    const kinWithTax = rounding(
                        untWithTax * Util.parseFloat(detailObj.acptsuryo),
                        detailObj.urikinhasukeisankbn);
                    // 税込金額から金額を減じて受注消費税金額を算出
                    detailObj.acptshohitaxkin = rounding(
                        kinWithTax - Util.parseFloat(detailObj.kin),
                        detailObj.taxhasukeisankbn);
                } else {
                    // 外税の場合
                    console.debug("外税");
                    // 金額に対する消費税(受注消費税金額)を算出
                    detailObj.acptshohitaxkin = calculateTaxEx(detailObj.kin, taxRate, detailObj.urikinhasukeisankbn);
                    // 受注単価消費税金額はゼロ
                    detailObj.acptuntshohitaxkin = 0;
                }
            } else {
                // 課税対象ではない場合
                console.debug("非課税");
                if (detailObj.keisanhoho !== KEISANHOHO.NONE) {
                    detailObj.acptuntshohitaxkin = 0;
                    detailObj.acptshohitaxkin = 0;
                }
            }
        }
        console.groupCollapsed("計算後の各種項目値");
        console.debug(`受注数量 = ${detailObj.acptsuryo}`);
        console.debug(`販売価格 = ${detailObj.acptunt}`);
        console.debug(`総数 = ${detailObj.sokazu}`);
        console.debug(`金額 = ${detailObj.kin}`);
        console.debug(`受注単価消費税金額 = ${detailObj.acptuntshohitaxkin}`);
        console.debug(`受注消費税金額 = ${detailObj.acptshohitaxkin}`);
        console.groupEnd();
        console.groupEnd();
    }
    
    /**
     * 課税対象かを判定する
     *
     * @param {object} detailObj 受注配達明細オブジェクト
     * @return {boolean} 課税対象の場合、true
     */
    isTaxable = (detailObj) => {
        // 以下の条件を満たす場合、課税とする
        //   商品税区分が空、課税、軽減のいずれか
        //   計算方法が明細
        //   得意先区分が経費以外
        return (Util.isEmptyString(detailObj.shntaxkbn)
                || detailObj.shntaxkbn === TAXKBN.KAZEI
                || detailObj.shntaxkbn === TAXKBN.KEIGEN)
                && detailObj.keisanhoho === KEISANHOHO.MEISAI
                && this.tokuisakikbn !== TOKUISAKIKBN.KEIHI;
    }
    
    /**
     * 得意先区分を元に卸対象かを判定する
     *
     * @return {boolean} 卸対象の場合true
     */
    isWholesale = () =>  OROSHI.includes(this.tokuisakikbn);
    
    /**
     * 計算区分を取得する
     *
     * @param {object} detailObj 受注配達明細オブジェクト
     * @return {string} 計算区分
     */
    getKeisankbn = (detailObj) => {
        if (!Util.isEmptyString(this.invoicshikoymd)
            && Util.isAfter(this.invoicshikoymd, this.dlvryytidd)) {
            console.debug("インボイス施行日以降のため外税");
            return KEISANKBN.EX;
        } else {
            if (this.isWholesale()) {
                console.debug("卸対象のため外税");
                return KEISANKBN.EX;
            } else {
                console.debug(`税計算区分切替日により判定(切替日 = ${detailObj.taxkeisankbnurikirikaedd})`);
                return Util.getCurrentValue(
                    detailObj.genzkeisankbn,
                    detailObj.shinkeisankbn,
                    detailObj.taxkeisankbnurikirikaedd,
                    this.dlvryytidd);
            }
        }
    }

    /**
     * 消費税率を取得する
     *
     * @param {string} shntaxkbn 商品税区分
     * @return {number} 消費税率
     */
    getTaxRate = (shntaxkbn) => {
        const taxInfo = this._taxInfoList.find((info) =>
            // 消費税情報から商品税区分が一致し適用期間内の情報を取得する 
            shntaxkbn === info.taxkbn && Util.isDateBetween(this.dlvryytidd, info.tekiyostrtdd, info.tekiyoshrydd)
        );
        return taxInfo === void 0 ? 0 : Util.parseFloat(taxInfo.shohitax) / 100;
    }
}

/**
 * 外税の消費税を算出する
 *
 * @param {number} amount 税抜金額
 * @param {number} taxRate 消費税率
 * @param {string} hasukeisankbn 端数計算区分
 * @return {number} 消費税額
 */
const calculateTaxEx = (amount, taxRate, hasukeisankbn) => rounding(amount * taxRate, hasukeisankbn);

/**
 * 端数計算区分に従い、任意の桁で値を丸める
 *
 * @param {number} value 丸める数値
 * @param {string} hasukeisankbn 端数計算区分
 * @param {number} [pricision] 精度
 * @return {number} 丸めた値
 */
const rounding = (value, hasukeisankbn, pricision = 0) => {
    switch (hasukeisankbn) {
        case HASUKEISANKBN.KIRISUTE:
            return floor(value, pricision);
        case HASUKEISANKBN.KIRIAGE:
            return ceil(value, pricision);
        default:
            return round(value, pricision);
    }
}

/**
 * 任意の桁で四捨五入する
 *
 * @param {number} value 四捨五入する数値
 * @param {number} [pricision] 精度
 * @return {number} 四捨五入した値
 */
const round =  (value, pricision = 0) => _extendRoundingMethod(Math.round)(value, pricision);

/**
 * 任意の桁で切り捨てする
 
 * @param {number} value 切り捨てする数値
 * @param {number} [pricision] 精度
 * @return {number} 切り捨てした値
 */
const floor =  (value, pricision = 0) => _extendRoundingMethod(Math.floor)(value, pricision);

/**
 * 任意の桁で切り上げする
 *
 * @param {number} value 切り上げする数値
 * @param {number} [pricision] 精度
 * @return {number} 切り上げした値
 */
const ceil =  (value, pricision = 0) => _extendRoundingMethod(Math.ceil)(value, pricision);

/**
 * Mathクラスの丸めメソッドを精度や負数に対応するため拡張した関数を返す
 *
 * @param {function} rFunc Mathに定義された丸め用メソッド
 * @return {function} 負数を考慮し精度を指定可能な丸めメソッド
 */
const _extendRoundingMethod = (rFunc) => {
    return (value, pricision = 0) => {
        const parsed = Util.parseFloat(value);
        const sign = Math.sign(parsed) < 0 ? -1 : 1;
        const pow = Math.pow(10, pricision);
        return rFunc((parsed * sign) * pow) / pow * sign;
    };
}
