/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.mapper;

import com.ruoyi.yz.domain.WuliuKjPlat;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.common.annotation.TargetDS;

/**
 *
 * @author wmao
 */
public interface WuliuKjPlatMapper{
    @TargetDS
    WuliuKjPlat getOne(@Param("keyName") String keyName);
}
