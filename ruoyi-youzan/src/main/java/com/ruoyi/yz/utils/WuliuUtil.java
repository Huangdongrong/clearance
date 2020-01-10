/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.utils;

import static com.ruoyi.common.utils.UuidUtil.get32UUID;
import com.ruoyi.yz.domain.WuliuPlat;
import java.util.Calendar;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.endsWithIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;
import static org.apache.commons.lang3.StringUtils.trim;
import org.springframework.http.HttpHeaders;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.util.DigestUtils;

/**
 *
 * @author wmao
 */
public final class WuliuUtil {

    public static HttpHeaders generateKjHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("nonce", lowerCase(get32UUID()));
        headers.add("timestamp", "" + Calendar.getInstance().getTimeInMillis());
        headers.setContentType(APPLICATION_FORM_URLENCODED);
        return headers;
    }

    public static HttpHeaders generateHeader(String content, boolean isJson, WuliuPlat plat) {
        HttpHeaders headers = null;
        if (nonNull(plat)) {
            headers = new HttpHeaders();
            headers.add("appid", plat.getAppKey());
            headers.add("secretKey", plat.getAppSecret());
            String nonce = lowerCase(get32UUID());
            headers.add("nonce", nonce);
            String timestamp = "" + Calendar.getInstance().getTimeInMillis();
            headers.add("timestamp", timestamp);
            String ctnt = content + timestamp + nonce + plat.getAppSecret();
            String signature = DigestUtils.md5DigestAsHex(ctnt.getBytes());
            headers.add("signature", signature);
            if (isJson) {
                headers.setContentType(APPLICATION_JSON);
            }
        }
        return headers;
    }

    public static String assembleUrl(String prefixUrl, String reqUrl) {
        String url = null;
        if (isNotBlank(prefixUrl) && nonNull(reqUrl)) {
            reqUrl = trim(reqUrl);
            String urlPrefix = trim(prefixUrl);
            if (isNotBlank(urlPrefix)) {
                if (endsWithIgnoreCase(urlPrefix, "/")) {
                    urlPrefix = substringBeforeLast(urlPrefix, "/");
                }
                if (!startsWithIgnoreCase(reqUrl, "/")) {
                    reqUrl += "/";
                }
                url = urlPrefix + reqUrl;
            }
        }
        return url;
    }
}
