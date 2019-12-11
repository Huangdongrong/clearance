/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.codec.binary.Base64;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author wmao
 */
public class RSAUtil {

    private static final Logger LOG = LoggerFactory.getLogger(RSAUtil.class);

    private static final String KEY_ALGORITHM = "RSA";
    private final static String UTF8 = "utf-8";
    private static final int MAX_DECRYPT_BLOCK = 128;

    public static String decryptByPublicKey(String encryptedData, String publicKey) {
        String derypt = null;
        try {
            if (isNotBlank(encryptedData) && isNotBlank(publicKey)) {
                byte[] dataBytes = Base64.decodeBase64(encryptedData);
                byte[] keyBytes = Base64.decodeBase64(publicKey.getBytes());
                if (isNotEmpty(keyBytes)) {
                    X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
                    KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
                    Key publicK = keyFactory.generatePublic(x509KeySpec);
                    Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
                    cipher.init(Cipher.DECRYPT_MODE, publicK);
                    int inputLen = dataBytes.length;
                    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                        int offSet = 0;
                        int idx = 1;
                        int drift = inputLen - offSet;
                        while (drift > 0) {
                            byte[] cache = null;
                            if (drift > MAX_DECRYPT_BLOCK) {
                                cache = cipher.doFinal(dataBytes, offSet, MAX_DECRYPT_BLOCK);
                            } else {
                                cache = cipher.doFinal(dataBytes, offSet, drift);
                            }
                            out.write(cache, 0, isNotEmpty(cache) ? cache.length : 0);
                            offSet = idx * MAX_DECRYPT_BLOCK;
                            drift = inputLen - offSet;
                            idx++;
                        }
                        byte[] decryptedData = out.toByteArray();
                        derypt = isNotEmpty(decryptedData) ? new String(decryptedData, UTF8) : "";
                    }
                }
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | IOException ex) {
            ex.printStackTrace();
            LOG.error("failed to decrypt xml data:{}", ex.getCause());
        }
        return derypt;
    }
}
