/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.framework.aspectj;

import com.ruoyi.common.config.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.common.enums.DataSourceType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ruoyi.common.annotation.TargetDS;
import static com.ruoyi.common.enums.DataSourceType.MASTER;

/**
 *
 * @author wmao
 */
//@Aspect
//@Order(-10)
//@Component
public class DynamicDataSourceAspect {

    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceAspect.class);

    @Before("@annotation(targetDS)")
    public void changeDataSource(JoinPoint point, TargetDS targetDS) throws Throwable {
        DataSourceType dst = targetDS.value();
        if (DynamicDataSourceContextHolder.DATA_SOURCE_IDS.contains(dst)) {
            logger.debug("Use DataSource :{} >", dst, point.getSignature());
            DynamicDataSourceContextHolder.setDataSourceType(dst);
        } else {
            logger.info("数据源[{}]不存在，使用默认数据源 >{}", dst, point.getSignature());
            DynamicDataSourceContextHolder.setDataSourceType(MASTER);
        }
    }

    @After("@annotation(targetDS)")
    public void restoreDataSource(JoinPoint point, TargetDS ds) {
        logger.debug("Revert DataSource : " + ds.value() + " > " + point.getSignature());
        DynamicDataSourceContextHolder.clearDataSourceType();

    }
}
