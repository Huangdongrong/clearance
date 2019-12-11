/**
 * Copyright 2019 bejson.com
 */
package com.ruoyi.yz.wuliu.stokj.order;

import com.ruoyi.common.core.domain.BaseQingmaEntity;

/**
 * Auto-generated: 2019-06-19 23:33:49
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class StoKjOrderResponse extends BaseQingmaEntity {

    private String tradeNo;
    private String success;
    private String waybillNo;
    private String errorMsg;

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getSuccess() {
        return success;
    }

    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
    }

    public String getWaybillNo() {
        return waybillNo;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

}
