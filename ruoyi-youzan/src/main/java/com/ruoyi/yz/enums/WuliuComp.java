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
public enum WuliuComp {
    STO("sto", "申通速递", "申通国际"),
    YUNDA("yunda", "韵达速递", "韵达国际");

    private String key;

    private String value;
    
    private String alias;

    WuliuComp(String key, String value, String alias) {
        this.key = key;
        this.value = value;
        this.alias = alias;
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

    public static WuliuComp getEntByValue(String entValue) {
        WuliuComp comp = null;
        if (isNotBlank(entValue)) {
            WuliuComp[] ents = WuliuComp.values();
            if (isNotEmpty(ents)) {
                for (WuliuComp ent : ents) {
                    if (equalsIgnoreCase(ent.getValue(), entValue)) {
                        comp = ent;
                        break;
                    }
                }
            }
        }
        return comp;
    }

    /**
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * @param alias the alias to set
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }
}
