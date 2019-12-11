/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.web.core.enums;

/**
 *
 * @author wmao
 */
public enum YundaRespRet {
    SUCCESS("S"), FAILED("F");
    private String key;
    
    YundaRespRet(String key){
        this.key = key;
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
    
}
