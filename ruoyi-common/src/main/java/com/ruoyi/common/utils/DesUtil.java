package com.ruoyi.common.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.length;
import static org.apache.commons.lang3.StringUtils.upperCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DesUtil {
    private static final Logger LOG = LoggerFactory.getLogger(DesUtil.class);

    private static final String DES = "DES";
    private static final String KEY = "5dfdK@7(M$woim!3U$yw";

    private static Cipher getCipher(int mode, byte[] key) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(DES);
        cipher.init(mode, secretKey, sr);
        return cipher;
    }

    private static byte[] encrypt(byte[] src, byte[] key) throws Exception {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(src);
    }

    private static byte[] decrypt(byte[] src, byte[] key) throws Exception {
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(src);
    }

    private static String byte2hex(byte[] b) {
        String hs = "";
        for (int n = 0; n < b.length; n++) {
            String temp  = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (isNotBlank(temp) && length(temp) == 1) {
                hs = hs + "0" + temp;
            } else {
                hs = hs + temp;
            }
        }
        return upperCase(hs);
    }

    private static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0) {
            throw new IllegalArgumentException("length not even");
        }
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

    private static String decode(String src, String key) {
        String decryptStr = "";
        try {
            decryptStr = new String(decrypt(hex2byte(src.getBytes()), key.getBytes()));
        } catch (Exception e) {
            LOG.error("failed to decode message:" + e.getMessage());
        }
        return decryptStr;
    }

    private static String encode(String src, String key) {
        byte[] bytes = null;
        String encryptStr = "";
        try {
            bytes = encrypt(src.getBytes(), key.getBytes());
        } catch (Exception ex) {
            LOG.error("failed to encode message:" + ex.getMessage());
        }
        if (isNotEmpty(bytes)) {
            encryptStr = byte2hex(bytes);
        }
        return encryptStr;
    }

    public static String decode(String src) {
        return decode(src, KEY);
    }

    public static String encode(String src) {
        return encode(src, KEY);
    }
}
