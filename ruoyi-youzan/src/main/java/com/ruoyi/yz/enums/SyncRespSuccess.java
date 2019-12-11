/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.enums;

/**
 * 清关申请同步响应返回正确值
 *
 * @author wmao
 */
public enum SyncRespSuccess {
    /**
     * 韵达同步响应正确值
     */
    YUNDA_SUCC("R-001"),
    /**
     * 海关同步响应正确值
     */
    CUSTOMS_SUCC("5"),
    /**
     * 有赞同步响应正确值
     */
    YOUZAN_SUCC("200");
    private String key;

    SyncRespSuccess(String key) {
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
