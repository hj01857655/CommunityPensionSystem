package com.communitypension.communitypensionadmin.annotation;

import com.communitypension.communitypensionadmin.enums.BusinessType;
import com.communitypension.communitypensionadmin.enums.OperatorType;

import java.lang.annotation.*;

/**
 * 自定义注解，用于操作日志记录
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    /**
     * 模块
     *
     * @return 模块名称
     */
    public String title() default "";

    /**
     * 功能
     *
     * @return 功能名称
     */
    public BusinessType businessType() default BusinessType.OTHER;

    /**
     * 操作人类型
     *
     * @return 操作人类型
     */
    public OperatorType operatorType() default OperatorType.MANAGE;

    /**
     * 是否保存请求的参数
     *
     * @return 是否保存请求的参数
     */
    public boolean isSaveRequestData() default true;

    /**
     * 是否保存响应的参数
     *
     * @return 是否保存响应的参数
     */
    public boolean isSaveResponseData() default true;

    /**
     * 排除请求的参数
     *
     * @return 排除请求的参数
     */
    public String[] excludeParams() default {};


}
