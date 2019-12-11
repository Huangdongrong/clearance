/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.mapper;

import com.ruoyi.yz.domain.YouzanKdt;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.common.annotation.TargetDS;

/**
 *
 * @author wmao
 */
public interface YouzanKdtMapper {
    @TargetDS
    YouzanKdt getOne(@Param("id") String id);
    
    @TargetDS
    YouzanKdt getOneByAuthId(@Param("kdtId")String kdtId);
    
    @TargetDS
    List<YouzanKdt> getAvailKdts();
    
    @TargetDS
    int insert(YouzanKdt kdt); 
    
    @TargetDS
    int update(YouzanKdt kdt);
    
    @TargetDS
    List<YouzanKdt> getPageList(@Param("kdt") YouzanKdt kdt);
    
    @TargetDS
    int delete(@Param("authId") String authId);
    
    @TargetDS
    int batchDelete(@Param("ids") List<String> ids);
    
    @TargetDS
    int batchRecovery(@Param("ids") List<String> ids);
}
