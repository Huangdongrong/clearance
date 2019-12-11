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
@Alias("WuliuPlat")
public class WuliuPlat extends BaseQingmaEntity {
    private String appKey;
    private String appSecret;
    private String url;
    private String bizSiteCode;
    private String bizId;
    private String bizPwd;
    private String keyName;
    private String customsName;
    private String customsCode;

    /**
     * @return the appKey
     */
    public String getAppKey() {
        return appKey;
    }

    /**
     * @param appKey the appKey to set
     */
    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    /**
     * @return the appSecret
     */
    public String getAppSecret() {
        return appSecret;
    }

    /**
     * @param appSecret the appSecret to set
     */
    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the bizSiteCode
     */
    public String getBizSiteCode() {
        return bizSiteCode;
    }

    /**
     * @param bizSiteCode the bizSiteCode to set
     */
    public void setBizSiteCode(String bizSiteCode) {
        this.bizSiteCode = bizSiteCode;
    }

    /**
     * @return the bizId
     */
    public String getBizId() {
        return bizId;
    }

    /**
     * @param bizId the bizId to set
     */
    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    /**
     * @return the bizPwd
     */
    public String getBizPwd() {
        return bizPwd;
    }

    /**
     * @param bizPwd the bizPwd to set
     */
    public void setBizPwd(String bizPwd) {
        this.bizPwd = bizPwd;
    }

    /**
     * @return the keyName
     */
    public String getKeyName() {
        return keyName;
    }

    /**
     * @param keyName the keyName to set
     */
    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    /**
     * @return the customsName
     */
    public String getCustomsName() {
        return customsName;
    }

    /**
     * @param customsName the customsName to set
     */
    public void setCustomsName(String customsName) {
        this.customsName = customsName;
    }

    /**
     * @return the customsCode
     */
    public String getCustomsCode() {
        return customsCode;
    }

    /**
     * @param customsCode the customsCode to set
     */
    public void setCustomsCode(String customsCode) {
        this.customsCode = customsCode;
    }

}
