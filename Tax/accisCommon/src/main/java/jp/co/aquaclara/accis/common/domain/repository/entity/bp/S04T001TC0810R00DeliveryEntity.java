package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntityKey;
import lombok.Data;

/**
 * Database Table Remarks: 納品書データ
 *
 * This class corresponds to the database table S04T001T
 */
@Data
public class S04T001TC0810R00DeliveryEntity extends S04T001TEntityKey implements Serializable {

    // AP基本コード
    private String apkihoncd;
    // ショップコード
    private String shopcd;
    // 受注伝票番号
    private String acptdnpyno;
    // 枝番号
    private Short edano;
    // 運用日付
    private String unyodate;
    // 伝票ステータス_配達済
    private String dnpysts;
    // 印字フラグ1
    private String prntmemon1injiflg;
    // 印字フラグ2
    private String prntmemon2injiflg;
    // 印字フラグ3
    private String prntmemon3injiflg;
    // 受託配達顧客区分_NA受託
    private String jutakudlvrycstkbn_5;
    // 受託配達顧客区分_配達受託
    private String jutakudlvrycstkbn_6;
    // 得意先区分_NA受託
    private String tokuisakikbn_5;
    // 得意先区分_配達受託
    private String tokuisakikbn_6;
    // 回収方法区分_代引き
    private String kaishuhohokbn_7;
    // 明細区分_配布物
    private String meisaikbn_1;
    // 明細区分_代引きレンタル代等
    private String meisaikbn_2;
    // 明細区分_消費税調整
    private String meisaikbn_3;
    // 税区分_課税
    private String taxkbn_1;
    // 税区分_非課税
    private String taxkbn_2;
    // 税区分_不課税
    private String taxkbn_3;
    // 税区分_課税軽減
    private String taxkbn_4;
    // 税区分_子明細商品の構成品
    private String taxkbn_5;
    // 単品明細フラグ_単品明細行
    private String onemeisaicolumn;
    // 消費税_8%
    private String zei8per;
    // 消費税_10%
    private String zei10per;
    // 伝票種別_納品書兼受領書
    private String dnpykind;
    // 有効区分
    private String yukokbn;
    // セット商品明細印字フラグ_印字する
    private String stshnmeisaiinjiflg;
    // 入力明細フラグ_入力行
    private String nyuryokumeisaiflg;
    // 得意先区分_顧客
    private String tokuisakikbn_1;
    // 支払サイト用コンディションID
    private String conditionid_086;
    // 得意先敬称区分用コンディションID
    private String conditionid_082;
    // インボイス施行フラグ
    private String invoiceFlg;
    // システム管理マスタ用AP基本コード
    private String sys_apkihoncd;
    // システム管理マスタ用ショップコード
    private String sys_shopcd;
    // システム管理マスタ用レコード区分
    private String sys_recordkbn;
    // 計上区分取得用設定番号
    private String setteino_009;

