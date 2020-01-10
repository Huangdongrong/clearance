/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.support;

import com.ruoyi.yz.customs.Message;
import com.ruoyi.yz.customs.order.CEB311Message;
import com.ruoyi.yz.customs.order.Order;
import com.ruoyi.yz.customs.order.OrderHead;
import com.ruoyi.yz.customs.order.OrderList;
import com.ruoyi.yz.domain.CustomsPlat;
import com.ruoyi.yz.domain.MvGoodsEntity;
import com.ruoyi.yz.domain.YouzanKdt;
import static com.ruoyi.yz.enums.AppStatus.COMMIT;
import static com.ruoyi.yz.enums.CrossBorder.isCrossBorder;
import static com.ruoyi.yz.enums.Currency.CNY;
import static com.ruoyi.yz.enums.IdentityType.ID;
import static com.ruoyi.yz.enums.OrderType.IMPORT;
import static com.ruoyi.common.utils.DateUtil.format;
import static com.ruoyi.common.utils.DateUtil.parse;
import static com.ruoyi.common.utils.JsonUtil.getValueByKey;
import static com.ruoyi.common.utils.UuidUtil.gen36UUID;
import com.ruoyi.common.core.domain.BaseQingmaEntity;
import static com.ruoyi.common.utils.StringUtils.nullToEmpty;
import com.ruoyi.yz.domain.YouzanConfig;
import static com.ruoyi.yz.enums.AppType.INSERT;
import static com.ruoyi.yz.enums.Currency.getKeyByCnName;
import com.ruoyi.yz.enums.Unit;
import static com.ruoyi.yz.enums.Unit.Thfive;
import static com.ruoyi.yz.enums.Unit.Thsix;
import com.ruoyi.yz.wuliu.Receiver;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradesSoldGetResult.YouzanTradesSoldGetResultAddressinfo;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradesSoldGetResult.YouzanTradesSoldGetResultBuyerinfo;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradesSoldGetResult.YouzanTradesSoldGetResultFullorderinfo;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradesSoldGetResult.YouzanTradesSoldGetResultOrderextra;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradesSoldGetResult.YouzanTradesSoldGetResultOrderinfo;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradesSoldGetResult.YouzanTradesSoldGetResultOrders;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradesSoldGetResult.YouzanTradesSoldGetResultPayinfo;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections.CollectionUtils.get;
import static org.apache.commons.collections.CollectionUtils.select;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.size;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trim;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.math.BigDecimal.ROUND_HALF_DOWN;
import com.ruoyi.yz.service.MvGoodsService;
import com.ruoyi.yz.service.YouzanConfigService;
import static com.ruoyi.yz.utils.CustomsUtil.getCopNoByTid;
import static com.ruoyi.yz.utils.YouZanUtil.priceCaculateFst;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static com.ruoyi.yz.enums.Unit.getKeyByValue;
import static com.ruoyi.yz.enums.Unit.getUnitByValue;
import java.math.RoundingMode;
import java.util.Map.Entry;

/**
 *
 * @author wmao
 */
@Component
@Scope("prototype")
public class YzOrderCustomSupport extends CustomsSupport<YouzanTradesSoldGetResultFullorderinfo> {

    private static final Logger LOG = LoggerFactory.getLogger(YzOrderCustomSupport.class);

    public static final String ORDER_DATE_PATTERN = "yyyyMMddHHmmss";

    @Autowired
    private MvGoodsService mvGoodsService;

    @Autowired
    private YouzanConfigService youzanConfigService;

    public YzOrderCustomSupport() {
        super();
    }

    public YzOrderCustomSupport(CustomsPlat plat) {
        super(plat);
    }

    @Override
    protected List<Message> assemble(YouzanTradesSoldGetResultFullorderinfo info, BaseQingmaEntity bqe) {
        List<Message> messages = null;
        if (nonNull(info)) {
            YouzanKdt kdt = (YouzanKdt) bqe;
            List<YouzanTradesSoldGetResultOrders> orders = info.getOrders();
            if (isNotEmpty(orders)) {
                messages = new ArrayList<>();
                final Set<String> subOrderNos = new HashSet<>();
                orders.forEach((order) -> {
                    String subOrderNo = trimToEmpty(order.getSubOrderNo());
                    if (isNotBlank(subOrderNo) && !equalsIgnoreCase(subOrderNo, "NULL")) {
                        subOrderNos.add(subOrderNo);
                    }
                });
                if (isNotEmpty(subOrderNos)) {
                    for (String subOrderNo : subOrderNos) {
                        messages = append(messages, assemble(info, kdt, subOrderNo));
                    }
                    int sonLen = size(subOrderNos);
                    if (sonLen > 1) {
                        LOG.warn("this is fenchai orders, pls pay attentioin to:{}", sonLen);
                    }
                } else {
                    LOG.error("order is not cross border order:{}", subOrderNos);
                }
            } else {
                LOG.error("order infos is empty, it's weired!");
            }
        } else {
            LOG.error("order info is empty, it's weired!");
        }
        return messages;
    }

