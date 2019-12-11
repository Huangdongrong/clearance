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
@Alias("YouzanUserToken")
public class YouzanUserToken extends BaseQingmaEntity{

    private String phone;
    private String kdtId;
    private String appId;
    private String shopName;
    private String userId;

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the kdtId
     */
    public String getKdtId() {
        return kdtId;
    }

    /**
     * @param kdtId the kdtId to set
     */
    public void setKdtId(String kdtId) {
        this.kdtId = kdtId;
    }

    /**
     * @return the appId
     */
    public String getAppId() {
        return appId;
    }

    /**
     * @param appId the appId to set
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * @return the shopName
     */
    public String getShopName() {
        return shopName;
    }

    /**
     * @param shopName the shopName to set
     */
    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
