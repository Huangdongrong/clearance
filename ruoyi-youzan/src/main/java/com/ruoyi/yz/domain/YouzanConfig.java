/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.domain;

import com.ruoyi.common.core.domain.BaseQingmaEntity;
import org.apache.ibatis.type.Alias;

/**
 *
 * @author wmao
 */
@Alias("YouzanConfig")
public class YouzanConfig extends BaseQingmaEntity {
    
    private String name;
    
    private String code;
    
    private String payName;
    
    private String payCode;
    
    private String clientKey;
    
    private String clientSecret;
    /**
     * @return the clientKey
     */
    public String getClientKey() {
        return clientKey;
    }

    /**
     * @param clientKey the clientKey to set
     */
    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    /**
     * @return the clientSecret
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * @param clientSecret the clientSecret to set
     */
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the payName
     */
    public String getPayName() {
        return payName;
    }

    /**
     * @param payName the payName to set
     */
    public void setPayName(String payName) {
        this.payName = payName;
    }

    /**
     * @return the payCode
     */
    public String getPayCode() {
        return payCode;
    }

    /**
     * @param payCode the payCode to set
     */
    public void setPayCode(String payCode) {
        this.payCode = payCode;
    }
}
