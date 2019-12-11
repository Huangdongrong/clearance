/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service.thread;

import static com.ruoyi.common.utils.DateUtil.addDays;
import com.ruoyi.yz.domain.CustomsPlat;
import com.ruoyi.yz.domain.YouzanKdt;
import static com.ruoyi.yz.utils.YouZanUtil.listOrders;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradesSoldGetResult;
import java.util.Date;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.time.DateUtils.addMinutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author wmao
 */
@Component("pullAllOrdersFromYzThread")
@Scope("prototype")
@Transactional
public class PullAllOrdersFromYzThread extends PullOrdersFromYzThread {
    private static final Logger LOG = LoggerFactory.getLogger(PullAllOrdersFromYzThread.class);


    public void init(YouzanKdt kdt, CustomsPlat plat) {
        Date st = null;
        final Date lastPulledDate = nonNull(kdt) ? kdt.getLastPulledDate() : null;
        if (nonNull(lastPulledDate)) {
            if (lastPulledDate.before(currentTime)) {
                st = addMinutes(lastPulledDate, -1);
            } else {
                LOG.warn("lastPulledDate is after currentTime {}", lastPulledDate, currentTime);
                st = addDays(currentTime, -1);
            }
        } else {
            LOG.warn("lastPulleddate is null");
            st = addDays(currentTime, -1);
        }
        super.init(kdt, plat, nonNull(st) ? st : addDays(currentTime, -1), currentTime);
    }
    @Override
    protected YouzanTradesSoldGetResult getSoldResult() {
        return listOrders(kdt, startTime, endTime);
    }

    @Override
    @Transactional
    protected int updateKdt() {
        int ret = -1;
        if (nonNull(kdt)) {
            kdt.setLastPulledDate(currentTime);
            ret = youzanKdtMapper.update(kdt);
        }
        return ret;
    }

}