    private List<Message> append(final List<Message> origMessages, Message appendMessage) {
        if (nonNull(origMessages) && nonNull(appendMessage)) {
            origMessages.add(appendMessage);
        }
        return origMessages;
    }

    private Message assemble(YouzanTradesSoldGetResultFullorderinfo info, YouzanKdt kdt, String subOrderNo) {
        Message message = null;
        if (nonNull(info) && nonNull(kdt)) {
            List<YouzanTradesSoldGetResultOrders> orders = info.getOrders();
            if (isNotEmpty(orders)) {
                Collection<YouzanTradesSoldGetResultOrders> subOrders = select(orders, (Object obj) -> {
                    YouzanTradesSoldGetResultOrders order = (YouzanTradesSoldGetResultOrders) obj;
                    String tmpSubOrderNo = nullToEmpty(nonNull(order) ? trim(order.getSubOrderNo()) : "");
                    if (isCrossBorder(trimToEmpty(order.getIsCrossBorder()))) {
                        return isNotBlank(subOrderNo) ? equalsIgnoreCase(subOrderNo, tmpSubOrderNo) : isBlank(tmpSubOrderNo);
                    } else {
                        return false;
                    }
                });
                message = assemble(info, subOrders, kdt);
            } else {
                LOG.error("orders list is empty, it's weired!");
            }
        } else {
            LOG.error("order info is empty or kdt is empty, it's weired!");
        }
        return message;
    }

    private Message assemble(YouzanTradesSoldGetResultFullorderinfo info, Collection<YouzanTradesSoldGetResultOrders> orders, YouzanKdt kdt) {
        CEB311Message message = null;
        if (nonNull(info) && isNotEmpty(orders) && nonNull(kdt)) {
            OrderHead head = assembleHead(info, orders, kdt);
            String transaction = head.getPayTransactionId();
            if (isNotBlank(transaction)) {
                List<OrderList> ols = assembleOrderList(orders, head);
                Order order = new Order();
                order.setOrderHead(head);
                order.setOrderList(ols);
                message = new CEB311Message();
                message.setOrder(order);
            } else {
                LOG.warn("{} desn't payed by customer, will igore it", head);
            }
        } else {
            LOG.error("order info is empty or kdt is empty or orders is empty, it's weired!info:{}, orders:{}, kdt:{}", isNull(info), isEmpty(orders), isNull(kdt));
        }
        return message;
    }

