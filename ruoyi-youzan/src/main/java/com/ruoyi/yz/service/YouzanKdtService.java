/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service;

import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.yz.domain.YouzanKdt;
import com.ruoyi.yz.domain.YouzanUserToken;
import com.ruoyi.yz.service.intf.Enterprise;
import com.youzan.cloud.open.sdk.core.oauth.model.OAuthToken;
import java.util.List;

/**
 *
 * @author wmao
 */
public interface YouzanKdtService extends Enterprise{
    
    int insert(YouzanKdt kdt);
    
    int update(YouzanKdt kdt);
    
    YouzanKdt fill(YouzanKdt kdt, OAuthToken token); 
    
    YouzanKdt getKdtById(String id);
    
    YouzanKdt getOneByAuthId(String authId);
    
    int updateYouzanKdtByUserToken(YouzanUserToken token, SysUser sysUser);
    
    List<YouzanKdt> getAvailKdts();
    
    void refreshToken();
    
    List<YouzanKdt> getList(YouzanKdt kdt);
    
    int remove(String[] ids) throws BusinessException;
    
    int recovery(String[] ids) throws BusinessException;
}
