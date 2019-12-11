package com.ruoyi.framework.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import com.ruoyi.common.config.datasource.DynamicDataSourceContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 动态数据源
 *
 * @author ruoyi
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    private static final Logger LOG = LoggerFactory.getLogger(DynamicDataSource.class);

    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceContextHolder.getDataSourceType();
    }
}
