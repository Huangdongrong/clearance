/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.common.utils;

import java.util.Random;
import java.util.UUID;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.RegExUtils.replaceAll;
import static org.apache.commons.lang3.StringUtils.upperCase;

public class UuidUtil {

    private static final String SAMPLE = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    /**
     * 封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.
     *
     * @return
     */
    public static String get32UUID() {
        return replaceAll(trim(gen36UUID()), "-", "");
    }

    /**
     * 封装JDK自带的UUID, 通过Random数字生成, 中间有-分割.
     *
     * @return
     */
    public static String gen36UUID() {
        return upperCase(UUID.randomUUID().toString());
    }

    public static String getRandom(int length) {
        double randNum = Math.random();
        String randStr = String.valueOf(randNum).replace("0.", "");
        return randStr.substring(0, length);
    }

    public static String getRandomStr(int length) {
        int sampleLen = SAMPLE.length();
        StringBuilder sb = new StringBuilder();
        for (int r = 0; length > r; ++r) {
            sb.append(SAMPLE.charAt(new Random().nextInt(sampleLen)));
        }
        return sb.toString();
    }
}
