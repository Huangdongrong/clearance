/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service.thread;

import com.ruoyi.common.exception.BusinessException;
import static com.ruoyi.common.utils.spring.SpringUtils.getBean;
import static com.ruoyi.yz.cnst.Const.CREATE_BY_PROGRAM;
import static com.ruoyi.yz.cnst.Const.DEFAULT_CLEARANCE_ERROR_STATUS;
import com.ruoyi.yz.customs.order.CEB312Message;
import com.ruoyi.yz.customs.order.OrderReturn;
import com.ruoyi.yz.domain.ClearanceStatus;
import com.ruoyi.yz.domain.CustomsPlat;
import com.ruoyi.yz.domain.YouzanKdt;
import com.ruoyi.yz.domain.YouzanOrder;
import com.ruoyi.yz.mapper.YouzanOrderMapper;
import com.ruoyi.yz.service.StoKjOrderService;
import com.ruoyi.yz.service.YundaKjOrderService;
import static com.ruoyi.yz.utils.CustomsUtil.sendReq;
import static com.ruoyi.yz.utils.YouZanUtil.reportPay;
import com.ruoyi.yz.wuliu.stokj.order.StoKjOrderRequest;
import com.ruoyi.yz.wuliu.stokj.order.StoKjOrderResponse;
import com.youzan.cloud.open.sdk.gen.v1_0_1.model.YouzanPayCustomsDeclarationReportpaymentReportResult;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.Objects.nonNull;
import java.util.concurrent.Callable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trim;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import static com.ruoyi.yz.enums.SyncRespSuccess.CUSTOMS_SUCC;
import static com.ruoyi.yz.enums.SyncRespSuccess.YUNDA_SUCC;
import com.ruoyi.yz.enums.WuliuComp;
import static com.ruoyi.yz.enums.WuliuComp.getEntByValue;
import org.springframework.beans.BeansException;
import static com.ruoyi.yz.utils.StoUtil.assembleStoWlReq;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static com.ruoyi.yz.utils.YundaUtil.assembleYdApplyReq;
import com.ruoyi.yz.wuliu.ydkj.apply.YdApplyResponse;
import com.ruoyi.yz.wuliu.ydkj.apply.YdApplyResponses;
import static java.lang.Integer.parseInt;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import org.apache.commons.lang.ArrayUtils;
import org.apache.ibatis.reflection.ArrayUtil;
import static com.ruoyi.yz.cnst.Const.RUOYI_INTERNAL_ERROR;
import static com.ruoyi.yz.enums.OrderStatus.STATUS_REJECTED;
import static com.ruoyi.yz.enums.OrderStatus.isReadyApply;
import com.youzan.cloud.open.sdk.gen.v1_0_1.model.YouzanPayCustomsDeclarationReportpaymentReportResult.YouzanPayCustomsDeclarationReportpaymentReportResultData;

/**
 *
 * @author wmao
 */
