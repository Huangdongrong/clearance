/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.system.enums;

/**
 *
 * @author wmao
 */
public enum DataScope {
    ALL("1", "全部数据权限"), DEFINED("2","自定义数据权限"), DEPARTMENT("3", "本部门数据权限"), SUB_DEPARTMENT("4", "本部门及以下数据权限");
    
    private String key;
    private String value;
    
    DataScope(String key, String value){
        this.key = key;
        this.value = value;
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
