package wsq.study.common.utils;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * @author weisq
 * @date 2018年11月4日
 */
public class PageUtil {
    /**
     * 拼接排序依据条件，在数据库操作时起作用的排序条件
     *
     * @param orderKey
     * @param orderValue
     * @param order
     * @return
     */
    public static String getSortOrder(String orderKey, String orderValue, String order) {
        if (!StringUtils.isBlank(orderValue)) {
            if (orderValue.equals("ascending")) {
                order = orderKey + " asc," + order;
            } else {
                order = orderKey + " desc," + order;
            }
        }
        return order;
    }

    /**
     * 拼接排序依据条件，在list里按bean的字段排序时起作用
     *
     * @param fieldName
     * @return
     */
    public static ComparatorChain buildOrderByFieldName(String orderKey, String fieldName) {
        //创建一个排序规则
        Comparator mycmp = ComparableComparator.getInstance();
        mycmp = ComparatorUtils.nullLowComparator(mycmp);  //允许null
        if (orderKey.equals("descending"))
            mycmp = ComparatorUtils.reversedComparator(mycmp); //逆序
        //声明要排序的对象的属性，并指明所使用的排序规则，如果不指明，则用默认排序
        ArrayList<Object> sortFields = new ArrayList<Object>();
        sortFields.add(new BeanComparator(fieldName, mycmp)); //id逆序  (主)

        //创建一个排序链
        ComparatorChain multiSort = new ComparatorChain(sortFields);
        return multiSort;
    }
}
