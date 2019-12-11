/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.enums;

/**
 *
 * @author wmao
 */
public enum Port {

    CD("成都青白江关区","7924", "7924"),
    YUNDA("韵达代理海关","0000", "0000");

    /**
     * 口岸关键信息
     */
    private String key;
    
    /**
     * 申报海关代码
     */
    private String cCode;
    
    /**
     * 口岸海关代码
     */
    private String pCode;

    Port(String key, String cCode, String pCode) {
        this.key = key;
        this.cCode = cCode;
        this.pCode = pCode;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the cCode
     */
    public String getcCode() {
        return cCode;
    }

    /**
     * @param cCode the cCode to set
     */
    public void setcCode(String cCode) {
        this.cCode = cCode;
    }

    /**
     * @return the pCode
     */
    public String getpCode() {
        return pCode;
    }

    /**
     * @param pCode the pCode to set
     */
    public void setpCode(String pCode) {
        this.pCode = pCode;
    }
}
