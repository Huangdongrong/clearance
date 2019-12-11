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
public enum Customer {
    YOUZAN("有赞合作客户", 101, "ctmroptr", "yzctmr");

    private String deptName;

    private long parentDeptId;

    private String roleKey;

    private String postCode;

    Customer(String deptName, long parentDeptId, String roleKey, String postCode) {
        this.deptName = deptName;
        this.parentDeptId = parentDeptId;
        this.roleKey = roleKey;
        this.postCode = postCode;
    }

    /**
     * @return the deptName
     */
    public String getDeptName() {
        return deptName;
    }

    /**
     * @param deptName the deptName to set
     */
    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    /**
     * @return the roleKey
     */
    public String getRoleKey() {
        return roleKey;
    }

    /**
     * @param roleKey the roleKey to set
     */
    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
    }

    /**
     * @return the postCode
     */
    public String getPostCode() {
        return postCode;
    }

    /**
     * @param postCode the postCode to set
     */
    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    /**
     * @return the parentDeptId
     */
    public long getParentDeptId() {
        return parentDeptId;
    }

    /**
     * @param parentDeptId the parentDeptId to set
     */
    public void setParentDeptId(long parentDeptId) {
        this.parentDeptId = parentDeptId;
    }

    public static Customer getCustomerByDeptName(String deptName) {
        Customer customer = null;
        if (isNotBlank(deptName)) {
            Customer[] custs = Customer.values();
            if (isNotEmpty(custs)) {
                for (Customer cust : custs) {
                    if (equalsIgnoreCase(deptName, cust.getDeptName())) {
                        customer = cust;
                        break;
                    }
                }
            }
        }
        return customer;
    }
}
