/**
 * Copyright 2019 bejson.com
 */
package com.ruoyi.yz.wuliu.stokj.order;

import com.ruoyi.yz.base.BaseCif;
import java.math.BigDecimal;

/**
 * Auto-generated: 2019-06-19 23:28:8
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class OtherInfo extends BaseCif{

    private String appType;
    private BigDecimal freight;
    private String billNo;
    private BigDecimal insuredFee;
    private Integer packNo;
    private String goodsInfo;
    private String currency;
    private String note;
    private String receiptPath;

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getAppType() {
        return appType;
    }

    public void setFreight(BigDecimal freight) {
        this.freight = freight;
    }

    public BigDecimal getFreight() {
        return freight;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setInsuredFee(BigDecimal insuredFee) {
        this.insuredFee = insuredFee;
    }

    public BigDecimal getInsuredFee() {
        return insuredFee;
    }

    public void setPackNo(Integer packNo) {
        this.packNo = packNo;
    }

    public Integer getPackNo() {
        return packNo;
    }

    public void setGoodsInfo(String goodsInfo) {
        this.goodsInfo = goodsInfo;
    }

    public String getGoodsInfo() {
        return goodsInfo;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public void setReceiptPath(String receiptPath) {
        this.receiptPath = receiptPath;
    }

    public String getReceiptPath() {
        return receiptPath;
    }

}
