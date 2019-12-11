/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.redis;

import java.util.List;

/**
 *
 * @author wmao
 * @param <T>
 */
public interface EntityRedisService<T> {

    boolean add(T entity);

    boolean add(List<T> list);

    void delete(String key);

    void delete(List<String> list);

    String get(String key);
    
    boolean update(final T entity);
}
