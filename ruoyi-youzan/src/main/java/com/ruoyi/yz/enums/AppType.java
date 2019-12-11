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
public enum AppType {
    INSERT("1"), UPDATE("2");

    private String key;

    AppType(String key) {
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

    public static AppType getByKey(String key) {
        if (isNotBlank(key)) {
            AppType[] appStatus = AppType.values();
            if (isNotEmpty(appStatus)) {
                for (AppType appStatu : appStatus) {
                    if (equalsIgnoreCase(key, appStatu.getKey())) {
                        return appStatu;
                    }
                }
            }
        }
        return null;
    }
}
