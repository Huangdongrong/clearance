/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.domain;

/**
 *
 * @author wmao
 */
public class WuliuItem extends WuliuKjPlat {

    /**
     * 运单编号
     */
    private String logisticsNo;

    /**
     * @return the logisticsNo
     */
    public String getLogisticsNo() {
        return logisticsNo;
    }

    /**
     * @param logisticsNo the logisticsNo to set
     */
    public void setLogisticsNo(String logisticsNo) {
        this.logisticsNo = logisticsNo;
    }
}
