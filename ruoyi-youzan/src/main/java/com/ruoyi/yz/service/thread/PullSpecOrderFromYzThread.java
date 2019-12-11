/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service.thread;

import com.ruoyi.yz.customs.order.CEB311Message;
import com.ruoyi.yz.domain.CustomsPlat;
import com.ruoyi.yz.domain.YouzanKdt;
import com.ruoyi.yz.domain.YouzanOrder;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradesSoldGetResult;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import static com.ruoyi.yz.utils.YouZanUtil.queryOrder;
import java.util.Date;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.apache.commons.lang3.time.DateUtils.addSeconds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author wmao
 */
@Component("pullSpecOrderFromYzThread")
@Scope("prototype")
@Transactional
public class PullSpecOrderFromYzThread extends PullOrdersFromYzThread {

    private static final Logger LOG = LoggerFactory.getLogger(PullSpecOrderFromYzThread.class);
    private YouzanOrder order;

    public void init(YouzanOrder order, YouzanKdt kdt, CustomsPlat plat) {
        this.order = order;
        Date tmpStartTime = null;
        Date tmpEndTime = null;
        if (nonNull(order) && nonNull(order.getBody())) {
            CEB311Message message = (CEB311Message) order.getBody();
            if (nonNull(message) && nonNull(message.getOrder()) && nonNull(message.getOrder().getOrderHead())) {
                Date st = message.getOrder().getOrderHead().getOrderTime();
                if (nonNull(st) && st.before(currentTime)) {
                    //起始时间是订单时间前1秒
                    tmpStartTime = addSeconds(st, -1);
                    //结束时间是订单时间后一天或者当前时间
                    tmpEndTime = addDays(st, 1);
                    tmpEndTime = tmpEndTime.after(currentTime) ? currentTime : tmpEndTime;
                }
            }
        }
        super.init(kdt, plat, nonNull(tmpStartTime) ? tmpStartTime : addDays(currentTime, -1), nonNull(tmpEndTime) ? tmpEndTime : currentTime);

    }

    @Override
    protected YouzanTradesSoldGetResult getSoldResult() {
        return nonNull(order) ? queryOrder(kdt, order.getTid()) : null;
    }

    @Override
    protected int updateKdt() {
        LOG.warn("pull specified order, on need to update kdt pull order date");
        return 0;
    }

}
