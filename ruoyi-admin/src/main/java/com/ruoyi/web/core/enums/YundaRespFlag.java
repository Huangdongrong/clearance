/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.web.core.enums;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;

/**
 *
 * @author wmao
 */
public enum YundaRespFlag {
    SUCCESS(1), FAILED(0);

    private int key;

    YundaRespFlag(int key) {
        this.key = key;
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

    public static YundaRespFlag getByKey(int key) {
        YundaRespFlag[] flags = YundaRespFlag.values();
        if (isNotEmpty(flags)) {
            for (YundaRespFlag flag : flags) {
                if (nonNull(flag) && key == flag.getKey()) {
                    return flag;
                }
            }
        }
        return FAILED;
    }
}
