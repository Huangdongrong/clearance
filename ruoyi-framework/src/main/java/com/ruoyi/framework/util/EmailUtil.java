/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.framework.util;

import static com.ruoyi.common.utils.StringUtils.trim;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.framework.config.EmailConfig;
import io.github.biezhi.ome.OhMyEmail;
import io.github.biezhi.ome.SendMailException;
import java.io.StringWriter;
import java.util.Map;
import jetbrick.template.JetEngine;
import jetbrick.template.JetTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static io.github.biezhi.ome.OhMyEmail.SMTP_ENT_QQ;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.MapUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 *
 * @author wmao
 */
public final class EmailUtil {
    
    private static final Logger LOG = LoggerFactory.getLogger(EmailUtil.class);
    
    private static final EmailConfig EMAIL_CONFIG;
    
    static {
        EMAIL_CONFIG = SpringUtils.getBean(EmailConfig.class);
        OhMyEmail.config(SMTP_ENT_QQ(false), trim(EMAIL_CONFIG.getAddress()), trim(EMAIL_CONFIG.getPasswd()));
    }
    
    public static void sendEmail(String template, String subject, Map<String, Object> ctntMap, String toAddr) {
        if (isNotBlank(template) && isNotBlank(subject) && isNotBlank(toAddr) && isNotEmpty(ctntMap)) {
            JetEngine engine = JetEngine.create();
            JetTemplate jt = engine.getTemplate(template);
            if (nonNull(jt)) {
                try {
                    StringWriter writer = new StringWriter();
                    jt.render(ctntMap, writer);
                    OhMyEmail.subject(subject)
                            .from(EMAIL_CONFIG.getName())
                            .to(trim(toAddr))
                            .html(writer.toString())
                            .send();
                } catch (SendMailException ex) {
                    LOG.error("failed to send email to {}, ex:{}", toAddr, ex.getMessage());
                }
            } else {
                LOG.warn("can't get valid template:{}", template);
            }
        } else {
            LOG.warn("subject:{}, toAddr:{} or content:{} is empty", subject, toAddr, ctntMap);
        }
    }
}
