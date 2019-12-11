/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.enums;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

/**
 *
 * @author wmao
 */
public enum ReportError {
    INVALID_PARAMS("112400000", "非法请求参数", "请检查您的请求参数"), 
    NOT_EXIST("112401000", "报关单据不存在", "填写错误或者就不存在"), 
    PULL_PAY_FAILED("112401001", "获取支付单失败", "请检查该笔订单是否已经支付成功"), 
    EXCEED_LIMIT("112401006", "报关金额超额", "报关金额大于payment, 请按照payment值传"), 
    WRONG_TRANSACTION("112401008", "有赞支付流水号不正确", "tid和支付流水号不一致导致"), 
    LESS_PAYMENT("112401011", "报关金额少于支付金额", "修改报关金额"), 
    ORDER_NOTEXIST("112401010", "采购单不存在", "请确认采购单存在并且已支付成功"), 
    NOT_EQUAL("112401012", "报关金额与支付金额不一致", "请修改报关金额"), 
    UNIFORM_ERROR("112401013", "支付报关统一错误码", "请根据返回错误信息修改报关信息"), 
    SUCCEED("" + HTTP_OK, "成功", "成功码200");

    private String code;
    private String description;
    private String solution;

    ReportError(String code, String description, String solution) {
        this.code = code;
        this.description = description;
        this.solution = solution;
    }

    public static ReportError getErrorByCode(String code) {
        if (isNotBlank(code)) {
            ReportError[] errors = ReportError.values();
            for (ReportError error : errors) {
                if (nonNull(error) && equalsIgnoreCase(error.getCode(), code)) {
                    return error;
                }
            }
        }
        return null;
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
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the solution
     */
    public String getSolution() {
        return solution;
    }

    /**
     * @param solution the solution to set
     */
    public void setSolution(String solution) {
        this.solution = solution;
    }
}
