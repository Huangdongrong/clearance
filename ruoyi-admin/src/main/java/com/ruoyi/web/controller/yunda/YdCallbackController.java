/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.web.controller.yunda;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.yz.service.YouzanOrderService;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static com.ruoyi.common.utils.RSAUtil.decryptByPublicKey;
import static com.ruoyi.common.utils.xml.XmlUtil.convertXmlStrToObject;
import static com.ruoyi.common.utils.security.Base64Util.safeUrlBase64BeforeDecode;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.yz.domain.CustomsPlat;
import static com.ruoyi.yz.enums.Port.YUNDA;
import com.ruoyi.yz.service.CustomsPlatService;
import com.ruoyi.yz.wuliu.ydkj.apply.YdApplyAsyncDeclareResult;
import com.ruoyi.yz.wuliu.ydkj.apply.YdApplyAsyncResponse;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import com.ruoyi.yz.domain.ClearanceStatus;
import com.ruoyi.yz.domain.YouzanOrder;
import java.util.ArrayList;
import static com.ruoyi.yz.cnst.Const.CREATE_BY_PROGRAM;
import static com.ruoyi.web.core.enums.YundaRespRet.FAILED;
import static com.ruoyi.web.core.enums.YundaRespRet.SUCCESS;
import com.ruoyi.web.core.enums.YundaRespFlag;
import static com.ruoyi.yz.enums.OrderStatus.STATUS_COMPLETED;
import static com.ruoyi.yz.enums.OrderStatus.STATUS_DISCARDED;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

/**
 *
 * @author wmao
 */
@Controller
@RequestMapping("/rest/yd")
public class YdCallbackController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(YdCallbackController.class);

    @Autowired
    private YouzanOrderService youzanOrderService;

    @Autowired
    private CustomsPlatService customsPlatService;

    @RequestMapping(value = "/callback", method = POST)
    @ResponseBody
    public String callback(String xmldata) {
        String ret = FAILED.getKey();
        if (isNotBlank(xmldata)) {
            CustomsPlat plat = customsPlatService.getOneByDistrict(YUNDA.name());
            if (nonNull(plat)) {
                LOG.info("xmldata:{}", xmldata);
                String decryData = decryptByPublicKey(safeUrlBase64BeforeDecode(xmldata), plat.getRemark());
                if (isNotBlank(decryData)) {
                    YdApplyAsyncResponse resp = convertXmlStrToObject(YdApplyAsyncResponse.class, decryData);
                    if (nonNull(resp)) {
                        LOG.info("resp:{}", resp);
                        List<YdApplyAsyncDeclareResult> results = resp.getDeclareResult();
                        if (isNotEmpty(results)) {
                            Date currentTime = Calendar.getInstance().getTime();
                            final List<YouzanOrder> needUpdated = new ArrayList<>();
                            results.forEach((result) -> {
                                if (nonNull(result)) {
                                    String orderNo = result.getOrderNo();
                                    if (isNotBlank(orderNo)) {
                                        YouzanOrder order = youzanOrderService.getOneByOrderNo(orderNo);
                                        if (nonNull(order)
                                            && !equalsIgnoreCase(order.getStatus(), STATUS_DISCARDED.name())
                                            && !equalsIgnoreCase(order.getStatus(), STATUS_COMPLETED.name())) {
                                            ClearanceStatus status = new ClearanceStatus(result.getSuccessFlag(), (YundaRespFlag.getByKey(result.getSuccessFlag()) == YundaRespFlag.SUCCESS), result.getRemark());
                                            order.setUpdateTime(currentTime);
                                            order.setUpdateBy(CREATE_BY_PROGRAM);
                                            order.setSyncWuliuStatus(status);
                                            needUpdated.add(order);
                                        }
                                    }
                                }
                            });
                            int updated = youzanOrderService.batchUpdate(needUpdated);
                            LOG.info("already update {} order wuliu status", updated);
                        }
                        ret = SUCCESS.getKey();
                    }
                }
            } else {
                LOG.error("failed to find yunda proxy for customs");
            }
        } else {
            LOG.error("failed to get data from yunda");
        }
        return ret;
    }
}
