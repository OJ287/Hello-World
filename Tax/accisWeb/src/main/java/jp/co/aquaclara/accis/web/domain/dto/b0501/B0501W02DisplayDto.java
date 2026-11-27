package jp.co.aquaclara.accis.web.domain.dto.b0501;

import lombok.Data;

/**
 * B0501w02_得意先マスタ(登録画面)初期表示DTO.
 * 
 * @author accisdev021
 *
 */
@Data
public class B0501W02DisplayDto {

    /* 得意先情報 */
    // ＡＰ基本コード
    private String apkihoncd;
    // ショップコード
    private String shopcd;
    // ショップ区分
    private String shopkbn;
    // 事業所管理
    private String jigyoshokanri;
    // 得意先コード
    private String tokuisakicd;
    // 得意先状態
    private String tokuisakijotai;
    // 得意先状態名称
    private String tokuisakijotainm;
    // 状態ステータス
    private String jotaists;
    // 状態ステータス名称
    private String jotaistsnm;
    // 得意先区分
    private String tokuisakikbn;
    // 受託配達顧客区分
    private String jutakudlvrycstkbn;
    // コード種別区分
    private String cdkindkbn;
    // 利用サーバー区分
    private String riyowsvkbn;
    // 業種区分
    private String gyoshukbn;
    // 請求先コード
    private String skysakicd;
    // 得意先ショップコード
    private String tokuisakishopcd;
    // 得意先ショップ名称
    private String tokuisakishopnm;
    // 事業所コード
    private String jigyoshocd;
    // 得意先名称１
    private String tokuisakinmn1;
    // 得意先名称２
    private String tokuisakinmn2;
    // 得意先敬称区分
    private String tokuisakikeishokbn;
    // 得意先名称カナ１
    private String tokuisakinmkanan1;
    // 得意先名称カナ２
    private String tokuisakinmkanan2;
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
    // コールセンター検索用電話番号１
    private String clckensakuyotelnon1;
    // コールセンター検索用電話番号２
    private String clckensakuyotelnon2;
    // コールセンター検索用電話番号３
    private String clckensakuyotelnon3;
    // コールセンター検索用電話番号４
    private String clckensakuyotelnon4;
    // メールアドレス
    private String mailadrse;
    // メルマガ区分
    private String merumagakbn;
    // 利用者数
    private String kazokukoseikazudansei;
    // 内子供数
    private String kazokukoseikazujosei;
    // 家族構成数子供
    private String kazokukoseikazukodomo;
    // 入会者誕生日
    private String nyushatanjodd;
    // 申込受付日
    private String moshikomircptdd;
    // 入会申込書番号
    private String nyukaimokoshono;
    // 申込キャンセル日
    private String moshikomicncldd;
    // 申込登録者コード
    private String moshikomitoshacd;
    // 申込登録者名
    private String moshikomitoshamei;
    // 入会日
    private String nyukaidd;
    // 申込受付区分
    private String moshikomircptkbn;
    // 会員申込書番号
    private String kaiinmokoshono;
    // 入会日登録者コード
    private String nyukaiddtoshacd;
    // 入会日登録者名
    private String nyukaiddtoshamei;
    // 退会日
    private String taikaidd;
    // 退会理由区分
    private String taikairiyukbn;
    // 退会理由詳細
    private String taikairiyushs;
    // 退会日登録者コード
    private String taikaiddtoshacd;
    // 退会日登録者名
    private String taikaiddtoshamei;
    // 営業エリアコード
    private String eigyoareacd;
    // 営業エリア名称
    private String eigyoareanm;
    // 特別単価区分
    private String tokubetsuuntkbn;
    // 営業担当者コード
    private String eigyotnshacd;
    // 営業担当者名
    private String eigyotnshamei;
    // 売上計上担当者区分
    private String urikeijotnshakbn;
    // 獲得区分
    private String kakutokukbn;
    // 受付区分
    private String rcptkbn;
    // 媒体区分
    private String baitaikbn;
    // 獲得担当者コード
    private String kakutokutnshacd;
    // 獲得担当者名
    private String kakutokutnshamei;
    // 本部エージェントコード
    private String honbuejntcd;
    // 本部エージェント名
    private String honbuejntmei;
    // 獲得エージェントコード
    private String kakutokuejntcd;
    // 獲得エージェント名
    private String kakutokuejntmei;
    // 社内担当者コード
    private String shanaitnshacd;
    // 社内担当者名
    private String shanaitnshamei;
    // 紹介得意先コード
    private String shoukaitokuisakicd;
    // 紹介得意先名
    private String shoukaitokuisakinm;
    // 販促詳細コード
    private String hansokushscd;
    // 販促詳細名称
    private String hansokushsnm;
    // 獲得詳細メモ
    private String kakutokushsmemo;
    // マージン対象区分
    private String mgntrgtkbn;
    // ワンショットコード
    private String wanshottocd;
    // ワンショット名称
    private String wanshottonm;
    // ボーナスマージン件数
    private String bonasumgncnt;
    // ボーナスマージン金額
    private String bonasumgnkin;
    // ボトルマージン対象期間
    private String btlmgntrgtdkikan;
    // マージン用商品コード１
    private String mgnyoshncdn1;
    // マージン用商品名称１
    private String mgnyoshnnm1;
    // マージン用商品コード２
    private String mgnyoshncdn2;
    // マージン用商品名称２
    private String mgnyoshnnm2;
    // マージン用商品コード３
    private String mgnyoshncdn3;
    // マージン用商品名称３
    private String mgnyoshnnm3;
    // マージン用商品コード４
    private String mgnyoshncdn4;
    // マージン用商品名称４
    private String mgnyoshnnm4;
    // マージンコード１
    private String mgncdn1;
    // マージン名称１
    private String mgnnm1;
    // マージンコード２
    private String mgncdn2;
    // マージン名称２
    private String mgnnm2;
    // マージンコード３
    private String mgncdn3;
    // マージン名称３
    private String mgnnm3;
    // マージンコード４
    private String mgncdn4;
    // マージン名称４
    private String mgnnm4;
    // 自動値引コード１
    private String jidonebikicdn1;
    // 自動値引名称１
    private String jidonebikinm1;
    // 月間販売本数１
    String gekkanhanbaihns1;
    // 自動値引種別１
    private String jidonebikikind1;
    // 自動値引種別名称１
    private String jidonebikikindnm1;
    // 値引ボトル本数１
    private String nebikibtlhns1;
    // 値引金額１
    private String nebikikin1;
    // 自動値引商品コード１
    private String jidonebishncd1;
    // 自動値引商品名称１
    private String jidonebishnnm1;
    // 自動値引コード２
    private String jidonebikicdn2;
    // 自動値引名称２
    private String jidonebikinm2;
    // 月間販売本数２
    String gekkanhanbaihns2;
    // 自動値引種別２
    private String jidonebikikind2;
    // 自動値引種別名称２
    private String jidonebikikindnm2;
    // 値引ボトル本数２
    private String nebikibtlhns2;
    // 値引金額２
    private String nebikikin2;
    // 自動値引商品コード２
    private String jidonebishncd2;
    // 自動値引商品名称２
    private String jidonebishnnm2;
    // 自動値引コード３
    private String jidonebikicdn3;
    // 自動値引名称３
    private String jidonebikinm3;
    // 月間販売本数３
    String gekkanhanbaihns3;
    // 自動値引種別３
    private String jidonebikikind3;
    // 自動値引種別名称３
    private String jidonebikikindnm3;
    // 値引ボトル本数３
    private String nebikibtlhns3;
    // 値引金額３
    private String nebikikin3;
    // 自動値引商品コード３
    private String jidonebishncd3;
    // 自動値引商品名称３
    private String jidonebishnnm3;
    // ポイント管理有無
    private String pointkbn;
    // マイアクアポイント利用許諾
    private String myaquapointriyokyodaku;
    // マイアクア会員登録有無
    private String myaquakaiininptumu;
    // まとめ買いポイント区分
    private String mtmkipointkbn;
    // まとめ買い数量
    private String mtmkisuryo;
    // まとめ買いポイント数
    private String mtmkipointsu;
    // 入会特典区分
    private String nyukaitokutenumu;
    // 入会特典ポイント数
    private String nyukaitokutenpointsu;
    // ステージ修正（新）
    private String pointrnkshin;
    // ステージ切替日
    private String tekiyoudd;
    // 適用ステージ（現）
    private String pointrnkgen;
    // ポイントランクコード（現）
    private String pointrnkcdgen;
    // レンタル請求管理区分
    private String rtlskykanrikbn;
    // サブレンタル倉庫コード
    private String saburtlsokocd;
    // サブレンタル倉庫名称
    private String saburtlsokonm;
    // 初回サーバー設置日
    private String shokaiwsvstupdd;
    // 設置日登録者ＡＰ基本コード
    private String stupddtoshaapkihoncd;
    // 設置日登録者ショップコード
    private String stupddtoshashopcd;
    // 設置日登録者コード
    private String stupddtoshacd;
    // 設置日登録者名
    private String stupddtoshamei;
    // サーバー引上日
    private String wsvhikiagedd;
    // 引上日登録者ＡＰ基本コード
    private String hikiageddtoshaapkihoncd;
    // 引上日登録者ショップコード
    private String hikiageddtoshashopcd;
    // 引上日登録者コード
    private String hikiageddtoshacd;
    // 引上日登録者名
    private String hikiageddtoshamei;
    // 配達管轄ＡＰ基本コード
    private String dlvrykankatsuapkihoncd;
    // 配達管轄ＡＰ基本名称
    private String dlvrykankatsuapkihonnm;
    // 配達管轄ショップコード
    private String dlvrykankatsushopcd;
    // 配達管轄ショップ名称
    private String dlvrykankatsushopnm;
    // 配達管轄拠点コード
    private String dlvrykankatsukyotencd;
    // 配達管轄拠点名称
    private String dlvrykankatsukyotennm;
    // 配達会社コード
    private String dlvrycmpnycd;
    // 配達会社名称
    private String dlvrycmpnynm;
    // 配達担当者コード
    private String dlvrytnshacd;
    // 配達担当者名
    private String dlvrytnshamei;
    // 配達順
    private String dlvryjun;
    // 配達エリアコード
    private String dlvryareacd;
    // 配達エリア名称
    private String dlvryareanm;
    // 配達区分
    private String dlvrykbn;
    // 不在置き可能フラグ
    private String fzaiokikanoflg;
    // 納品先情報１
    private String nohinsakijohon1;
    // 納品先情報２
    private String nohinsakijohon2;
    // 地図番号
    private String chizuno;
    // 外部連携配達コード
    private String gaiburenkeidlvrycd;
    // 受注受付区分
    private String acptrcptkbn;
    // 受注受付区分名称
    private String acptrcptkbnnm;
    // ネスレ発注先区分
    private String nestlehachusakikbn;
    // 受注停止フラグ
    private String acptteishiflg;
    // 受注時間指定時刻
    private String acptjikanshiteijikoku;
    // 受注時間指定時刻終了
    private String acptjikanshiteijikokushry;
    // 受注時間指定区分
    private String acptjikanshiteikbn;
    // 注意得意先区分ＡＣＩ
    private String chuitokuisakikbnaci;
    // 注意内容ＡＣＩ
    private String chuinaiyoaci;
    // 注意得意先区分ＡＰ
    private String chuitokuisakikbnap;
    // 注意内容ＡＰ
    private String chuinaiyoap;
    // 連絡事項公開
    private String renrakujikokokai;
    // 特記事項非公開
    private String tokkijikohikokai;
    // 印刷メモ１
    private String prntmemon1;
    // 印刷メモ２
    private String prntmemon2;
    // 印刷メモ３
    private String prntmemon3;
    // 金額出力無フラグ
    private String kinotptnashiflg;
    // 保証金区分
    private String hoshokinkbn;
    // 保証金預かり日
    private String hoshokinazukaridd;
    // 保証金預かり金額
    private String hoshokinazukarikin;
    // 預かり書番号
    private String azushono;
    // 保証金返却日
    private String hoshokinhenkyakudd;
    // 保証金返却金額
    private String hoshokinhenkyakukin;
    // 継続ランク
    private String keizokurnk;
    // 消費ランク
    private String shohirnk;
    // ＡＰ別得意先ランク
    private String apbetsutokuisakirnk;
    // 休眠区分
    private String kyuminkbn;
    // 休眠区分名称
    private String kyuminkbnnm;
    // 与信限度金額
    private String yoshingendokin;
    // 得意先外部識別コード
    private String tokuisakigaibuskbtscd;
    // 得意先外部識別コード2
    private String tokuisakigaibuskbtscd2;
    // 得意先外部識別コード3
    private String tokuisakigaibuskbtscd3;
    // 得意先集計コード
    private String tokuisakishukeicd;
    // 得意先紹介者数
    private String tokuisakishoshakazu;
    // 有効区分
    private String yukokbn;
    // 登録者ＡＰ基本コード
    private String toshaapkihoncd;
    // 登録者ショップコード
    private String toshashopcd;
    // 登録者コード
    private String toshacd;
    // 登録者名
    private String toshamei;
    // 登録システム日付
    private String inptsystmymd;
    // 登録システム時間
    private String inptsystmjikan;
    // 登録運用日付
    private String inptunyoymd;
    // 登録ＩＰアドレス
    private String inptipadrse;
    // 登録プログラムＩＤ
    private String inptpgmid;
    // 更新者コード
    private String koshacd;
    // 更新者名
    private String koshamei;
    // 更新者ＡＰ基本コード
    private String koshaapkihoncd;
    // 更新者ショップコード
    private String koshashopcd;
    // 更新システム日付
    private String kshnsystmymd;
    // 更新システム時間
    private String kshnsystmjikan;
    // 更新運用日付
    private String kshnunyoymd;
    // 更新ＩＰアドレス
    private String kshnipadrse;
    // 更新プログラムＩＤ
    private String kshnpgmid;
    // 本部エージェントＡＰ基本コード
    private String honbuejntapkihoncd;
    // 本部エージェントショップコード
    private String honbuejntshopcd;
    // 申込登録者ＡＰ基本コード
    private String moshikomitoshaapkihoncd;
    // 申込登録者ショップコード
    private String moshikomitoshashopcd;
    // 入会日登録者ＡＰ基本コード
    private String nyukaiddtoshaapkihoncd;
    // 入会日登録者ショップコード
    private String nyukaiddtoshashopcd;
    // 退会日登録者ＡＰ基本コード
    private String taikaiddtoshaapkihoncd;
    // 退会日登録者ショップコード
    private String taikaiddtoshashopcd;
    // 休眠管理
    private String kyuminkanri;
    // 勤務先１
    private String kinmusakin1;
    // 勤務先２
    private String kinmusakin2;
    // 勤務先郵便番号
    private String kinmusakiyubinno;
    // 勤務先住所１
    private String kinmusakiadrsn1;
    // 勤務先住所２
    private String kinmusakiadrsn2;
    // 勤務先住所３
    private String kinmusakiadrsn3;
    // 勤務先電話番号
    private String kinmusakitelno;
    // 勤務先ＦＡＸ番号
    private String kinmusakifaxno;
    // 連絡先メモ１
    private String renrakusakimemon1;
    // 連絡先メモ２
    private String renrakusakimemon2;
    // 連絡先メモ３
    private String renrakusakimemon3;
    // 連絡先メモ４
    private String renrakusakimemon4;
    // スタートアップ
    private String startup;
    // 子供名１
    private String kodomomein1;
    // 子供誕生日１
    private String kodomotanjoddn1;
    // 子供名２
    private String kodomomein2;
    // 子供誕生日２
    private String kodomotanjoddn2;
    // 子供名３
    private String kodomomein3;
    // 子供誕生日３
    private String kodomotanjoddn3;
    // 値引開始年月１
    private String nebikistrtdydmn1;
    // 値引終了年月１
    private String nebikishrydydmn1;
    // 値引開始年月２
    private String nebikistrtdydmn2;
    // 値引終了年月２
    private String nebikishrydydmn2;
    // 値引開始年月３
    private String nebikistrtdydmn3;
    // 値引終了年月３
    private String nebikishrydydmn3;
    // まとめ買いサービス区分
    private String mtmkisvkbn;
    // まとめ買い有料本数
    private String mtmkiyuryohns;
    // まとめ買い無料本数
    private String mtmkimuryohns;
    // 無料ボトルサービス区分
    private String muryobtlsvkbn;
    // マイアクア単価情報表示有無フラグ
    private String myaqauntjohodsplyumuflg;
    // マイアクア請求情報表示有無フラグ
    private String myaqaskyjohodsplyumuflg;
    // 料金プラン設定日
    private String rknplnsetteidd;
    // 受注日
    private String acptdd;
    // 有償本数
    private String yusyohns;
    // 無償本数
    private String mushohns;
    // 預かりボトル１２Ｌ本数
    private String azukaribtln1n2rittoruhns;
    // 預かりボトル８Ｌ本数
    private String azukaribtln8rittoruhns;
    // 預かりボトル名称レギュラー
    private String regularbottlenm;
    // 預かりボトル名称スリム
    private String slimbottlenm;
    // 最終定期受注配達予定日
    private String saishuteikiacptdlvryytidd;
    // 現在残高
    private String genzaizd;
    // インボイス施行日
    private String invoicshikoymd;
    // 計上前受注金額
    private String keijomaeacptkin;
    // 前回配達受注日
    private String znkdlvryacptdd;
    // 前回配達日
    private String znkdlvrydd;
    // 前回配達レギュラー有償本数
    private String znkdlvryrgrayusyohns;
    // 前回配達レギュラー無償本数
    private String znkdlvryrgramushohns;
    // 前回配達スリム有償本数
    private String znkdlvrysrmyusyohns;
    // 前回配達スリム無償本数
    private String znkdlvrysrmmushohns;
    // 前回配達その他有償本数
    private String znkdlvrysonotayusyohns;
    // 前回配達その他無償本数
    private String znkdlvrysonotamushohns;
    // 前回配達製品水売上金額
    private String znkdlvryseihinmizuurikin;
    // 前回配達その他売上金額
    private String znkdlvrysonotaurikin;
    // 前々回配達受注日
    private String zzkaidlvryacptdd;
    // 前々回配達日
    private String zzkhtdd;
    // 前々回配達レギュラー有償本数
    private String zzkhtrgrayusyohns;
    // 前々回配達レギュラー無償本数
    private String zzkhtrgramushohns;
    // 前々回配達スリム有償本数
    private String zzkhtsrmyusyohns;
    // 前々回配達スリム無償本数
    private String zzkhtsrmmushohns;
    // 前々回配達その他有償本数
    private String zzkhtsonotayusyohns;
    // 前々回配達その他無償本数
    private String zzkhtsonotamushohns;
    // 前々回配達製品水売上金額
    private String zzkhtseihinmizuurikin;
    // 前々回配達その他売上金額
    private String zzkhtsonotaurikin;
    // 前々前回配達受注日
    private String zzzkhtacptdd;
    // 前々前回配達日
    private String zzzkhtdd;
    // 前々前回配達レギュラー有償本数
    private String zzzkhtrgrayusyohns;
    // 前々前回配達レギュラー無償本数
    private String zzzkhtrgramushohns;
    // 前々前回配達スリム有償本数
    private String zzzkhtsrmyusyohns;
    // 前々前回配達スリム無償本数
    private String zzzkhtsrmmushohns;
    // 前々前回配達その他有償本数
    private String zzzkhtsonotayusyohns;
    // 前々前回配達その他無償本数
    private String zzzkhtsonotamushohns;
    // 前々前回配達製品水売上金額
    private String zzzkhtseihinmizuurikin;
    // 前々前回配達その他売上金額
    private String zzzkhtsonotaurikin;
    // 料金プラン継続更新日
    private String rknplnkeizokukshndd;
    // 違約金管理フラグ
    private String iyakukinkanriflg;
    // 紹介番号
    private String honshokaino;
    // 取次店会員ID
    private String toritugitenkaiinid;
    // 獲得本部キャンペーンコード
    private String kakutokuhonbukynpncd;
    // 獲得本部キャンペーン名称
    private String kakutokuhonbukynpnnm;
    // 違約金金額
    private String iyakukinkin;
    // 反映元得意先コード
    private String haneimotottcd;
    // 反映元得意先名称１
    private String haneimototokuisakinm;
    // 再入会フラグ
    private String fttbnyukaiflg;
    // 業種詳細区分
    private String gyoshushskbn;
    // 委託相手ＡＰ基本コード
    private String itakuaiteapkihoncd;
    // 委託相手ショップコード
    private String itakuaiteshopcd;
    // 委託相手得意先コード
    private String itakuaitetokuisakicd;
    // 委託相手得意先名称１
    private String itakuaitetokuisakinm;
    // 会員コード
    private String kaiincd;
    // 地方公共団体コード１
    private String chihoupubdanticdn1;
    // 地方公共団体コード２
    private String chihoupubdanticdn2;
    // 勤務先地方公共団体コード１
    private String kinmusakichihoupubdanticdn1;
    // 勤務先地方公共団体コード２
    private String kinmusakichihoupubdanticdn2;
    // 料金種別基本フラグ
    private String rknkindkihonflg;
    // 料金種別特別単価フラグ
    private String rknkindtokubetsuuntflg;
    // 料金種別特別値引フラグ
    private String rknkindtokubetsunebikiflg;
    // プランコード
    private String plncd;
    // プランコード２
    private String plncd2;
    // 料金プラン設定日２
    private String rknplnsetteidd2;
    // 料金プラン継続更新日２
    private String rknplnkeizokukshndd2;
    // プランコード３
    private String plncd3;
    // 料金プラン設定日３
    private String rknplnsetteidd3;
    // 料金プラン継続更新日３
    private String rknplnkeizokukshndd3;
    // 自社サービス
    private String jishasvcd;
    // 本部キャンペーン景品区分
    private String honbukynpnkeihinkbn;
    // 獲得キャンペーンコード
    private String kakutokukynpncd;
    // キャンペーン名称
    private String kakutokukynpnnm;
    // キャンペーン景品区分
    private String kynpnkeihinkbn;
    // 初回特典メモ
    private String shokaitokutenmemo;
    // 入会登録日
    private String nyukaiinptdd;
    // 利用目的区分
    private String riyomokutekikbn;
    // 入社年月
    private String nyushadydm;
    // 性別区分
    private String seibetsukbn;
    // 職業区分
    private String sykugyokbn;
    // 配偶有無フラグ
    private String haiguumuflg;
    // その他同居人有無フラグ
    private String sonotadokyoninumuflg;
    // 支払者続柄区分
    private String shihasyazokukbn;
    // 個人証明種別区分
    private String kojinsyoumeikindkbn;
    // 個人証明番号
    private String kojinsyoumeino;
    // 世帯情報登録日
    private String setaijohoinptdd;
    // 世帯主
    private String setainushi;
    // 従業員規模区分
    private String jugyoinkibkbn;
    // 居住年数区分
    private String kojudykazukbn;
    // ペット情報区分
    private String petjohokbn;
    // 世帯収入
    private String setaisyunyu;
    // 同一生計人数区分
    private String doitsusekeninzukbn;
    // 外国人人数
    private String gaikokujinninzu;
    // 外国人備考
    private String gaikokujinbiko;
    // プラン期限年月
    private String plnkgndydm;
    // 他社切替
    private String tashakirikae;
    // コラボ区分
    private String collabokbn;
    // 請求先名称１
    private String skysakinmn1;
    // 親得意先コード
    private String oyatokuisakicd;
    // 会員作成有無区分
    private String kainsakuseiumukbn;
    // サーバーレンタル料金
    private String svrtlrkn;
    // 定例売上マスタアクセス可否
    private String accesskahiteireiuriage;
    // 得意先設置サーバーアクセス可否
    private String accesskahitokuisakisettisv;
    // 請求先マスタ修正・照会アクセス可否
    private String accesskahiskysaki;
    // 会員マスタ照会アクセス可否
    private String accesskahikain;
    // 違約金情報アクセス可否
    private String accesskahiiyakukin;
    // 定期受注登録・照会アクセス可否
    private String accesskahiteikijuchu;

