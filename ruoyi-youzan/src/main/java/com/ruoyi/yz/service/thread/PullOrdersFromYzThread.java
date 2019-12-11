/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service.thread;

import static com.ruoyi.common.utils.UuidUtil.get32UUID;
import static com.ruoyi.common.utils.spring.SpringUtils.getBean;
import static com.ruoyi.yz.cnst.Const.CREATE_BY_PROGRAM;
import com.ruoyi.yz.domain.YouzanOrder;
import static com.ruoyi.yz.enums.CrossBorder.YES;
import static com.ruoyi.yz.enums.CrossBorder.getByKey;
import static com.ruoyi.yz.enums.OrderStatus.alreadyLocked;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.ruoyi.yz.mapper.YouzanKdtMapper;
import com.ruoyi.yz.mapper.YouzanOrderMapper;
import com.ruoyi.yz.support.CustomsSupport;
import com.ruoyi.yz.support.YzOrderCustomSupport;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradesSoldGetResult;
import static java.net.HttpURLConnection.HTTP_OK;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.collect;
import static org.apache.commons.collections4.CollectionUtils.size;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.ArrayUtils;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import com.ruoyi.yz.customs.Message;
import com.ruoyi.yz.customs.order.CEB311Message;
import com.ruoyi.yz.customs.order.OrderHead;
import com.ruoyi.yz.domain.CustomsPlat;
import com.ruoyi.yz.domain.YouzanKdt;
import static com.ruoyi.yz.enums.OrderStatus.STATUS_INIT;
import com.ruoyi.yz.service.MvGoodsService;
import static com.ruoyi.yz.utils.YouZanUtil.logYzSdkObj;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradesSoldGetResult.YouzanTradesSoldGetResultData;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradesSoldGetResult.YouzanTradesSoldGetResultFullorderinfo;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradesSoldGetResult.YouzanTradesSoldGetResultFullorderinfolist;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradesSoldGetResult.YouzanTradesSoldGetResultOrderinfo;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradesSoldGetResult.YouzanTradesSoldGetResultOrders;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static com.ruoyi.yz.enums.YzOrderStatus.isNeedToClearStatus;
import static com.ruoyi.yz.utils.YouZanUtil.isSeperatedOrder;

/**
 *
 * @author wmao
 */
