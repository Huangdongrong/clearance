/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service.impl;

import com.ruoyi.yz.domain.MvStockCusEntity;
import com.ruoyi.yz.mapper.MdCusMapper;
import com.ruoyi.yz.mapper.MvStockCusMapper;
import com.ruoyi.yz.mapper.YouzanKdtMapper;
import com.ruoyi.yz.service.MvStockCusService;
import java.util.List;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author wmao
 */
@Service("mvStockCusService")
public class MvStockCusServiceImpl implements MvStockCusService {
    
    private static final Logger LOG = LoggerFactory.getLogger(MvStockCusServiceImpl.class);
    
    @Autowired
    private MvStockCusMapper mvStockCusMapper;
    
    @Override
    public List<MvStockCusEntity> getList(MvStockCusEntity entity) {
        return nonNull(entity) && isNotBlank(entity.getCusCode()) ? mvStockCusMapper.getPageList(entity) : null;
    }
    
}
