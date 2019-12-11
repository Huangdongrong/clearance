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
public enum TrafMode {
    /**
     * 监管仓就是进口保税仓、出口监管仓，监管仓已经办完全部出口手续，已经结汇
     */
    BONDED(7, "保税仓"), LIMITED_BONDED(1, "监管仓"); 

    private int key;
    private String value;

    TrafMode(int key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * @return the key
     */
    public int getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(int key) {
        this.key = key;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
}
