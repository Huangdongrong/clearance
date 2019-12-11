/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import static com.ruoyi.common.utils.DateUtil.parse;
import static com.ruoyi.common.utils.JsonUtil.OBJECT_MAPPER;
import com.ruoyi.yz.config.StoKjProperties;
import com.ruoyi.yz.customs.order.CEB311Message;
import com.ruoyi.yz.customs.order.Order;
import com.ruoyi.yz.customs.order.OrderHead;
import com.ruoyi.yz.domain.WuliuKjPlat;
import com.ruoyi.yz.domain.YouzanOrder;
import static com.ruoyi.yz.enums.AppType.INSERT;
import static com.ruoyi.yz.enums.Currency.CNY;
import com.ruoyi.yz.wuliu.stokj.order.StoKjOrderRequest;
import com.ruoyi.yz.wuliu.stokj.order.StoKjOrderResponse;
import com.ruoyi.yz.wuliu.stokj.trace.StoKjTraceRequest;
import com.ruoyi.yz.wuliu.stokj.trace.StoKjTraceResponse;
import static com.ruoyi.yz.utils.WuliuUtil.assembleUrl;
import static com.ruoyi.yz.utils.WuliuUtil.generateKjHeader;
import com.ruoyi.yz.wuliu.Receiver;
import com.ruoyi.yz.wuliu.Sender;
import com.ruoyi.yz.wuliu.stokj.order.Items;
import com.ruoyi.yz.wuliu.stokj.order.OtherInfo;
import java.math.BigDecimal;
import static java.math.BigDecimal.ROUND_HALF_DOWN;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Map;
import static java.util.Objects.nonNull;
import static org.apache.commons.codec.digest.DigestUtils.sha1Hex;
import org.apache.commons.collections.MapUtils;
import static org.apache.commons.collections.MapUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.StringUtils.upperCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import static org.springframework.http.HttpMethod.POST;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author wmao
 */
public final class StoUtil {

    private static final Logger LOG = LoggerFactory.getLogger(StoUtil.class);

    public static StoKjOrderRequest assembleStoWlReq(YouzanOrder order, Sender sender) {
        StoKjOrderRequest req = null;
        if (nonNull(order)) {
            req = new StoKjOrderRequest();
            req.setTradeNo(order.getTransaction());
            req.setWaybillNo(order.getWayBillNo());
            req.setWeight(new BigDecimal(0.5).setScale(2, ROUND_HALF_DOWN));
            req.setLogisticId(order.getOrderNo());
            Items items = new Items();
            CEB311Message message = (CEB311Message) order.getBody();
            if (nonNull(message)) {
                Order msgOrder = message.getOrder();
                if (nonNull(msgOrder)) {
                    OrderHead oh = msgOrder.getOrderHead();
                    if (nonNull(oh)) {
                        try {
                            req.setOrderDate(parse(oh.getAppTime(), "yyyyMMddHHmmss"));
                        } catch (ParseException ex) {
                            LOG.error("failed to parse apply date:{}", ex.getMessage());
                        }
                        Receiver receiver = oh.getReceiver();
                        if (nonNull(receiver)) {
                            req.setReceiverProv(receiver.getProvince());
                            req.setReceiverPostcode(receiver.getPostCode());
                            req.setReceiverCity(receiver.getCity());
                            req.setReceiverCityCode("");
                            req.setReceiverArea(receiver.getArea());
                            req.setReceiverAreaCode("");
                            req.setReceiverTown("");
                            req.setReceiverTownCode("");
                            req.setReceiverAddress(receiver.getAddress());
                            req.setReceiverMobile(receiver.getMobile());
                            req.setReceiverName(receiver.getName());
                            req.setReceiverPhone(receiver.getTel());
                        }
                        OtherInfo otherInfo = new OtherInfo();
                        otherInfo.setAppType(INSERT.getKey());
                        otherInfo.setFreight(nonNull(oh.getFreight()) ? oh.getFreight().setScale(2, ROUND_HALF_DOWN) : BigDecimal.ZERO);
                        otherInfo.setInsuredFee(nonNull(oh.getTaxTotal()) ? oh.getTaxTotal().setScale(2, ROUND_HALF_DOWN) : BigDecimal.ZERO);
                        otherInfo.setCurrency(CNY.getKey());
                        otherInfo.setPackNo(1);
                        otherInfo.setGoodsInfo(oh.getNote());
                        items.setOtherInfo(otherInfo);
                    } else {
                        LOG.warn("order head is empty, it's weired!");
                    }
                } else {
                    LOG.warn("order in message is empty, it's weired!");
                }
            } else {
                LOG.warn("message is empty, it's weired!");
            }
            req.setItems(items);
            if (nonNull(sender)) {
                req.setSenderAddress(sender.getAddress());
                req.setSenderProv(sender.getProvince());
                req.setSenderProvCode("");
                req.setSenderCity(sender.getCity());
                req.setSenderCityCode("");
                req.setSenderArea(sender.getArea());
                req.setSenderAreaCode("");
                req.setSenderTown("");
                req.setSenderTownCode("");
                req.setSenderMobile(sender.getMobile());
                req.setSenderName(sender.getName());
                req.setSenderPhone(sender.getTel());
                req.setSenderPostcode(sender.getPostCode());
            }else{
                LOG.warn("sender is empty, it's weired!");
            }
        }
        return req;
    }

