/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service.impl;

import static com.ruoyi.yz.cnst.Const.REDIS_ENTITY_EXPIRATION_DEFAULT;
import com.ruoyi.yz.domain.YouzanKdt;
import com.ruoyi.yz.redis.EntityRedisService;
import com.ruoyi.framework.redis.service.RedisGeneratorService;
import com.ruoyi.yz.domain.YouzanConfig;
import com.ruoyi.yz.mapper.YouzanConfigMapper;
import java.util.List;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import org.apache.commons.lang3.ArrayUtils;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

/**
 *
 * @author wmao
 */
@Service(value = "youzanKdtReidsService")
public class YouzanKdtRedisServiceImpl extends RedisGeneratorService<String, YouzanKdt> implements EntityRedisService<YouzanKdt>{

    @Autowired
    private YouzanConfigMapper youzanConfigMapper;
    
    private static final String QINGMA_PREFIX = "QINGMA";
    
    @Override
    public boolean add(YouzanKdt entity) {
        boolean result = nonNull(entity) && isNotBlank(entity.getAuthorityId()) ? redisTemplate.execute((RedisConnection connection) -> {
            RedisSerializer<String> serializer = getRedisSerializer();
            YouzanConfig config = youzanConfigMapper.getOne();
            String prefixName = nonNull(config) ? trimToEmpty(config.getName()) : QINGMA_PREFIX;
            byte[] key = serializer.serialize(prefixName + "-" + entity.getAuthorityId());
            byte[] name = serializer.serialize(entity.getAccessToken());
            connection.setEx(key, REDIS_ENTITY_EXPIRATION_DEFAULT, name);
            return true;
        }) : false;
        return result;
    }

    @Override
    public boolean add(List<YouzanKdt> list) {
        boolean result = false;
        if (isNotEmpty(list)) {
            result = redisTemplate.execute((RedisConnection connection) -> {
                RedisSerializer<String> serializer = getRedisSerializer();
                list.forEach((YouzanKdt kdt) -> {
                    byte[] key = serializer.serialize(getRedisKey(kdt.getAuthorityId()));
                    byte[] name = serializer.serialize(kdt.getAccessToken());
                    connection.setEx(key, REDIS_ENTITY_EXPIRATION_DEFAULT, name);
                });
                return true;
            }, false, true);
        }
        return result;
    }

    @Override
    public void delete(String key) {
        YouzanConfig config = youzanConfigMapper.getOne();
        String prefix = nonNull(config) ? trimToEmpty(config.getName()) : QINGMA_PREFIX;
        String redisKey = startsWithIgnoreCase(key, prefix) ? key : getRedisKey(key);
        if (isNotBlank(redisKey)) {
            redisTemplate.delete(redisKey);
        }
    }

    @Override
    public String get(String keyId) {
        String result = redisTemplate.execute((RedisConnection connection) -> {
            RedisSerializer<String> serializer = getRedisSerializer();
            byte[] key = serializer.serialize(getRedisKey(keyId));
            byte[] value = connection.get(key);
            return ArrayUtils.isNotEmpty(value) ? serializer.deserialize(value) : null;
        });
        return result;
    }

    @Override
    public void delete(List<String> list) {
        if (isNotEmpty(list)) {
            list.forEach((key) -> {
                delete(key);
            });
        }
    }

    @Override
    public boolean update(YouzanKdt entity) {
        boolean result = nonNull(entity) && isNotBlank(entity.getAuthorityId()) ? redisTemplate.execute((RedisConnection connection) -> {
            RedisSerializer<String> serializer = getRedisSerializer();
            byte[] key = serializer.serialize(getRedisKey(entity.getAuthorityId()));
            byte[] name = serializer.serialize(entity.getAccessToken());
            connection.setEx(key, REDIS_ENTITY_EXPIRATION_DEFAULT, name);
            return true;
        }) : false;
        return result;
    }

    private String getRedisKey(String kdtId) {
        YouzanConfig config = youzanConfigMapper.getOne();
        String prefix = nonNull(config) ? trimToEmpty(config.getName()) : QINGMA_PREFIX;
        return isNotBlank(kdtId) ? prefix + "-" + kdtId : "";
    }
}
