/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author wmao
 */
public final class ObjectUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ObjectUtil.class);

    public static byte[] serialize(Object object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            // 序列化
            oos.writeObject(object);
            return baos.toByteArray();
        } catch (IOException e) {
            LOG.error("failed to  serialize object:{}", e.getMessage());
        }
        return null;
    }

    public static Object deserialize(byte[] bytes) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            // 反序列化
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            LOG.error("failed to  deserialize object:{}", e.getMessage());
        }
        return null;
    }
}
