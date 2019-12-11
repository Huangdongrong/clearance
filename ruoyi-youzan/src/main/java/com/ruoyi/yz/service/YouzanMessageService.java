/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service;

import com.ruoyi.yz.domain.YouzanMessage;

/**
 *
 * @author wmao
 */
public interface YouzanMessageService{

    int insert(YouzanMessage message);
    
    YouzanMessage getOne(String kdtId);
}
