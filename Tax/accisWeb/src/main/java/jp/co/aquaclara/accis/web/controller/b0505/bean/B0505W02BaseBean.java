package jp.co.aquaclara.accis.web.controller.b0505.bean;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 定例売上マスタ（登録画面）-基本情報Beanクラス.
 */
@Data
@AllArgsConstructor
public class B0505W02BaseBean implements Serializable {

    public B0505W02BaseBean() {
        super();
    }

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** 権限フラグ */
    private String authflag;
    /** ＡＰ基本コード */
    private String apkihoncd;
    /** ショップコード */
    private String shopcd;
    /** ＡＰ名称 */
    private String apnm;
    /** ショップ名称 */
    private String shopnm;
    /** 年月日 */
    private String ymd;
    /** 次回計上日 */
    private String jikaikeijodd;
    /** 初回計上日 */
    private String shokaikeijodd;
    /** 商品税区分 */
    private String shntaxkbn;
    /** 事業所コード */
    private String jigyoshocd;
    /** 定例発生番号 */
    private String teireihasseino;
    /** 伝票区分 */
    private String dnpykbn;
    /** 売上仕入区分 */
    private String urisiirekbn;
    /** APショップ固定フラグ */
    private String apshopfixedflag;
    /** 遷移元得意先コード */
    private String origintokuisakicd;
}
