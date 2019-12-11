/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 * @author wmao
 */
@Component
@ConfigurationProperties(prefix = "yd-customs")
public class YundaProxyCustomsProperties extends YundaKjProperties{
    
}
