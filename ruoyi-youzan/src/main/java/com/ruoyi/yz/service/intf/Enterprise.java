/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service.intf;

import com.ruoyi.yz.wuliu.Sender;

/**
 *
 * @author wmao
 */
public interface Enterprise {
    int updateAddress(String kdtId, Sender sender);
    
    Sender getSenderByKdtId(String kdtId);
}
