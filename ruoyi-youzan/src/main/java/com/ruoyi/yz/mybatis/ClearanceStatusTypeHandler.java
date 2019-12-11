/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.mybatis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.ruoyi.common.mybatis.JsonTypeHandler;
import static com.ruoyi.common.utils.JsonUtil.OBJECT_MAPPER;
import com.ruoyi.yz.domain.ClearanceStatus;
import java.io.IOException;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author wmao
 */
@MappedTypes(ClearanceStatus.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class ClearanceStatusTypeHandler extends JsonTypeHandler<ClearanceStatus> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClearanceStatusTypeHandler.class);

    @Override
    protected String stringify(ClearanceStatus objs) {
        try {
            return OBJECT_MAPPER.writeValueAsString(objs);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected ClearanceStatus parse(String json) {
        if (isBlank(json)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<ClearanceStatus>() {
            });
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return null;
    }

}
