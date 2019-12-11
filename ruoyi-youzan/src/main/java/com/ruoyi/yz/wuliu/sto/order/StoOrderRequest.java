/**
 * Copyright 2019 bejson.com
 */
package com.ruoyi.yz.wuliu.sto.order;

import com.ruoyi.yz.wuliu.Sender;
import com.ruoyi.yz.wuliu.Receiver;
import com.ruoyi.yz.base.BaseCif;
import java.math.BigDecimal;

/**
 * Auto-generated: 2019-06-18 22:37:15
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class StoOrderRequest extends BaseCif {

    protected String orderNo;
    protected String orderSource;
    protected String monthCustomer;
    protected BigDecimal length;
    protected BigDecimal width;
    protected BigDecimal height;
    protected BigDecimal weight;
    protected String goodsName;
    protected BigDecimal goodsAmount;
    protected String goodsPrice;
    protected String remark;
    protected Sender sender;
    protected Receiver receiver;

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderSource(String orderSource) {
        this.orderSource = orderSource;
    }

    public String getOrderSource() {
        return orderSource;
    }

    public void setMonthCustomer(String monthCustomer) {
        this.monthCustomer = monthCustomer;
    }

    public String getMonthCustomer() {
        return monthCustomer;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }

    public BigDecimal getLength() {
        return length;
    }

    public void setWidth(BigDecimal width) {
        this.width = width;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsAmount(BigDecimal goodsAmount) {
        this.goodsAmount = goodsAmount;
    }

    public BigDecimal getGoodsAmount() {
        return goodsAmount;
    }

    public void setGoodsPrice(String goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public String getGoodsPrice() {
        return goodsPrice;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemark() {
        return remark;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public Sender getSender() {
        return sender;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public Receiver getReceiver() {
        return receiver;
    }

}
