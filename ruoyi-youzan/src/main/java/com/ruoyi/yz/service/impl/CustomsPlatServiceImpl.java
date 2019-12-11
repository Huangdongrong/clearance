/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service.impl;

import com.ruoyi.yz.domain.CustomsPlat;
import com.ruoyi.yz.mapper.CustomsPlatMapper;
import com.ruoyi.yz.service.CustomsPlatService;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author wmao
 */
@Service("customsPlatService")
public class CustomsPlatServiceImpl implements CustomsPlatService{
    
    @Autowired
    private CustomsPlatMapper customsPlatMapper;

    @Override
    public CustomsPlat getOneByDistrict(String district) {
        return isNotBlank(district) ? customsPlatMapper.getOneByDistrict(district) : null;
    }
    
}
