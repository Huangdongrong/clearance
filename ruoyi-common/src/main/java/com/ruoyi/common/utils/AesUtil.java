/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.common.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static javax.crypto.Cipher.DECRYPT_MODE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;

/**
 * @Author fengyuhao
 * @Date 2019-04-09 20:42
 * https://blog.csdn.net/u011514810/article/details/72725398
 */
public class AesUtil {
    private static final Logger LOG = LoggerFactory.getLogger(AesUtil.class);

    private static final String KEY_ALGORITHM = "AES";

    private static final String CIPHER_ALGORITHM_ECB = "AES/CBC/PKCS5Padding";

    private static final String PARAMETER_KEY = "0102030405060708";

    /**
     * AES解密  
     *
     * @param msg
     * @param key
     * @return
     * @throws Exception
     */
    public static String decrypt(String msg, String key) throws Exception {
        if (isNotBlank(msg) && isNotBlank(key)) {
            LOG.info("msg:{}", msg);
            //String message = URLDecoder.decode(safeUrlBase64BeforeDecodemsg, "GBK");
            //byte[] bytes = getMimeDecoder().decode(safeUrlBase64BeforeDecode(msg));
            
            byte[] bytes = new BASE64Decoder().decodeBuffer(msg);
            //Decoder decoder = Base64.getDecoder();
            //byte[] bytes = decoder.decode(msg);
            if (isNotEmpty(bytes)) {
                return decrypt(bytes, key);
            }
        }else{
            LOG.error("msg:{} or key:{} is empty");
        }
        return null;
    }

    /**
     * AES解密  
     *
     * @param content
     * @param strKey
     * @return
     * @throws java.lang.Exception
     */
    public static String decrypt(byte[] content, String strKey) throws Exception {
        return new String(getCipher(strKey).doFinal(content));
    }

    private static Cipher getCipher(String strKey) throws Exception {
        SecretKeySpec skeySpec = getKey(strKey);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_ECB);
        cipher.init(DECRYPT_MODE, skeySpec, new IvParameterSpec(PARAMETER_KEY.getBytes()));
        return cipher;
    }

    private static SecretKeySpec getKey(String strKey) {
        byte[] arrBTmp = strKey.getBytes();
        // 创建一个空的16位字节数组（默认值为0）
        byte[] arrB = new byte[16];
        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
            arrB[i] = arrBTmp[i];
        }
        return new SecretKeySpec(arrB, KEY_ALGORITHM);
    }
    /*
    public static void main(String[] args) {
        try {
            //String token = encrypt2(json.toString(), "cb6cbc38de64b473bf31e8d8dc4c85ed");
            //System.out.println("加密后的数据>>" + token);

            String token = "7MCfwcsVib4W3jKMgUChnqcknVTYYVm%2BjHtnNIcBZL%2Brb%2BrT1cKtIHIf2fGxyWLMqOdsJVfByphP%0AjWRmlwM5zg%3D%3D";
            //1、从map中取出加密过的message，使用urlDecode转码,
            //String message = "y1cfJmonkFPrjc2zE23R3Brp0DEWvexcd1GjdSie61Wmbkvd3cjruQ76 KApHx3pGMuWOl2hur6K";
            //2、解密消息内容,密钥为client_secret注意修改
            String decryResult = decrypt(token, "8c0c629c95c9bcb36269634788f504fc");
            JSONObject jsonObject = JSONObject.fromObject(decryResult);
            YouzanMessage stu = (YouzanMessage) JSONObject.toBean(jsonObject, YouzanMessage.class);
            //3、解密后的数据
            System.out.println("解密后的数据>>" + decryResult + ", \n" + stu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     */
}
