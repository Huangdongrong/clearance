/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service.thread;

import com.ruoyi.common.exception.BusinessException;
import static com.ruoyi.common.utils.StringUtils.isNumeric;
import static com.ruoyi.common.utils.bean.BeanUtils.copyBeanProp;
import static com.ruoyi.common.utils.spring.SpringUtils.getBean;
import static com.ruoyi.yz.cnst.Const.CREATE_BY_PROGRAM;
import static com.ruoyi.yz.cnst.Const.DEFAULT_CLEARANCE_ERROR_STATUS;
import com.ruoyi.yz.customs.Message;
import com.ruoyi.yz.customs.details.CEB622Message;
import com.ruoyi.yz.domain.ClearanceStatus;
import com.ruoyi.yz.domain.CustomsPlat;
import com.ruoyi.yz.domain.WuliuItem;
import com.ruoyi.yz.domain.WuliuKjPlat;
import com.ruoyi.yz.domain.YouzanOrder;
import static com.ruoyi.yz.enums.OrderStatus.STATUS_REJECTED;
import static com.ruoyi.yz.enums.SyncRespSuccess.CUSTOMS_SUCC;
import com.ruoyi.yz.mapper.YouzanOrderMapper;
import com.ruoyi.yz.support.YzDetailsCustomSupport;
import static com.ruoyi.yz.utils.CustomsUtil.sendReq;
import static java.lang.Integer.parseInt;
import java.util.Calendar;
import java.util.List;
import static java.util.Objects.nonNull;
import java.util.concurrent.Callable;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author wmao
 */
@Component("clearDetailsToCustomsThread")
@Scope("prototype")
@Transactional
public class ClearDetailsToCustomsThread implements Callable<Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(ClearDetailsToCustomsThread.class);
    @Autowired
    private YouzanOrderMapper youzanOrderMapper;

    private CustomsPlat customsPlat;

    private List<YouzanOrder> orders;

    private WuliuKjPlat wuliuKjPlat;

    public void init(List<YouzanOrder> orders,
            CustomsPlat customsPlat,
            WuliuKjPlat wuliuKjPlat) {
        this.orders = orders;
        this.customsPlat = customsPlat;
        this.wuliuKjPlat = wuliuKjPlat;
    }

    @Override
    public Integer call() throws Exception {
        Integer counter = 0;
        if (isNotEmpty(orders)) {
            for (YouzanOrder order : orders) {
                if (nonNull(order)
                        && order.isAlreadySyncWuliu()
                        && order.isAlreadySyncOrder()
                        && order.isAlreadySyncPay()) {
                    ClearanceStatus payClearanceStatus = order.getSyncPayStatus();
                    if (nonNull(payClearanceStatus) && payClearanceStatus.isSuccess()) {
                        ClearanceStatus detailsClearanceStatus = order.isAlreadySyncDetails() ? order.getSyncDetailsStatus() : sendFinalDetailToCustoms(order);
                        if (nonNull(detailsClearanceStatus) && detailsClearanceStatus.isSuccess()) {
                            counter++;
                        } else {
                            LOG.error("failed to send details clearance to customs:{}", detailsClearanceStatus);
                        }
                    } else {
                        LOG.error("pay response is error:{}", payClearanceStatus);
                    }
                } else {
                    LOG.error("failed to sent other info to customs, no need to send details info to customs:{}", order);
                }
            }
        }
        return counter;
    }

    /**
     * 清关单报关
     */
    private ClearanceStatus sendFinalDetailToCustoms(YouzanOrder order) throws BusinessException {
        ClearanceStatus syncDetailsStatus = null;
        if (nonNull(order)) {
            try {
                YzDetailsCustomSupport support = getBean(YzDetailsCustomSupport.class);
                if (nonNull(support)) {
                    support.setCurrentTime(Calendar.getInstance().getTime());
                    support.setPlat(customsPlat);
                    WuliuItem wuliuItem = (WuliuItem) copyBeanProp(new WuliuItem(), wuliuKjPlat);
                    wuliuItem.setLogisticsNo(order.getWayBillNo());
                    List<Message> messages = support.generateRequest(order.getBody(), wuliuItem);
                    if (isNotEmpty(messages)) {
                        for (Message message : messages) {
                            if (nonNull(message)) {
                                CEB622Message detailsResp = (CEB622Message) sendReq(message, customsPlat, CEB622Message.class);
                                if (nonNull(detailsResp) && nonNull(detailsResp.getInventoryReturn())) {
                                    String sta = detailsResp.getInventoryReturn().getReturnStatus();
                                    if (isNumeric(sta)) {
                                        syncDetailsStatus = new ClearanceStatus(parseInt(sta), equalsIgnoreCase(sta, CUSTOMS_SUCC.getKey()), detailsResp.getInventoryReturn().getReturnInfo());
                                    } else {
                                        LOG.error("failed to get customer detail response because of invalid status");
                                        syncDetailsStatus = DEFAULT_CLEARANCE_ERROR_STATUS;
                                    }
                                } else {
                                    LOG.error("failed to get customer detail response");
                                    syncDetailsStatus = DEFAULT_CLEARANCE_ERROR_STATUS;
                                }
                                order.setSyncDetailsStatus(syncDetailsStatus);
                                order.setAlreadySyncDetails(true);
                                order.setUpdateTime(Calendar.getInstance().getTime());
                                order.setUpdateBy(CREATE_BY_PROGRAM);
                                if (!syncDetailsStatus.isSuccess()) {
                                    order.setStatus(STATUS_REJECTED.name());
                                    order.setStatusMessage(STATUS_REJECTED.getValue() + "[" + syncDetailsStatus.getMessage() + "]");
                                }
                                youzanOrderMapper.update(order);
                            } else {
                                LOG.error("failed to get order req from order body;{}", order);
                            }
                        }
                    }
                } else {
                    LOG.error("failed to get details message support!");
                }
            } catch (NumberFormatException ex) {
                LOG.error("failed to send detail info to customs:{}, exception:{}", order.getId(), ex.getMessage());
            }
        } else {
            LOG.error("failed to send detail info to customs:{} because of empty order", order);
        }
        return syncDetailsStatus;
    }

}