    private List<OrderList> assembleOrderList(Collection<YouzanTradesSoldGetResultOrders> orders, OrderHead orderHead) {
        List<OrderList> orderLists = null;
        if (isNotEmpty(orders)) {
            orderLists = new ArrayList<>();
            long idx = 1;
            BigDecimal goodsValues = BigDecimal.ZERO;
            BigDecimal taxTotals = BigDecimal.ZERO;
            BigDecimal disCounts = BigDecimal.ZERO;
            BigDecimal actualPaids = BigDecimal.ZERO;
            BigDecimal freights = BigDecimal.ZERO;
            StringBuilder goodNames = new StringBuilder();
            BigDecimal hqty = BigDecimal.ZERO;
            List<Map<Unit, BigDecimal>> weights = new ArrayList<>();
            for (YouzanTradesSoldGetResultOrders od : orders) {
                if (nonNull(od)) {
                    Map<Unit, BigDecimal> qtyUnits = new HashMap<>();
                    OrderList item = new OrderList();
                    item.setGnum(BigInteger.valueOf(idx));
                    item.setItemNo(od.getOuterItemId());
                    item.setQty(BigDecimal.valueOf(od.getNum()));
                    hqty = hqty.add(item.getQty());

                    item.setCurrency(CNY.getKey());
                    String outerSkuId = od.getOuterItemId();
                    if (isNotBlank(outerSkuId)) {
                        MvGoodsEntity entity = mvGoodsService.getOne(outerSkuId);
                        if (nonNull(entity)) {
                            item.setItemName(trim(entity.getShpMingCheng()));
                            item.setGmodel(trim(entity.getShpMingCheng()));
                            item.setItemRecordNo(entity.getShpCustomsOrderNo());
                            item.setCustomsBianMa(entity.getShpCustomsBianMa());
                            item.setBarCode(entity.getShpTiaoMa());
                            item.setUnit(getKeyByValue(entity.getShpCustomsUnit()));
                            qtyUnits.put(getUnitByValue(entity.getShpCustomsUnit()), item.getQty());
                            BigDecimal officialQty = nonNull(entity.getShpCustomsOfficialQty()) ? entity.getShpCustomsOfficialQty() : null;
                            if(nonNull(officialQty)){
                                item.setQty1(nonNull(officialQty) ? officialQty.multiply(item.getQty()).setScale(4, ROUND_HALF_DOWN) : null);
                                LOG.info("officialQty:{}, qty1:{}", officialQty.toString(), item.getQty1().toString());
                            }
                            
                            item.setUnit1(getKeyByValue(entity.getShpCustomsOfficialDanWei()));
                            Unit officialKey = getUnitByValue(entity.getShpCustomsOfficialDanWei());
                            if (nonNull(officialKey)) {
                                qtyUnits.put(officialKey, item.getQty1());
                            }
                            BigDecimal secondQty = nonNull(entity.getShpCustomsSecondQty()) ? entity.getShpCustomsSecondQty() : null;
                            item.setQty2(nonNull(secondQty) ? secondQty.multiply(item.getQty()).setScale(4, ROUND_HALF_DOWN) : null);
                            item.setUnit2(getKeyByValue(entity.getShpCustomsSecondDanWei()));
                            Unit secondKey = getUnitByValue(entity.getShpCustomsSecondDanWei());
                            if (nonNull(secondKey)) {
                                qtyUnits.put(secondKey, item.getQty2());
                            }
                            String country = getKeyByCnName(entity.getShpCustomsOriginCountry());
                            item.setCountry(isBlank(country) ? entity.getShpCustomsOriginCountry() : country);
                            BigDecimal taxRate = entity.getShpCustomsTaxRate();
                            BigDecimal realPayment = new BigDecimal(od.getPayment()).setScale(5, BigDecimal.ROUND_HALF_DOWN);
                            BigDecimal totalFeeAfterDiscount = new BigDecimal(od.getTotalFee()).setScale(5, BigDecimal.ROUND_HALF_DOWN);
                            String taxTotal = trimToEmpty(od.getTaxTotal());
                            LOG.info("taxRate:{}, odprice:{}, realPayment:{}, totalFeeBeforeDiscount:{}, taxTotal:{}, qty:{}", taxRate, od.getPrice(), realPayment, totalFeeAfterDiscount, taxTotal, item.getQty());

                            if (isNotBlank(taxTotal) && BigDecimal.ZERO.compareTo(new BigDecimal(taxTotal)) < 0) {
                                BigDecimal originPrice = new BigDecimal(od.getPrice());
                                BigDecimal totalPrice = originPrice.multiply(item.getQty());
                                BigDecimal actualPaid = realPayment;
                                BigDecimal tax = new BigDecimal(taxTotal);
                                BigDecimal discount = totalPrice.subtract(actualPaid);
                                LOG.info("originPrice:{}, totalPrice:{}, tax:{}, discount:{}, actualPaid:{}", originPrice, totalPrice, tax, discount, actualPaid);
                                item.setTotalPrice(nonNull(totalPrice) ? totalPrice.subtract(tax).setScale(4, ROUND_HALF_DOWN) : BigDecimal.ZERO);
                                item.setPrice(nonNull(item.getTotalPrice()) ? item.getTotalPrice().divide(item.getQty()).setScale(4, ROUND_HALF_DOWN) : BigDecimal.ZERO);
                                BigDecimal goodValue = nonNull(item.getTotalPrice()) ? item.getTotalPrice() : BigDecimal.ZERO;
                                goodsValues = goodsValues.add(nonNull(goodValue) ? goodValue.setScale(4, ROUND_HALF_DOWN) : BigDecimal.ZERO);
                                taxTotals = taxTotals.add(nonNull(tax) ? tax.setScale(4, ROUND_HALF_DOWN) : BigDecimal.ZERO);
                                disCounts = disCounts.add(nonNull(discount) ? discount.setScale(4, ROUND_HALF_DOWN) : BigDecimal.ZERO);
                                actualPaids = actualPaids.add(nonNull(actualPaid) ? actualPaid.setScale(4, ROUND_HALF_DOWN) : BigDecimal.ZERO);
                                LOG.info("splitted tax and price, goodsValues:{}, taxTotals:{}, disCounts:{}, actualPaids:{}", goodsValues, taxTotals, disCounts, actualPaids);
                            } else if (nonNull(taxRate) && nonNull(realPayment) && nonNull(totalFeeAfterDiscount)) {
                                Map<String, BigDecimal> priceMap = priceCaculateFst(totalFeeAfterDiscount, realPayment, item.getQty(), taxRate);
                                BigDecimal totalPrice = priceMap.get("totalPrice");
                                BigDecimal pricePerOne = priceMap.get("perOnePrice");
                                BigDecimal tax = priceMap.get("tax");
                                BigDecimal discount = priceMap.get("discount");
                                BigDecimal actualPaid = priceMap.get("actualPaid");
                                item.setTotalPrice(nonNull(totalPrice) ? totalPrice.setScale(4, ROUND_HALF_DOWN) : BigDecimal.ZERO);
                                item.setPrice(nonNull(pricePerOne) ? pricePerOne.setScale(4, ROUND_HALF_DOWN) : BigDecimal.ZERO);
                                goodsValues = goodsValues.add(nonNull(totalPrice) ? totalPrice.setScale(4, ROUND_HALF_DOWN) : BigDecimal.ZERO);
                                taxTotals = taxTotals.add(nonNull(tax) ? tax.setScale(4, ROUND_HALF_DOWN) : BigDecimal.ZERO);
                                disCounts = disCounts.add(nonNull(discount) ? discount.setScale(4, ROUND_HALF_DOWN) : BigDecimal.ZERO);
                                actualPaids = actualPaids.add(nonNull(actualPaid) ? actualPaid.setScale(4, ROUND_HALF_DOWN) : BigDecimal.ZERO);
                                LOG.info("calculate tax and price:{}, taxTotals:{}, disCounts:{}, actualPaids:{}", goodsValues, taxTotals, disCounts, actualPaids);
                            } else {
                                LOG.error("taxRate:{} is null", taxRate);
                            }
                            if(isNotBlank(od.getFreight())){
                                BigDecimal freight = new BigDecimal(od.getFreight());
                                freights = freights.add(nonNull(freight) ? freight.setScale(4, ROUND_HALF_DOWN) : BigDecimal.ZERO);
                            }
                        }
                    }
                    weights.add(qtyUnits);
                    goodNames.append(item.getItemName()).append("|");
                    idx++;
                    orderLists.add(item);
                }
            }
            orderHead.setQty(hqty);
            orderHead.setGoodsValue(goodsValues);
            orderHead.setFreight(freights);
            orderHead.setDiscount(disCounts);
            orderHead.setTaxTotal(taxTotals);
            orderHead.setActuralPaid(actualPaids.add(freights).setScale(4, ROUND_HALF_DOWN));
            orderHead.setNote(trim(goodNames.toString()));
            orderHead.setWeight(calcWeight(weights).setScale(4, ROUND_HALF_DOWN));
        }
        return orderLists;
    }

