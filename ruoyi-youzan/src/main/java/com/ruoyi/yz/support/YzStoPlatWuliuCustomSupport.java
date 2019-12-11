/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.support;

import static com.ruoyi.common.utils.DateUtil.format;
import static com.ruoyi.common.utils.UuidUtil.gen36UUID;
import com.ruoyi.common.core.domain.BaseQingmaEntity;
import com.ruoyi.yz.customs.Message;
import com.ruoyi.yz.customs.wuliu.CEB511Message;
import com.ruoyi.yz.customs.wuliu.Logistics;
import com.ruoyi.yz.customs.wuliu.LogisticsHead;
import com.ruoyi.yz.domain.CustomsPlat;
import com.ruoyi.yz.domain.WuliuPlat;
import static com.ruoyi.yz.enums.AppStatus.COMMIT;
import static com.ruoyi.yz.enums.AppType.INSERT;
import static com.ruoyi.yz.enums.Currency.CNY;
import com.ruoyi.yz.wuliu.Receiver;
import com.ruoyi.yz.wuliu.sto.platorder.StoPlatOrderRequest;
import com.ruoyi.yz.wuliu.sto.platorder.StoPlatOrderResponse;
import com.ruoyi.yz.wuliu.sto.platorder.StoPlatOrderResponseData;
import static com.ruoyi.yz.support.YzOrderCustomSupport.ORDER_DATE_PATTERN;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import static java.util.Objects.nonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author wmao
 */
@Component
@Scope("prototype")
public class YzStoPlatWuliuCustomSupport extends CustomsSupport<StoPlatOrderResponse> {

    private static final Logger LOG = LoggerFactory.getLogger(YzStoPlatWuliuCustomSupport.class);

    private StoPlatOrderRequest req;

    public YzStoPlatWuliuCustomSupport() {
        super();
    }

    public YzStoPlatWuliuCustomSupport(CustomsPlat plat) {
        super(plat);
    }

    @Override
    protected List<Message> assemble(StoPlatOrderResponse info, BaseQingmaEntity bqe) {
        List<Message> messages = null;
        if (nonNull(info) && nonNull(bqe)) {
            messages = new ArrayList<>();
            WuliuPlat wlPlat = (WuliuPlat) bqe;
            CEB511Message message = new CEB511Message();
            Logistics logistics = new Logistics();
            LogisticsHead head = assembleHead(info, wlPlat);
            logistics.setLogisticsHead(head);
            message.setLogistics(logistics);
            messages.add(message);
        }
        return messages;
    }

    private LogisticsHead assembleHead(StoPlatOrderResponse info, WuliuPlat plat) {
        LogisticsHead head = null;
        if (nonNull(info) && nonNull(plat)) {
            StoPlatOrderResponseData data = info.getData();
            Date currentTime = Calendar.getInstance().getTime();
            head = new LogisticsHead();
            head.setGuid(gen36UUID());
            head.setAppType(INSERT.getKey());
            head.setAppStatus(COMMIT.getKey());
            head.setAppTime(format(currentTime, ORDER_DATE_PATTERN));
            head.setLogisticsCode(plat.getCustomsCode());
            head.setLogisticsName(plat.getCustomsName());
            head.setLogisticsNo(nonNull(data) ? data.getWaybillNo() : "");
            head.setOrderNo(nonNull(data) ? data.getOrderNo() : "");
            head.setFreight(BigDecimal.ZERO);
            head.setInsuredFee(BigDecimal.ZERO);
            head.setCurrency(CNY.getKey());
            head.setWeight(nonNull(req) ? req.getWeight() : BigDecimal.ZERO);
            head.setPackNo(BigInteger.ONE);
            head.setGoodsInfo(req.getGoodsName() + " " + (nonNull(req.getGoodsAmount()) ? req.getGoodsAmount().toString() : ""));
            Receiver receiver = nonNull(req) ? req.getReceiver() : null;
            if (nonNull(receiver)) {
                head.setConsignee(receiver.getName());
                head.setConsigneeTelephone(receiver.getTel() + " " + receiver.getMobile());
                head.setConsigneeAddress(receiver.getCountry() + receiver.getProvince() + receiver.getCity() + receiver.getArea() + receiver.getAddress());
            }
        }
        return head;
    }

    /**
     * @return the req
     */
    public StoPlatOrderRequest getReq() {
        return req;
    }

    /**
     * @param req the req to set
     */
    public void setReq(StoPlatOrderRequest req) {
        this.req = req;
    }

}
