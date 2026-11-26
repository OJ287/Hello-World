package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.StringUtils;
import lombok.Value;

/**
 * 請求残高テーブル回収方法検索条件エンティティ
 */
@Value
public class S07T001TD1501W00KaishuHohoConditionEntity implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    /** 回収方法区分 */
    private String kaishuHohoKbn;
    /** 決済機関コード */
    private String ksikknCd;

    /**
     * コンストラクタ
     * <p>
     * 回収方法区分は選択状態かどうかを受け取る（null,"","0"が未選択、それ以外は選択済みとする）
     * </p>
     * 
     * @param kaishuHohoKbn01 回収方法区分：現金
     * @param kaishuHohoKbn02 回収方法区分：クレジット
     * @param kaishuHohoKbn03 回収方法区分：銀行振込
     * @param kaishuHohoKbn04 回収方法区分：郵便振込
     * @param kaishuHohoKbn05 回収方法区分：口座振替
     * @param kaishuHohoKbn06 回収方法区分：コンビニ
     * @param kaishuHohoKbn07 回収方法区分：代引き
     * @param kaishuHohoKbn08 回収方法区分：その他
     * @param kaishuHohoKbn09 回収方法区分：請求無し
     * @param kaishuHohoKbn10 回収方法区分：その他２
     * @param kaishuHohoKbn11 回収方法区分：その他３
     * @param ksikknCd02 決済機関コード：クレジット
     * @param ksikknCd05 決済機関コード：口座振替
     */
    public static List<S07T001TD1501W00KaishuHohoConditionEntity> create(String kaishuHohoKbn01,
        String kaishuHohoKbn02, String kaishuHohoKbn03, String kaishuHohoKbn04,
        String kaishuHohoKbn05, String kaishuHohoKbn06, String kaishuHohoKbn07,
        String kaishuHohoKbn08, String kaishuHohoKbn09, String kaishuHohoKbn10,
        String kaishuHohoKbn11, String ksikknCd02, String ksikknCd05) {

        List<S07T001TD1501W00KaishuHohoConditionEntity> list = new ArrayList<>();
        if (isSelected(kaishuHohoKbn01)) {
            list.add(new S07T001TD1501W00KaishuHohoConditionEntity("1", null));
        }
        if (isSelected(kaishuHohoKbn02)) {
            String ksikknCd = StringUtils.isEmpty(ksikknCd02) ? null : ksikknCd02;
            list.add(new S07T001TD1501W00KaishuHohoConditionEntity("2", ksikknCd));
        }
        if (isSelected(kaishuHohoKbn03)) {
            list.add(new S07T001TD1501W00KaishuHohoConditionEntity("3", null));
        }
        if (isSelected(kaishuHohoKbn04)) {
            list.add(new S07T001TD1501W00KaishuHohoConditionEntity("4", null));
        }
        if (isSelected(kaishuHohoKbn05)) {
            String ksikknCd = StringUtils.isEmpty(ksikknCd05) ? null : ksikknCd05;
            list.add(new S07T001TD1501W00KaishuHohoConditionEntity("5", ksikknCd));
        }
        if (isSelected(kaishuHohoKbn06)) {
            list.add(new S07T001TD1501W00KaishuHohoConditionEntity("6", null));
        }
        if (isSelected(kaishuHohoKbn07)) {
            list.add(new S07T001TD1501W00KaishuHohoConditionEntity("7", null));
        }
        if (isSelected(kaishuHohoKbn08)) {
            list.add(new S07T001TD1501W00KaishuHohoConditionEntity("8", null));
        }
        if (isSelected(kaishuHohoKbn09)) {
            list.add(new S07T001TD1501W00KaishuHohoConditionEntity("9", null));
        }
        if (isSelected(kaishuHohoKbn10)) {
            list.add(new S07T001TD1501W00KaishuHohoConditionEntity("a", null));
        }
        if (isSelected(kaishuHohoKbn11)) {
            list.add(new S07T001TD1501W00KaishuHohoConditionEntity("b", null));
        }

        return list;
    }

    /**
     * 回収方法区分が選択済みか
     * 
     * @param kaishuHohoKbn 回収方法区分
     * @return 選択済みの場合<code>true</code>
     */
    private static boolean isSelected(String kaishuHohoKbn) {
        if (StringUtils.isEmpty(kaishuHohoKbn)) {
            return false;
        }
        if (kaishuHohoKbn.equals("0")) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(" kaishuHohoKbn=").append(kaishuHohoKbn);
        sb.append(" ksikknCd=").append(ksikknCd);
        sb.append("]");
        return sb.toString();
    }
}
