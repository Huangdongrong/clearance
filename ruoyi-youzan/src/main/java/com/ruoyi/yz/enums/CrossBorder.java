/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.enums;

import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;

/**
 *
 * @author wmao
 */
public enum CrossBorder {
    YES(1), NO(0);

    private int key;

    CrossBorder(int key) {
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

    public static CrossBorder getByName(String name) {
        if (isNotBlank(name)) {
            CrossBorder[] crossBorders = CrossBorder.values();
            if (isNotEmpty(crossBorders)) {
                for (CrossBorder crossBorder : crossBorders) {
                    if (equalsIgnoreCase(name, crossBorder.name())) {
                        return crossBorder;
                    }
                }
            }
        }
        return null;
    }

    public static boolean isCrossBorder(String key) {
        return getByKey(key) == YES;
    }

    /**
     * 根据Key获取跨境参数
     * @param key
     * @return 
     */
    public static CrossBorder getByKey(String key) {
        if (isNotBlank(key) && isNumeric(key)) {
            int keyValue = Integer.parseInt(key);
            CrossBorder[] crossBorders = CrossBorder.values();
            if (isNotEmpty(crossBorders)) {
                for (CrossBorder crossBorder : crossBorders) {
                    if (keyValue == crossBorder.getKey()) {
                        return crossBorder;
                    }
                }
            }
        }
        return null;
    }
}
