/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.framework.web.http;

import static com.ruoyi.common.utils.StringUtils.isNumeric;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.MapUtils;
import org.springframework.aop.framework.Advised;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.StringUtils.upperCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author wmao
 */
public class InvokeAnnotationInterceptor extends HandlerInterceptorAdapter {
    
    private static final Logger LOG = LoggerFactory.getLogger(InvokeAnnotationInterceptor.class);

    private static final String CHARSET = "utf-8";

    private static final String PARAM_SIGN = "sign";

    private static final String PARAM_TIME = "time";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean ret = true;
        Invoke invoke = getInvokeAnnotation(handler);
            LOG.info("invoke:{}", invoke);
        if (invoke != null) {
            long currentTime = Calendar.getInstance().getTimeInMillis();
            String method = trim(request.getMethod());
            LOG.info("method:{}", method);
            String sign = null;
            String timeParam = null;
            long timestamp = 0L;
            if (isNotBlank(method)) {
                switch (upperCase(method)) {
                    case "GET":
                        sign = request.getParameter(PARAM_SIGN);
                        timeParam = request.getParameter(PARAM_TIME);
                        if(isNumeric(timeParam)){
                            timestamp = Long.parseLong(timeParam);
                        }else{
                            timestamp = -1L;
                            LOG.warn("timestamp is not long, pls check it, sign:{}, timestamp:{}", sign, timeParam);
                        }
                        break;
                    case "POST":
                        Map<String, String> formParams = getFormParams(request);
                        sign = formParams.get(PARAM_SIGN);
                        timeParam = formParams.get(PARAM_TIME);
                        if(isNumeric(timeParam)){
                            timestamp = Long.parseLong(timeParam);
                        }else{
                            timestamp = -1L;
                            LOG.warn("timestamp is not long, pls check it, sign:{}, timestamp:{}", sign, timeParam);
                        }
                        break;
                    default:
                        return false;
                }
            }

            if (isNotBlank(sign) && (timestamp >= (currentTime - invoke.drift()) && timestamp <= (currentTime + invoke.drift()))) {
                ret = equalsIgnoreCase(md5Hex(getContentBytes("" + timestamp + invoke.token(), CHARSET)), sign);
            } else {
                ret = false;
            }
        }
        return ret;
    }

    private byte[] getContentBytes(String content, String charset) {
        if (isBlank(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("failed to sign by md5, charset is:" + charset);
        }
    }

    private Map<String, String> getFormParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> map = request.getParameterMap();
        if (MapUtils.isNotEmpty(map)) {
            Set<String> keys = map.keySet();
            keys.forEach((key) -> {
                params.put(key, map.get(key)[0]);
            });
        }
        return params;
    }

    private Invoke getInvokeAnnotation(Object handler) {
        Invoke invoke = null;
        if (handler != null && (handler instanceof HandlerMethod)) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Object target = getTargetObject(handlerMethod.getBean());
            if (target.getClass().isAnnotationPresent(Invoke.class)) {
                invoke = target.getClass().getAnnotation(Invoke.class);
            } else {
                invoke = handlerMethod.getMethodAnnotation(Invoke.class);
            }
        }
        return invoke;
    }

    private Object getTargetObject(Object proxy) {
        Object target = null;
        try {
            if (proxy instanceof Advised) {
                target = ((Advised) proxy).getTargetSource().getTarget();
            } else {
                target = proxy;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return target;
    }
}
