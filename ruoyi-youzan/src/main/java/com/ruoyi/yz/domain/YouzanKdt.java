/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.domain;

import java.util.Date;
import com.ruoyi.common.core.domain.BaseQingmaEntity;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;
import static com.ruoyi.common.utils.UuidUtil.get32UUID;
import com.ruoyi.yz.wuliu.Sender;
import java.util.Calendar;

/**
 *
 * @author wmao
 */
@Alias("YouzanKdt")
public class YouzanKdt extends BaseQingmaEntity {
    /**
     * 海关注册店铺名
     */
    private String name;
    /**
     * 海关注册店铺代码
     */
    private String regCode;
    private String code;
    private String accessToken;
    private Date expires;
    private String scope;
    private String refreshToken;
    private String authorityId;
    private String authorityName;
    private String authorityDisplayNo;
    private String status;
    private boolean autoPulling;
    private boolean autoClearing;
    private Date lastPulledDate;
    private Sender sender;

    public YouzanKdt() {

    }

    public YouzanKdt(String code, String regName, String regCode) {
        this.id = get32UUID();
        this.code = code;
        this.name = regName;
        this.regCode = regCode;
        this.createTime = Calendar.getInstance().getTime();
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
     * @return the accessToken
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * @param accessToken the accessToken to set
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * @return the expires
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getExpires() {
        return expires;
    }

    /**
     * @param expires the expires to set
     */
    public void setExpires(Date expires) {
        this.expires = expires;
    }

    /**
     * @return the scope
     */
    public String getScope() {
        return scope;
    }

    /**
     * @param scope the scope to set
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * @return the refreshToken
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * @param refreshToken the refreshToken to set
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * @return the authorityId
     */
    public String getAuthorityId() {
        return authorityId;
    }

    /**
     * @param authorityId the authorityId to set
     */
    public void setAuthorityId(String authorityId) {
        this.authorityId = authorityId;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the lastPulledDate
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getLastPulledDate() {
        return lastPulledDate;
    }

    /**
     * @param lastPulledDate the lastPulledDate to set
     */
    public void setLastPulledDate(Date lastPulledDate) {
        this.lastPulledDate = lastPulledDate;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the regCode
     */
    public String getRegCode() {
        return regCode;
    }

    /**
     * @param regCode the regCode to set
     */
    public void setRegCode(String regCode) {
        this.regCode = regCode;
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
     * @return the authorityName
     */
    public String getAuthorityName() {
        return authorityName;
    }

    /**
     * @param authorityName the authorityName to set
     */
    public void setAuthorityName(String authorityName) {
        this.authorityName = authorityName;
    }

    /**
     * @return the authorityDisplayNo
     */
    public String getAuthorityDisplayNo() {
        return authorityDisplayNo;
    }

    /**
     * @param authorityDisplayNo the authorityDisplayNo to set
     */
    public void setAuthorityDisplayNo(String authorityDisplayNo) {
        this.authorityDisplayNo = authorityDisplayNo;
    }

    /**
     * @return the autoPulling
     */
    public boolean isAutoPulling() {
        return autoPulling;
    }

    /**
     * @param autoPulling the autoPulling to set
     */
    public void setAutoPulling(boolean autoPulling) {
        this.autoPulling = autoPulling;
    }

    /**
     * @return the autoClearing
     */
    public boolean isAutoClearing() {
        return autoClearing;
    }

    /**
     * @param autoClearing the autoClearing to set
     */
    public void setAutoClearing(boolean autoClearing) {
        this.autoClearing = autoClearing;
    }
}
