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
public enum MessageType {
    APP_AUTH(0), APP_SUBSCRIBE(1);

    private int key;

    MessageType(int key) {
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

    public static MessageType geByName(String name) {
        if (isNotBlank(name)) {
            MessageType[] msgTypes = MessageType.values();
            if (isNotEmpty(msgTypes)) {
                for (MessageType msgType : msgTypes) {
                    if (equalsIgnoreCase(name, msgType.name())) {
                        return msgType;
                    }
                }
            }
        }
        return null;
    }

}
