package com.ruoyi.common.config.datasource;

import com.ruoyi.common.enums.DataSourceType;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据源切换处理
 *
 * @author ruoyi
 */
public class DynamicDataSourceContextHolder {

    public static final Logger LOG = LoggerFactory.getLogger(DynamicDataSourceContextHolder.class);

    /**
     * 存储已经注册的数据源的key
     */
    public static List<DataSourceType> DATA_SOURCE_IDS = new ArrayList<>();
    /**
     * 使用ThreadLocal维护变量，ThreadLocal为每个使用该变量的线程提供独立的变量副本，
     * 所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。
     */
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 设置数据源的变量
     *
     * @param dataSourceType
     */
    public static void setDataSourceType(DataSourceType dataSourceType) {
        LOG.debug("切换到{}数据源", dataSourceType);
        CONTEXT_HOLDER.set(dataSourceType.getKey());
    }

    /**
     * 获得数据源的变量
     *
     * @return
     */
    public static String getDataSourceType() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 清空数据源变量
     */
    public static void clearDataSourceType() {
        CONTEXT_HOLDER.remove();
    }

    public static boolean containsDataSource(DataSourceType dataSourceId) {
        return DATA_SOURCE_IDS.contains(dataSourceId);
    }
}
