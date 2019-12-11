/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service;

import com.ruoyi.yz.wuliu.stokj.order.StoKjOrderRequest;
import com.ruoyi.yz.wuliu.stokj.order.StoKjOrderResponse;
import com.ruoyi.yz.wuliu.stokj.trace.StoKjTraceRequest;
import com.ruoyi.yz.wuliu.stokj.trace.StoKjTraceResponse;

/**
 *
 * @author wmao
 */
public interface StoKjOrderService {
    
    StoKjOrderResponse sendRequest(StoKjOrderRequest request);
    
    StoKjTraceResponse findTrail(StoKjTraceRequest request);
}
