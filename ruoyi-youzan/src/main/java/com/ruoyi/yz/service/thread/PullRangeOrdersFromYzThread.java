/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service.thread;

import static com.ruoyi.yz.utils.YouZanUtil.listOrders;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradesSoldGetResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author wmao
 */
@Component("pullRangeOrdersFromYzThread")
@Scope("prototype")
@Transactional
public class PullRangeOrdersFromYzThread extends PullOrdersFromYzThread {
    private static final Logger LOG = LoggerFactory.getLogger(PullRangeOrdersFromYzThread.class);

    @Override
    protected YouzanTradesSoldGetResult getSoldResult() {
        return listOrders(kdt, startTime, endTime);
    }

    @Override
    protected int updateKdt() {
        LOG.warn("pull orders by date time range, on need to update kdt pulling order date");
        return 0;
    }
    
}
