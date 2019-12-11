/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.web.controller.customs;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import static com.ruoyi.common.utils.StringUtils.isNumeric;
import static com.ruoyi.common.utils.xml.XmlUtil.convertXmlStrToObject;
import static com.ruoyi.yz.cnst.Const.CREATE_BY_PROGRAM;
import com.ruoyi.yz.customs.details.CEB622Message;
import com.ruoyi.yz.customs.details.InventoryReturn;
import com.ruoyi.yz.customs.order.CEB312Message;
import com.ruoyi.yz.customs.order.OrderReturn;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trim;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.yz.domain.ClearanceStatus;
import com.ruoyi.yz.domain.YouzanOrder;
import static com.ruoyi.yz.enums.YzCustomsErrorCode.isNotError;
import com.ruoyi.yz.service.YouzanOrderService;
import static java.lang.Integer.parseInt;
import java.util.Calendar;
import static java.util.Objects.nonNull;
import org.springframework.beans.factory.annotation.Autowired;
import static com.ruoyi.yz.cnst.Const.RUOYI_INTERNAL_ERROR;
import static com.ruoyi.yz.enums.CustomsDetailsError.isFailed;
import static com.ruoyi.yz.enums.CustomsDetailsError.isSuccess;
import static com.ruoyi.yz.enums.OrderStatus.STATUS_APPLYING;
import static com.ruoyi.yz.enums.OrderStatus.STATUS_COMPLETED;
import static com.ruoyi.yz.enums.OrderStatus.STATUS_DISCARDED;
import static com.ruoyi.yz.enums.OrderStatus.STATUS_REJECTED;
import static com.ruoyi.yz.enums.OrderStatus.STATUS_SUCCESS;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

/**
 *
 * @author wmao
 */
@Controller
@RequestMapping("/rest/customs")
public class CustomsCallbackController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(CustomsCallbackController.class);
    private static final ClearanceStatus DEFAULT_CLEARANCE_ERROR_STATUS = new ClearanceStatus(RUOYI_INTERNAL_ERROR, false, "default error");

    @Autowired
    private YouzanOrderService youzanOrderService;

    @RequestMapping(value = "/callback", method = POST)
    @ResponseBody
    public AjaxResult callback(String message) {
        LOG.info("message:{}", message);
        AjaxResult ret = new AjaxResult();
        if (isNotBlank(message)) {
            if (containsIgnoreCase(message, "CEB622Message")) {
                ret = updateDetailsResp(message);
            } else if (containsIgnoreCase(message, "CEB312Message")) {
                ret = updateOrderResp(message);
            } else {
                LOG.error("not valid response from customs");
            }
        }
        return ret;
    }

    private AjaxResult updateDetailsResp(String message) {
        AjaxResult ret = new AjaxResult();
        if (isNotBlank(message)) {
            CEB622Message resp = convertXmlStrToObject(CEB622Message.class, message);
            if (nonNull(resp) && nonNull(resp.getInventoryReturn())) {
                InventoryReturn ir = resp.getInventoryReturn();
                String copNo = ir.getCopNo();
                YouzanOrder order = youzanOrderService.getOneByCopNo(copNo);
                if (nonNull(order)
                     && !equalsIgnoreCase(order.getStatus(), STATUS_DISCARDED.name())
                     && !equalsIgnoreCase(order.getStatus(), STATUS_COMPLETED.name())) {
                    ClearanceStatus retStatus = null;
                    String sta = ir.getReturnStatus();
                    if (isNumeric(sta)) {
                        int staValue = parseInt(sta);
                        retStatus = new ClearanceStatus(staValue, isSuccess(staValue), ir.getReturnInfo());
                    } else {
                        LOG.error("failed to get customer detail response because of invalid status");
                        retStatus = DEFAULT_CLEARANCE_ERROR_STATUS;
                    }
                    int detailRetStatus = retStatus.getStatus();
                    LOG.info("detail clearance return status:{}", retStatus);
                    if (isSuccess(detailRetStatus)) {
                        order.setStatus(STATUS_SUCCESS.name());
                        order.setStatusMessage(STATUS_SUCCESS.getValue() + "[" + ir.getReturnInfo() + "]");
                    } else if (isFailed(retStatus.getStatus())) {
                        order.setStatus(STATUS_REJECTED.name());
                        order.setStatusMessage(STATUS_REJECTED.getValue() + "[" + ir.getReturnInfo() + "]");
                    } else {
                        order.setStatus(STATUS_APPLYING.name());
                        order.setStatusMessage(STATUS_APPLYING.getValue() + "[" + ir.getReturnInfo() + "]");
                    }
                    order.setSyncDetailsStatus(retStatus);
                    order.setUpdateTime(Calendar.getInstance().getTime());
                    order.setUpdateBy(CREATE_BY_PROGRAM);
                    youzanOrderService.update(order);
                    ret = success("update order status successfully");
                } else {
                    LOG.error("couldn't find right order by orderNo:{}", copNo);
                }
            } else {
                LOG.error("failed to get customer detail response");
            }
        }
        return ret;
    }

    private AjaxResult updateOrderResp(String message) {
        AjaxResult ret = new AjaxResult();
        if (isNotBlank(message)) {
            CEB312Message resp = convertXmlStrToObject(CEB312Message.class, message);
            if (nonNull(resp)) {
                OrderReturn or = resp.getOrderReturn();
                if (nonNull(or)) {
                    String orderNo = or.getOrderNo();
                    YouzanOrder order = youzanOrderService.getOneByOrderNo(orderNo);
                    if (nonNull(order) 
                     && !equalsIgnoreCase(order.getStatus(), STATUS_DISCARDED.name())
                     && !equalsIgnoreCase(order.getStatus(), STATUS_COMPLETED.name())) {
                        ClearanceStatus retStatus = null;
                        String status = trim(or.getReturnStatus());
                        try {
                            int statusCode = isNotBlank(status) ? parseInt(status) : -1;
                            retStatus = new ClearanceStatus(statusCode, isNotError(statusCode), or.getReturnInfo());
                        } catch (NumberFormatException ex) {
                            LOG.error("failed to parse error code:{}", ex.getMessage());
                            retStatus = DEFAULT_CLEARANCE_ERROR_STATUS;
                        }
                        order.setSyncOrderStatus(retStatus);
                        order.setUpdateTime(Calendar.getInstance().getTime());
                        order.setUpdateBy(CREATE_BY_PROGRAM);
                        youzanOrderService.update(order);
                        ret = success("update order status successfully");
                    } else {
                        LOG.error("couldn't find right order by orderNo:{}", orderNo);
                    }
                } else {
                    LOG.error("repor order with no order return");
                }
            } else {
                LOG.error("repor order with no order response");
            }
        }
        return ret;
    }
}
