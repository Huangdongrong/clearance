/**
 * Copyright 2019 bejson.com
 */
package com.ruoyi.yz.wuliu.stokj.trace;

import com.ruoyi.yz.base.BaseCif;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Auto-generated: 2019-06-19 23:36:57
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Traces extends BaseCif {

    private String action;
    private String opcodeDesc;
    private String detailDesc;
    private Date timeStr;
    private BigDecimal weight;
    private String facilityName;
    private String uploadSiteId;
    private String facilityNo;
    private String city;
    private String facilityType;
    private String nextNodeName;
    private String contacter;
    private String contactPhone;

    public void setDetailDesc(String detailDesc) {
        this.detailDesc = detailDesc;
    }

    public String getDetailDesc() {
        return detailDesc;
    }

    public void setContacter(String contacter) {
        this.contacter = contacter;
    }

    public String getContacter() {
        return contacter;
    }

    public void setUploadSiteId(String uploadSiteId) {
        this.uploadSiteId = uploadSiteId;
    }

    public String getUploadSiteId() {
        return uploadSiteId;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setFacilityNo(String facilityNo) {
        this.facilityNo = facilityNo;
    }

    public String getFacilityNo() {
        return facilityNo;
    }

    public void setTimeStr(Date timeStr) {
        this.timeStr = timeStr;
    }

    public Date getTimeStr() {
        return timeStr;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setOpcodeDesc(String opcodeDesc) {
        this.opcodeDesc = opcodeDesc;
    }

    public String getOpcodeDesc() {
        return opcodeDesc;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    /**
     * @return the weight
     */
    public BigDecimal getWeight() {
        return weight;
    }

    /**
     * @param weight the weight to set
     */
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    /**
     * @return the nextNodeName
     */
    public String getNextNodeName() {
        return nextNodeName;
    }

    /**
     * @param nextNodeName the nextNodeName to set
     */
    public void setNextNodeName(String nextNodeName) {
        this.nextNodeName = nextNodeName;
    }

}
