/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.domain;

import java.util.Date;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author wmao
 */
@Alias("YouzanAuthMessage")
public class YouzanAuthMessage extends YouzanMessage {

    private Date expireTime;
    private Date effectTime;

    /**
     * @return the expireTime
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getExpireTime() {
        return expireTime;
    }

    /**
     * @param expireTime the expireTime to set
     */
    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    /**
     * @return the effectTime
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getEffectTime() {
        return effectTime;
    }

    /**
     * @param effectTime the effectTime to set
     */
    public void setEffectTime(Date effectTime) {
        this.effectTime = effectTime;
    }
}
