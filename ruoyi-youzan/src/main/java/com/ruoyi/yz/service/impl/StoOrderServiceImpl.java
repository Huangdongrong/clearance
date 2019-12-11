/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service.impl;

import static com.ruoyi.common.utils.JsonUtil.stringify;
import static com.ruoyi.common.utils.spring.SpringUtils.getBean;
import com.ruoyi.yz.customs.Message;
import com.ruoyi.yz.domain.CustomsPlat;
import com.ruoyi.yz.domain.WuliuPlat;
import static com.ruoyi.yz.enums.Port.CD;
import static com.ruoyi.yz.enums.WuliuComp.STO;
import com.ruoyi.yz.mapper.CustomsPlatMapper;
import com.ruoyi.yz.mapper.WuliuPlatMapper;
import com.ruoyi.yz.service.StoOrderService;
import com.ruoyi.yz.wuliu.sto.order.StoOrderRequest;
import com.ruoyi.yz.wuliu.sto.order.StoOrderResponse;
import com.ruoyi.yz.wuliu.sto.platorder.StoPlatOrderRequest;
import com.ruoyi.yz.wuliu.sto.platorder.StoPlatOrderResponse;
import com.ruoyi.yz.wuliu.sto.trail.StoTrailResponse;
import com.ruoyi.yz.support.YzStoPlatWuliuCustomSupport;
import com.ruoyi.yz.support.YzStoWuliuCustomSupport;
import static com.ruoyi.yz.utils.WuliuUtil.assembleUrl;
import static com.ruoyi.yz.utils.WuliuUtil.generateHeader;
import java.util.List;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.trim;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.IterableUtils.get;

/**
 *
 * @author wmao
 */
@Service("stoOrderService")
@Transactional
public class StoOrderServiceImpl implements StoOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(StoOrderServiceImpl.class);

    private static final String PLAT_ORDER_REQUEST_URL = "/edi-order/platform/order/create";

    private static final String ORDER_REQUEST_URL = "/edi-order/order/create";

    private static final String TRACE_REQUEST_URL = "/edi-trace/trace/list";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WuliuPlatMapper wuliuPlatMapper;

    @Autowired
    private CustomsPlatMapper customsPlatMapper;

    @Override
    public StoPlatOrderResponse sendPlatRequest(StoPlatOrderRequest request) {
        StoPlatOrderResponse resp = null;
        WuliuPlat plat = wuliuPlatMapper.getOne(STO.name());
        if (nonNull(request) && nonNull(plat)) {
            request.setBizId(plat.getBizId());
            request.setBizPwd(plat.getBizPwd());
            request.setBizSiteCode(plat.getBizSiteCode());
            HttpHeaders headers = generateHeader(stringify(request), true, plat);
            HttpEntity<StoPlatOrderRequest> reqEntity = new HttpEntity<>(request, headers);
            String url = assembleUrl(plat.getUrl(), PLAT_ORDER_REQUEST_URL);
            ResponseEntity<StoPlatOrderResponse> response = restTemplate.exchange(url, POST, reqEntity, StoPlatOrderResponse.class);
            resp = nonNull(response) ? response.getBody() : null;
        }
        return resp;
    }

    @Override
    public StoOrderResponse sendRequest(StoOrderRequest request) {
        StoOrderResponse resp = null;
        WuliuPlat plat = wuliuPlatMapper.getOne(STO.name());
        if (nonNull(request) && nonNull(plat)) {
            HttpHeaders headers = generateHeader(stringify(request), true, plat);
            HttpEntity<StoOrderRequest> reqEntity = new HttpEntity<>(request, headers);
            String url = assembleUrl(plat.getUrl(), ORDER_REQUEST_URL);
            ResponseEntity<StoOrderResponse> response = restTemplate.exchange(url, POST, reqEntity, StoOrderResponse.class);
            resp = nonNull(response) ? response.getBody() : null;
        }
        return resp;
    }

    @Override
    public StoTrailResponse findTrail(String... waybillNos) {
        StoTrailResponse resp = null;
        WuliuPlat plat = wuliuPlatMapper.getOne(STO.name());
        if (isNotEmpty(waybillNos) && nonNull(plat)) {
            String url = assembleUrl(plat.getUrl(), TRACE_REQUEST_URL);
            String params = "waybillNos=" + trim(join(waybillNos, ",")) + "&sort=desc";
            HttpHeaders headers = generateHeader(params, false, plat);
            HttpEntity<MultiValueMap<String, String>> reqEntity = new HttpEntity<>(headers);
            ResponseEntity<StoTrailResponse> response = restTemplate.exchange(url + "?" + params, GET, reqEntity, StoTrailResponse.class);
            resp = nonNull(response) ? response.getBody() : null;
        }
        return resp;
    }

    @Override
    public Message cvtStoRes2CustomsMsg(StoOrderRequest request, StoOrderResponse response) {
        Message message = null;
        if (nonNull(response)) {
            CustomsPlat plat = customsPlatMapper.getOneByDistrict(CD.name());
            if (nonNull(plat)) {
                WuliuPlat wlPlat = wuliuPlatMapper.getOne(STO.name());
                YzStoWuliuCustomSupport support = getBean(YzStoWuliuCustomSupport.class);
                support.setPlat(plat);
                support.setReq(request);
                List<Message> messages = support.generateRequest(response, wlPlat);
                if (isNotEmpty(messages)) {
                    message = get(messages, 0);
                }
            }
        }
        return message;
    }

    @Override
    public Message cvtStoPlatRes2CustomsMsg(StoPlatOrderRequest request, StoPlatOrderResponse response) {
        Message message = null;
        if (nonNull(response)) {
            CustomsPlat plat = customsPlatMapper.getOneByDistrict(CD.name());
            if (nonNull(plat)) {
                WuliuPlat wlPlat = wuliuPlatMapper.getOne(STO.name());
                YzStoPlatWuliuCustomSupport support = getBean(YzStoPlatWuliuCustomSupport.class);
                support.setPlat(plat);
                support.setReq(request);
                List<Message> messages = support.generateRequest(response, wlPlat);
                if (isNotEmpty(messages)) {
                    message = get(messages, 0);
                }
            }
        }
        return message;
    }

}
