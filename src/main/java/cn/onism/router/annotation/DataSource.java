package cn.onism.router.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据源(选择)
 *
 * @author HeXin
 * @date 2024/06/02
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSource {
    /**
     * 数据源名称
     *
     * @return {@link String }
     */
    String value();
}
