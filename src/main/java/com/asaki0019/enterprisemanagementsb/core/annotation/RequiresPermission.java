package com.asaki0019.enterprisemanagementsb.core.annotation;


import com.asaki0019.enterprisemanagementsb.core.enums.Logical;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验注解
 *
 * <p>用于标注需要进行权限校验的方法。可以指定多个权限标识符,并通过逻辑运算符确定校验方式。</p>
 *
 * @author asaki0019
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {

    /**
     * 需要校验的权限标识符
     *
     * @return 权限标识符数组
     */
    String[] value() default {};

    /**
     * 多个权限的逻辑操作类型
     *
     * @return 权限的逻辑操作类型，默认为AND
     */
    Logical logical() default Logical.AND;

}