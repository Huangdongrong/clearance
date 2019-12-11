/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service.thread;

import static com.ruoyi.yz.cnst.Const.CREATE_BY_PROGRAM;
import static com.ruoyi.yz.cnst.Const.PRE_PROCESS_SUCCEED;
import com.ruoyi.yz.domain.ClearanceStatus;
import com.ruoyi.yz.mapper.YouzanOrderMapper;
import java.util.concurrent.Callable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.yz.domain.YouzanOrder;
import com.ruoyi.yz.domain.YouzanKdt;
import com.youzan.cloud.open.sdk.gen.v1_0_1.model.YouzanPayCustomsDeclarationReportpaymentQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.youzan.cloud.open.sdk.gen.v1_0_1.model.YouzanPayCustomsDeclarationReportpaymentQueryResult.YouzanPayCustomsDeclarationReportpaymentQueryResultData;
import java.util.Calendar;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.Objects.nonNull;
import static com.ruoyi.yz.cnst.Const.RUOYI_INTERNAL_ERROR;
import static com.ruoyi.yz.enums.OrderStatus.isReadyApply;
import java.util.List;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static com.ruoyi.yz.utils.YouZanUtil.queryReportPayResult;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 *
 * @author wmao
 */
@Component("syncPayResultFromYouzanThread")
@Scope("prototype")
@Transactional
public class SyncPayResultFromYouzanThread implements Callable<Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(SyncPayResultFromYouzanThread.class);
    private static final ClearanceStatus DEFAULT_CLEARANCE_ERROR_STATUS = new ClearanceStatus(RUOYI_INTERNAL_ERROR, false, "default error");
    @Autowired
    protected YouzanOrderMapper youzanOrderMapper;

    private List<YouzanOrder> orders;

    private YouzanKdt youzanKdt;

    public void init(YouzanKdt youzanKdt, List<YouzanOrder> orders) {
        this.orders = orders;
        this.youzanKdt = youzanKdt;
    }

    @Override
    public Integer call() throws Exception {
        int retLen = 0;
        if (isNotEmpty(orders)) {
            for (YouzanOrder order : orders) {
                try {
                    if (nonNull(order)
                            && nonNull(youzanKdt)
                            && isReadyApply(order.getStatus())
                            && order.isAlreadySyncPay()) {
                        YouzanPayCustomsDeclarationReportpaymentQueryResult result = queryReportPayResult(youzanKdt, order);
                        if (nonNull(result)) {
                            ClearanceStatus syncPayStatus = null;
                            if (nonNull(result)) {
                                int retCode = result.getCode();
                                YouzanPayCustomsDeclarationReportpaymentQueryResultData data = result.getData();
                                
                                String message = nonNull(data) ? (result.getMessage() + data.getCustomsInfo()) : result.getMessage();
                                if(isBlank(message) && retCode == HTTP_OK){
                                    message = PRE_PROCESS_SUCCEED;
                                }
                                
                                syncPayStatus = new ClearanceStatus(retCode == HTTP_OK && nonNull(data) ? data.getCustomsStatus() : retCode, result.getSuccess(), message);
                            } else {
                                LOG.error("repor pay with no response");
                                syncPayStatus = DEFAULT_CLEARANCE_ERROR_STATUS;
                            }
                            if (nonNull(syncPayStatus)) {
                                order.setSyncPayStatus(syncPayStatus);
                                order.setUpdateBy(CREATE_BY_PROGRAM);
                                order.setUpdateTime(Calendar.getInstance().getTime());
                                retLen += youzanOrderMapper.update(order);
                            } else {
                                LOG.error("sync pay status is null, pls check it");
                            }
                        } else {
                            LOG.warn("result is empty or null:{}", result);
                        }
                    } else {
                        LOG.warn("empty order or kdt, pls check it");
                    }
                } catch (Exception ex) {
                    LOG.error("failded to get order:{} result, ex:{}", order, ex.getMessage());
                }
            }
            LOG.info("对商户{}同步支付清关结果请求发送结束,共发送{}个请求，请等待结果", youzanKdt.getAuthorityName(), retLen);
        } else {
            LOG.error("order list is empty");
        }
        return retLen;
    }
}
