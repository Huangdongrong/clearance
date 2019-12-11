/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.customs;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author wmao
 */
@XmlTransient
public abstract class RequestMessage extends Message {

    @XmlElement(name = "BaseTransfer", required = true, namespace = "http://www.chinaport.gov.cn/ceb")
    public BaseTransfer baseTransfer;
    @XmlElement(name = "Authentication", required = true, namespace = "http://www.chinaport.gov.cn/ceb")
    public Authentication authentication;

    /**
     * @return the baseTransfer
     */
    public BaseTransfer getBaseTransfer() {
        return baseTransfer;
    }

    /**
     * @param baseTransfer the baseTransfer to set
     */
    public void setBaseTransfer(BaseTransfer baseTransfer) {
        this.baseTransfer = baseTransfer;
    }

    /**
     * @return the authentication
     */
    public Authentication getAuthentication() {
        return authentication;
    }

    /**
     * @param authentication the authentication to set
     */
    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

}
