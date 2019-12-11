/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.enums;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.trim;

/**
 *
 * @author wmao
 */
public enum OrderStatus {
    STATUS_INIT("0", "待申请"), 
    STATUS_WAITING("1", "已下单"), 
    STATUS_APPLYING("2", "清关中"), 
    STATUS_SUCCESS("3", "清关成功"), 
    STATUS_REJECTED("4", "清关失败"),  
    STATUS_DISCARDED("5", "订单废弃"),  
    STATUS_COMPLETED("6", "已完成");

    private String key;

    private String value;

    OrderStatus(String key, String value) {
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

    public static String getNameByKey(String key) {
        OrderStatus[] values = OrderStatus.values();
        if (isNotEmpty(values)) {
            for (OrderStatus os : values) {
                if (nonNull(os) && equalsIgnoreCase(key, os.getKey())) {
                    return os.name();
                }
            }
        }
        return "";
    }

    public static String getValueByName(String name) {
        OrderStatus[] values = OrderStatus.values();
        if (isNotEmpty(values)) {
            for (OrderStatus os : values) {
                if (nonNull(os) && equalsIgnoreCase(name, os.name())) {
                    return os.getValue();
                }
            }
        }
        return "";
    }

    public static boolean alreadyLocked(String name) {
        boolean locked = false;
        OrderStatus[] values = OrderStatus.values();
        if (isNotEmpty(values)) {
            for (OrderStatus os : values) {
                if (nonNull(os) && equalsIgnoreCase(name, os.name())) {
                    locked = (os == STATUS_WAITING) || (os == STATUS_APPLYING) || (os == STATUS_COMPLETED);
                    break;
                }
            }
        }
        return locked;
    }
    
    public static boolean isDiscarded(String name){
        boolean locked = false;
        OrderStatus[] values = OrderStatus.values();
        if (isNotEmpty(values)) {
            for (OrderStatus os : values) {
                if (nonNull(os) && equalsIgnoreCase(trim(name), os.name())) {
                    locked = (os == STATUS_DISCARDED);
                    break;
                }
            }
        }
        return locked;
    }
    
    public static boolean isSucceed(String name){
        boolean locked = false;
        OrderStatus[] values = OrderStatus.values();
        if (isNotEmpty(values)) {
            for (OrderStatus os : values) {
                if (nonNull(os) && equalsIgnoreCase(trim(name), os.name())) {
                    locked = (os == STATUS_SUCCESS);
                    break;
                }
            }
        }
        return locked;
    }

    public static boolean isReadyApply(String name) {
        boolean locked = false;
        OrderStatus[] values = OrderStatus.values();
        if (isNotEmpty(values)) {
            for (OrderStatus os : values) {
                if (nonNull(os) && equalsIgnoreCase(trim(name), os.name())) {
                    locked = (os == STATUS_APPLYING);
                    break;
                }
            }
        }
        return locked;
    }

    public static boolean needToPull(String name) {
        boolean init = false;
        OrderStatus[] values = OrderStatus.values();
        if (isNotEmpty(values)) {
            for (OrderStatus os : values) {
                if (nonNull(os) && equalsIgnoreCase(name, os.name())) {
                    init = (os == STATUS_INIT);
                    break;
                }
            }
        }
        return init;
    }
}
