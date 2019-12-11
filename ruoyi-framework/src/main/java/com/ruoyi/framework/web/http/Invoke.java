/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.framework.web.http;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

/**
 *
 * @author wmao
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Invoke {
    // default token
    static final String DEFAULT_TOKEN = "cdd_2017";
    
    //30 seconds drift is accecptable
    static final long DEFAULT_DRIFT = 30L * 1000;
    
    @AliasFor("token")
    String value() default DEFAULT_TOKEN;
    
    @AliasFor("value")
    String token() default DEFAULT_TOKEN;
    
    long drift() default DEFAULT_DRIFT;
}
