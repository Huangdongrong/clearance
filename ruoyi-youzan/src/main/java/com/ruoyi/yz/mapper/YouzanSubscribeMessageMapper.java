/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.mapper;

import com.ruoyi.yz.domain.YouzanSubscribeMessage;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.common.annotation.TargetDS;

/**
 *
 * @author wmao
 */
public interface YouzanSubscribeMessageMapper {
    @TargetDS
    YouzanSubscribeMessage getOne(@Param("kdtId") String kdtId);
    
    @TargetDS
    int insert(YouzanSubscribeMessage message);
}
