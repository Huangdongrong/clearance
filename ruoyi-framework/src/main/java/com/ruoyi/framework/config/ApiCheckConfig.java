/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.framework.config;

import com.ruoyi.framework.web.http.InvokeAnnotationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * @author wmao
 */
@Configuration
public class ApiCheckConfig implements WebMvcConfigurer {

    @Bean
    public InvokeAnnotationInterceptor ivaInterceptor() {
        return new InvokeAnnotationInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ivaInterceptor())
                .addPathPatterns("/rest/yz")
                .excludePathPatterns("/test")
                .excludePathPatterns("/robots.txt");
    }
}
