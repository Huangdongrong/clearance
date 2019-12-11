/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.config;

import java.io.Serializable;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 * @author wmao
 */
@Component
@ConfigurationProperties(prefix = "sto-kj")
public class StoKjProperties implements Serializable{

    private Map<String, String> order;
    private Map<String, String> trace;

    /**
     * @return the order
     */
    public Map<String, String> getOrder() {
        return order;
    }

    /**
     * @param order the order to set
     */
    public void setOrder(Map<String, String> order) {
        this.order = order;
    }

    /**
     * @return the trace
     */
    public Map<String, String> getTrace() {
        return trace;
    }

    /**
     * @param trace the trace to set
     */
    public void setTrace(Map<String, String> trace) {
        this.trace = trace;
    }
}
