/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.domain;

import com.ruoyi.common.core.domain.BaseQingmaEntity;
import com.ruoyi.yz.wuliu.Sender;
import org.apache.ibatis.type.Alias;

/**
 *
 * @author wmao
 */
@Alias("CustomsPlat")
public class CustomsPlat extends BaseQingmaEntity {
    private String url;
    private String copCode;
    private String copName;
    private String dxpMode;
    private String dxpId;
    private String district;
    private String version;
    private Sender sender;
    private String emsNo;

    /**
     * @return the copCode
     */
    public String getCopCode() {
        return copCode;
    }

    /**
     * @param copCode the copCode to set
     */
    public void setCopCode(String copCode) {
        this.copCode = copCode;
    }

    /**
     * @return the copName
     */
    public String getCopName() {
        return copName;
    }

    /**
     * @param copName the copName to set
     */
    public void setCopName(String copName) {
        this.copName = copName;
    }

    /**
     * @return the dxpMode
     */
    public String getDxpMode() {
        return dxpMode;
    }

    /**
     * @param dxpMode the dxpMode to set
     */
    public void setDxpMode(String dxpMode) {
        this.dxpMode = dxpMode;
    }

    /**
     * @return the dxpId
     */
    public String getDxpId() {
        return dxpId;
    }

    /**
     * @param dxpId the dxpId to set
     */
    public void setDxpId(String dxpId) {
        this.dxpId = dxpId;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the district
     */
    public String getDistrict() {
        return district;
    }

    /**
     * @param district the district to set
     */
    public void setDistrict(String district) {
        this.district = district;
    }

    /**
     * @return the sender
     */
    public Sender getSender() {
        return sender;
    }

    /**
     * @param sender the sender to set
     */
    public void setSender(Sender sender) {
        this.sender = sender;
    }

    /**
     * @return the emsNo
     */
    public String getEmsNo() {
        return emsNo;
    }

    /**
     * @param emsNo the emsNo to set
     */
    public void setEmsNo(String emsNo) {
        this.emsNo = emsNo;
    }
}
