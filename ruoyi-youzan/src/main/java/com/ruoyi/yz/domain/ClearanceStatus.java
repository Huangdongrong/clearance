/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.domain;

import com.ruoyi.yz.base.BaseCif;

/**
 *
 * @author wmao
 */
public class ClearanceStatus extends BaseCif {

    /**
     * status HTTP_OK:成功 , RUOYI_INTERNAL_ERROR:失败
     */
    private Integer status;

    private boolean success;

    private String message;

    public ClearanceStatus() {

    }

    public ClearanceStatus(Integer status, Boolean success, String message) {
        this.status = status;
        this.success = success;
        this.message = message;
    }

    /**
     * @return the status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Integer status) {
        this.status = status;
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

    /**
     * @return the success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @param success the success to set
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

}
