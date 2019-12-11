/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.domain;

import java.util.Date;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author wmao
 */
@Alias("YouzanSubscribeMessage")
public class YouzanSubscribeMessage extends YouzanMessage{
    private String skuVersionText;
    private String skuIntervalText;
    private Date payTime;
    private String kdtID;
    private Long price;
    private String status;

    /**
     * @return the skuVersionText
     */
    public String getSkuVersionText() {
        return skuVersionText;
    }

    /**
     * @param skuVersionText the skuVersionText to set
     */
    public void setSkuVersionText(String skuVersionText) {
        this.skuVersionText = skuVersionText;
    }

    /**
     * @return the skuIntervalText
     */
    public String getSkuIntervalText() {
        return skuIntervalText;
    }

    /**
     * @param skuIntervalText the skuIntervalText to set
     */
    public void setSkuIntervalText(String skuIntervalText) {
        this.skuIntervalText = skuIntervalText;
    }

    /**
     * @return the payTime
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getPayTime() {
        return payTime;
    }

    /**
     * @param payTime the payTime to set
     */
    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    /**
     * @return the price
     */
    public Long getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(Long price) {
        this.price = price;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the kdtID
     */
    public String getKdtID() {
        return kdtID;
    }

    /**
     * @param kdtID the kdtID to set
     */
    public void setKdtID(String kdtID) {
        this.kdtID = kdtID;
    }
}
