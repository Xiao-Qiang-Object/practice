 package wsq.study.quartz.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * 获取原子变量
 *
 * @author weisq
 * @date 2018/11/15
 */
public class AtomicUtil {
    // 静态变量存储最大值
    public static final AtomicInteger ATOMICNUM = new AtomicInteger();

    /**
     * @Author javaloveiphone
     * @Description :获取最新分组编号
     * @return int 注：此方法并没有使用synchronized进行同步，因为共享的编号自增操作是原子操作，线程安全的
     */
    public static String getNewAutoNum() {
        // 线程安全的原子操作，所以此方法无需同步
        int newNum = ATOMICNUM.incrementAndGet();
        // 数字长度为5位，长度不够数字前面补0
        String newStrNum = String.format("%05d", newNum);
        return newStrNum;
    }

    /**
     * 判断是否为整数
     * 
     * @param str 传入的字符串
     * @return 是整数返回true,否则返回false
     */

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[\\d]*$");
        return pattern.matcher(str).matches();
    }
}
