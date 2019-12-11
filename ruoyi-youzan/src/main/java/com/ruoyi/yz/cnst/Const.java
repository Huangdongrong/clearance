/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.cnst;

import com.ruoyi.yz.domain.ClearanceStatus;
import java.math.BigDecimal;

/**
 *
 * @author wmao
 */
public final class Const {

    public static final String COOKIE_KEY = "kdtkj";

    public static final String COOKIE_KEY_TIME_PATTERN = "yyyyMMddHHmmss";

    public static final int REDIS_ENTITY_EXPIRATION_DEFAULT = 60 * 60;

    /**
     * 返回值 没有权限 100
     */
    public static final int NO_AUTHORIZED = 100;
    /**
     * 没有权限返回中文说明
     */
    public static final String NO_AUTHORIZED_MSG = "当前角色没有权限";//
    /**
     * 返回值 成功(1)
     */
    public static final int SUCCEED = 1;
    /**
     * 返回值 失败(0)
     */
    public static final int FAIL = 0;
    /**
     * 删除成功
     */
    public static final String DEL_SUCCEED = "删除成功";
    /**
     * 删除失败
     */
    public static final String DEL_FAIL = "删除失败";
    /**
     * 修改成功
     */
    public static final String UPDATE_SUCCEED = "修改成功";
    /**
     * 修改失败
     */
    public static final String UPDATE_FAIL = "修改失败";
    /**
     * 数据获取成功
     */
    public static final String DATA_SUCCEED = "数据获取成功";
    /**
     * 数据获取失败
     */
    public static final String DATA_FAIL = "数据获取失败";
    
    /**
     * 程序录入使用名称
     */
    public static final String CREATE_BY_PROGRAM = "AUTO";
    
    /**
     * 有赞用户前缀
     */
    public static final String YOUZAN_USER_PREFIX = "YOUZAN_";
    
    /**
     * 有赞用户前缀中文
     */
    public static final String YOUZAN_USER_CN_PREFIX = "有赞_";
    
    /**
     * 有赞用户后缀中文
     */
    public static final String YOUZAN_USER_CN_SUFFIX = "_商铺";
    
    /**
     * 默认清关过期天数
     */
    public static final int DEAULT_CLEAR_DEADLINE = -10;
    
    /**
     * 清关平台内部错误码
     */
    public static final int RUOYI_INTERNAL_ERROR = 8888;
    
    /**
     * 系统默认非法kdt Id
     */
    public static final String DEFAULT_INVALID_KDT_ID = "00000000000000000000000000000000";
    
    /**
     * 默认预处理成功消息
     */
    public static final String PRE_PROCESS_SUCCEED = "预处理成功";
    
    /**
     * 包装净重
     */
    public static final BigDecimal PACKAGE_WEIGHT = new BigDecimal(0.1);
    
    /**
     * 默认清关错误
     */
    public static final ClearanceStatus DEFAULT_CLEARANCE_ERROR_STATUS = new ClearanceStatus(RUOYI_INTERNAL_ERROR, false, "default error");
}