public abstract class PullOrdersFromYzThread implements Callable<Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(PullOrdersFromYzThread.class);

    @Autowired
    protected YouzanKdtMapper youzanKdtMapper;

    @Autowired
    protected YouzanOrderMapper youzanOrderMapper;

    @Autowired
    protected MvGoodsService mvGoodsService;

    protected YouzanKdt kdt;

    protected CustomsPlat plat;

    protected Date startTime;

    protected Date endTime;

    protected final Date currentTime;

    public PullOrdersFromYzThread() {
        this.currentTime = Calendar.getInstance().getTime();
    }

    public void init(YouzanKdt kdt, CustomsPlat plat, Date startTime, Date endTime) {
        this.kdt = kdt;
        this.plat = plat;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    protected abstract YouzanTradesSoldGetResult getSoldResult();

    protected abstract int updateKdt();

    /**
     * 是否是wms已经存在的商品
     *
     * @param outerSkuId
     * @param wmsGoods
     * @return
     */
    private boolean existedInWms(String outerSkuId, List<String> wmsGoods) {
        boolean existedInWms = false;
        if (isNotBlank(outerSkuId) && isNotEmpty(wmsGoods)) {
            Set wmsGoodsSet = new HashSet(wmsGoods);
            if (isNotEmpty(wmsGoodsSet) && wmsGoodsSet.contains(outerSkuId)) {
                existedInWms = true;
            }
        }
        return existedInWms;
    }

    /**
     * 订单中已经存在的不许要插入只需要更新
     *
     * @param originOrders
     * @return
     */
    private List<YouzanOrder> needUpdateOrders(List<YouzanOrder> originOrders) {
        final List<YouzanOrder> needUpdateOrders = new ArrayList<>();
        if (isNotEmpty(originOrders)) {// find all existed orders
            // find all transaction id
            List<String> trans = (List<String>) collect(originOrders, (Object obj) -> {
                YouzanOrder yo = (YouzanOrder) obj;
                return (nonNull(yo) ? yo.getTransaction() : null);
            });
            // clean empty transaction id
            if (isNotEmpty(trans)) {
                trans.removeAll(Collections.singleton(null));
            }

            List<YouzanOrder> existedOrders = existedOrders(trans);
            if (isNotEmpty(existedOrders)) {
                existedOrders.forEach((yo) -> {
                    YouzanOrder yoo = IteratorUtils.find(originOrders.iterator(),
                            (YouzanOrder yor) -> equalsIgnoreCase(yo.getTid(), yor.getTid())
                            && equalsIgnoreCase(yo.getKdtId(), yor.getKdtId())
                            && equalsIgnoreCase(yo.getTransaction(), yor.getTransaction())
                            && equalsIgnoreCase(yo.getOrderNo(), yor.getOrderNo()));
                    if (nonNull(yoo)) {
                        //remove order from origin order list
                        originOrders.remove(yoo);
                        //check if already send clear request, if yes ,do not update it.
                        if (!alreadyLocked(yo.getStatus())) {
                            yoo.setId(yo.getId());
                            yoo.setCreateBy(CREATE_BY_PROGRAM);
                            yoo.setCreateTime(yo.getCreateTime());
                            yoo.setUpdateBy(CREATE_BY_PROGRAM);
                            yoo.setUpdateTime(currentTime);
                            needUpdateOrders.add(yoo);
                        } else {
                            LOG.warn("order {} was locked!", yoo.getOrderNo());
                        }
                    }
                });
            }
        }
        return needUpdateOrders;
    }

    /**
     * 更新订单或者插入新订单
     *
     * @param info
     * @param orders
     * @return
     */
    private Integer updateOrInsert(YouzanTradesSoldGetResultFullorderinfo info, List<YouzanTradesSoldGetResultOrders> orders) {
        int retNum = 0;
        if (nonNull(info) && isNotEmpty(orders)) {
            info.setOrders(orders);
            CustomsSupport support = getBean(YzOrderCustomSupport.class);
            if (support != null) {
                support.setPlat(plat);
                List<YouzanOrder> yzOrders = cvtMessge2Order(support.generateRequest(info, kdt), kdt.getAuthorityId());
                LOG.info("pulled {} orders from youzan", size(yzOrders));
                // update orders which need to be updated
                List<YouzanOrder> needUpdateOrders = needUpdateOrders(yzOrders);
                if (isNotEmpty(needUpdateOrders)) {
                    int orderNum = youzanOrderMapper.batchUpdate(needUpdateOrders);
                    LOG.info("batch update {} into database!", orderNum);
                    retNum += orderNum;
                }

                // batch insert orders which are unique
                if (isNotEmpty(yzOrders)) {
                    int insertNum = youzanOrderMapper.batchInsert(yzOrders);
                    LOG.info("batch insert {} into database!", insertNum);
                    retNum += insertNum;
                }
            } else {
                LOG.error("cant get YzOrderCustomSupport  from sping container");
            }
        } else {
            LOG.warn("there is no valid order need to processed!");
        }
        return retNum;
    }

    /**
     * 只有跨境和本wms保存的物品才会是合法订单
     *
     * @param originOrders
     * @return
     */
    private List<YouzanTradesSoldGetResultOrders> validOrders(List<YouzanTradesSoldGetResultOrders> originOrders) {
        if (isNotEmpty(originOrders)) {
            String cusCode = mvGoodsService.getCusCodeByAuthId(kdt.getAuthorityId());
            LOG.info("cusCode:{}", cusCode);
            List<String> wmsGoods = mvGoodsService.allShpBianMa(cusCode);
            LOG.info("goods:{}", wmsGoods);
            if (isNotEmpty(wmsGoods)) {
                List<YouzanTradesSoldGetResultOrders> filteredOrders = new ArrayList<>(size(originOrders));
                originOrders.forEach((originOrder) -> {
                    if (nonNull(originOrder)) {
                        if (getByKey(originOrder.getIsCrossBorder()) == YES
                                && existedInWms(originOrder.getOuterItemId(), wmsGoods)) {
                            filteredOrders.add(originOrder);
                        } else {
                            LOG.warn("no need to pull youzan order, crossBorder:{}, skuId:{} ", originOrder.getIsCrossBorder(), originOrder.getOuterItemId());
                        }
                    }
                });
                return filteredOrders;
            } else {
                LOG.warn("wms has no goods {} is empty!", size(wmsGoods));
            }
        } else {
            LOG.warn("origin orders {} is empty!", size(originOrders));
        }
        return null;
    }

    @Override
    public Integer call() throws Exception {
        int retNum = 0;
        final List<Integer> retNums = new ArrayList<>();
        if (nonNull(kdt) && nonNull(youzanOrderMapper)) {
            try {
                YouzanTradesSoldGetResult result = getSoldResult();
                if (nonNull(result)) {
                    int code = result.getCode();
                    boolean success = result.getSuccess();
                    if (code == HTTP_OK && success) {
                        YouzanTradesSoldGetResultData data = result.getData();
                        if (nonNull(data)) {
                            LOG.info("kdt:{}, total results:{}", kdt.getAuthorityId(), data.getTotalResults());
                            List<YouzanTradesSoldGetResultFullorderinfolist> list = data.getFullOrderInfoList();
                            if (isNotEmpty(list)) {
                                list.stream().map((lst) -> lst.getFullOrderInfo()).forEachOrdered((YouzanTradesSoldGetResultFullorderinfo info) -> {
                                    if (isReadyToClearStatus(info.getOrderInfo())) {
                                        //only pull cross border records && goods belongs to qingma
                                        List<YouzanTradesSoldGetResultOrders> filterOrders = validOrders(info.getOrders());

                                        // update or insert orders
                                        Integer uiNums = updateOrInsert(info, filterOrders);
                                        if (nonNull(uiNums) && uiNums > 0) {
                                            retNums.add(uiNums);
                                        }
                                    } else {
                                        LOG.warn("is not payed order or was cancelled, not need to clearance");
                                    }
                                });
                            } else {
                                LOG.warn("no order list in the order result:{}", data);
                            }
                        } else {
                            LOG.warn("data of result is null:{}", result);
                        }
                        // update last pull date of kdt
                        updateKdt();
                    } else {
                        LOG.error("failed to call code: {}, success:{}, message: {}", code, success, result.getMessage());
                    }
                } else {
                    LOG.warn("can't pull orders from youzan");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                LOG.error("failed to call:{}", ArrayUtils.toString(ex.getStackTrace()));
            }
        }
        return retNums.stream().map((num) -> num).reduce(retNum, Integer::sum);
    }

    private boolean isReadyToClearStatus(YouzanTradesSoldGetResultOrderinfo orderInfo) {
        logYzSdkObj("isReadyToClearStatus", orderInfo);
        if (nonNull(orderInfo) && isNotBlank(orderInfo.getStatus())) {
            return isNeedToClearStatus(orderInfo.getStatus());
        }
        return false;
    }

    private List<YouzanOrder> cvtMessge2Order(final List<Message> messages, final String kdtId) {
        List<YouzanOrder> orders = null;
        if (nonNull(messages)) {
            orders = new ArrayList<>();
            for (Message message : messages) {
                if (nonNull(message)) {
                    YouzanOrder order = new YouzanOrder();
                    OrderHead head = ((CEB311Message) message).getOrder().getOrderHead();
                    order.setId(get32UUID());
                    order.setKdtId(kdtId);
                    order.setOrderNo(head.getOrderNo());
                    BigDecimal acPaid = head.getActuralPaid();
                    order.setAmount(nonNull(acPaid) ? acPaid.toString() : "");
                    order.setStatus(STATUS_INIT.name());
                    order.setStatusMessage(STATUS_INIT.getValue());
                    order.setTid(head.getTid());
                    order.setCopNo(head.getCopNo());
                    order.setTransaction(head.getPayTransactionId());
                    order.setBody(message);
                    order.setCreateBy(CREATE_BY_PROGRAM);
                    order.setCreateTime(currentTime);
                    order.setUpdateBy(CREATE_BY_PROGRAM);
                    order.setUpdateTime(currentTime);
                    orders.add(order);
                }
            }

            /**
             * 确定是否是分拆订单
             */
            boolean isSeperatedOrder = isSeperatedOrder(collect(orders, (Object obj) -> nonNull(obj) ? ((YouzanOrder) obj).getOrderNo() : null));
            orders.forEach(order -> {
                order.setSeperated(isSeperatedOrder);
            });
        }
        return orders;
    }

    private List<YouzanOrder> existedOrders(List<String> trans) {
        return isNotEmpty(trans) ? youzanOrderMapper.existed(trans, startTime, endTime) : null;
    }
}
