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
public enum YundaReqType {
    CREATE(0, "create_order"), UPDATE(1, "update_order"), CANCEL(2, "cancel_order"), QUERY(3, "query_order");
    
    private int key;
    private String value;
    
    YundaReqType(int key, String value){
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
