/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.mapper;

import static com.ruoyi.common.enums.DataSourceType.SLAVE;
import com.ruoyi.yz.domain.MvGoodsEntity;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.common.annotation.TargetDS;
import static com.ruoyi.common.constant.RedisConstants.REDIS_WMS_CACHE_NAME;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;

/**
 *
 * @author wmao
 */
public interface MvGoodsMapper {

    @TargetDS(value = SLAVE)
    MvGoodsEntity getOneByShpBianMa(@Param("shpBianMa") String shpBianMa);

    @TargetDS(value = SLAVE)
    int howManyGoods();

    @TargetDS(value = SLAVE)
    //@Cacheable(cacheNames = REDIS_WMS_CACHE_NAME, key = "'shp_bian_ma_list_'+#cusCode")
    List<String> allShpBianMa(@Param("cusCode") String cusCode);

    @TargetDS(value = SLAVE)
    List<MvGoodsEntity> getPageList(@Param("good") MvGoodsEntity good);
}
