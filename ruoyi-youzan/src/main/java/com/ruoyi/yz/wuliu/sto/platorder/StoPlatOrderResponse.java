/**
 * Copyright 2019 bejson.com
 */
package com.ruoyi.yz.wuliu.sto.platorder;

import com.ruoyi.common.core.domain.BaseQingmaEntity;

/**
 * Auto-generated: 2019-06-18 22:44:23
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class StoPlatOrderResponse extends BaseQingmaEntity{

    private boolean status;
    private String code;
    private String message;
    private StoPlatOrderResponseData data;

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setData(StoPlatOrderResponseData data) {
        this.data = data;
    }

    public StoPlatOrderResponseData getData() {
        return data;
    }

}