    /* 定例売上情報 */
    // 売上仕入区分
    private String urisiirekbn;
    // 定例発生番号
    private String teireihasseino;
    // 定例売上仕入登録件数
    private int teireicount = 0;
    // 計上日
    private String keijodd;
    // 初回計上年月
    private String shokaikeijodydm;
    // 次回計上年月
    private String jikaikeijodydm;
    // 終了年月
    private String shrydydm;
    // 前回発生計上日
    private String znkhasseikeijodd;
    // 実績担当者コード
    private String jissekitnshacd;
    // 実績担当者名
    private String jissekitnshamei;
    // 数量
    private String suryo;
    // 標準価格
    private String hyojunprice;
    // 単価
    private String unt;
    // 合計金額
    private String kin;
    // 消費税
    private String shohitaxkin;
    // 初回単価
    private String shokaiunt;
    // 初回金額
    private String shokaikin;
    // 初回消費税額
    private String shokaishohitaxkin;
    // 明細備考
    private String meisaibiko;
    // 売上設定
    private String rtlrknshukeikbn;
    // 発生間隔計上パターン
    private String hasseikankakukeijoptn;
    // 売上発生有無フラグ
    private String dnpyhasseiarinashiflg;
    // 有効区分
    private String yukokbn1;
    // 登録システム日付
    private String inptsystmymd1;
    // 登録システム時間
    private String inptsystmjikan1;
    // 登録者ＡＰ基本コード
    private String toshaapkihoncd1;
    // 登録者ショップコード
    private String toshashopcd1;
    // 登録者コード
    private String toshacd1;
    // 登録者名
    private String toshamei1;
    // 更新システム日付
    private String kshnsystmymd1;
    // 更新システム時間
    private String kshnsystmjikan1;
    // 更新者ＡＰ基本コード
    private String koshaapkihoncd1;
    // 更新者ショップコード
    private String koshashopcd1;
    // 更新者コード
    private String koshacd1;
    // 更新者名
    private String koshamei1;
    // 現標準価格
    private String genzhyojunprice;
    // 新標準価格
    private String shinhyojunprice;
    // 標準価格切替日
    private String hyojunpricekirikaedd;
    // 商品コード
    private String shncd;
    // 定例設定区分
    private String tteireikbn;
    // 税設定区分
    private String taxsetteikbn;
    // 税区分
    private String taxkbn;
    // 計上区分
    private String keijokbn;
    // 計上区分名
    private String keijokbnnm;
    // 価格情報
    private String priceinfo;

