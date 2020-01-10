/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.mapper;

import com.ruoyi.yz.domain.YouzanOrder;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.common.annotation.TargetDS;
import java.util.Map;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author wmao
 */
@Scope("prototype")
public interface YouzanOrderMapper {
    @TargetDS
    List<YouzanOrder> getPageList(@Param("order") YouzanOrder order);

    @TargetDS
    List<YouzanOrder> getInitOrdersOfKdt(@Param("kdt") Map<String, Object> kdt);

    @TargetDS
    List<YouzanOrder> getByIds(@Param("ids") String[] ids, @Param("authId") String authId);

    @TargetDS
    List<YouzanOrder> getReadyApplyingOrdersOfKdt(@Param("kdt") Map<String, Object> kdt);

    @TargetDS
    List<YouzanOrder> getReadyCompleteOrdersOfKdt(@Param("params") Map<String, Object> params);

    @TargetDS
    List<YouzanOrder> getReadyClearingDetailsOfKdt(@Param("params") Map<String, Object> params);

    @TargetDS
    List<YouzanOrder> getNeedToQueryPayClearanceOrdersOfKdt(@Param("kdt") Map<String, Object> kdt);

    @TargetDS
    YouzanOrder getOne(@Param("id") String id);

    @TargetDS
    YouzanOrder getOneByOrderNo(@Param("orderNo") String orderNo);

    @TargetDS
    int insert(YouzanOrder order);

    @TargetDS
    int batchInsert(@Param("orders") List<YouzanOrder> orders);

    @TargetDS
    List<YouzanOrder> queryExisted(@Param("params") Map<String, Object> params);

    @TargetDS
    int batchUpdate(@Param("orders") List<YouzanOrder> orders);

    @TargetDS
    int batchDelete(@Param("ids") List<String> ids);

    @TargetDS
    List<YouzanOrder> existed(@Param("trans") List<String> trans, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    @TargetDS
    List<YouzanOrder> existedOrders(@Param("orderNos") List<String> orderNos, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    @TargetDS
    int update(YouzanOrder order);

    @TargetDS
    List<YouzanOrder> pullOrders(@Param("lastPulledDate") Date lastPulledDate);

    @TargetDS
    YouzanOrder getOneByCopNo(@Param("copNo") String copNo);

    @TargetDS
    List<Map<String, Object>> getOrdersOfKdts(@Param("kdtIds") List<String> kdtIds, @Param("status") String status, @Param("startTime") Date startTime, @Param("endTime") Date endTime);
    
    @TargetDS
    int batchClearStatus(@Param("orders") List<YouzanOrder> orders);
}
