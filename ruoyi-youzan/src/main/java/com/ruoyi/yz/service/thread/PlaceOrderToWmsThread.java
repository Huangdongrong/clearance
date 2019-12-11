/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service.thread;

import static com.ruoyi.yz.cnst.Const.CREATE_BY_PROGRAM;
import com.ruoyi.yz.domain.YouzanKdt;
import com.ruoyi.yz.domain.YouzanOrder;
import static com.ruoyi.yz.enums.OrderStatus.STATUS_INIT;
import static com.ruoyi.yz.enums.OrderStatus.STATUS_WAITING;
import com.ruoyi.yz.mapper.YouzanOrderMapper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.Objects.nonNull;
import java.util.concurrent.Callable;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.CollectionUtils.size;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author wmao
 */
@Component("placeOrderToWmsThread")
@Scope("prototype")
@Transactional
public class PlaceOrderToWmsThread implements Callable<Integer> {
    private static final Logger LOG = LoggerFactory.getLogger(PlaceOrderToWmsThread.class);

    private YouzanKdt kdt;

    @Autowired
    private YouzanOrderMapper youzanOrderMapper;

    @Autowired
    private ThreadPoolTaskExecutor executor;

    public void init(final YouzanKdt kdt) {
        this.kdt = kdt;
    }

    @Override
    public Integer call() throws Exception {
        int retLen = 0;
        if (nonNull(kdt) && isNotBlank(kdt.getAuthorityId())) {
            Date endTime = Calendar.getInstance().getTime();
            Date startTime = DateUtils.addDays(endTime, -1);
            Map<String, Object> params = new HashMap<String, Object>(){{
                put("startTime", startTime);
                put("endTime", endTime);
                put("authId", kdt.getAuthorityId());
            }};
            List<YouzanOrder> orders = youzanOrderMapper.getInitOrdersOfKdt(params);
            if (isNotEmpty(orders)) {
                Date currentTime = Calendar.getInstance().getTime();
                List<YouzanOrder> needUpdateList = new ArrayList<>();
                orders.forEach((order) -> {
                    if (nonNull(order)
                            && isBlank(order.getWayBillNo())
                            && equalsIgnoreCase(order.getStatus(), STATUS_INIT.name())) {
                        order.setStatus(STATUS_WAITING.name());
                        order.setStatusMessage(STATUS_WAITING.getValue());
                        order.setUpdateBy(CREATE_BY_PROGRAM);
                        order.setUpdateTime(currentTime);
                        needUpdateList.add(order);
                    } else {
                        LOG.error("no need to apply clearance for order:{}", order);
                    }
                });
                LOG.info("clear orders length:{}, need update orders length:{}", size(orders), size(needUpdateList));
                if (nonNull(needUpdateList)) {
                    retLen += youzanOrderMapper.batchUpdate(needUpdateList);
                    needUpdateList.forEach((od) -> {
                        if (nonNull(od)) {
                            executor.submit(new SendEmailThread(kdt, od.getTid()));
                        }
                    });
                } else {
                    LOG.warn("no need to update youzan order");
                }
            } else {
                LOG.error("no valid orders were found!");
            }
        }
        return retLen;
    }

}
