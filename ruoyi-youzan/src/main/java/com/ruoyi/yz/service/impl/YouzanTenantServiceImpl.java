/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service.impl;

import com.ruoyi.yz.domain.YouzanUserToken;
import com.ruoyi.yz.mapper.YouzanUserTokenMapper;
import com.ruoyi.yz.service.YouzanTenantService;
import static java.util.Objects.nonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author wmao
 */
@Service("youzanTenantService")
@Transactional
public class YouzanTenantServiceImpl implements YouzanTenantService{
    
    @Autowired
    private YouzanUserTokenMapper youzanUserTokenMapper;

    @Override
    public int insert(YouzanUserToken userToken) {
        if(nonNull(userToken)){
            return youzanUserTokenMapper.insert(userToken);
        }
        return -1;
    }
    
}
