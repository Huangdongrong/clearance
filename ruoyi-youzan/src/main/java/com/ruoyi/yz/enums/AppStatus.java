/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.enums;

import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 *
 * @author wmao
 */
public enum AppStatus {
    
    DRAFT("1"), COMMIT("2");

    private String key;

    AppStatus(String key) {
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

    public static AppStatus getByKey(String key) {
        if (isNotBlank(key)) {
            AppStatus[] appTypes = AppStatus.values();
            if (isNotEmpty(appTypes)) {
                for (AppStatus appType : appTypes) {
                    if (equalsIgnoreCase(key, appType.getKey())) {
                        return appType;
                    }
                }
            }
        }
        return null;
    }
}
