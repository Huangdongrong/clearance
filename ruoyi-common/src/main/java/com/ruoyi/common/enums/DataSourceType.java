package com.ruoyi.common.enums;

/**
 * 数据源
 *
 * @author ruoyi
 */
public enum DataSourceType {
    MASTER("master"), SLAVE("slave");

    private String key;

    DataSourceType(String key) {
        this.key = key;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }
}
