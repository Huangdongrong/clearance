/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.enums;

import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;

/**
 *
 * @author wmao
 */
public enum CustomsErrorCode {
    SYSTEM_ERROR(-1, "系统异常处理"), PLAT_TEMPRARY_SAVED(1004, "平台已暂存"), PORT_TEMPRARY_SAVED(1, "电子口岸已暂存"), SUBMITTING(2, "电子口岸申报中"), SENT_SUCCESS(3, "发送海关成功"), SENT_FAILED(4, "发送海关失败"), REJECTED(100, "海关退单"), ACCEPTED(120, "海关入库");

    private int key;

    private String value;

    CustomsErrorCode(int key, String value) {
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

    public static CustomsErrorCode getByKey(int key) {
        CustomsErrorCode[] codes = CustomsErrorCode.values();
        if (isNotEmpty(codes)) {
            for (CustomsErrorCode errorCode : codes) {
                if (errorCode.getKey() == key) {
                    return errorCode;
                }
            }
        }
        return null;
    }
}
