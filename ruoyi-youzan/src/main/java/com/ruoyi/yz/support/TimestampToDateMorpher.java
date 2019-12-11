/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.support;

import static com.ruoyi.common.utils.DateUtil.fromMillise;
import java.text.ParseException;
import java.util.Date;
import net.sf.ezmorph.object.AbstractObjectMorpher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author wmao
 */
public class TimestampToDateMorpher extends AbstractObjectMorpher {

    private static final Logger LOG = LoggerFactory.getLogger(TimestampToDateMorpher.class);
    
    @Override
    public Object morph(Object obj) {
        if (obj != null) {
            try {
                return fromMillise(Long.parseLong(String.valueOf(obj)));
            } catch (ParseException ex) {
                LOG.error("failed to convert timestamp to date:" + ex.getMessage());
                return null;
            }
        }
        return null;
    }
    
    @Override
    public Class morphsTo() {
        return Date.class;
    }
    
    @Override
    public boolean supports(Class clazz) {
        return Long.class.isAssignableFrom(clazz);
    }
}
