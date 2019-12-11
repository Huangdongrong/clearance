/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.enums;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

/**
 *
 * @author wmao
 */
public enum YzOrderStatus {
    WAIT_SELLER_SEND_GOODS("等待卖家发货"), WAIT_BUYER_CONFIRM_GOODS("等待买家确认收货");

    private String value;

    YzOrderStatus(String value) {
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

    public static boolean isNeedToClearStatus(String name){
        YzOrderStatus[] status = YzOrderStatus.values();
        if(isNotEmpty(status)){
            for(YzOrderStatus statu : status){
                if(nonNull(statu) && equalsIgnoreCase(name, statu.name())){
                    return true;
                }
            }
        }
        return false;
    }
}