    // システム日付
    private String ymd;
    // 受注伝票番号
    private String acptdnpynomei;
    // 枝番号
    private String edanomei;
    // 売上伝票番号
    private String uridnpyno;
    // 売上伝票枝番号
    private String uridnpyedano;
    // AP基本コード
    private String apkihoncdmei;
    // ショップコード
    private String shopcdmei;
    // 得意先コード
    private String tokuisakicd;
    // 配達希望時間
    private String dlvrykibojikan;
    // 不在置き可能フラグ
    private String fzaiokikanoflg;
    // 配達予定日
    private String dlvryytidd;
    // 売上日
    private String uridd;
    // 次回配達予定日
    private String jikaidlvryytidd;
    // 印刷メモ１
    private String prntmemon1;
    // 印刷メモ２
    private String prntmemon2;
    // 印刷メモ３
    private String prntmemon3;
    // 印刷メモ１印字フラグ
    private String prntmemon1injiflgmei;
    // 印刷メモ２印字フラグ
    private String prntmemon2injiflmei;
    // 印刷メモ３印字フラグ
    private String asprntmemon3injiflg;
    // 配達順
    private String dlvryjun;
    // 前回締日
    private String znkshimedd;
    // 前回繰越金額
    private String znkkurikoshikin;
    // 預かりボトル１２Ｌ本数
    private String azukaribtln1n2rittoruhns;
    // 預かりボトル7L本数
    private String azukaribtln8rittoruhns;
    // 回収予定ボトル１２Ｌ本数
    private String kybtln1n2rittoruhns;
    // 回収予定ボトル7L本数
    private String kybtln8rittoruhns;
    // 前回配達日
    private String znkdlvrydd;
    // 前回配達本数
    private String znkdlvryhns;
    // 受注日
    private String acptdd;
    // 配達希望時間区分
    private String dlvrykibojikankbn;
    // 税計算区分
    private String taxkeisankbn;
    // 受注時代引フラグ
    private String acptthdaibikiflg;
    // 受注数量（明細）
    private String acptsuryo;
    // 単価（明細）※単価＋単価消費税金額
    private String tanka;
    // 税込み金額（明細）※金額＋消費税金額
    private String uzei;
    // 受注金額（明細）※金額
    private String acptkin;
    // 受注消費税金額（明細）※消費税金額
    private String acptshohitaxkin;
    // 受注金額（納品金額）※金額 + 消費税
    private String zan_uzei;
    // 委託先数量
    private String suryoitaku;
    // 配達希望時間終了
    private String dlvrykibojikanshry;
    // 代引きレンタル代のみの明細税込金額の合計
    private String acptuzei;
    // 伝票ステータス
    private String dnpystsmei;
    // 受注区分詳細
    private String acptkbnshs;
    // 明細備考
    private String meisaibiko;
    // 明細区分
    private String meisaikbn;
    // 入力明細フラグ
    private String nyuryokumeisaiflgmei;
    // 計上区分
    private String keijokbn;
    // 計上区分名称
    private String keijokbnnm;
    // 郵便番号
    private String yubinno;
    // 住所１
    private String adrsn1;
    // 住所２
    private String adrsn2;
    // 住所３
    private String adrsn3;
    // 連絡先番号区分１
    private String renrakusakinokbnn1;
    // 連絡先番号１
    private String renrakusakinon1;
    // 連絡先番号区分２
    private String renrakusakinokbnn2;
    // 連絡先番号２
    private String renrakusakinon2;
    // 連絡先番号区分３
    private String renrakusakinokbnn3;
    // 連絡先番号３
    private String renrakusakinon3;
    // 連絡先番号区分４
    private String renrakusakinokbnn4;
    // 連絡先番号４
    private String renrakusakinon4;
    // 得意先名称１
    private String tokuisakinmn1;
    // 得意先名称２
    private String tokuisakinmn2;
    // 得意先敬称区分
    private String tokuisakikeishokbn;
    // 地図番号
    private String chizuno;
    // 入会日
    private String nyukaidd;
    // 申込受付日
    private String moshikomircptdd;
    // 注意得意先区分ＡＰ
    private String chuitokuisakikbnap;
    // 納品先情報１
    private String nohinsakijohon1;
    // 納品先情報２
    private String nohinsakijohon2;
    // 得意先状態
    private String tokuisakijotai;
    // 得意先区分
    private String tokuisakikbn;
    // 受託配達顧客区分
    private String jutakudlvrycstkbn;
    // 委託先得意先区分
    private String tokuisakikbn_itaku;
    // 事業所コード
    private String jigyoshocd;
    // 継続ランク
    private String keizokurnk;
    // 消費ランク
    private String shohirnk;
    // AP別ランク
    private String apbetsutokuisakirnk;
    // 受注受付区分
    private String acptrcptkbn;
    // 商品名称切替日
    private String shnnmkirikaedd;
    // 商品コード
    private String shncd;
    // 現商品名称
    private String genzshnnm;
    // 新商品名称
    private String shinshnnm;
    // 売上取扱単位名称
    private String uritrtskitaninm;
    // 税区分
    private String taxkbn;
    // 配達エリア名称
    private String dlvryareanm;
    // 配達ＷＥＥＫＡ
    private String dlvryshua;
    // 配達ＷＥＥＫＢ
    private String dlvryshub;
    // 配達ＷＥＥＫＣ
    private String dlvryshuc;
    // 配達ＷＥＥＫＤ
    private String dlvryshud;
    // 日曜配達可能日
    private String sunddlvrykanodd;
    // 月曜配達可能日
    private String monddlvrykanodd;
    // 火曜配達可能日
    private String tueddlvrykanodd;
    // 水曜配達可能日
    private String wedddlvrykanodd;
    // 木曜配達可能日
    private String theddlvrykanodd;
    // 金曜配達可能日
    private String friddlvrykanodd;
    // 土曜配達可能日
    private String satddlvrykanodd;
    // 配達担当者名
    private String dlvrytnshamei;
    // 営業担当者名称
    private String eigyotnshamei;
    // 決済機関手続中フラグ
    private String ksikkntetsuzukiflg;
    // 請求締日
    private String skyshimedd;
    // 支払日
    private String shrdd;
    // 支払サイト
    private String shrsaito;
    // 滞留区分
    private String tairyukbn;
    // 回収方法区分
    private String kaishuhohokbn;
    // 伝票お知らせ内容１
    private String dnpyoshirasenaiyon1;
    // 納品書お預け容器数表示区分
    private String noshoazyokikazudsplykbn;
    // 差出人イメージ
    private String sshdshhitoimage;
    // 退会時未回収金額表示区分
    private String taikaithmikaishukindsplykbn;
    // 現在残高（得意先実績テーブル）
    private String genzaizd;
    // 税込売上金額
    private String urikin;
    // 金額出力無フラグ（納品金額印字 ）
    private String kinotptnashiflg;
    // 得意先ショップコード
    private String tokuisakishopcd;
    // 未来売上
    private String sakiuzei;
    // 受注明細伝票番号
    private String acptdnpymeisaino;
    // 受注明細伝票枝番号
    private String acptdnpymeisaiedano;
    // 売上伝票明細番号
    private String uridnpymeisaino;
    // 売上伝票明細枝番号
    private String uridnpymeisaiedano;
    // 委託相手ＡＰ基本コード
    private String itakuaiteapkihoncd;
    // 委託相手ショップコード
    private String itakuaiteshopcd;
    // 委託相手受注伝票番号
    private String itakuaiteacptdnpyno;
    // 8%税抜金額
    private String zeinuki8per;
    // 8%消費税
    private String zei8permei;
    // 10%税抜金額
    private String zeinuki10per;
    // 10%消費税
    private String zei10permei;
    // 非課税税抜
    private String zeinukihikazei;
    // 非課税消費税
    private String zeihikazei;
    // 不課税税抜
    private String zeinukifukazei;
    // 不課税消費税
    private String zeifukakazei;
    // 金額合計
    private String totalkin;
    // 消費税金額合計
    private String totalzei;
    // セット商品コード
    private String stshncd;
    // （親）税区分
    private String oyazeikbn;
    // 事業所名称
    private String jigyoshonm;
    // 印刷メモ出力区分
    private String prntmemootptkbn;
    // ショップ名称
    private String shopnm;
    // 得意先ショップ名称
    private String tokushopnm;
    // 支払サイト名称
    private String shrsaitonm;
    // 得意先敬称区分名称
    private String tokuisakikeishokbnnm;
    // 単体伝票締フラグ
    private String singldnpyflg;
    // 配達済みフラグ
    private boolean dlvryedflg;

    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", ymd=").append(ymd);
        sb.append(", acptdnpynomei=").append(acptdnpynomei);
        sb.append(", edanomei=").append(edanomei);
        sb.append(", uridnpyno=").append(uridnpyno);
        sb.append(", uridnpyedano=").append(uridnpyedano);
        sb.append(", apkihoncdmei=").append(apkihoncdmei);
        sb.append(", shopcdmei=").append(shopcdmei);
        sb.append(", tokuisakicd=").append(tokuisakicd);
        sb.append(", dlvrykibojikan=").append(dlvrykibojikan);
        sb.append(", fzaiokikanoflg=").append(fzaiokikanoflg);
        sb.append(", dlvryytidd=").append(dlvryytidd);
        sb.append(", uridd=").append(uridd);
        sb.append(", jikaidlvryytidd=").append(jikaidlvryytidd);
        sb.append(", prntmemon1=").append(prntmemon1);
        sb.append(", prntmemon2=").append(prntmemon2);
        sb.append(", prntmemon3=").append(prntmemon3);
        sb.append(", prntmemon1injiflgmei=").append(prntmemon1injiflgmei);
        sb.append(", prntmemon2injiflgmei=").append(prntmemon2injiflmei);
        sb.append(", asprntmemon3injiflg=").append(asprntmemon3injiflg);
        sb.append(", dlvryjun=").append(dlvryjun);
        sb.append(", znkshimedd=").append(znkshimedd);
        sb.append(", znkkurikoshikin=").append(znkkurikoshikin);
        sb.append(", azukaribtln1n2rittoruhns=").append(azukaribtln1n2rittoruhns);
        sb.append(", azukaribtln8rittoruhns=").append(azukaribtln8rittoruhns);
        sb.append(", kybtln1n2rittoruhns=").append(kybtln1n2rittoruhns);
        sb.append(", kybtln8rittoruhns=").append(kybtln8rittoruhns);
        sb.append(", znkdlvrydd=").append(znkdlvrydd);
        sb.append(", znkdlvryhns=").append(znkdlvryhns);
        sb.append(", acptdd=").append(acptdd);
        sb.append(", dlvrykibojikankbn=").append(dlvrykibojikankbn);
        sb.append(", taxkeisankbn=").append(taxkeisankbn);
        sb.append(", acptthdaibikiflg=").append(acptthdaibikiflg);
        sb.append(", acptsuryo=").append(acptsuryo);
        sb.append(", tanka=").append(tanka);
        sb.append(", uzei=").append(uzei);
        sb.append(", acptkin=").append(acptkin);
        sb.append(", acptshohitaxkin=").append(acptshohitaxkin);
        sb.append(", zan_uzei=").append(zan_uzei);
        sb.append(", suryoitaku=").append(suryoitaku);
        sb.append(", dlvrykibojikanshry=").append(dlvrykibojikanshry);
        sb.append(", acptuzei=").append(acptuzei);
        sb.append(", dnpystsmei=").append(dnpystsmei);
        sb.append(", acptkbnshs=").append(acptkbnshs);
        sb.append(", meisaibiko=").append(meisaibiko);
        sb.append(", meisaikbn=").append(meisaikbn);
        sb.append(", nyuryokumeisaiflgmei=").append(nyuryokumeisaiflgmei);
        sb.append(", keijokbn=").append(keijokbn);
        sb.append(", keijokbnnm=").append(keijokbnnm);
        sb.append(", yubinno=").append(yubinno);
        sb.append(", adrsn1=").append(adrsn1);
        sb.append(", adrsn2=").append(adrsn2);
        sb.append(", adrsn3=").append(adrsn3);
        sb.append(", renrakusakinokbnn1=").append(renrakusakinokbnn1);
        sb.append(", renrakusakinon1=").append(renrakusakinon1);
        sb.append(", renrakusakinokbnn2=").append(renrakusakinokbnn2);
        sb.append(", renrakusakinon2=").append(renrakusakinon2);
        sb.append(", renrakusakinokbnn3=").append(renrakusakinokbnn3);
        sb.append(", renrakusakinon3=").append(renrakusakinon3);
        sb.append(", renrakusakinokbnn4=").append(renrakusakinokbnn4);
        sb.append(", renrakusakinon4=").append(renrakusakinon4);
        sb.append(", tokuisakinmn1=").append(tokuisakinmn1);
        sb.append(", tokuisakinmn2=").append(tokuisakinmn2);
        sb.append(", tokuisakikeishokbn=").append(tokuisakikeishokbn);
        sb.append(", chizuno=").append(chizuno);
        sb.append(", nyukaidd=").append(nyukaidd);
        sb.append(", moshikomircptdd=").append(moshikomircptdd);
        sb.append(", chuitokuisakikbnap=").append(chuitokuisakikbnap);
        sb.append(", nohinsakijohon1=").append(nohinsakijohon1);
        sb.append(", nohinsakijohon2=").append(nohinsakijohon2);
        sb.append(", tokuisakijotai=").append(tokuisakijotai);
        sb.append(", tokuisakikbn=").append(tokuisakikbn);
        sb.append(", jutakudlvrycstkbn=").append(jutakudlvrycstkbn);
        sb.append(", tokuisakikbn_itaku=").append(tokuisakikbn_itaku);
        sb.append(", jigyoshocd=").append(jigyoshocd);
        sb.append(", keizokurnk=").append(keizokurnk);
        sb.append(", shohirnk=").append(shohirnk);
        sb.append(", apbetsutokuisakirnk=").append(apbetsutokuisakirnk);
        sb.append(", acptrcptkbn=").append(acptrcptkbn);
        sb.append(", shnnmkirikaedd=").append(shnnmkirikaedd);
        sb.append(", shncd=").append(shncd);
        sb.append(", genzshnnm=").append(genzshnnm);
        sb.append(", shinshnnm=").append(shinshnnm);
        sb.append(", uritrtskitaninm=").append(uritrtskitaninm);
        sb.append(", taxkbn=").append(taxkbn);
        sb.append(", dlvryareanm=").append(dlvryareanm);
        sb.append(", dlvryshua=").append(dlvryshua);
        sb.append(", dlvryshub=").append(dlvryshub);
        sb.append(", dlvryshuc=").append(dlvryshuc);
        sb.append(", dlvryshud=").append(dlvryshud);
        sb.append(", sunddlvrykanodd=").append(sunddlvrykanodd);
        sb.append(", monddlvrykanodd=").append(monddlvrykanodd);
        sb.append(", tueddlvrykanodd=").append(tueddlvrykanodd);
        sb.append(", wedddlvrykanodd=").append(wedddlvrykanodd);
        sb.append(", theddlvrykanodd=").append(theddlvrykanodd);
        sb.append(", friddlvrykanodd=").append(friddlvrykanodd);
        sb.append(", satddlvrykanodd=").append(satddlvrykanodd);
        sb.append(", dlvrytnshamei=").append(dlvrytnshamei);
        sb.append(", eigyotnshamei=").append(eigyotnshamei);
        sb.append(", ksikkntetsuzukiflg=").append(ksikkntetsuzukiflg);
        sb.append(", skyshimedd=").append(skyshimedd);
        sb.append(", shrdd=").append(shrdd);
        sb.append(", shrsaito=").append(shrsaito);
        sb.append(", tairyukbn=").append(tairyukbn);
        sb.append(", kaishuhohokbn=").append(kaishuhohokbn);
        sb.append(", dnpyoshirasenaiyon1=").append(dnpyoshirasenaiyon1);
        sb.append(", noshoazyokikazudsplykbn=").append(noshoazyokikazudsplykbn);
        sb.append(", sshdshhitoimage=").append(sshdshhitoimage);
        sb.append(", taikaithmikaishukindsplykbn=").append(taikaithmikaishukindsplykbn);
        sb.append(", genzaizd=").append(genzaizd);
        sb.append(", urikin=").append(urikin);
        sb.append(", kinotptnashiflg=").append(kinotptnashiflg);
        sb.append(", tokuisakishopcd=").append(tokuisakishopcd);
        sb.append(", sakiuzei=").append(sakiuzei);
        sb.append(", acptdnpymeisaino=").append(acptdnpymeisaino);
        sb.append(", acptdnpymeisaiedano=").append(acptdnpymeisaiedano);
        sb.append(", uridnpymeisaino=").append(uridnpymeisaino);
        sb.append(", uridnpymeisaiedano=").append(uridnpymeisaiedano);
        sb.append(", itakuaiteapkihoncd=").append(itakuaiteapkihoncd);
        sb.append(", itakuaiteshopcd=").append(itakuaiteshopcd);
        sb.append(", itakuaiteacptdnpyno=").append(itakuaiteacptdnpyno);
        sb.append(", zeinuki8per=").append(zeinuki8per);
        sb.append(", zei8permei=").append(zei8permei);
        sb.append(", zeinuki10per=").append(zeinuki10per);
        sb.append(", zei10permei=").append(zei10permei);
        sb.append(", zeinukihikazei=").append(zeinukihikazei);
        sb.append(", zeihikazei=").append(zeihikazei);
        sb.append(", zeinukifukazei=").append(zeinukifukazei);
        sb.append(", zeifukakazei=").append(zeifukakazei);
        sb.append(", totalkin=").append(totalkin);
        sb.append(", totalzei=").append(totalzei);
        sb.append(", stshncd=").append(stshncd);
        sb.append(", oyazeikbn=").append(oyazeikbn);
        sb.append(", jigyoshonm=").append(jigyoshonm);
        sb.append(", prntmemootptkbn=").append(prntmemootptkbn);
        sb.append(", shopnm=").append(shopnm);
        sb.append(", tokushopnm=").append(tokushopnm);
        sb.append(", shrsaitonm=").append(shrsaitonm);
        sb.append(", tokuisakikeishokbnnm=").append(tokuisakikeishokbnnm);
        sb.append(", singldnpyflg=").append(singldnpyflg);
        sb.append("]");
        return sb.toString();
    }
}
