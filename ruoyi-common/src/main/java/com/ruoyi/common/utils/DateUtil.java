/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static java.util.Objects.isNull;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author wmao
 */
public class DateUtil {

    private static final Logger LOG = LoggerFactory.getLogger(DateUtil.class);

    public static final String STANDARD_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final ThreadLocal<Map<String, SimpleDateFormat>> DATE_FORMAT_MAP = new ThreadLocal<Map<String, SimpleDateFormat>>() {
        @Override
        protected Map<String, SimpleDateFormat> initialValue() {
            return new HashMap<>();
        }
    };

    private static SimpleDateFormat getSimpleDateFormat(final String pattern) {
        Map<String, SimpleDateFormat> map = DATE_FORMAT_MAP.get();
        SimpleDateFormat format = map.get(pattern);
        if (isNull(format)) {
            format = new SimpleDateFormat(pattern);
            format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            map.put(pattern, format);
        }
        return format;
    }

    public static String format(String date, String pattern) {
        return getSimpleDateFormat(pattern).format(date);
    }

    public static String format(Date date, String pattern) {
        return getSimpleDateFormat(pattern).format(date);
    }

    public static String format(Date date) {
        return format(date, STANDARD_DATE_PATTERN);
    }

    public static String format(String date) {
        return format(date, STANDARD_DATE_PATTERN);
    }

    public static Date parse(String date, String pattern) throws ParseException {
        return getSimpleDateFormat(pattern).parse(date);
    }

    public static Date parse(Date date) throws ParseException {
        return parse(date, STANDARD_DATE_PATTERN);
    }

    public static Date parse(Date date, String pattern) throws ParseException {
        return parse(format(date, pattern), pattern);
    }

    public static Date addDays(Date date, int inc) {
        return DateUtils.addDays(date, inc);
    }

    public static Date fromMillise(long milliseconds) throws ParseException {
        return parse(new Date(milliseconds), STANDARD_DATE_PATTERN);
    }
}
