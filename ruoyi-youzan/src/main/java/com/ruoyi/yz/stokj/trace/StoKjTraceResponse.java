/**
 * Copyright 2019 bejson.com
 */
package com.ruoyi.yz.wuliu.stokj.trace;

import com.ruoyi.yz.base.BaseCif;
import java.util.List;

/**
 * Auto-generated: 2019-06-19 23:36:57
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class StoKjTraceResponse extends BaseCif {

    private Integer responseState;
    private String errorDesc;
    private List<ResponseItems> responseItems;

    public void setResponseState(Integer responseState) {
        this.responseState = responseState;
    }

    public Integer getResponseState() {
        return responseState;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setResponseItems(List<ResponseItems> responseItems) {
        this.responseItems = responseItems;
    }

    public List<ResponseItems> getResponseItems() {
        return responseItems;
    }

}
