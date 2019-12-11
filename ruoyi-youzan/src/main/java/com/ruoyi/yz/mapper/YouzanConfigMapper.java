/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.mapper;

import com.ruoyi.yz.domain.YouzanConfig;
import com.ruoyi.common.annotation.TargetDS;

/**
 *
 * @author wmao
 */
public interface YouzanConfigMapper {
    
    @TargetDS
    YouzanConfig getOne();
    
    //@TargetDS
    //int update(YouzanConfig config);
}