    public static StoKjOrderResponse sendReq(StoKjOrderRequest request, RestTemplate template, WuliuKjPlat plat, StoKjProperties props) {
        StoKjOrderResponse resp = null;
        if (nonNull(request) && nonNull(template) && nonNull(plat) && nonNull(props)) {
            try {
                request.setCustomerCode(trim(plat.getCustomerCode()));
                request.setCustomerName(trim(plat.getCustomerName()));
                request.setSiteCode(trim(plat.getSiteCode()));
                request.setSiteName(trim(plat.getSiteName()));
                request.setOrderSource(trim(plat.getPartnerCode()));
                long timestamp = Calendar.getInstance().getTimeInMillis();
                String logisticsInfo = trim(OBJECT_MAPPER.writeValueAsString(request));
                LOG.info("logisticsInfo:{}", logisticsInfo);
                StringBuilder sb = new StringBuilder(trim(plat.getSecretKey()));
                sb.append("logisticsInfo").append(logisticsInfo);
                sb.append("partnerCode").append(trim(plat.getPartnerCode()));
                sb.append("timestamp").append(timestamp);
                sb.append(trim(plat.getSecretKey()));
                HttpEntity<MultiValueMap<String, Object>> reqEntity = new HttpEntity<>(generateKjHeader());
                Map<String, String> order = props.getOrder();
                StringBuilder urlSb = new StringBuilder(plat.getUrl());
                urlSb.append(MapUtils.isNotEmpty(order) ? ":" + order.get("port") : "");
                String url = assembleUrl(urlSb.toString(), (isNotEmpty(order) && isNotBlank(order.get("path")) ? order.get("path") : ""));
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                        .queryParam("logisticsInfo", logisticsInfo)
                        .queryParam("partnerCode", trim(plat.getPartnerCode()))
                        .queryParam("timestamp", timestamp)
                        .queryParam("signer", upperCase(sha1Hex(trim(sb.toString()))));
                ResponseEntity<StoKjOrderResponse> response = template.exchange(builder.build().encode().toUri(), POST, reqEntity, StoKjOrderResponse.class);
                resp = nonNull(response) ? response.getBody() : null;
            } catch (JsonProcessingException ex) {
                LOG.error("failed to cvt order req bean to string:{}", ex.getMessage());
            }
        }
        return resp;
    }

    public static StoKjTraceResponse trace(StoKjTraceRequest request, RestTemplate template, WuliuKjPlat plat, StoKjProperties props) {
        StoKjTraceResponse resp = null;
        if (nonNull(request) && nonNull(template) && nonNull(plat) && nonNull(props)) {
            try {
                request.setPartnerCode(trim(plat.getPartnerCode()));
                String logisticsInfo = trim(OBJECT_MAPPER.writeValueAsString(request));
                LOG.info("logisticsInfo:{}", logisticsInfo);
                StringBuilder sb = new StringBuilder(trim(plat.getSecretKey()));
                sb.append("logisticsInfo").append(logisticsInfo);
                sb.append("partnerCode").append(trim(plat.getPartnerCode()));
                sb.append(trim(plat.getSecretKey()));
                HttpEntity<MultiValueMap<String, Object>> reqEntity = new HttpEntity<>(generateKjHeader());
                Map<String, String> trace = props.getTrace();
                StringBuilder urlSb = new StringBuilder(plat.getUrl());
                urlSb.append(MapUtils.isNotEmpty(trace) ? ":" + trace.get("port") : "");
                String url = assembleUrl(urlSb.toString(), (isNotEmpty(trace) && isNotBlank(trace.get("path")) ? trace.get("path") : ""));
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                        .queryParam("logisticsInfo", logisticsInfo)
                        .queryParam("partnerCode", trim(plat.getPartnerCode()))
                        .queryParam("signer", upperCase(sha1Hex(trim(sb.toString()))));
                ResponseEntity<StoKjTraceResponse> response = template.exchange(builder.build().encode().toUri(), POST, reqEntity, StoKjTraceResponse.class);
                resp = nonNull(response) ? response.getBody() : null;
            } catch (JsonProcessingException ex) {
                LOG.error("failed to cvt trace req bean to string:{}", ex.getMessage());
            }
        }
        return resp;
    }
}
