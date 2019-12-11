/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service;

import com.ruoyi.yz.domain.MvGoodsEntity;
import java.util.List;

/**
 *
 * @author wmao
 */
public interface MvGoodsService {
    MvGoodsEntity getOne(String shpBianMa);
    
    int howManyGoods();
    
    List<String> allShpBianMa(String cusCode);
    
    String getCusCodeByAuthId(String kdtId);
    
    List<MvGoodsEntity> getPageList(MvGoodsEntity entity);
}
