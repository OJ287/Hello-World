package jp.co.aquaclara.accis.web.domain.dto.master;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 仕入先マスタ－Dtoクラス.
 */
@Data
@AllArgsConstructor
public class S02M021TDto implements Serializable {

    public S02M021TDto() {
        super();
    }

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** ＡＰ基本コード */
    private String apkihoncd;
    /** ショップコード */
    private String shopcd;
    /** 仕入先コード */
    private String siiresakicd;
    /** 仕入先名称１ */
    private String siiresakinmn1;
    /** 仕入先名称２ */
    private String siiresakinmn2;
    /** 仕入先名称カナ */
    private String siiresakinmkana;
    /** 仕入先名称略称 */
    private String siiresakinmryakusho;
    /** 郵便番号 */
    private String yubinno;
    /** 住所１ */
    private String adrsn1;
    /** 住所２ */
    private String adrsn2;
    /** 住所３ */
    private String adrsn3;
    /** 連絡先番号区分１ */
    private String renrakusakinokbnn1;
    /** 連絡先番号１ */
    private String renrakusakinon1;
    /** 連絡先番号区分２ */
    private String renrakusakinokbnn2;
    /** 連絡先番号２ */
    private String renrakusakinon2;
    /** 連絡先番号区分３ */
    private String renrakusakinokbnn3;
    /** 連絡先番号３ */
    private String renrakusakinon3;
    /** メールアドレス */
    private String mailadrse;
    /** 締日 */
    private String shimedd;
    /** 支払サイト */
    private String shrsaito;
    /** 支払日 */
    private String shrdd;
    /** 支払方法区分 */
    private String shrhohokbn;
    /** 買掛管理フラグ */
    private String kaikakekanriflg;
    /** 仕入先外部識別コード */
    private String siiresakigaibuskbtscd;
    /** 仕入先集計コード */
    private String siiresakishukeicd;
    /** 振込先銀行コード */
    private String frkmskbnkcd;
    /** 振込先支店コード */
    private String frkmskshitencd;
    /** 振込先口座種別 */
    private String frkmskkzkind;
    /** 振込先口座番号 */
    private String frkmskkzno;
    /** 振込先銀行名称 */
    private String frkmskbnknm;
    /** 振込先支店名称 */
    private String frkmskshitennm;
    /** 振込先口座名義 */
    private String frkmskkzmeigi;
    /** 支払先名称１ */
    private String shrsakinmn1;
    /** 支払先名称２ */
    private String shrsakinmn2;
    /** 計算方法 */
    private String keisanhoho;
    /** 仕入金額端数計算区分 */
    private String siirekinhasukeisankbn;
    /** 端数計算区分 */
    private String hasukeisankbn;
    /** 税区分 */
    private String taxkbn;
    /** 税計算区分 */
    private String taxkeisankbn;
    /** 備考 */
    private String biko;
    /** 過払金額 */
    private String kabaraikin;
    /** 有効区分 */
    private String yukokbn;
    /** 登録者ＡＰ基本コード */
    private String toshaapkihoncd;
    /** 登録者ショップコード */
    private String toshashopcd;
    /** 登録者コード */
    private String toshacd;
    /** 登録システム日付 */
    private String inptsystmymd;
    /** 登録システム時間 */
    private String inptsystmjikan;
    /** 登録運用日付 */
    private String inptunyoymd;
    /** 登録ＩＰアドレス */
    private String inptipadrse;
    /** 登録プログラムＩＤ */
    private String inptpgmid;
    /** 更新者ＡＰ基本コード */
    private String koshaapkihoncd;
    /** 更新者ショップコード */
    private String koshashopcd;
    /** 更新者コード */
    private String koshacd;
    /** 更新システム日付 */
    private String kshnsystmymd;
    /** 更新システム時間 */
    private String kshnsystmjikan;
    /** 更新運用日付 */
    private String kshnunyoymd;
    /** 更新ＩＰアドレス */
    private String kshnipadrse;
    /** 更新プログラムＩＤ */
    private String kshnpgmid;

}
