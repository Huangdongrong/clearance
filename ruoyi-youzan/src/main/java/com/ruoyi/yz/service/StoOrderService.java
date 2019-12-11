/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service;

import com.ruoyi.yz.customs.Message;
import com.ruoyi.yz.wuliu.sto.order.StoOrderRequest;
import com.ruoyi.yz.wuliu.sto.order.StoOrderResponse;
import com.ruoyi.yz.wuliu.sto.platorder.StoPlatOrderRequest;
import com.ruoyi.yz.wuliu.sto.platorder.StoPlatOrderResponse;
import com.ruoyi.yz.wuliu.sto.trail.StoTrailResponse;

/**
 *
 * @author wmao
 */
public interface StoOrderService {
    StoPlatOrderResponse sendPlatRequest(StoPlatOrderRequest request);
    
    StoOrderResponse sendRequest(StoOrderRequest request);
    
    StoTrailResponse findTrail(String[] waybillNos);
    
    Message cvtStoRes2CustomsMsg(StoOrderRequest request, StoOrderResponse response);
    
    Message cvtStoPlatRes2CustomsMsg(StoPlatOrderRequest request, StoPlatOrderResponse response);
}
