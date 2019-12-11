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
public enum KdtStatus {
    STATUS_VALID(0, "合法"), STATUS_WARNING(1, "可疑"), STATUS_DELETED(2, "删除");

    private int key;
    private String value;

    KdtStatus(int key, String value) {
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

    public static KdtStatus getStatusByName(String name) {
        if (isNotBlank(name)) {
            KdtStatus[] status = KdtStatus.values();
            if (isNotEmpty(status)) {
                for (KdtStatus st : status) {
                    if (equalsIgnoreCase(name, st.name())) {
                        return st;
                    }
                }
            }
        }
        return STATUS_DELETED;
    }
}