    /* 得意先予約情報 */
    // 予約登録日
    private String yoyakuinptdd;
    // 得意先マスタ変更日
    private String ttmasutachgdd;
    // 登録システム日付
    private String inptsystmymd2;
    // 登録システム時間
    private String inptsystmjikan2;
    // 登録者ＡＰ基本コード
    private String toshaapkihoncd2;
    // 登録者ショップコード
    private String toshashopcd2;
    // 登録者コード
    private String toshacd2;
    // 登録者名称
    private String toshamei2;
    // 更新システム日付
    private String kshnsystmymd2;
    // 更新システム時間
    private String kshnsystmjikan2;
    // 更新者ＡＰ基本コード
    private String koshaapkihoncd2;
    // 更新者ショップコード
    private String koshashopcd2;
    // 更新者コード
    private String koshacd2;
    // 更新者名称
    private String koshamei2;
    // 得意先名称１
    private String tokuisakinmn11;
    // 得意先名称２
    private String tokuisakinmn21;
    // 得意先敬称区分
    private String tokuisakikeishokbn1;
    // 得意先名称カナ１
    private String tokuisakinmkanan11;
    // 得意先名称カナ２
    private String tokuisakinmkanan21;
    // 郵便番号
    private String yubinno1;
    // 地方公共団体コード１
    private String chihoupubdanticdn11;
    // 地方公共団体コード２
    private String chihoupubdanticdn21;
    // 住所１
    private String adrsn11;
    // 住所２
    private String adrsn21;
    // 住所３
    private String adrsn31;
    // 連絡先番号区分１
    private String renrakusakinokbnn11;
    // 連絡先番号１
    private String renrakusakinon11;
    // 連絡先メモ１
    private String renrakusakimemon11;
    // 連絡先番号区分２
    private String renrakusakinokbnn21;
    // 連絡先番号２
    private String renrakusakinon21;
    // 連絡先メモ２
    private String renrakusakimemon21;
    // 連絡先番号区分３
    private String renrakusakinokbnn31;
    // 連絡先番号３
    private String renrakusakinon31;
    // 連絡先メモ３
    private String renrakusakimemon31;
    // 連絡先番号区分４
    private String renrakusakinokbnn41;
    // 連絡先番号４
    private String renrakusakinon41;
    // 連絡先メモ４
    private String renrakusakimemon41;
    // 事業所コード
    private String jigyoshocd1;
    // 営業エリアコード
    private String eigyoareacd1;
    // 営業エリア名称
    private String eigyoareanm1;
    // 営業担当者コード
    private String eigyotnshacd1;
    // 営業担当者名称
    private String eigyotnshanm1;
    // 配達管轄ＡＰ基本コード
    private String dlvrykankatsuapkihoncd1;
    // 配達管轄ショップコード
    private String dlvrykankatsushopcd1;
    // ＡＰ名称
    private String apnm;
    // ショップ名称
    private String shopnm;
    // 配達管轄拠点コード
    private String dlvrykankatsukyotencd1;
    // 配達拠点名称
    private String dlvrykyotennm;
    // 配達エリアコード
    private String dlvryareacd1;
    // 配達エリア名称
    private String dlvryareanm1;
    // 配達区分
    private String dlvrykbn1;
    // 配達順
    private String dlvryjun1;
    // 配達会社コード
    private String dlvrycmpnycd1;
    // 配達会社名称
    private String dlvrycmpnynm1;
    // 配達担当者コード
    private String dlvrytnshacd1;
    // 配達担当者名称
    private String dlvrytnshamei1;
    // 印刷メモ１
    private String prntmemon11;
    // 印刷メモ２
    private String prntmemon21;
    // 印刷メモ３
    private String prntmemon31;
    // 納品先情報１
    private String nohinsakijohon11;
    // 納品先情報２
    private String nohinsakijohon21;
    // 地図番号
    private String chizuno1;
    // 受注時間指定区分
    private String acptjikanshiteikbn1;
    // 受注時間指定時刻
    private String acptjikanshiteijikoku1;
    // 受注時間指定時刻終了
    private String acptjikanshiteijikokushry1;

