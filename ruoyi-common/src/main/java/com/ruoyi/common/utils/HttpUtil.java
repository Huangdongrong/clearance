/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.common.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author wmao
 */
public final class HttpUtil {

    private static final int COOKIE_MAX_AGE = 60 * 60;

    public static void updateCookie(HttpServletResponse resp, String key, String value) {
        Cookie cookie = new Cookie(key, DesUtil.encode(value));
        cookie.setHttpOnly(true);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        resp.addCookie(cookie);
    }
}
