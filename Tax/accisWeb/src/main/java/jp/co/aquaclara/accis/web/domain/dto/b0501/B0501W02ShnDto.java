package jp.co.aquaclara.accis.web.domain.dto.b0501;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * B0501w02_得意先マスタ(登録画面)商品DTO.
 * 
 * @author accisdev021
 *
 */
@Data
@AllArgsConstructor
public class B0501W02ShnDto implements Serializable {

    public B0501W02ShnDto() {
        super();
    }

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    // 商品コード
    private String shncd;
    // 商品名称略称
    private String shnnmryakusho;
    // 標準価格
    private String hyojunprice;
    // 税設定区分
    private String taxsetteikbn;
    // 税区分
    private String taxkbn;
}
