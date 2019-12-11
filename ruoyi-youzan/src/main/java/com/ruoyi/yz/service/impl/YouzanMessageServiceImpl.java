/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service.impl;

import com.ruoyi.yz.domain.YouzanAuthMessage;
import com.ruoyi.yz.domain.YouzanMessage;
import com.ruoyi.yz.domain.YouzanSubscribeMessage;
import com.ruoyi.yz.enums.MessageType;
import static com.ruoyi.yz.enums.MessageType.APP_AUTH;
import static com.ruoyi.yz.enums.MessageType.APP_SUBSCRIBE;
import static com.ruoyi.yz.enums.MessageType.geByName;
import com.ruoyi.yz.mapper.YouzanAuthMessageMapper;
import com.ruoyi.yz.mapper.YouzanSubscribeMessageMapper;
import com.ruoyi.yz.service.YouzanMessageService;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author wmao
 */
@Service("youzanMessageService")
@Transactional
public class YouzanMessageServiceImpl implements YouzanMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(YouzanMessageServiceImpl.class);

    @Autowired
    private YouzanAuthMessageMapper youzanAuthMessageMapper;

    @Autowired
    private YouzanSubscribeMessageMapper youzanSubscribeMessageMapper;

    @Override
    public int insert(YouzanMessage message) {
        int ret = -1;
        if (nonNull(message)) {
            MessageType msgType = geByName(message.getType());
            if (nonNull(msgType)) {
                switch (msgType) {
                    case APP_AUTH:
                        ret = youzanAuthMessageMapper.insert((YouzanAuthMessage) message);
                        break;
                    case APP_SUBSCRIBE:
                        ret = youzanSubscribeMessageMapper.insert((YouzanSubscribeMessage) message);
                        break;
                    default:
                        LOG.warn("it's invalid message, pls check it:" + message);
                }
            }
        }
        return ret;
    }

    @Override
    public YouzanMessage getOne(String kdtId) {
        return isNotBlank(kdtId) ? youzanAuthMessageMapper.getOne(kdtId) : null;
    }

}
