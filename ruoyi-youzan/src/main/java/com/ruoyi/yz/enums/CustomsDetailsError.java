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
public enum CustomsDetailsError {
    SYSTEM_ERROR(-1, "系统异常处理"),
    PORT_APPLYING(2, "电子口岸申报中"),
    FAILED_SENT(4, "发送海关失败"),
    REJECTED(100, "海关退单"),
    EXPORT_CLOSE(899, "结关"),
    DETAINED_TG(501, "扣留移送通关"),
    DETAINED_JS(502, "扣留移送缉私"),
    DETAINED_FG(503, "扣留移送法规"),
    DETAINED_OT(599, "其它扣留"),
    RETURN_WULIU(700, "退运"),
    RETURN_GOODS(900, "退货"),
    IDENTITY_CHECKING(1001, "身份验证中"),
    IDENTITY_INVALID(1002, "身份验证不通过"),
    DRAFT_SAVED(1004, "平台已暂存"),
    PORT_APPLICATION_SENDING(1006, "正在发往电子口岸"),
    WITHDRAW(399, "海关审结"),
    MANUAL_CHECK(300, "人工审核"),
    PASS_SUCCESS(800, "放行");

    private int key;

    private String message;

    CustomsDetailsError(int key, String message) {
        this.key = key;
        this.message = message;
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
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    public static boolean isSuccess(int key) {
        boolean isSucc = false;
        CustomsDetailsError[] codes = CustomsDetailsError.values();
        if (isNotEmpty(codes)) {
            isSucc = (PASS_SUCCESS.getKey() == key);
        }
        return isSucc;
    }

    public static boolean isChecking(int key) {
        boolean isChecking = false;
        CustomsDetailsError[] codes = CustomsDetailsError.values();
        if (isNotEmpty(codes)) {
            isChecking = (key == IDENTITY_CHECKING.getKey()
                    || key == PORT_APPLICATION_SENDING.getKey()
                    || key == PORT_APPLYING.getKey()
                    || key == DRAFT_SAVED.getKey()
                    || key == MANUAL_CHECK.getKey());
        }
        return isChecking;
    }

    public static boolean isFailed(int key) {
        return !isSuccess(key) && !isChecking(key);
    }

    public static boolean isPending(int key) {
        boolean isPending = true;
        CustomsDetailsError[] codes = CustomsDetailsError.values();
        if (isNotEmpty(codes)) {
            for (CustomsDetailsError code : codes) {
                if (code.getKey() == key) {
                    isPending = false;
                }
            }
        }
        return isPending;
    }
}
