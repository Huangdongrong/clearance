/**
 * Copyright 2019 bejson.com
 */
package com.ruoyi.yz.wuliu.sto.trail;

import com.ruoyi.yz.base.BaseCif;
import java.util.List;

/**
 * Auto-generated: 2019-06-18 23:13:12
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class StoTrailResponse extends BaseCif {

    private boolean status;
    private String code;
    private String message;
    private List<StoTrailResponseData> data;

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

    public void setData(List<StoTrailResponseData> data) {
        this.data = data;
    }

    public List<StoTrailResponseData> getData() {
        return data;
    }

}