@Component("clearOrderToCustomsThread")
@Scope("prototype")
@Transactional
public class ClearOrderToCustomsThread implements Callable<Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(ClearOrderToCustomsThread.class);
    
    @Autowired
    private YouzanOrderMapper youzanOrderMapper;

    private YouzanKdt youzanKdt;

    private CustomsPlat customsPlat;    
    
    private CustomsPlat customsWuliuProxyPlat;
    
    private List<YouzanOrder> orders;

    public void init(List<YouzanOrder> orders,
            YouzanKdt youzanKdt,
            CustomsPlat customsPlat,
            CustomsPlat customsWuliuProxyPlat) {
        this.orders = orders;
        this.youzanKdt = youzanKdt;
        this.customsPlat = customsPlat;
        this.customsWuliuProxyPlat = customsWuliuProxyPlat;
    }

    /**
     * 支付单报关
     */
    private ClearanceStatus sendPayToCustoms(YouzanOrder order) throws BusinessException {
        ClearanceStatus syncPayStatus = null;
        if (nonNull(order)) {
            try {
                YouzanPayCustomsDeclarationReportpaymentReportResult reportResult = reportPay(youzanKdt, order);
                if (nonNull(reportResult)) {
                    int retCode = reportResult.getCode();
                    YouzanPayCustomsDeclarationReportpaymentReportResultData data = reportResult.getData();
                    LOG.info("code:{}, message:{}, success:{}", reportResult.getCode(), reportResult.getMessage(), reportResult.getSuccess());
                    syncPayStatus = new ClearanceStatus(retCode == HTTP_OK && nonNull(data) ? data.getCustomsStatus() : retCode, reportResult.getSuccess(), reportResult.getMessage());
                } else {
                    LOG.error("repor pay with no response");
                    syncPayStatus = DEFAULT_CLEARANCE_ERROR_STATUS;
                }
                order.setSyncPayStatus(syncPayStatus);
                order.setAlreadySyncPay(true);
                order.setUpdateTime(Calendar.getInstance().getTime());
                order.setUpdateBy(CREATE_BY_PROGRAM);
                if (!syncPayStatus.isSuccess()) {
                    order.setStatus(STATUS_REJECTED.name());
                    order.setStatusMessage(STATUS_REJECTED.getValue() + "[" + syncPayStatus.getMessage() + "]");
                }
                youzanOrderMapper.update(order);
            } catch (Exception ex) {
                ex.printStackTrace();
                LOG.error("failed to send pay info to customs:{} {}", ArrayUtils.toString(ex.getStackTrace()), order.getTid());
                throw new BusinessException("failed to send pay info to customs " + order.getTid());
            }
        } else {
            LOG.error("failed to send pay info to customs:{} because of empty order", order);
            throw new BusinessException("failed to send pay info to customs " + order.getTid());
        }
        return syncPayStatus;
    }

    /**
     * 订单报关
     */
    private ClearanceStatus sendOrderToCustoms(YouzanOrder order) throws BusinessException {
        ClearanceStatus syncOrderStatus = null;
        if (nonNull(order)) {
            try {
                CEB312Message orderResp = (CEB312Message) sendReq(order.getBody(), customsPlat, CEB312Message.class);
                if (nonNull(orderResp)) {
                    OrderReturn or = orderResp.getOrderReturn();
                    if (nonNull(or)) {
                        try {
                            String retureStatus = trim(or.getReturnStatus());
                            syncOrderStatus = new ClearanceStatus(parseInt(retureStatus), equalsIgnoreCase(CUSTOMS_SUCC.getKey(), retureStatus), or.getReturnInfo());
                        } catch (NumberFormatException ex) {
                            LOG.error("failed to parse error code:{}, {}", ArrayUtil.toString(ex.getStackTrace()), order.getTid());
                            syncOrderStatus = DEFAULT_CLEARANCE_ERROR_STATUS;
                        }
                    } else {
                        LOG.error("report order with no order return, {}", order.getTid());
                        syncOrderStatus = DEFAULT_CLEARANCE_ERROR_STATUS;
                    }
                } else {
                    LOG.error("report order with no order response, {}", order.getTid());
                    syncOrderStatus = DEFAULT_CLEARANCE_ERROR_STATUS;
                }
                order.setSyncOrderStatus(syncOrderStatus);
                order.setAlreadySyncOrder(true);
                order.setUpdateTime(Calendar.getInstance().getTime());
                order.setUpdateBy(CREATE_BY_PROGRAM);
                if (!syncOrderStatus.isSuccess()) {
                    order.setStatus(STATUS_REJECTED.name());
                    order.setStatusMessage(STATUS_REJECTED.getValue() + "[" + syncOrderStatus.getMessage() + "]");
                }
                youzanOrderMapper.update(order);
            } catch (Exception ex) {
                ex.printStackTrace();
                LOG.error("failed to send order info to customs:{}", order.getTid());
                throw new BusinessException("failed to send order info to customs " + order.getTid());
            }
        } else {
            LOG.error("failed to send order info to customs:{} because of empty order", order);
            throw new BusinessException("failed to send order info to customs " + order);
        }
        return syncOrderStatus;
    }

    /**
     * STO物流单报关
     */
    private ClearanceStatus sendStoWlToCustoms(YouzanOrder order) throws BusinessException {
        ClearanceStatus syncWuliuStatus = null;
        if (nonNull(order)) {
            try {
                StoKjOrderService service = (StoKjOrderService) getBean("stoKjOrderService");
                if (nonNull(service)) {
                    StoKjOrderRequest stoReq = assembleStoWlReq(order, youzanKdt.getSender());
                    StoKjOrderResponse stoResp = service.sendRequest(stoReq);
                    if (nonNull(stoResp)) {
                        Boolean success = Boolean.parseBoolean(stoResp.getSuccess());
                        syncWuliuStatus = new ClearanceStatus(success ? HTTP_OK : RUOYI_INTERNAL_ERROR, success, stoResp.getErrorMsg());
                    } else {
                        LOG.error("failed to get sto response");
                        syncWuliuStatus = DEFAULT_CLEARANCE_ERROR_STATUS;
                    }
                } else {
                    LOG.error("repor order with no order response");
                    syncWuliuStatus = DEFAULT_CLEARANCE_ERROR_STATUS;
                }
                order.setSyncWuliuStatus(syncWuliuStatus);
                order.setAlreadySyncWuliu(true);
                order.setUpdateTime(Calendar.getInstance().getTime());
                order.setUpdateBy(CREATE_BY_PROGRAM);
                if (!syncWuliuStatus.isSuccess()) {
                    order.setStatus(STATUS_REJECTED.name());
                    order.setStatusMessage(STATUS_REJECTED.getValue() + "[" + syncWuliuStatus.getMessage() + "]");
                }
                youzanOrderMapper.update(order);
            } catch (BeansException ex) {
                LOG.error("failed to send sto wuliu info to customs:{}", order);
                throw new BusinessException("failed to send sto wuliu info to customs " + order.getTid());
            }
        } else {
            LOG.error("failed to send sto wuliu info to customs:{} because of empty order", order);
            throw new BusinessException("failed to send sto wuliu info to customs " + order.getTid());
        }
        return syncWuliuStatus;
    }

    /**
     * YUNDA物流单报关
     */
    private ClearanceStatus sendYundaWlToCustoms(YouzanOrder order) throws BusinessException {
        ClearanceStatus syncWuliuStatus = null;
        if (nonNull(order)) {
            LOG.info("begin to push wuliu form yunda to customs:{}", order.getWayBillNo());
            if (isNotBlank(order.getWayBillNo())) {
                try {
                    YundaKjOrderService service = (YundaKjOrderService) getBean("yundaKjOrderService");
                    if (nonNull(service)) {
                        YdApplyResponses ydResps = service.sendRequest(assembleYdApplyReq(order, youzanKdt.getSender(), customsWuliuProxyPlat.getCopCode()));
                        LOG.info("yunda response:{}", ydResps);
                        if (nonNull(ydResps)) {
                            List<YdApplyResponse> ydRespList = ydResps.getResponse();
                            if (isNotEmpty(ydRespList)) {
                                YdApplyResponse resp = (YdApplyResponse) CollectionUtils.get(ydRespList, 0);
                                if (nonNull(resp)) {
                                    try {
                                        String respCode = resp.getCode();
                                        boolean isSucc = equalsIgnoreCase(respCode, YUNDA_SUCC.getKey());
                                        syncWuliuStatus = new ClearanceStatus(isSucc ? HTTP_OK : RUOYI_INTERNAL_ERROR, isSucc, resp.getMsg());
                                    } catch (NumberFormatException ex) {
                                        LOG.error("failed to parse error code:{}, {}", ArrayUtil.toString(ex.getStackTrace()), order.getTid());
                                        syncWuliuStatus = DEFAULT_CLEARANCE_ERROR_STATUS;
                                    }
                                } else {
                                    LOG.error("failed to yunda response item, {}", order.getTid());
                                    syncWuliuStatus = DEFAULT_CLEARANCE_ERROR_STATUS;
                                }
                            } else {
                                LOG.error("failed to yunda response list, {}", order.getTid());
                                syncWuliuStatus = DEFAULT_CLEARANCE_ERROR_STATUS;
                            }
                        } else {
                            LOG.error("failed to get yunda response, {}", order.getTid());
                            syncWuliuStatus = DEFAULT_CLEARANCE_ERROR_STATUS;
                        }
                        order.setSyncWuliuStatus(syncWuliuStatus);
                        order.setAlreadySyncWuliu(true);
                        order.setUpdateTime(Calendar.getInstance().getTime());
                        order.setUpdateBy(CREATE_BY_PROGRAM);
                        if (!syncWuliuStatus.isSuccess()) {
                            order.setStatus(STATUS_REJECTED.name());
                            order.setStatusMessage(STATUS_REJECTED.getValue() + "[" + syncWuliuStatus.getMessage() + "]");
                        }
                        youzanOrderMapper.update(order);
                    } else {
                        LOG.error("can't find right YundaKjOrderService :{}", order.getTid());
                        throw new BusinessException("can't find right YundaKjOrderService  " + order.getTid());
                    }
                } catch (BeansException ex) {
                    ex.printStackTrace();
                    LOG.error("failed to send sto wuliu info to customs:{}, {}", ArrayUtils.toString(ex.getStackTrace()), order.getTid());
                    throw new BusinessException("failed to send sto wuliu info to customs " + order.getTid());
                }
            } else {
                LOG.error("way bill no of order is empty, {}", order.getTid());
                throw new BusinessException("way bill no of order is empty, {}" + order.getTid());
            }
        } else {
            LOG.error("failed to send yunda wuliu info to customs:{} because of empty order", order);
            throw new BusinessException("failed to send yunda wuliu info to customs " + order);
        }
        return syncWuliuStatus;
    }

    /**
     * 物流单报关
     */
    private ClearanceStatus sendWuliuToCustoms(YouzanOrder order) throws BusinessException {
        ClearanceStatus clearanceStatus = null;
        if (nonNull(order) && !order.isAlreadySyncWuliu()) {
            WuliuComp wlEnt = getEntByValue(order.getWayBillEnt());
            if (nonNull(wlEnt)) {
                switch (wlEnt) {
                    case STO:
                        LOG.info("send sto wuliu info to customs");
                        clearanceStatus = sendStoWlToCustoms(order);
                        break;
                    case YUNDA:
                        LOG.info("send yunda wuliu info to customs");
                        clearanceStatus = sendYundaWlToCustoms(order);
                        break;
                    default:
                        LOG.error("no need to send wuliu info to customs");
                        throw new BusinessException("no need to send wuliu info to customs");
                }
            } else {
                LOG.warn("can't find valid wuliu company!");
            }
        } else {
            LOG.warn("order is empty or is already send clearance to customs");
        }
        return clearanceStatus;
    }

    @Override
    public Integer call() throws Exception {
        Integer counter = 0;
        if (isNotEmpty(orders)) {
            for (YouzanOrder order : orders) {
                try {
                    if (nonNull(order)
                            && nonNull(youzanKdt)
                            && equalsIgnoreCase(order.getKdtId(), youzanKdt.getAuthorityId())
                            && nonNull(youzanOrderMapper)
                            && isReadyApply(order.getStatus())) {
                        //发送物流单信息到海关清关
                        ClearanceStatus wuliuClearanceStatus = order.isAlreadySyncWuliu() ? order.getSyncWuliuStatus() : sendWuliuToCustoms(order);
                        if (nonNull(wuliuClearanceStatus) && wuliuClearanceStatus.isSuccess()) {
                            //发送订单信息到海关清关
                            ClearanceStatus orderClearanceStatus = order.isAlreadySyncOrder() ? order.getSyncOrderStatus() : sendOrderToCustoms(order);
                            if (nonNull(orderClearanceStatus) && orderClearanceStatus.isSuccess()) {
                                //发送支付单信息到海关清关
                                ClearanceStatus payClearanceStatus = order.isAlreadySyncPay() ? order.getSyncPayStatus() : sendPayToCustoms(order);
                                if (nonNull(payClearanceStatus) && payClearanceStatus.isSuccess()) {
                                    //发送清关单信息到海关清关
                                    LOG.info("will send details clearance to customs till 10 minutes later");
                                } else {
                                    LOG.error("failed to send pay clearance to customs:{}", payClearanceStatus);
                                }
                            } else {
                                LOG.error("failed to send order clearance to customs:{}", orderClearanceStatus);
                            }
                        } else {
                            LOG.error("failed to send wuliu clearance to customs:{}", wuliuClearanceStatus);
                        }
                    } else {
                        LOG.warn("invalid order status, it's weired! order:{}, youzanKdt:{}", order, youzanKdt);
                    }
                } catch (BusinessException ex) {
                    LOG.error("failed to clear order:{}, ex:{}", order.getTid(), ex.getMessage());
                }
            }
            LOG.info("商户[{}]清关请求发送结束,共发送{}个请求，请等待结果", youzanKdt.getAuthorityName(), counter);
        } else {
            LOG.error("order list is empty");
        }
        return counter;
    }
}
