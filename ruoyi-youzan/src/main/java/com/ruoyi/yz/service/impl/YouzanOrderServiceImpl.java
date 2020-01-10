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
import static com.ruoyi.yz.cnst.Const.CREATE_BY_PROGRAM;
import com.ruoyi.yz.config.YundaKjProperties;
import com.ruoyi.yz.domain.CustomsPlat;
import com.ruoyi.yz.domain.YouzanConfig;
import com.ruoyi.yz.domain.YouzanKdt;
import com.ruoyi.yz.domain.YouzanOrder;
import static com.ruoyi.yz.enums.Port.CD;
import com.ruoyi.yz.mapper.YouzanConfigMapper;
import com.ruoyi.yz.mapper.YouzanKdtMapper;
import com.ruoyi.yz.mapper.YouzanOrderMapper;
import com.ruoyi.yz.service.YouzanOrderService;
import static com.ruoyi.yz.enums.OrderStatus.STATUS_DISCARDED;
import static com.ruoyi.yz.enums.OrderStatus.STATUS_INIT;
import static com.ruoyi.yz.enums.OrderStatus.STATUS_WAITING;
import static com.ruoyi.yz.enums.OrderStatus.STATUS_REJECTED;
import static com.ruoyi.yz.enums.OrderStatus.getNameByKey;
import java.util.List;
import java.util.ArrayList;
import static java.util.Collections.singleton;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
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
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import com.ruoyi.yz.mapper.YouzanOrderBkMapper;
import static org.apache.commons.collections4.CollectionUtils.collect;
import com.ruoyi.yz.domain.ClearanceStatus;
import com.ruoyi.yz.domain.WuliuKjPlat;
import com.ruoyi.yz.domain.excel.XlsOrder;
import com.ruoyi.yz.domain.excel.XlsOrderHelper;
import com.ruoyi.yz.domain.excel.XlsOrderItem;
import static com.ruoyi.yz.enums.OrderStatus.STATUS_APPLYING;
import static com.ruoyi.yz.enums.OrderStatus.isDiscarded;
import com.ruoyi.yz.enums.Port;
import static com.ruoyi.yz.enums.WuliuComp.YUNDA;
import com.ruoyi.yz.service.thread.ClearDetailsToCustomsThread;
import com.ruoyi.yz.service.thread.PlaceOrderToWmsThread;
import com.ruoyi.yz.service.thread.PullRangeOrdersFromYzThread;
import com.ruoyi.yz.service.thread.PullSpecOrderFromYzThread;
import static com.ruoyi.yz.utils.YundaUtil.assembleYdCreateReq;
import static com.ruoyi.yz.utils.YundaUtil.sendReq;
import com.ruoyi.yz.wuliu.ydkj.create.YdCreateRequest;
import com.ruoyi.yz.wuliu.ydkj.create.YdCreateResponse;
import com.ruoyi.yz.wuliu.ydkj.create.YdCreateResponses;
import java.util.Arrays;
import java.util.Map;
import static java.util.Objects.nonNull;
import static java.util.Objects.isNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;
import static com.ruoyi.yz.utils.YouZanUtil.getOrderTime;
import java.util.Collections;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.CollectionUtils.removeAll;
import org.apache.commons.collections4.Equator;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *
 * @author wmao
 */
