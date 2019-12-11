/**
  * Copyright 2019 bejson.com 
  */
package com.ruoyi.yz.wuliu.stokj.trace;
import com.ruoyi.yz.base.BaseCif;
import java.util.List;

/**
 * Auto-generated: 2019-06-19 23:31:26
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class StoKjTraceRequest  extends BaseCif{

    private String partnerCode;
    private List<String> waybillNos;
    public void setPartnerCode(String partnerCode) {
         this.partnerCode = partnerCode;
     }
     public String getPartnerCode() {
         return partnerCode;
     }

    public void setWaybillNos(List<String> waybillNos) {
         this.waybillNos = waybillNos;
     }
     public List<String> getWaybillNos() {
         return waybillNos;
     }

}