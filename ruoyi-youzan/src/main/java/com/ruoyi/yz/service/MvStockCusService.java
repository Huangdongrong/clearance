/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service;

import com.ruoyi.yz.domain.MvStockCusEntity;
import java.util.List;

/**
 *
 * @author wmao
 */
public interface MvStockCusService {
    
    List<MvStockCusEntity> getList(MvStockCusEntity entity);
    
}
