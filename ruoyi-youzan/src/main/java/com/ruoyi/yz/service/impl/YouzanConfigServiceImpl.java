/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service.impl;

import com.ruoyi.yz.domain.YouzanConfig;
import com.ruoyi.yz.mapper.YouzanConfigMapper;
import com.ruoyi.yz.service.YouzanConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author wmao
 */
@Service("youzanConfigService")
@Transactional
public class YouzanConfigServiceImpl implements YouzanConfigService {

    private static final Logger LOG = LoggerFactory.getLogger(YouzanConfigServiceImpl.class);

    @Autowired
    private YouzanConfigMapper youzanConfigMapper;

    @Override
    public YouzanConfig getOne() {
        return youzanConfigMapper.getOne();
    }
}