    private BigDecimal calcWeight(List<Map<Unit, BigDecimal>> qtyUnitsList) {
        BigDecimal weight = BigDecimal.ZERO;
        if(isNotEmpty(qtyUnitsList)){
            for(Map<Unit, BigDecimal> qtyUnits : qtyUnitsList){
                if (MapUtils.isNotEmpty(qtyUnits)) {
                    for (Entry<Unit, BigDecimal> entry : qtyUnits.entrySet()) {
                        Unit key = entry.getKey();
                        BigDecimal value = entry.getValue();
                        if (nonNull(value)) {
                            if (key == Thfive) {
                                weight = weight.add(value);
                                break;
                            } else if (key == Thsix) {
                                value = value.divide(new BigDecimal(1000), 4, RoundingMode.DOWN);
                                weight = weight.add(value);
                                break;
                            }
                        }else{
                            LOG.error("it's weired, key:{}, value:{}", key, value);
                        }
                    }
                }
            }
        }
        return weight;
    }

    private OrderHead assembleHead(YouzanTradesSoldGetResultFullorderinfo info, Collection<YouzanTradesSoldGetResultOrders> orders, YouzanKdt kdt) {
        OrderHead orderHead = null;
        if (nonNull(info) && isNotEmpty(orders) && nonNull(kdt)) {
            YouzanConfig config = youzanConfigService.getOne();
            YouzanTradesSoldGetResultPayinfo payInfo = info.getPayInfo();
            YouzanTradesSoldGetResultOrderinfo orderInfo = info.getOrderInfo();
            YouzanTradesSoldGetResultBuyerinfo buyerInfo = info.getBuyerInfo();
            YouzanTradesSoldGetResultAddressinfo addrInfo = info.getAddressInfo();
            Date currentTime = Calendar.getInstance().getTime();
            orderHead = new OrderHead();
            orderHead.setGuid(gen36UUID());
            orderHead.setAppType(INSERT.getKey());
            orderHead.setAppTime(format(currentTime, ORDER_DATE_PATTERN));
            orderHead.setAppStatus(COMMIT.getKey());
            String orderNo = nullToEmpty(((YouzanTradesSoldGetResultOrders) get(orders, 0)).getSubOrderNo());
            orderHead.setOrderNo(orderNo);
            orderHead.setBatchNumbers(orderNo);
            orderHead.setOrderType(IMPORT.getKey());
            orderHead.setEbpCode(nonNull(config) ? trimToEmpty(config.getCode()) : "");
            orderHead.setEbpName(nonNull(config) ? trimToEmpty(config.getName()) : "");
            orderHead.setEbcCode(plat.getCopCode());
            orderHead.setEbcName(plat.getCopName());
            orderHead.setFreight(BigDecimal.ZERO);
            orderHead.setCurrency(CNY.getKey());
            orderHead.setBuyerRegNo(nonNull(buyerInfo) ? "" + buyerInfo.getBuyerId() : "");
            orderHead.setBuyerTelephone(nonNull(buyerInfo) ? buyerInfo.getBuyerPhone() : "");
            if (nonNull(orderInfo)) {
                YouzanTradesSoldGetResultOrderextra extra = orderInfo.getOrderExtra();
                orderHead.setBuyerName(nonNull(extra) ? extra.getIdCardName() : "");
                orderHead.setBuyerIdType(ID.getKey());
                orderHead.setBuyerIdNumber(nonNull(extra) ? extra.getIdCardNumber() : "");
            }
            orderHead.setPayCode(nonNull(config) ? trimToEmpty(config.getPayCode()) : "");
            orderHead.setPayName(nonNull(config) ? trimToEmpty(config.getPayName()) : "");
            List<String> tranIds = payInfo.getTransaction();
            orderHead.setPayTransactionId(isNotEmpty(tranIds) ? (String) get(tranIds, 0) : "");
            orderHead.setConsignee(nonNull(addrInfo) ? addrInfo.getReceiverName() : "");
            orderHead.setConsigneeTelephone(nonNull(addrInfo) ? addrInfo.getReceiverTel() : "");

            if (isBlank(orderHead.getBuyerTelephone())) {
                orderHead.setBuyerTelephone(orderHead.getConsigneeTelephone());
            }
            if (isBlank(orderHead.getConsigneeTelephone())) {
                orderHead.setConsigneeTelephone(orderHead.getBuyerTelephone());
            }
            orderHead.setConsigneeAddress(nonNull(addrInfo) ? (new StringBuilder().append(addrInfo.getDeliveryProvince()).append(" ").append(addrInfo.getDeliveryCity()).append(" ").append(addrInfo.getDeliveryDistrict()).append(" ").append(addrInfo.getDeliveryAddress()).append(" ").append(addrInfo.getDeliveryPostalCode())).toString() : "");
            Receiver receiver = new Receiver();
            receiver.setCountry("中国");
            receiver.setProvince(nonNull(addrInfo) ? addrInfo.getDeliveryProvince() : "");
            receiver.setCity(nonNull(addrInfo) ? addrInfo.getDeliveryCity() : "");
            receiver.setArea(nonNull(addrInfo) ? addrInfo.getDeliveryDistrict() : "");
            receiver.setAddress(nonNull(addrInfo) ? addrInfo.getDeliveryAddress() : "");
            receiver.setMobile(nonNull(addrInfo) ? addrInfo.getReceiverTel() : "");
            receiver.setTel(nonNull(addrInfo) ? addrInfo.getReceiverTel() : "");
            receiver.setName(nonNull(addrInfo) ? addrInfo.getReceiverName() : "");
            String addrExtra = nonNull(addrInfo) ? addrInfo.getAddressExtra() : "";
            receiver.setPostCode(isNotBlank(addrExtra) ? getValueByKey(addrExtra, "areaCode") : "");
            orderHead.setReceiver(receiver);
            orderHead.setTid(orderInfo.getTid());
            orderHead.setCopNo(getCopNoByTid(orderHead.getTid()));
            orderHead.setEmsNo(plat.getEmsNo());
            try {
                Date ot = nonNull(orderInfo.getPayTime()) ? orderInfo.getPayTime() : orderInfo.getSuccessTime();
                if (nonNull(ot)) {
                    orderHead.setOrderTime(parse(ot));
                }
            } catch (ParseException ex) {
                LOG.error("failed to parse order time {}", ex.getMessage());
            }
        }
        return orderHead;
    }
}
