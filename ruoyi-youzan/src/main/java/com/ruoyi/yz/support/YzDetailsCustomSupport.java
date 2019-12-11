/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.support;

import static com.ruoyi.common.utils.DateUtil.format;
import static com.ruoyi.common.utils.UuidUtil.gen36UUID;
import com.ruoyi.common.core.domain.BaseQingmaEntity;
import static com.ruoyi.yz.cnst.Const.COOKIE_KEY_TIME_PATTERN;
import static com.ruoyi.yz.cnst.Const.PACKAGE_WEIGHT;
import com.ruoyi.yz.customs.BaseTransfer;
import com.ruoyi.yz.customs.Message;
import com.ruoyi.yz.customs.details.CEB621Message;
import com.ruoyi.yz.customs.details.Inventory;
import com.ruoyi.yz.customs.details.InventoryHead;
import com.ruoyi.yz.customs.details.InventoryList;
import com.ruoyi.yz.customs.order.CEB311Message;
import com.ruoyi.yz.customs.order.Order;
import com.ruoyi.yz.customs.order.OrderHead;
import com.ruoyi.yz.customs.order.OrderList;
import com.ruoyi.yz.domain.WuliuItem;
import static com.ruoyi.yz.enums.AppStatus.COMMIT;
import static com.ruoyi.yz.enums.AppType.INSERT;
import static com.ruoyi.yz.enums.Currency.getKeyByCnName;
import static com.ruoyi.yz.enums.OrderType.IMPORT;
import static com.ruoyi.yz.enums.Port.CD;
import static com.ruoyi.yz.enums.TradeMode.TAXFREE;
import static com.ruoyi.yz.enums.TrafMode.BONDED;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import static com.ruoyi.yz.enums.Unit.getKeyByValue;
import static java.math.BigDecimal.ROUND_HALF_DOWN;

/**
 *
 * @author wmao
 */
@Component
@Scope("prototype")
public class YzDetailsCustomSupport extends CustomsSupport<Message> {

    private Date currentTime;

    public YzDetailsCustomSupport() {
    }

    @Override
    protected List<Message> assemble(Message info, BaseQingmaEntity bqe) {
        List<Message> messages = null;
        if (nonNull(info) && nonNull(bqe)) {
            messages = new ArrayList<>();
            CEB311Message orderReq = (CEB311Message) info;
            WuliuItem wuliuPlat = (WuliuItem) bqe;
            CEB621Message detailsReq = new CEB621Message();
            Inventory inventory = new Inventory();
            inventory.setInventoryHead(assembleHead(orderReq, wuliuPlat));
            inventory.setInventoryList(assembleBodyList(orderReq));
            detailsReq.setInventory(inventory);
            messages.add(detailsReq);
        }
        return messages;
    }

    private InventoryHead assembleHead(CEB311Message message, WuliuItem wuliuPlat) {
        InventoryHead head = new InventoryHead();
        BaseTransfer auth = message.getBaseTransfer();
        head.setAgentCode(auth.getCopCode());
        head.setAgentName(auth.getCopName());
        head.setAppStatus(COMMIT.getKey());
        head.setAppTime(format(currentTime, COOKIE_KEY_TIME_PATTERN));
        head.setAppType(INSERT.getKey());
        head.setAreaCode(auth.getCopCode());
        head.setAreaName(auth.getCopName());
        Order order = message.getOrder();
        if (nonNull(order)) {
            OrderHead oh = order.getOrderHead();
            if (nonNull(oh)) {
                head.setBuyerIdNumber(oh.getBuyerIdNumber());
                head.setBuyerIdType(oh.getBuyerIdType());
                head.setBuyerName(oh.getBuyerName());
                head.setBuyerTelephone(oh.getBuyerTelephone());
                head.setConsigneeAddress(oh.getConsigneeAddress());
                head.setCopNo(oh.getCopNo());
                String country = getKeyByCnName(oh.getCurrency());
                head.setCountry(isBlank(country) ? oh.getCurrency(): country);
                head.setCurrency(oh.getCurrency());
                head.setCustomsCode(CD.getcCode());
                head.setDeclTime(format(currentTime, "yyyyMMdd"));
                head.setEbcCode(oh.getEbcCode());
                head.setEbcName(oh.getEbcName());
                head.setEbpCode(oh.getEbpCode());
                head.setEbpName(oh.getEbpName());
                head.setAssureCode(oh.getEbcCode());
                head.setFreight(oh.getFreight());
                head.setIeDate(format(oh.getOrderTime(), "yyyyMMdd"));
                head.setIeFlag(IMPORT.getKey());
                head.setInsuredFee(BigDecimal.ZERO);
                head.setOrderNo(oh.getOrderNo());
                head.setPackNo(BigInteger.ONE);
                head.setPortCode(CD.getpCode());
                head.setTradeMode(TAXFREE.getKey());
                head.setEmsNo(oh.getEmsNo());
                head.setWrapType("0");
                head.setNetWeight(oh.getWeight().setScale(4, ROUND_HALF_DOWN));
                head.setGrossWeight(PACKAGE_WEIGHT.add(oh.getWeight()).setScale(4, ROUND_HALF_DOWN));
            }
        }
        head.setLogisticsCode(wuliuPlat.getCustomsCode());
        head.setLogisticsName(wuliuPlat.getCustomsName());
        head.setLogisticsNo(wuliuPlat.getLogisticsNo());
        head.setTrafMode("" + BONDED.getKey());
        head.setGuid(gen36UUID());
        return head;
    }

    private List<InventoryList> assembleBodyList(CEB311Message message) {
        final List<InventoryList> inventoryList = new ArrayList<>();
        if (nonNull(message)) {
            Order order = message.getOrder();
            if (nonNull(order)) {
                List<OrderList> ols = order.getOrderList();
                if (isNotEmpty(ols)) {
                    ols.forEach((ol) -> {
                        InventoryList il = new InventoryList();
                        il.setBarCode(ol.getBarCode());
                        String country = getKeyByCnName(ol.getCountry());
                        il.setCountry(isBlank(country) ? ol.getCountry() : country);
                        il.setCurrency(ol.getCurrency());
                        il.setGcode(ol.getCustomsBianMa());
                        il.setGmodel(ol.getGmodel());
                        il.setGname(ol.getItemName());
                        il.setGnum(ol.getGnum());
                        il.setItemRecordNo(ol.getItemRecordNo());
                        il.setItemName(ol.getItemName());
                        il.setItemNo(ol.getItemNo());
                        il.setNote(ol.getNote());
                        il.setPrice(ol.getPrice());
                        il.setQty(ol.getQty());
                        il.setQty1(ol.getQty1());
                        il.setQty2(ol.getQty2());
                        il.setTotalPrice(ol.getTotalPrice());
                        il.setUnit(getKeyByValue(ol.getUnit()));
                        il.setUnit1(getKeyByValue(ol.getUnit1()));
                        il.setUnit2(getKeyByValue(ol.getUnit2()));
                        inventoryList.add(il);
                    });
                }
            }
        }
        return inventoryList;
    }

    /**
     * @param currentTime the currentTime to set
     */
    public void setCurrentTime(Date currentTime) {
        this.currentTime = currentTime;
    }

}
