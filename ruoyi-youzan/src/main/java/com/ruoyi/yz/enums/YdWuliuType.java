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
public enum YdWuliuType {
    SUPPORT_VALUE("保价"), GSS("国际件"), GSS_EXPORT("国际件出口"), COMMON("普通");
    
    private String value;
    
    YdWuliuType(String value){
        this.value = value;
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
