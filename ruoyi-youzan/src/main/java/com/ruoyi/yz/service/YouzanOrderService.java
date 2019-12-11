/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service;

import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.yz.domain.YouzanOrder;
import com.ruoyi.yz.domain.YouzanKdt;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 *
 * @author wmao
 */
public interface YouzanOrderService{
    
    void placeWmsOrders();
    
    void pullOrders();
    
    int pullOrders(YouzanOrder order, YouzanKdt kdt);
    
    List<YouzanOrder> getList(YouzanOrder order);
    
    int execute(String[] ids, String kdtId) throws BusinessException;
    
    int autoQueryPayClearanceResult(YouzanKdt kdt, Date lastQueryDate) throws BusinessException;
    
    int autoExecute(YouzanKdt kdt, Date lastExecuteDate) throws BusinessException;
    
    int autoDetailsExecute(YouzanKdt kdt, Date lastExecuteDate) throws BusinessException;
    
    int autoComplete(YouzanKdt kdt, Date lastCompleteDate) throws BusinessException;
    
    int discard(String[] ids, String kdtId) throws BusinessException;
    
    int remove(String[] ids, String kdtId) throws BusinessException;
    
    int recovery(String[] ids, String kdtId) throws BusinessException;
    
    List<YouzanOrder> wmsPullOrders(Date lastPulledDate) throws BusinessException;
    
    YouzanOrder getOne(String orderId);
    
    YouzanOrder getOneByOrderNo(@Param("orderNo") String orderNo);
    
    YouzanOrder getOneByCopNo(@Param("copNo") String copNo);
    
    int update(YouzanOrder order);
    
    int batchInsert(List<YouzanOrder> orders);
    
    int batchUpdate(List<YouzanOrder> orders);
    
    List<Map<String, Object>> getOrdersOfKdts(List<String> kdtIds, String status, Date startTime, Date endTime);
}
