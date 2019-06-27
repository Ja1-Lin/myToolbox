package com.linyoga.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注属性类型转换
 * 1.在类属性上加上此注解
 * 2.通过BeanCopyUtil进行类型转换
 *
 * @author Kris
 * @date 2018/06/25
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Convert {

    /**
     * 目标属性名
     *
     * @return
     */
    String name();

    /**
     * 需忽略的属性名
     *
     * @return
     */
    String[] ignoreProperty() default {};

    /**
     * 目标属性类型
     *
     * @return
     */
    Class<?> className();

    Class<?> group() default Void.class;
}