    /* 請求先予約情報 */
    // 請求先コード
    private String skysakicd1;
    // 予約登録日
    private String yoyakuinptdd1;
    // 請求先マスタ変更日
    private String sksmasutachgdd;
    // 登録システム日付
    private String inptsystmymd3;
    // 登録システム時間
    private String inptsystmjikan3;
    // 登録者ＡＰ基本コード
    private String toshaapkihoncd3;
    // 登録者ショップコード
    private String toshashopcd3;
    // 登録者コード
    private String toshacd3;
    // 登録者名
    private String toshamei3;
    // 更新システム日付
    private String kshnsystmymd3;
    // 更新システム時間
    private String kshnsystmjikan3;
    // 更新者ＡＰ基本コード
    private String koshaapkihoncd3;
    // 更新者ショップコード
    private String koshashopcd3;
    // 更新者コード
    private String koshacd3;
    // 更新者名
    private String koshamei3;
    // 請求先名称１
    private String skysakinmn11;
    // 請求先名称２
    private String skysakinmn21;
    // 請求先敬称区分
    private String skysakikeishokbn;
    // 請求先名称カナ
    private String skysakinmkana;
    // 請求先名称略称
    private String skysakinmryakusho;
    // 郵便番号
    private String yubinno2;
    // 住所１
    private String adrsn12;
    // 住所２
    private String adrsn22;
    // 住所３
    private String adrsn32;
    // 連絡先番号区分１
    private String renrakusakinokbnn12;
    // 連絡先番号１
    private String renrakusakinon12;
    // 連絡先番号区分２
    private String renrakusakinokbnn22;
    // 連絡先番号２
    private String renrakusakinon22;
    // 連絡先番号区分３
    private String renrakusakinokbnn32;
    // 連絡先番号３
    private String renrakusakinon32;
    // 連絡先番号区分４
    private String renrakusakinokbnn42;
    // 連絡先番号４
    private String renrakusakinon42;
    // 営業担当者コード
    private String eigyotnshacd2;
    // 営業担当者名
    private String eigyotnshamei2;
    // 回収担当者コード
    private String kaishutnshacd;
    // 回収担当者名
    private String kaishutnshamei;
    // 事業所コード
    private String jigyoshocd2;

}
