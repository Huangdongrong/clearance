/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service.thread;

import static com.ruoyi.common.utils.JsonUtil.stringify;
import static com.ruoyi.yz.cnst.Const.CREATE_BY_PROGRAM;
import java.util.concurrent.Callable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.yz.domain.YouzanKdt;
import com.ruoyi.yz.domain.YouzanOrder;
import static com.ruoyi.yz.enums.OrderStatus.STATUS_COMPLETED;
import static com.ruoyi.yz.enums.OrderStatus.isSucceed;
import static com.ruoyi.yz.enums.WuliuComp.getEntByValue;
import com.ruoyi.yz.service.YouzanOrderService;
import static com.ruoyi.yz.utils.YouZanUtil.confirm;
import static com.ruoyi.yz.utils.YouZanUtil.findWuliuCode;
import static java.net.HttpURLConnection.HTTP_OK;
import java.util.Calendar;
import java.util.List;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.ruoyi.yz.utils.YouZanUtil.listLogistics;
import com.youzan.cloud.open.sdk.gen.v3_0_0.model.YouzanLogisticsExpressGetResult;
import com.youzan.cloud.open.sdk.gen.v3_0_0.model.YouzanLogisticsOnlineConfirmResult;

/**
 *
 * @author wmao
 */
@Component("completeOrderThread")
@Scope("prototype")
@Transactional
public class CompleteOrderThread implements Callable<Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(CompleteOrderThread.class);

    @Autowired
    private YouzanOrderService youzanOrderService;

    private YouzanKdt kdt;

    private List<YouzanOrder> orders;

    public void init(YouzanKdt kdt, List<YouzanOrder> orders) {
        this.kdt = kdt;
        this.orders = orders;
    }

    @Override
    public Integer call() throws Exception {
        int ret = 0;
        if (isNotEmpty(orders) && nonNull(kdt)) {
            final List<YouzanLogisticsExpressGetResult.YouzanLogisticsExpressGetResultAllexpress> logistics = listLogistics(kdt);
            if (isNotEmpty(logistics)) {
                for (YouzanOrder order : orders) {
                    try {
                        if (nonNull(order)
                                && equalsIgnoreCase(order.getKdtId(), kdt.getAuthorityId())
                                && isSucceed(order.getStatus())) {
                            order.setSearchValue(findWuliuCode(logistics, getEntByValue(order.getWayBillEnt())));
                            YouzanLogisticsOnlineConfirmResult result = confirm(kdt, order);
                            if (nonNull(result) && result.getCode() == HTTP_OK && result.getSuccess()) {
                                order.setStatus(STATUS_COMPLETED.name());
                                order.setStatusMessage(STATUS_COMPLETED.getValue());
                                order.setUpdateTime(Calendar.getInstance().getTime());
                                order.setUpdateBy(CREATE_BY_PROGRAM);
                                ret += youzanOrderService.update(order);
                                LOG.info("completed order {} ", order.getTid());
                            } else {
                                LOG.error("failed to get any result of order:{}, {}", order.getTid(), stringify(result));
                            }
                        } else {
                            LOG.warn("invalid order status, it's weired! order:{}, youzanKdt:{}", order, kdt);
                        }
                    } catch (Exception ex) {
                        LOG.error("failed to comfirm order:{} is completed. {}", order, ex.getMessage());
                    }
                }
                LOG.info("对商户{}结单请求发送结束,共发送{}个请求，请等待结果", kdt.getAuthorityName(), ret);
            } else {
                LOG.error("failed to get logistics list:{}", logistics);
            }
        } else {
            LOG.error("Failed to find any success clearance orders");
        }
        LOG.info("completed orders:{} of kdt:{}", ret, kdt.getAuthorityName());
        return ret;
    }
}
