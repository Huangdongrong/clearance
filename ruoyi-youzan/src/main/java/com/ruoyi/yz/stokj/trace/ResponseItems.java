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
public class ResponseItems extends BaseCif {

    private String logisticsProviderCode;
    private List<Traces> traces;
    private String waybillCode;

    public void setLogisticsProviderCode(String logisticsProviderCode) {
        this.logisticsProviderCode = logisticsProviderCode;
    }

    public String getLogisticsProviderCode() {
        return logisticsProviderCode;
    }

    public void setTraces(List<Traces> traces) {
        this.traces = traces;
    }

    public List<Traces> getTraces() {
        return traces;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

}
