/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.web.core.factory;

import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.yz.service.YouzanKdtService;
import com.ruoyi.yz.service.intf.Enterprise;
import static com.ruoyi.common.enums.Customer.YOUZAN;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 *
 * @author wmao
 */
public final class EnterpriseFactory {
    public static Enterprise getServiceByPlatName(String platName){
        Enterprise obj = null;
        if(isNotBlank(platName)){
            if(containsIgnoreCase(platName, YOUZAN.name())){
                obj = SpringUtils.getBean(YouzanKdtService.class);
            }
        }
        return obj;
    }
}
