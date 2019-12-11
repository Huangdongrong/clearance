package com.ruoyi.framework.datasource;

import com.ruoyi.common.annotation.TargetDS;
import com.ruoyi.common.config.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.common.enums.DataSourceType;
import static com.ruoyi.common.enums.DataSourceType.MASTER;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import static java.util.Objects.nonNull;

/**
 * @Auther: yukong
 * @Date: 2018/8/17 09:15
 * @Description:
 */
public class DynamicDataSourceAnnotationInterceptor implements MethodInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicDataSourceAnnotationInterceptor.class);

    /**
     * 缓存方法注解值
     */
    private static final Map<Method, DataSourceType> METHOD_CACHE = new HashMap<>();

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            DataSourceType ds = determineDatasource(invocation);
            if (!DynamicDataSourceContextHolder.containsDataSource(ds)) {
                LOG.info("数据源[{}]不存在，使用默认数据源 > {}", ds, MASTER);
                DynamicDataSourceContextHolder.setDataSourceType(MASTER);
            } else {
                DynamicDataSourceContextHolder.setDataSourceType(ds);
            }
            return invocation.proceed();
        } finally {
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

    private DataSourceType determineDatasource(MethodInvocation invocation) {
        Method method = invocation.getMethod();
        if (nonNull(method)) {
            if (METHOD_CACHE.containsKey(method)) {
                return METHOD_CACHE.get(method);
            } else {
                TargetDS ds = method.isAnnotationPresent(TargetDS.class) ? method.getAnnotation(TargetDS.class)
                        : AnnotationUtils.findAnnotation(method.getDeclaringClass(), TargetDS.class);
                METHOD_CACHE.put(method, ds.value());
                return ds.value();
            }
        }
        return MASTER;
    }

}
