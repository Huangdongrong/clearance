/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.common.enums;

import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 *
 * @author wmao
 */
public enum Enterprise {
    //YOUZAN("四川自由贸易试验区青马跨境电子商务有限公司", "5151W6037H", "四川自由贸易试验区青马跨境电子商务有限公司", "5151W6037H"),
    //YOUZANTEST("杭州起码科技有限公司", "3301961H10", "北京高汇商业管理有限公司", "1105961K9C"),
    //QINGMA("四川自由贸易试验区青马跨境电子商务有限公司", "5151W6037H", "四川自由贸易试验区青马跨境电子商务有限公司", "5151W6037H"),
    WECHAT("财付通支付科技有限公司", "4403169D3W", "财付通支付科技有限公司", "4403169D3W");

    private String name;
    private String code;
    private String payName;
    private String payCode;

    Enterprise(String name, String code, String payName, String payCode) {
        this.name = name;
        this.code = code;
        this.payName = payName;
        this.payCode = payCode;
    }

    public static Enterprise getEntByCode(String code) {
        if (isNotBlank(code)) {
            Enterprise[] ents = Enterprise.values();
            if (isNotEmpty(ents)) {
                for (Enterprise ent : ents) {
                    if (equalsIgnoreCase(code, ent.getCode())) {
                        return ent;
                    }
                }
            }
        }
        return null;

    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the payName
     */
    public String getPayName() {
        return payName;
    }

    /**
     * @param payName the payName to set
     */
    public void setPayName(String payName) {
        this.payName = payName;
    }

    /**
     * @return the payCode
     */
    public String getPayCode() {
        return payCode;
    }

    /**
     * @param payCode the payCode to set
     */
    public void setPayCode(String payCode) {
        this.payCode = payCode;
    }

}
