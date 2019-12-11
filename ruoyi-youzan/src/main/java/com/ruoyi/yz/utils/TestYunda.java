/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.utils;

import com.ruoyi.common.utils.security.Base64Util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wmao
 */
public class TestYunda {

    public static void testOrderCreate() {
        try {
            Map<String, String> requestMap = new HashMap<>();
            String buz_type = "partner";
            String data_style = "xml";
            String method = "global_order_create";
            String tradeId = "1612140001";
            String version = "1.0";
            String validation = "buz_type" + buz_type + "data_style" + data_style + "traderId" + tradeId + "version" + version + "360588EE1A5F2720B58C50DF9B3AAE58";
            System.out.println("validation:" + validation);
            validation = md5(validation);
            System.out.println("security:" + validation);
            String data = "<beans><req_type>create_order</req_type><hawbs><hawb><mail_no></mail_no><hawbno>123131</hawbno><pre_express></pre_express><next_express></next_express><fcountry>KR</fcountry><tcountry>CN</tcountry><infor_origin></infor_origin><sent_type></sent_type><receiver><company>张大</company><contacts>张大</contacts><city>SHA</city><postal_code>210081</postal_code><address>上海市青浦区盈港东路6679号</address><o_address></o_address><res_signal>1</res_signal><rec_tele>021-12126565</rec_tele><e_mail>test@163.com</e_mail></receiver><sender><company>路西</company><city>IN</city><contacts>路西</contacts><address>上海市青浦区崧复路发件</address><sender_tele>18723234323</sender_tele><postal_code>000098</postal_code><e_mail>luxi@163.com</e_mail></sender><insurance_fee>0</insurance_fee><goods_money>0</goods_money><certificate_id>0</certificate_id><tax_no>0</tax_no><tax_rate>0</tax_rate><paytax_man>test</paytax_man><special>0</special><currency>0</currency><service>1</service><request></request><remark>备</remark><vat_service></vat_service><goods_list><goods><name>路路</name><hs_code>TEST</hs_code><unit_price>0</unit_price><act_weight>0</act_weight><dim_weight>0</dim_weight><quantity>0</quantity></goods></goods_list></hawb><hawb><mail_no>7700045273655</mail_no><hawbno></hawbno><pre_express></pre_express><next_express></next_express><fcountry>KR</fcountry><tcountry>CN</tcountry><infor_origin></infor_origin><sent_type></sent_type><receiver><company>张大</company><contacts>张大</contacts><city>SHA</city><postal_code>210081</postal_code><address>上海市青浦区盈港东路6679号</address><o_address></o_address><res_signal>1</res_signal><rec_tele>021-12126565</rec_tele><e_mail>test@163.com</e_mail></receiver><sender><company>路西</company><city>IN</city><contacts>路西</contacts><address>上海市青浦区崧复路发件</address><sender_tele>18723234323</sender_tele><postal_code>000098</postal_code><e_mail>luxi@163.com</e_mail></sender><insurance_fee>0</insurance_fee><goods_money>0</goods_money><certificate_id>0</certificate_id><tax_no>0</tax_no><tax_rate>0</tax_rate><paytax_man>test</paytax_man><special>0</special><currency>0</currency><service>1</service><request></request><remark>备</remark><vat_service></vat_service><goods_list><goods><name>路路</name><hs_code>TEST</hs_code><unit_price>0</unit_price><act_weight>0</act_weight><dim_weight>0</dim_weight><quantity>0</quantity></goods></goods_list></hawb></hawbs></beans>";
            data = Base64Util.encode(data.getBytes("UTF-8"));
            requestMap.put("buz_type", buz_type);
            requestMap.put("data_style", data_style);
            requestMap.put("method", method);
            requestMap.put("traderId", tradeId);
            requestMap.put("version", version);
            requestMap.put("validation", validation);
            requestMap.put("data", data);
            System.out.println(requestMap);
            //String result = PostUtils.call(requestMap, "http://116.228.72.137:16120/ydgos/peripheral/doHandle.jspx");
            //System.out.println("result：" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String md5(String source) throws NoSuchAlgorithmException {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(source.getBytes());
        byte[] tmp = md.digest();
        char[] str = new char[16 * 2];
        int k = 0;
        for (int i = 0; i < 16; i++) {
            byte byte0 = tmp[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        TestYunda.testOrderCreate();
    }
}
