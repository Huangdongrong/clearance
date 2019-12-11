/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.framework.redis.service;

import java.io.Serializable;
import javax.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 *
 * @author wmao
 * @param <K>
 * @param <V>
 */
public abstract class RedisGeneratorService<K extends Serializable, V extends Serializable> {

    @Resource(name="redisTemplate")  
    protected RedisTemplate<K, V> redisTemplate;

    /**
     * @param redisTemplate the redisTemplate to set
     */
    public void setRedisTemplate(RedisTemplate<K, V> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * <br>------------------------------<br>
     *
     * @return
     */
    protected RedisSerializer<String> getRedisSerializer() {
        return redisTemplate.getStringSerializer();
    }
}
