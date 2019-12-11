/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.common.annotation;

import com.ruoyi.common.enums.DataSourceType;
import static com.ruoyi.common.enums.DataSourceType.MASTER;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * 自定义多数据源切换注解
 * 
 * @author ruoyi
 */
@Target({METHOD, TYPE, PARAMETER})
@Retention(RUNTIME)
public @interface TargetDS
{
    /**
     * 切换数据源名称
     * @return 
     */
    public DataSourceType value() default MASTER;
}
