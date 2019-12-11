/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.mapper;

import com.ruoyi.yz.domain.YouzanAuthMessage;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.common.annotation.TargetDS;

/**
 *
 * @author wmao
 */
public interface YouzanAuthMessageMapper {

    @TargetDS
    YouzanAuthMessage getOne(@Param("kdtId") String kdtId);

    @TargetDS
    int insert(YouzanAuthMessage message);
    
    @TargetDS
    int update(YouzanAuthMessage message);
}
