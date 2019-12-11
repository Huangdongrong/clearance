/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.mapper;

import com.ruoyi.yz.domain.CustomsPlat;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.common.annotation.TargetDS;

/**
 *
 * @author wmao
 */
public interface CustomsPlatMapper{
    @TargetDS
    CustomsPlat getOneByDistrict(@Param("district") String district);
    
    @TargetDS
    CustomsPlat getOne(@Param("id") String id);
}
