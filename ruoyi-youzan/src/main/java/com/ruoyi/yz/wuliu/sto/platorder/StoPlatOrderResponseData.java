/**
 * Copyright 2019 bejson.com
 */
package com.ruoyi.yz.wuliu.sto.platorder;

import com.ruoyi.yz.base.BaseCif;

/**
 * Auto-generated: 2019-06-18 22:44:23
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class StoPlatOrderResponseData  extends BaseCif{

    private String orderNo;
    private String waybillNo;
    private String bigWord;
    private String packagePlace;

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
    }

    public String getWaybillNo() {
        return waybillNo;
    }

    public void setBigWord(String bigWord) {
        this.bigWord = bigWord;
    }

    public String getBigWord() {
        return bigWord;
    }

    public void setPackagePlace(String packagePlace) {
        this.packagePlace = packagePlace;
    }

    public String getPackagePlace() {
        return packagePlace;
    }

}
