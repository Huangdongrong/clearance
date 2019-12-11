/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.utils;

import static com.ruoyi.common.utils.UuidUtil.getRandomStr;
import static com.ruoyi.common.utils.xml.XmlUtil.convertToXml;
import static com.ruoyi.common.utils.xml.XmlUtil.convertXmlStrToObject;
import static com.ruoyi.common.utils.spring.SpringUtils.getBean;
import com.ruoyi.yz.customs.Message;
import com.ruoyi.yz.customs.support.CustomsPreferredMapper;
import com.ruoyi.yz.domain.CustomsPlat;
import static com.ruoyi.yz.utils.WuliuUtil.generateKjHeader;
import static java.util.Objects.nonNull;
import org.apache.commons.lang.StringUtils;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author wmao
 */
public final class CustomsUtil {

    private static final Logger LOG = LoggerFactory.getLogger(CustomsUtil.class);

    private static final String DATA_STYLE = "xml";

    private static final String DETAILS_TIME_PATTERN = "yyyyMMddHHmmss";

    public static Object sendReq(Message request, CustomsPlat plat, Class<? extends Message> clazz) {
        Object resp = null;
        if (nonNull(request) && nonNull(plat)) {
            HttpHeaders headers = generateKjHeader();
            headers.setContentType(APPLICATION_FORM_URLENCODED);
            String data = convertToXml(request, new CustomsPreferredMapper());
            LOG.info("request data:{}", data);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(plat.getUrl())
                    .queryParam(DATA_STYLE, data);
            RestTemplate template = (RestTemplate) getBean("restTemplate");
            if (nonNull(template)) {
                String response = template.postForObject(builder.build().encode().toUri(), new HttpEntity<>(headers), String.class);
                LOG.info("response:{}", response);
                if (isNotBlank(response)) {
                    resp = convertXmlStrToObject(clazz, response);
                } else {
                    LOG.error("failed to get create response from yunda platform:{}", request);
                }
            }
        }
        return resp;
    }

    public static String getCopNoByTid(String tid) {
        String copNo = null;
        tid = trimToEmpty(tid);
        if (isNotBlank(tid)) {
            int end = tid.length() > 15 ? 15 : tid.length();
            copNo = StringUtils.substring(tid, 0, end);
            copNo += getRandomStr(5);
        }
        return copNo;
    }
}
