/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service;

import com.ruoyi.yz.wuliu.ydkj.apply.YdApplyRequest;
import com.ruoyi.yz.wuliu.ydkj.apply.YdApplyResponses;
import com.ruoyi.yz.wuliu.ydkj.create.YdCreateRequest;
import com.ruoyi.yz.wuliu.ydkj.create.YdCreateResponses;
import com.ruoyi.yz.wuliu.ydkj.query.YdQueryRequest;
import com.ruoyi.yz.wuliu.ydkj.query.YdQueryResponses;
import com.ruoyi.yz.wuliu.ydkj.update.YdUpdateRequest;
import com.ruoyi.yz.wuliu.ydkj.update.YdUpdateResponses;

/**
 *
 * @author wmao
 */
public interface YundaKjOrderService {
    
    YdCreateResponses sendRequest(YdCreateRequest request);
    
    YdUpdateResponses updateRequest(YdUpdateRequest request);
    
    YdQueryResponses findTrail(YdQueryRequest request);
    
    YdApplyResponses sendRequest(YdApplyRequest request);
    
}
