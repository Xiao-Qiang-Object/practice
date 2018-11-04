package wsq.study.common.annotation;

import java.lang.annotation.*;

/**
 * Excel导出 注解
 *
 * @author weisq
 * @date 2018年11月4日
 */
@Target(value = ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Excel {

    /**
     * 是否忽略
     * @return
     */
    boolean ignore() default false;

    /**
     * excel 标题行的列名称
     * @return
     */
    String name();

    /**
     * excel 列的宽度
     * @return
     */
    int width() default 15;

    String dateFormat() default "yyyy-MM-dd";
}

