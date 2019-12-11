/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.mapper;

import com.ruoyi.common.annotation.TargetDS;
import static com.ruoyi.common.enums.DataSourceType.SLAVE;
import org.apache.ibatis.annotations.Param;

/**
 *
 * @author wmao
 */
public interface MdCusMapper {
    @TargetDS(value=SLAVE)
    String getCusCodeByAuthId(@Param("authId") String authId);
}
