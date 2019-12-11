/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service.impl;

import com.ruoyi.yz.domain.MvGoodsEntity;
import com.ruoyi.yz.mapper.MdCusMapper;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.yz.service.MvGoodsService;
import com.ruoyi.yz.mapper.MvGoodsMapper;
import java.util.ArrayList;
import java.util.List;
import static java.util.Objects.nonNull;

/**
 *
 * @author wmao
 */
@Service("mvGoodsService")
public class MvGoodsServiceImpl implements MvGoodsService {

    @Autowired
    private MvGoodsMapper mvGoodsMapper;

    @Autowired
    private MdCusMapper mdCusMapper;

    @Override
    public MvGoodsEntity getOne(String shpBianMa) {
        return isNotBlank(shpBianMa) ? mvGoodsMapper.getOneByShpBianMa(shpBianMa) : null;
    }

    @Override
    public int howManyGoods() {
        return mvGoodsMapper.howManyGoods();
    }

    @Override
    public List<String> allShpBianMa(String cusCode) {
        return isNotBlank(cusCode) ? mvGoodsMapper.allShpBianMa(cusCode) : new ArrayList<>();
    }

    @Override
    public String getCusCodeByAuthId(String authId) {
        String cusCode = null;
        if (isNotBlank(authId)) {
            cusCode = mdCusMapper.getCusCodeByAuthId(authId);
        }
        return cusCode;
    }

    @Override
    public List<MvGoodsEntity> getPageList(MvGoodsEntity entity) {
        List<MvGoodsEntity> goods = null;
        if (nonNull(entity)) {
            String cusCode = getCusCodeByAuthId(entity.getCusCode());
            if (isNotBlank(cusCode)) {
                entity.setCusCode(cusCode);
                goods = mvGoodsMapper.getPageList(entity);
            }
        }
        return goods;
    }

}
