/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service.impl;

import com.ruoyi.yz.config.StoKjProperties;
import com.ruoyi.yz.domain.WuliuKjPlat;
import static com.ruoyi.yz.enums.WuliuComp.STO;
import com.ruoyi.yz.mapper.WuliuKjPlatMapper;
import com.ruoyi.yz.service.StoKjOrderService;
import com.ruoyi.yz.wuliu.stokj.order.StoKjOrderRequest;
import com.ruoyi.yz.wuliu.stokj.order.StoKjOrderResponse;
import com.ruoyi.yz.wuliu.stokj.trace.StoKjTraceRequest;
import com.ruoyi.yz.wuliu.stokj.trace.StoKjTraceResponse;
import static com.ruoyi.yz.utils.StoUtil.sendReq;
import static com.ruoyi.yz.utils.StoUtil.trace;
import static java.util.Objects.nonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author wmao
 */
@Service("stoKjOrderService")
@Transactional
public class StoKjOrderServiceImpl implements StoKjOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(StoKjOrderServiceImpl.class);

    @Autowired
    private StoKjProperties stoKjProperties;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WuliuKjPlatMapper wuliuKjPlatMapper;

    @Override
    public StoKjOrderResponse sendRequest(StoKjOrderRequest request) {
        StoKjOrderResponse resp = null;
        if (nonNull(request)) {
            WuliuKjPlat plat = wuliuKjPlatMapper.getOne(STO.name());
            if (nonNull(plat)) {
                resp = sendReq(request, restTemplate, plat, stoKjProperties);
            }
        }
        return resp;
    }

    @Override
    public StoKjTraceResponse findTrail(StoKjTraceRequest request) {
        StoKjTraceResponse resp = null;
        if (nonNull(request)) {
            WuliuKjPlat plat = wuliuKjPlatMapper.getOne(STO.name());
            if (nonNull(plat)) {
                resp = trace(request, restTemplate, plat, stoKjProperties);
            }
        }
        return resp;
    }

}
