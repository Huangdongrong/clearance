/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service.impl;

import com.ruoyi.common.exception.BusinessException;
import static com.ruoyi.common.utils.DateUtil.addDays;
import static com.ruoyi.common.utils.DateUtil.format;
import static com.ruoyi.common.utils.DateUtils.getStartTimeOfDay;
import static com.ruoyi.common.utils.DateUtils.parseDate;
import static com.ruoyi.common.utils.spring.SpringUtils.getBean;
import com.ruoyi.yz.domain.CustomsPlat;
import com.ruoyi.yz.domain.YouzanConfig;
import com.ruoyi.yz.domain.YouzanKdt;
import com.ruoyi.yz.domain.YouzanOrder;
import static com.ruoyi.yz.enums.Port.CD;
import com.ruoyi.yz.mapper.YouzanConfigMapper;
import com.ruoyi.yz.mapper.YouzanKdtMapper;
import com.ruoyi.yz.mapper.YouzanOrderMapper;
import com.ruoyi.yz.service.YouzanOrderService;
import static com.ruoyi.yz.cnst.Const.CREATE_BY_PROGRAM;
import static com.ruoyi.yz.enums.OrderStatus.STATUS_DISCARDED;
import static com.ruoyi.yz.enums.OrderStatus.STATUS_INIT;
import static com.ruoyi.yz.enums.OrderStatus.STATUS_WAITING;
import static com.ruoyi.yz.enums.OrderStatus.STATUS_REJECTED;
import static com.ruoyi.yz.enums.OrderStatus.getNameByKey;
import static com.ruoyi.yz.enums.WuliuComp.YUNDA;
import java.util.List;
import java.util.ArrayList;
import static java.util.Collections.singleton;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.CollectionUtils.size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.yz.mapper.CustomsPlatMapper;
import com.ruoyi.yz.mapper.WuliuKjPlatMapper;
import com.ruoyi.yz.service.thread.ClearOrderToCustomsThread;
import com.ruoyi.yz.service.thread.CompleteOrderThread;
import com.ruoyi.yz.service.thread.PullAllOrdersFromYzThread;
import com.ruoyi.yz.service.thread.SendEmailThread;
import com.ruoyi.yz.service.thread.SyncPayResultFromYouzanThread;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import org.apache.commons.lang3.ArrayUtils;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import com.ruoyi.yz.mapper.YouzanOrderBkMapper;
import static org.apache.commons.collections4.CollectionUtils.collect;
import com.ruoyi.yz.domain.ClearanceStatus;
import static com.ruoyi.yz.enums.OrderStatus.STATUS_APPLYING;
import static com.ruoyi.yz.enums.OrderStatus.isDiscarded;
import com.ruoyi.yz.enums.Port;
import com.ruoyi.yz.service.thread.ClearDetailsToCustomsThread;
import com.ruoyi.yz.service.thread.PlaceOrderToWmsThread;
import com.ruoyi.yz.service.thread.PullRangeOrdersFromYzThread;
import java.util.Arrays;
import java.util.Map;
import static java.util.Objects.nonNull;
import org.apache.commons.collections4.MapUtils;

/**
 *
 * @author wmao
 */
