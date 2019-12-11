/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service.thread;

import static com.ruoyi.common.constant.Constants.PLAT_DOMAIN;
import static com.ruoyi.framework.util.EmailUtil.sendEmail;
import com.ruoyi.yz.domain.YouzanKdt;
import com.ruoyi.yz.wuliu.Sender;
import java.util.HashMap;
import java.util.Map;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trim;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author wmao
 */
public class SendEmailThread implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(SendEmailThread.class);

    private static final String EMAIL_SUBJECT = "%s有新的订单%s需要清关，请抓紧分配物流";

    private final String orderNo;

    private final YouzanKdt kdt;

    public SendEmailThread(YouzanKdt kdt, String orderNo) {
        this.kdt = kdt;
        this.orderNo = orderNo;
    }

    @Override
    public void run() {
        if (nonNull(kdt) && isNotBlank(orderNo)) {
            Sender sender = kdt.getSender();
            if (nonNull(sender)) {
                String kdtName = trim(kdt.getName());
                LOG.info("send email -> sender:{}, orderNo:{}, kdtName:{}", sender.getName(), orderNo, kdtName);
                Map<String, Object> ctntObjs = new HashMap<>();
                String custom = trim(kdtName);
                ctntObjs.put("username", "青马小二");
                ctntObjs.put("custom", custom);
                ctntObjs.put("url", PLAT_DOMAIN);
                String subject = String.format(EMAIL_SUBJECT, custom, orderNo);
                sendEmail("/email/nrcdnty.jetx", subject, ctntObjs, nonNull(sender) ? trim(sender.getEmail()) : "");
            }
        }
    }

}
