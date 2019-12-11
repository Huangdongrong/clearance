/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.enums;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;

/**
 *
 * @author wmao
 */
public enum YzCustomsErrorCode {
    SUBMITTING(1, "电子口岸申报中"), SENT_SUCCESS(2, "发送海关成功"), SENT_FAILED(3, "发送海关失败"), REJECTED(4, "海关退单"), ACCEPTED(5, "海关入库"), RETRY(6, "处理异常,请稍后重试");

    private int key;

    private String value;

    YzCustomsErrorCode(int key, String value) {
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

    public static YzCustomsErrorCode getByKey(int key) {
        YzCustomsErrorCode[] codes = YzCustomsErrorCode.values();
        if (isNotEmpty(codes)) {
            for (YzCustomsErrorCode errorCode : codes) {
                if (errorCode.getKey() == key) {
                    return errorCode;
                }
            }
        }
        return null;
    }
    

    public static boolean isError(int key) {
        YzCustomsErrorCode code = getByKey(key);
        return nonNull(code) && (code == SENT_FAILED || code == REJECTED);
    }
    
    public static boolean isAccepted(int key){
        YzCustomsErrorCode code = getByKey(key);
        return nonNull(code) && (code == ACCEPTED);
    }
    
    public static boolean isProcessing(int key){
        YzCustomsErrorCode code = getByKey(key);
        return nonNull(code) && (code == SUBMITTING || code == SENT_SUCCESS || code == RETRY);
    }

    public static boolean isNotError(int key) {
        return !isError(key);
    }
}
