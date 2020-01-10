/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service.impl;

import com.ruoyi.yz.config.YundaKjProperties;
import com.ruoyi.yz.config.YundaProxyCustomsProperties;
import com.ruoyi.yz.domain.WuliuKjPlat;
import com.ruoyi.yz.domain.CustomsPlat;
import static com.ruoyi.yz.enums.WuliuComp.YUNDA;
import com.ruoyi.yz.mapper.CustomsPlatMapper;
import com.ruoyi.yz.mapper.WuliuKjPlatMapper;
import com.ruoyi.yz.service.YundaKjOrderService;
import static com.ruoyi.yz.utils.YundaUtil.applyReq;
import static com.ruoyi.yz.utils.YundaUtil.sendReq;
import static com.ruoyi.yz.utils.YundaUtil.trace;
import static com.ruoyi.yz.utils.YundaUtil.updateReq;
import com.ruoyi.yz.wuliu.ydkj.apply.YdApplyRequest;
import com.ruoyi.yz.wuliu.ydkj.apply.YdApplyResponses;
import com.ruoyi.yz.wuliu.ydkj.create.YdCreateRequest;
import com.ruoyi.yz.wuliu.ydkj.create.YdCreateResponses;
import com.ruoyi.yz.wuliu.ydkj.query.YdQueryRequest;
import com.ruoyi.yz.wuliu.ydkj.query.YdQueryResponses;
import com.ruoyi.yz.wuliu.ydkj.update.YdUpdateRequest;
import com.ruoyi.yz.wuliu.ydkj.update.YdUpdateResponses;
import static java.util.Objects.nonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author wmao
 */
@Service("yundaKjOrderService")
@Transactional
public class YundaKjOrderServiceImpl implements YundaKjOrderService {

    @Autowired
    private YundaProxyCustomsProperties yundaProxyCustomsProperties;

    @Autowired
    private CustomsPlatMapper customsPlatMapper;
    
    @Autowired
    private YundaKjProperties yundaKjProperties;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WuliuKjPlatMapper wuliuKjPlatMapper;

    @Override
    public YdCreateResponses sendRequest(YdCreateRequest request) {
        YdCreateResponses resp = null;
        if (nonNull(request)) {
            WuliuKjPlat plat = wuliuKjPlatMapper.getOne(YUNDA.name());
            if (nonNull(plat)) {
                resp = sendReq(request, restTemplate, plat, yundaKjProperties);
            }
        }
        return resp;
    }

    @Override
    public YdUpdateResponses updateRequest(YdUpdateRequest request) {
        YdUpdateResponses resp = null;
        if (nonNull(request)) {
            WuliuKjPlat plat = wuliuKjPlatMapper.getOne(YUNDA.name());
            if (nonNull(plat)) {
                resp = updateReq(request, restTemplate, plat, yundaKjProperties);
            }
        }
        return resp;
    }

    @Override
    public YdQueryResponses findTrail(YdQueryRequest request) {
        YdQueryResponses resp = null;
        if (nonNull(request)) {
            WuliuKjPlat plat = wuliuKjPlatMapper.getOne(YUNDA.name());
            if (nonNull(plat)) {
                resp = trace(request, restTemplate, plat, yundaKjProperties);
            }
        }
        return resp;
    }

    @Override
    public YdApplyResponses sendRequest(YdApplyRequest request) {
        YdApplyResponses resp = null;
        if (nonNull(request)) {
            CustomsPlat plat = customsPlatMapper.getOneByDistrict(YUNDA.name());
            if (nonNull(plat)) {
                resp = applyReq(request, restTemplate, plat, yundaProxyCustomsProperties);
            }
        }
        return resp;
    }
}