@Service("youzanOrderService")
@Scope("prototype")
@Transactional
public class YouzanOrderServiceImpl implements YouzanOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(YouzanOrderServiceImpl.class);

    @Autowired
    private CustomsPlatMapper customPlatMapper;

    @Autowired
    private YouzanConfigMapper youzanConfigMapper;

    @Autowired
    private YouzanKdtMapper youzanKdtMapper;

    @Autowired
    private YouzanOrderMapper youzanOrderMapper;

    @Autowired
    private ThreadPoolTaskExecutor executor;

    @Autowired
    private YouzanOrderBkMapper youzanOrderBkMapper;

    @Autowired
    protected YundaKjProperties yundaKjProperties;

    @Autowired
    protected RestTemplate restTemplate;

    @Autowired
    protected WuliuKjPlatMapper wuliuKjPlatMapper;

    private YdCreateResponses sendRequest(YdCreateRequest request) {
        YdCreateResponses resp = null;
        if (nonNull(request)) {
            WuliuKjPlat plat = wuliuKjPlatMapper.getOne(YUNDA.name());
            if (nonNull(plat)) {
                resp = sendReq(request, restTemplate, plat, yundaKjProperties);
            }
        }
        return resp;
    }

    private YouzanOrder updateWuliuOfOrder(YouzanOrder order, YouzanKdt kdt) {
        YouzanOrder youzanOrder = null;
        if (nonNull(order) && nonNull(kdt)) {
            YdCreateRequest createRequest = assembleYdCreateReq(order, kdt.getSender());
            if (nonNull(createRequest)) {
                YdCreateResponses response = sendRequest(createRequest);
                if (nonNull(response)) {
                    List<YdCreateResponse> resps = response.getResponse();
                    if (CollectionUtils.isNotEmpty(resps)) {
                        YdCreateResponse resp = resps.get(0);
                        if (nonNull(resp) && isNotBlank(resp.getMailNo())) {
                            order.setWayBillEnt(YUNDA.getValue());
                            order.setWayBillNo(resp.getMailNo());
                            order.setStatus(STATUS_APPLYING.name());
                            order.setStatusMessage(STATUS_APPLYING.getValue());
                            order.setUpdateBy(CREATE_BY_PROGRAM);
                            order.setUpdateTime(Calendar.getInstance().getTime());
                            youzanOrderMapper.update(order);
                        } else if (nonNull(resp)) {
                            LOG.error("fst element of list is empty:{}", resp);
                        }
                    } else {
                        LOG.error("response list is empty:{}", response);
                    }
                } else {
                    LOG.error("create response is empty:{}", createRequest);
                }
            } else {
                LOG.error("failed to assemble yunda create request:{}", createRequest);
            }
        }
        return youzanOrder;
    }

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
                    } else {
                        ClearanceStatus cs = yo.getSyncDetailsStatus();
                        if (nonNull(cs)) {
                            sb.append("[").append(cs.getMessage()).append("]");
                        }
                    }
                    yo.setSearchValue(sb.toString());
                }
            });
        } else {
            LOG.warn("result list is empty:{}", size(retList));
        }
        return retList;
    }

    private List<YouzanOrder> needUpdateRetringOfOrder(List<YouzanOrder> orders, YouzanKdt kdt) {
        List<YouzanOrder> needUpdateList = new ArrayList<>();
        if (isNotEmpty(orders)) {
            orders.forEach((order) -> {
                if (nonNull(order)
                        && isBlank(order.getWayBillNo())
                        && (equalsIgnoreCase(order.getStatus(), STATUS_REJECTED.name())
                        || equalsIgnoreCase(order.getStatus(), STATUS_DISCARDED.name()))) {
                    order.reset();
                    needUpdateList.add(order);
                } else {
                    LOG.error("no need to wait clearing info for order:{}", order);
                }
            });
        }
        return needUpdateList;
    }

    private List<YouzanOrder> needUpdateApplyingOfOrder(List<YouzanOrder> orders, YouzanKdt kdt) {
        List<YouzanOrder> needUpdateList = new ArrayList<>();
        if (isNotEmpty(orders)) {
            Date currentTime = Calendar.getInstance().getTime();
            orders.forEach((order) -> {
                if (nonNull(order)
                        && isBlank(order.getWayBillNo())
                        && equalsIgnoreCase(order.getStatus(), STATUS_INIT.name())) {
                    YdCreateRequest createRequest = assembleYdCreateReq(order, kdt.getSender());
                    if (nonNull(createRequest)) {
                        YdCreateResponses response = sendRequest(createRequest);
                        if (nonNull(response)) {
                            List<YdCreateResponse> resps = response.getResponse();
                            if (CollectionUtils.isNotEmpty(resps)) {
                                YdCreateResponse resp = resps.get(0);
                                if (nonNull(resp) && isNotBlank(resp.getMailNo())) {
                                    order.setWayBillEnt(YUNDA.getValue());
                                    order.setWayBillNo(resp.getMailNo());
                                    order.setStatus(STATUS_APPLYING.name());
                                    order.setStatusMessage(STATUS_APPLYING.getValue());
                                    order.setUpdateBy(CREATE_BY_PROGRAM);
                                    order.setUpdateTime(currentTime);
                                    needUpdateList.add(order);
                                } else if (nonNull(resp)) {
                                    LOG.error("fst element of list is empty:{}", resp);
                                }
                            } else {
                                LOG.error("response list is empty:{}", response);
                            }
                        } else {
                            LOG.error("create response is empty:{}", createRequest);
                        }
                    } else {
                        LOG.error("failed to assemble yunda create request:{}", createRequest);
                    }
                } else {
                    LOG.error("no need to wait clearing info for order:{}", order);
                }
            });
        }
        return needUpdateList;
    }

    private List<YouzanOrder> needUpdateWaitingOfOrder(List<YouzanOrder> orders) {
        List<YouzanOrder> needUpdateList = new ArrayList<>();
        if (isNotEmpty(orders)) {
            Date currentTime = Calendar.getInstance().getTime();
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
                    LOG.error("no need to wait wuliu info for order:{}", order);
                }
            });
        }
        return needUpdateList;
    }

    @Override
    @Transactional
    public int exeRetry(List<YouzanOrder> orders, String kdtId) throws BusinessException {
        int pullLen = 0;
        if (nonNull(orders)) {
            YouzanKdt kdt = youzanKdtMapper.getOne(kdtId);
            CustomsPlat plat = customPlatMapper.getOneByDistrict(CD.name());
            if (nonNull(kdt) && nonNull(plat)) {
                final List<Future<Integer>> futures = new ArrayList<>(size(orders));
                orders.forEach((od) -> {
                    if (nonNull(od)) {
                        PullSpecOrderFromYzThread pullThread = getBean("pullSpecOrderFromYzThread");
                        pullThread.init(od, kdt, plat);
                        futures.add(executor.submit(pullThread));
                    }
                });
                pullLen += exeFutureList(futures);
                LOG.info("pulled {} orders", pullLen);
            } else {
                LOG.error("kdt {} is null or plat {} is null", kdt, plat);
            }
        }
        return pullLen;
    }

    @Override
    @Transactional
    public List<YouzanOrder> exeRetry(String[] args, String kdtId) throws BusinessException {
        List<YouzanOrder> needUpdateList = null;
        if (ArrayUtils.isNotEmpty(args) && isNotBlank(kdtId)) {
            YouzanKdt kdt = youzanKdtMapper.getOne(kdtId);
            if (nonNull(kdt)) {
                List<YouzanOrder> orders = youzanOrderMapper.getByIds(args, kdt.getAuthorityId());
                if (isNotEmpty(orders)) {
                    needUpdateList = needUpdateRetringOfOrder(orders, kdt);
                    LOG.info("clearance orders length:{}, need reseting orders length:{}", size(orders), size(needUpdateList));
                    if (isNotEmpty(needUpdateList)) {
                        int retLen = batchClearStatus(needUpdateList);
                        LOG.info("cleared {} orders", retLen);
                    } else {
                        LOG.warn("no need to update youzan order");
                    }
                } else {
                    LOG.error("no valid orders were found!");
                }
            } else {
                LOG.error("kdt is null");
            }
        }
        return needUpdateList;
    }

    @Override
    @Transactional
    public int execute(String[] args, String kdtId) throws BusinessException {
        int retLen = 0;
        if (ArrayUtils.isNotEmpty(args) && isNotBlank(kdtId)) {
            YouzanKdt kdt = youzanKdtMapper.getOne(kdtId);
            if (nonNull(kdt)) {
                List<YouzanOrder> orders = youzanOrderMapper.getByIds(args, kdt.getAuthorityId());
                if (isNotEmpty(orders)) {
                    List<YouzanOrder> needUpdateList = needUpdateApplyingOfOrder(orders, kdt);
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
                LOG.error("kdt is null");
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
                    final List<YouzanOrder> needUpdateList = new ArrayList<>();
                    orders.forEach((order) -> {
                        if (nonNull(order)
                                && equalsIgnoreCase(order.getKdtId(), kdt.getAuthorityId())) {
                            if (isDiscarded(order.getStatus())) {
                                order.reset();
                                needUpdateList.add(order);
                            } else {
                                LOG.warn("order is waiting to clear to customs, pls check it!");
                            }
                        } else {
                            LOG.error("faied to recovery order:{} of kdt:{}", order, kdt);
                        }
                    });
                    retLen = batchClearStatus(needUpdateList);
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
    @Transactional
    public int update(YouzanOrder order) {
        int ret = 0;
        if (nonNull(order)) {
            ret = youzanOrderMapper.update(order);
        }
        return ret;
    }

    private List<YouzanOrder> sort(List<YouzanOrder> orders) {
        if (isNotEmpty(orders)) {
            Collections.sort(orders, (YouzanOrder yo1, YouzanOrder yo2) -> {
                Date ot1 = getOrderTime(yo1);
                Date ot2 = getOrderTime(yo2);
                if (ot1.compareTo(ot2) > 0) {
                    return 1;
                } else {
                    return -1;
                }
            });
        }
        return orders;
    }

    @Override
    @Transactional
    public int batchInsert(List<YouzanOrder> orders) {
        int ret = 0;
        if (isNotEmpty(orders)) {
            List<YouzanOrder> removedOrders = sort(orders);
            LOG.info("before get rid of:{}", size(removedOrders));

            List<String> orderNos = (List<String>) collect(removedOrders, (Object obj) -> {
                YouzanOrder yo = (YouzanOrder) obj;
                return (nonNull(yo) ? yo.getOrderNo() : null);
            });
            if (isNotEmpty(orderNos)) {
                orderNos.removeAll(Collections.singleton(null));
            }

            List<YouzanOrder> yos = youzanOrderMapper.existedOrders(orderNos, getOrderTime(IteratorUtils.get(removedOrders.iterator(), 0)), Calendar.getInstance().getTime());
            if (isNotEmpty(yos)) {
                yos.removeAll(Collections.singleton(null));
                removedOrders = (List<YouzanOrder>) removeAll(removedOrders, yos, new Equator<YouzanOrder>() {
                    @Override
                    public boolean equate(YouzanOrder obj1, YouzanOrder obj2) {
                        if (isNull(obj1) && isNull(obj2)) {
                            return true;
                        }
                        if (isNull(obj1) || isNull(obj2)) {
                            return false;
                        }
                        if (obj1.getClass() != obj2.getClass()) {
                            return false;
                        }
                        return new EqualsBuilder().append(obj1.getTid(), obj2.getTid())
                                .append(obj1.getOrderNo(), obj2.getOrderNo())
                                .append(getOrderTime(obj1), getOrderTime(obj2)).isEquals();
                    }

                    @Override
                    public int hash(YouzanOrder obj) {
                        return new HashCodeBuilder(11, 21).append(obj.getTid())
                                .append(obj.getOrderNo())
                                .append(getOrderTime(obj))
                                .toHashCode();
                    }
                });
            }
            LOG.info("{} orders will be inserted into database", size(removedOrders));
            if (isNotEmpty(removedOrders)) {
                ret = youzanOrderMapper.batchInsert(removedOrders);
            }
        }
        return ret;
    }

    @Override
    @Transactional
    public int batchUpdate(List<YouzanOrder> orders) {
        int ret = 0;
        if (isNotEmpty(orders)) {
            ret = youzanOrderMapper.batchUpdate(orders);
        }
        return ret;
    }

    private int batchClearStatus(List<YouzanOrder> orders) {
        int ret = 0;
        if (isNotEmpty(orders)) {
            ret = youzanOrderMapper.batchClearStatus(orders);
        }
        return ret;
    }

    @Override
    @Transactional
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
    @Transactional
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
    @Transactional
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
    @Transactional
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
    @Transactional
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
    @Transactional
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
                LOG.warn("orders of kdt {} is empty:{}", kdt.getAuthorityName(), size(orders));
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

    @Override
    @Transactional
    public int wuliuEdit(final YouzanOrder order) throws BusinessException {
        int ret = 0;
        if (nonNull(order)) {
            String wuliuNo = order.getWayBillNo();
            YouzanOrder oldOrder = youzanOrderMapper.getOne(order.getId());
            if (nonNull(oldOrder)) {
                final String oldWuliuNo = oldOrder.getWayBillNo();
                if (isBlank(oldWuliuNo) || !equalsIgnoreCase(oldWuliuNo, wuliuNo)) {
                    LOG.info("old wuliu no:{}, update wuliu no:{}", oldWuliuNo, wuliuNo);
                    oldOrder.setWayBillEnt(YUNDA.getValue());
                    oldOrder.setWayBillNo(wuliuNo);
                    oldOrder.setStatus(STATUS_APPLYING.name());
                    oldOrder.setStatusMessage(STATUS_APPLYING.getValue());
                    oldOrder.setUpdateBy(CREATE_BY_PROGRAM);
                    oldOrder.setUpdateTime(Calendar.getInstance().getTime());
                    ret = youzanOrderMapper.update(oldOrder);
                } else {
                    LOG.warn("don't need to update wuliu info, already existed");
                }
            } else {
                LOG.error("failed to get youzan order history record:{}", oldOrder);
            }
        } else {
            LOG.error("id is empty or wuliu no is empty");
        }
        return ret;
    }

    @Override
    public List<Map<String, Object>> export(String ids, String kdtId) {
        List<Map<String, Object>> rets = new ArrayList<>();
        if (isNotBlank(ids)) {
            String[] idArray = ids.split(",");
            if (ArrayUtils.isNotEmpty(idArray)) {
                YouzanKdt kdt = youzanKdtMapper.getOne(kdtId);
                String authId = nonNull(kdt) ? kdt.getAuthorityId() : null;
                List<YouzanOrder> orders = youzanOrderMapper.getByIds(idArray, authId);
                LOG.error("idArray:{}, authId:{}, orders:{}", idArray, authId, size(orders));
                if (isNotEmpty(orders)) {
                    Map<String, Object> odMap = new HashMap<>();
                    List<XlsOrderItem> xlsItems = new ArrayList<>();
                    List<XlsOrder> xlsOrders = new ArrayList<>();
                    orders.forEach((order) -> {
                        xlsOrders.add(XlsOrderHelper.transform(order));
                    });
                    odMap.put("orders", xlsOrders);
                    rets.add(odMap);
                    Map<String, Object> itMap = new HashMap<>();
                    if (isNotEmpty(xlsOrders)) {
                        xlsOrders.forEach((xlsOrder) -> {
                            xlsItems.addAll(xlsOrder.getItems());
                        });
                        itMap.put("items", xlsItems);
                    }
                    rets.add(itMap);
                }
            }
        }
        return rets;
    }

    @Override
    public List<YouzanOrder> getInitOrdersOfKdt(Map<String, Object> kdt) {
        List<YouzanOrder> orders = null;
        if (MapUtils.isNotEmpty(kdt)) {
            orders = youzanOrderMapper.getInitOrdersOfKdt(kdt);
        }
        return orders;
    }

    @Override
    public List<YouzanOrder> existed(List<String> trans, Date startTime, Date endTime) {
        List<YouzanOrder> orders = null;
        if (isNotEmpty(trans)) {
            orders = youzanOrderMapper.existed(trans, startTime, endTime);
        }
        return orders;
    }
}
