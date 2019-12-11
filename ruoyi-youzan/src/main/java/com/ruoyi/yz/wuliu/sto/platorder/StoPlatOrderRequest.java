/**
 * Copyright 2019 bejson.com
 */
package com.ruoyi.yz.wuliu.sto.platorder;

import com.ruoyi.yz.wuliu.sto.order.StoOrderRequest;

/**
 * Auto-generated: 2019-06-18 22:43:23
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class StoPlatOrderRequest extends StoOrderRequest {

    private String bizSiteCode;
    private String bizId;
    private String bizPwd;
    private String orderType;
    /**
     * 是否需要上门揽件，如果是和固定网点合作的批量发件的情况，此处填写0
     * 如果是总台调度随机门店揽件，填写1
     */
    private Integer dispatchFlag;

    public void setBizSiteCode(String bizSiteCode) {
        this.bizSiteCode = bizSiteCode;
    }

    public String getBizSiteCode() {
        return bizSiteCode;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizPwd(String bizPwd) {
        this.bizPwd = bizPwd;
    }

    public String getBizPwd() {
        return bizPwd;
    }

    /**
     * @return the dispatchFlag
     */
    public Integer getDispatchFlag() {
        return dispatchFlag;
    }

    /**
     * @param dispatchFlag the dispatchFlag to set
     */
    public void setDispatchFlag(Integer dispatchFlag) {
        this.dispatchFlag = dispatchFlag;
    }

    /**
     * @return the orderType
     */
    public String getOrderType() {
        return orderType;
    }

    /**
     * @param orderType the orderType to set
     */
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
}