@Service("youzanOrderService")
@Transactional
public class YouzanOrderServiceImpl implements YouzanOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(YouzanOrderServiceImpl.class);

    @Autowired
    private CustomsPlatMapper customPlatMapper;

    @Autowired
    private YouzanConfigMapper youzanConfigMapper;

    @Autowired
    private WuliuKjPlatMapper wuliuKjPlatMapper;

    @Autowired
    private YouzanKdtMapper youzanKdtMapper;

    @Autowired
    private YouzanOrderMapper youzanOrderMapper;

    @Autowired
    private ThreadPoolTaskExecutor executor;

    @Autowired
    private YouzanOrderBkMapper youzanOrderBkMapper;

    @Override
    @Transactional
    public void placeWmsOrders() {
        List<YouzanKdt> kdts = youzanKdtMapper.getAvailKdts();
        if (isNotEmpty(kdts)) {
            final List<Future<Integer>> futures = new ArrayList<>(size(kdts));
            kdts.stream().filter((kdt) -> (nonNull(kdt) && kdt.isAutoClearing())).forEachOrdered((kdt) -> {
                PlaceOrderToWmsThread wmsThread = getBean("placeOrderToWmsThread");
                wmsThread.init(kdt);
                futures.add(executor.submit(wmsThread));
            });
            int retLen = exeFutureList(futures);
            LOG.info("pull {} orders from youzan", retLen);
        } else {
            LOG.warn("can't find all kdt:{}", size(kdts));
        }
    }

    @Override
    @Transactional
    public void pullOrders() {
        YouzanConfig config = youzanConfigMapper.getOne();
        if (nonNull(config)) {
            List<YouzanKdt> kdts = youzanKdtMapper.getAvailKdts();
            if (isNotEmpty(kdts)) {
                final List<Future<Integer>> futures = new ArrayList<>(size(kdts));
                kdts.stream().filter((kdt) -> (nonNull(kdt) && kdt.isAutoPulling())).forEachOrdered((kdt) -> {
                    PullAllOrdersFromYzThread pullThread = getBean("pullAllOrdersFromYzThread");
                    pullThread.init(kdt, customPlatMapper.getOneByDistrict(CD.name()));
                    futures.add(executor.submit(pullThread));
                });
                int retLen = exeFutureList(futures);
                LOG.info("pull {} orders from youzan", retLen);
            } else {
                LOG.warn("can't find all kdt:{}", size(kdts));
            }
        } else {
            LOG.warn("youzn config is empty!");
        }
    }

    @Override
    public List<YouzanOrder> getList(YouzanOrder order) {
        if (nonNull(order)) {
            if (isNotBlank(order.getStatus())) {
                order.setStatus(getNameByKey(order.getStatus()));
            }
            Map<String, Object> params = order.getParams();
            if (MapUtils.isNotEmpty(params)) {
                Date endTime = parseDate(params.get("endTime"));
                if (nonNull(endTime)) {
                    endTime = getStartTimeOfDay(addDays(endTime, 1));
                    params.put("endTime", format(endTime, "yyyy-MM-dd"));
                }
            }
        }
        List<YouzanOrder> retList = nonNull(order) ? youzanOrderMapper.getPageList(order) : null;
        if (isNotEmpty(retList)) {
            retList.forEach((yo) -> {
                if (nonNull(yo)) {
                    String statusMessage = yo.getStatusMessage();
                    StringBuilder sb = new StringBuilder();
                    if (isNotBlank(statusMessage)) {
                        sb.append(statusMessage);
                    }
                    ClearanceStatus cs = yo.getSyncDetailsStatus();
                    if (nonNull(cs)) {
                        sb.append("[").append(cs.getMessage()).append("]");
                    }
                    yo.setSearchValue(sb.toString());
                }
            });
        } else {
            LOG.warn("result list is empty:{}", size(retList));
        }
        return retList;
    }

    @Override
    @Transactional
    public int execute(String[] args, String kdtId) throws BusinessException {
        int retLen = 0;
        if (ArrayUtils.isNotEmpty(args) && isNotBlank(kdtId)) {
            YouzanConfig config = youzanConfigMapper.getOne();
            CustomsPlat plat = customPlatMapper.getOneByDistrict(CD.name());
            YouzanKdt kdt = youzanKdtMapper.getOne(kdtId);
            if (nonNull(config) && nonNull(plat) && nonNull(kdt)) {
                List<YouzanOrder> orders = youzanOrderMapper.getByIds(args, kdt.getAuthorityId());
                if (isNotEmpty(orders)) {
                    Date currentTime = Calendar.getInstance().getTime();
                    List<YouzanOrder> needUpdateList = new ArrayList<>();
                    orders.forEach((order) -> {
                        if (nonNull(order)
                                && isBlank(order.getWayBillNo())
                                && equalsIgnoreCase(order.getStatus(), STATUS_INIT.name())) {
                            order.setStatus(STATUS_WAITING.name());
                            order.setStatusMessage(STATUS_WAITING.getValue());
                            order.setUpdateBy(CREATE_BY_PROGRAM);
                            order.setUpdateTime(currentTime);
                            needUpdateList.add(order);
                        } else {
                            LOG.error("no need to apply clearance for order:{}", order);
                        }
                    });
                    LOG.info("clear orders length:{}, need update orders length:{}", size(orders), size(needUpdateList));
                    if (nonNull(needUpdateList)) {
                        retLen += batchUpdate(needUpdateList);
                        needUpdateList.forEach((od) -> {
                            if (nonNull(od)) {
                                executor.submit(new SendEmailThread(kdt, od.getTid()));
                            }
                        });
                    } else {
                        LOG.warn("no need to update youzan order");
                    }
                } else {
                    LOG.error("no valid orders were found!");
                }
            } else {
                LOG.error("config, plat or kdt is null");
            }
        } else {
            LOG.error("failed to clear orders");
        }
        return retLen;
    }

    private int exeFutureList(List<Future<Integer>> futures) {
        int retLen = 0;
        if (isNotEmpty(futures)) {
            futures.removeAll(singleton(null));
            if (isNotEmpty(futures)) {
                for (Future<Integer> future : futures) {
                    if (nonNull(future)) {
                        try {
                            retLen += future.get();
                        } catch (InterruptedException | ExecutionException ex) {
                            LOG.error("failed to run futrue thread:{}", ArrayUtils.toString(ex.getStackTrace()));
                        }
                    }
                }
            }
        }
        return retLen;
    }

    @Override
    @Transactional
    public int remove(String[] ids, String kdtId) throws BusinessException {
        int retLen = 0;
        if (ArrayUtils.isNotEmpty(ids) && isNotBlank(kdtId)) {
            YouzanKdt kdt = youzanKdtMapper.getOne(kdtId);
            if (nonNull(kdt)) {
                List<YouzanOrder> orders = youzanOrderMapper.getByIds(ids, kdt.getAuthorityId());
                if (isNotEmpty(orders)) {
                    Date currentTime = Calendar.getInstance().getTime();
                    final List<YouzanOrder> needUpdateList = new ArrayList<>();
                    orders.forEach((order) -> {
                        if (nonNull(order) && equalsIgnoreCase(order.getKdtId(), kdt.getAuthorityId())) {
                            if (!equalsIgnoreCase(order.getStatus(), STATUS_WAITING.name())
                                    && !equalsIgnoreCase(order.getStatus(), STATUS_APPLYING.name())) {
                                order.setUpdateTime(currentTime);
                                order.setUpdateBy(CREATE_BY_PROGRAM);
                                order.setRemark("删除此条记录！");
                                needUpdateList.add(order);
                            } else {
                                LOG.warn("order is waiting to clear to customs, pls check it!");
                            }
                        }
                    });
                    youzanOrderBkMapper.batchInsert(needUpdateList);
                    List<String> bkIds = (List<String>) collect(needUpdateList, (Object obj) -> {
                        YouzanOrder yo = (YouzanOrder) obj;
                        return yo.getId();
                    });
                    retLen = youzanOrderMapper.batchDelete(bkIds);
                } else {
                    LOG.warn("can't find orders of kdt:{}, args:{}", kdt.getAuthorityId(), ids);
                }
            } else {
                LOG.warn("cant' find kdt by id:{}", kdtId);
            }
        } else {
            LOG.warn("ids {} or kdt id {} is null", ids, kdtId);
        }
        return retLen;
    }

    @Override
    @Transactional
    public int recovery(String[] ids, String kdtId) throws BusinessException {
        int retLen = 0;
        if (ArrayUtils.isNotEmpty(ids) && isNotBlank(kdtId)) {
            YouzanKdt kdt = youzanKdtMapper.getOne(kdtId);
            if (nonNull(kdt)) {
                List<YouzanOrder> orders = youzanOrderMapper.getByIds(ids, kdt.getAuthorityId());
                if (isNotEmpty(orders)) {
                    final List<YouzanOrder> needUpdate = new ArrayList<>();
                    orders.forEach((order) -> {
                        if (nonNull(order)
                                && equalsIgnoreCase(order.getKdtId(), kdt.getAuthorityId())) {
                            if (isDiscarded(order.getStatus())) {
                                order.reset();
                                needUpdate.add(order);
                            } else {
                                LOG.warn("order is waiting to clear to customs, pls check it!");
                            }
                        } else {
                            LOG.error("faied to recovery order:{} of kdt:{}", order, kdt);
                        }
                    });
                    retLen = batchUpdate(needUpdate);
                }
            }
        }
        return retLen;
    }

    @Override
    public List<YouzanOrder> wmsPullOrders(Date lastPulledDate) throws BusinessException {
        List<YouzanOrder> orders = null;
        if (nonNull(lastPulledDate)) {
            orders = youzanOrderMapper.pullOrders(lastPulledDate);
        }
        return orders;
    }

    @Override
    public YouzanOrder getOne(String orderId) {
        YouzanOrder order = null;
        if (isNotBlank(orderId)) {
            order = youzanOrderMapper.getOne(orderId);
        }
        return order;
    }

    @Override
    public YouzanOrder getOneByOrderNo(String orderNo) {
        YouzanOrder order = null;
        if (isNotBlank(orderNo)) {
            order = youzanOrderMapper.getOneByOrderNo(orderNo);
        }
        return order;
    }

    @Override
    public int update(YouzanOrder order) {
        int ret = -1;
        if (nonNull(order)) {
            ret = youzanOrderMapper.update(order);
        }
        return ret;
    }

    @Override
    public int batchInsert(List<YouzanOrder> orders) {
        int ret = -1;
        if (isNotEmpty(orders)) {
            youzanOrderMapper.batchInsert(orders);
        }
        return ret;
    }

    @Override
    public int batchUpdate(List<YouzanOrder> orders) {
        int ret = -1;
        if (isNotEmpty(orders)) {
            ret = youzanOrderMapper.batchUpdate(orders);
        }
        return ret;
    }

    @Override
    public int discard(String[] ids, String kdtId) throws BusinessException {
        int retLen = 0;
        if (ArrayUtils.isNotEmpty(ids) && isNotBlank(kdtId)) {
            YouzanKdt kdt = youzanKdtMapper.getOne(kdtId);
            if (nonNull(kdt)) {
                List<YouzanOrder> orders = youzanOrderMapper.getByIds(ids, kdt.getAuthorityId());
                if (isNotEmpty(orders)) {
                    Date currentTime = Calendar.getInstance().getTime();
                    final List<YouzanOrder> needUpdate = new ArrayList<>();
                    orders.forEach((order) -> {
                        if (nonNull(order) && equalsIgnoreCase(order.getKdtId(), kdt.getAuthorityId())) {
                            if (equalsIgnoreCase(order.getStatus(), STATUS_INIT.name())
                                    || equalsIgnoreCase(order.getStatus(), STATUS_REJECTED.name())
                                    || equalsIgnoreCase(order.getStatus(), STATUS_WAITING.name())
                                    || equalsIgnoreCase(order.getStatus(), STATUS_APPLYING.name())) {
                                order.setStatus(STATUS_DISCARDED.name());
                                order.setStatusMessage("废弃掉此条记录！");
                                order.setUpdateTime(currentTime);
                                order.setUpdateBy(CREATE_BY_PROGRAM);
                                needUpdate.add(order);
                            } else {
                                LOG.warn("Now order is waiting to clear to customs, you can't discard it, pls check it!");
                            }
                        } else {
                            LOG.error("it's very weired, order is null or wrong kdt:{}, {}", order, kdt.getAuthorityId());
                        }
                    });
                    retLen = batchUpdate(needUpdate);
                }
            }
        }
        return retLen;
    }

    @Override
    public int pullOrders(YouzanOrder order, YouzanKdt kdt) {
        int ret = 0;
        if (nonNull(order)) {
            YouzanConfig config = youzanConfigMapper.getOne();
            if (nonNull(config) && nonNull(kdt)) {
                try {
                    PullRangeOrdersFromYzThread pullThread = getBean("pullRangeOrdersFromYzThread");
                    pullThread.init(kdt, customPlatMapper.getOneByDistrict(CD.name()), order.getCreateTime(), order.getUpdateTime());
                    Future<Integer> future = executor.submit(pullThread);
                    ret = future.get();
                    LOG.info("pull {} orders from youzan", ret);
                } catch (InterruptedException | ExecutionException ex) {
                    LOG.error("failed to pull orders from youzan:{}, ex:{}", order, Arrays.toString(ex.getStackTrace()));
                }
            } else {
                LOG.error("youzan config:{} is null or kdt:{} is null", config, kdt);
            }
        } else {
            LOG.error("order is null, pls check it");
        }
        return ret;
    }

    @Override
    public YouzanOrder getOneByCopNo(String copNo) {
        YouzanOrder order = null;
        if (isNotBlank(copNo)) {
            order = youzanOrderMapper.getOneByCopNo(copNo);
        }
        return order;
    }

    @Override
    public int autoExecute(YouzanKdt kdt, Date lastExecuteDate) throws BusinessException {
        int ret = 0;
        if (nonNull(kdt)) {
            Map<String, Object> params = new HashMap<String, Object>() {
                {
                    put("authId", kdt.getAuthorityId());
                    put("lastExecuteDate", lastExecuteDate);
                }
            };
            List<YouzanOrder> orders = youzanOrderMapper.getReadyApplyingOrdersOfKdt(params);
            LOG.info("params:{}, orders:{}", params, size(orders));
            if (isNotEmpty(orders)) {
                ClearOrderToCustomsThread clearThread = getBean("clearOrderToCustomsThread");
                clearThread.init(orders,
                        kdt,
                        customPlatMapper.getOneByDistrict(CD.name()),
                        customPlatMapper.getOneByDistrict(Port.YUNDA.name()));
                Future<Integer> future = executor.submit(clearThread);
                try {
                    ret = future.get();
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    LOG.error("failed to get return value from thread:{}", ex.getMessage());
                }
            } else {
                LOG.warn("orders of kdt is empty:{}", size(orders));
            }
        } else {
            LOG.error("kdt is empty:{}", kdt);
        }
        return ret;
    }

    @Override
    public int autoDetailsExecute(YouzanKdt kdt, Date lastExecuteDate) throws BusinessException {
        int ret = 0;
        if (nonNull(kdt)) {
            Map<String, Object> params = new HashMap<String, Object>() {
                {
                    put("authId", kdt.getAuthorityId());
                    put("lastExecuteDate", lastExecuteDate);
                }
            };
            List<YouzanOrder> orders = youzanOrderMapper.getReadyClearingDetailsOfKdt(params);
            LOG.info("params:{}, orders:{}", params, size(orders));
            if (isNotEmpty(orders)) {
                ClearDetailsToCustomsThread clearThread = getBean("clearDetailsToCustomsThread");
                clearThread.init(orders,
                        customPlatMapper.getOneByDistrict(CD.name()),
                        wuliuKjPlatMapper.getOne(YUNDA.name()));
                Future<Integer> future = executor.submit(clearThread);
                try {
                    ret = future.get();
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    LOG.error("failed to get return value from thread:{}", ex.getMessage());
                }
            } else {
                LOG.warn("orders of kdt is empty:{}", size(orders));
            }
        } else {
            LOG.error("kdt is empty:{}", kdt);
        }
        return ret;
    }

    @Override
    public int autoComplete(YouzanKdt kdt, Date lastCompleteDate) throws BusinessException {
        int ret = 0;
        if (nonNull(kdt)) {
            Map<String, Object> params = new HashMap<String, Object>() {
                {
                    put("authId", kdt.getAuthorityId());
                    put("lastCompleteDate", lastCompleteDate);
                }
            };
            List<YouzanOrder> orders = youzanOrderMapper.getReadyCompleteOrdersOfKdt(params);
            LOG.info("params:{}, orders:{}", params, size(orders));
            if (isNotEmpty(orders)) {
                CompleteOrderThread completeThread = getBean("completeOrderThread");
                completeThread.init(kdt, orders);
                Future<Integer> future = executor.submit(completeThread);
                try {
                    ret = future.get();
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    LOG.error("failed to get result of thread! {}", ex.getMessage());
                }
            } else {
                LOG.warn("orders of kdt is empty:{}", size(orders));
            }
        } else {
            LOG.error("kdt is empty:{}", kdt);
        }
        return ret;
    }

    @Override
    public int autoQueryPayClearanceResult(YouzanKdt kdt, Date lastQueryDate) throws BusinessException {
        int retLen = 0;
        if (nonNull(kdt)) {
            Map<String, Object> params = new HashMap<String, Object>() {
                {
                    put("authId", kdt.getAuthorityId());
                    put("lastQueryDate", lastQueryDate);
                }
            };
            List<YouzanOrder> orders = youzanOrderMapper.getNeedToQueryPayClearanceOrdersOfKdt(params);
            LOG.info("params:{}, orders:{}", params, size(orders));
            if (isNotEmpty(orders)) {
                SyncPayResultFromYouzanThread queryThread = getBean("syncPayResultFromYouzanThread");
                queryThread.init(kdt, orders);
                Future<Integer> future = executor.submit(queryThread);
                try {
                    retLen = future.get();
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    LOG.error("failed to get result of thread! {}", ex.getMessage());
                }
            } else {
                LOG.warn("orders of kdt is empty:{}", size(orders));
            }
        } else {
            LOG.error("kdt is empty:{}", kdt);
        }
        return retLen;
    }

    @Override
    public List<Map<String, Object>> getOrdersOfKdts(List<String> kdtIds, String status, Date startTime, Date endTime) {
        List<Map<String, Object>> orders = null;
        if (isNotEmpty(kdtIds) && nonNull(startTime) && nonNull(endTime)) {
            orders = youzanOrderMapper.getOrdersOfKdts(kdtIds, status, startTime, endTime);
        }
        return orders;
    }
}
