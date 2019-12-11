/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author wmao
 */
public class ChineseCharacterUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ChineseCharacterUtil.class);

    public static String convertHanzi2Pinyin(String hanzi, boolean full) {
        String regExp = "^[\u4E00-\u9FFF]+$";
        StringBuilder sb = new StringBuilder();
        if (isNotBlank(hanzi)) {
            for (int i = 0, j = hanzi.length(); i < j; i++) {
                char unit = hanzi.charAt(i);
                if (match(String.valueOf(unit), regExp)) {
                    String pinyin = convertSingleHanzi2Pinyin(unit);
                    if (full) {
                        sb.append(pinyin);
                    } else {
                        sb.append(pinyin.charAt(0));
                    }
                } else {
                    sb.append(unit);
                }
            }
        }
        return sb.toString();

    }

    private static String convertSingleHanzi2Pinyin(char hanzi) {
        HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
        outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        String[] res;
        StringBuilder sb = new StringBuilder();
        try {
            res = PinyinHelper.toHanyuPinyinStringArray(hanzi, outputFormat);
            sb.append(res[0]);
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
            return "";
        }
        return sb.toString();
    }

    public static boolean match(String str, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    private static String cnHashCode(String kdtCnName) {
        StringBuilder sb = new StringBuilder();
        if (isNotBlank(kdtCnName)) {
            char[] chinese = kdtCnName.toCharArray();
            for (int i = 0; i < chinese.length; i++) {
                System.out.print((int) chinese[i] + " ");
                sb.append((int) chinese[i]).append(",");
            }
        }
        return sb.toString();
    }

    public static String merge(String str1, String str2) {
        if (isBlank(str1) || isBlank(str2)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int i = -1;
        while (i++ < str1.length() - 1 || i < str2.length()) {
            sb.append(i < str1.length() ? str1.charAt(i) + "" : "").append(i < str2.length() ? str2.charAt(i) : "");
        }
        return sb.toString();

    }
}
