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
@Alias("YouzanMessage")
public class YouzanMessage extends BaseQingmaEntity {

    protected String orderNo;
    protected String buyerPhone;
    protected String kdtId;
    protected String appId;
    protected String buyerId;
    protected String type;
    protected String shopDisPlayNo;

    /**
     * @return the orderNo
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * @param orderNo the orderNo to set
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * @return the buyerPhone
     */
    public String getBuyerPhone() {
        return buyerPhone;
    }

    /**
     * @param buyerPhone the buyerPhone to set
     */
    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
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
     * @return the buyerId
     */
    public String getBuyerId() {
        return buyerId;
    }

    /**
     * @param buyerId the buyerId to set
     */
    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
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
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the shopDisPlayNo
     */
    public String getShopDisPlayNo() {
        return shopDisPlayNo;
    }

    /**
     * @param shopDisPlayNo the shopDisPlayNo to set
     */
    public void setShopDisPlayNo(String shopDisPlayNo) {
        this.shopDisPlayNo = shopDisPlayNo;
    }
}
