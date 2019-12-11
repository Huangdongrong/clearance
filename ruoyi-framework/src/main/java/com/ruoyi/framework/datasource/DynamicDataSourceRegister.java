/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.framework.datasource;

import com.ruoyi.common.config.datasource.DynamicDataSourceContextHolder;
import static com.ruoyi.common.enums.DataSourceType.MASTER;
import static com.ruoyi.common.enums.DataSourceType.SLAVE;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertyNameAliases;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

/**
 * 动态数据源注册 实现 ImportBeanDefinitionRegistrar 实现数据源注册 实现 EnvironmentAware
 * 用于读取application.yml配置
 */
public class DynamicDataSourceRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicDataSourceRegister.class);
    
    private static final String[] DRUID_PARAMS = {"initialSize", "minIdle", "maxActive", "maxWait", "timeBetweenEvictionRunsMillis", "validationQuery", "minEvictableIdleTimeMillis", "testWhileIdle", "testOnBorrow", "testOnReturn"};

    /**
     * 配置上下文（也可以理解为配置文件的获取工具）
     */
    private Environment evn;

    /**
     * 别名
     */
    private final static ConfigurationPropertyNameAliases aliases = new ConfigurationPropertyNameAliases();

    /**
     * 由于部分数据源配置不同，所以在此处添加别名，避免切换数据源出现某些参数无法注入的情况
     */
    static {
        aliases.addAliases("url", new String[]{"jdbc-url"});
        aliases.addAliases("username", new String[]{"user"});
    }

    /**
     * 存储我们注册的数据源
     */
    private final Map<String, DataSource> customDataSources = new HashMap<>();

    /**
     * 参数绑定工具 springboot2.0新推出
     */
    private Binder binder;

    /**
     * ImportBeanDefinitionRegistrar接口的实现方法，通过该方法可以按照自己的方式注册bean
     *
     * @param annotationMetadata
     * @param beanDefinitionRegistry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        // 获取数据源类型
        String typeStr = evn.getProperty("spring.datasource.type");
        // 获取数据源类型
        Class<? extends DataSource> clazz = getDataSourceType(typeStr);
        Map basicDataSorceProperties = binder.bind("spring.datasource.druid", Map.class).get();
        // 获取所有数据源配置
        Map masterDataSourceProperties = binder.bind("spring.datasource.druid.master", Map.class).get();
        for(String param : DRUID_PARAMS){
            masterDataSourceProperties.put(param, basicDataSorceProperties.get(param));
        }
        // 绑定默认数据源参数 也就是主数据源
        DataSource masterDatasource = bind(clazz, masterDataSourceProperties);
        //((DruidDataSource)masterDatasource).setMinEvictableIdleTimeMillis(Long.parseLong("" + masterDataSourceProperties.get("minEvictableIdleTimeMillis")));
        //((DruidDataSource)masterDatasource).setMaxEvictableIdleTimeMillis(Long.parseLong("" + masterDataSourceProperties.get("maxEvictableIdleTimeMillis")));
        DynamicDataSourceContextHolder.DATA_SOURCE_IDS.add(MASTER);
        customDataSources.put(MASTER.getKey(), masterDatasource);
        //customDataSources.put(MASTER.getKey(), masterDatasource);
        LOG.info("注册默认数据源成功");
        // 获取所有数据源配置
        Map slaveDataSourceProperties = binder.bind("spring.datasource.druid.slave", Map.class).get();
        for(String param : DRUID_PARAMS){
            slaveDataSourceProperties.put(param, basicDataSorceProperties.get(param));
        }
        // 绑定默认数据源参数 也就是主数据源
        DataSource slaveDatasource = bind(clazz, slaveDataSourceProperties);
        //((DruidDataSource)slaveDatasource).setMinEvictableIdleTimeMillis(Long.parseLong("" + masterDataSourceProperties.get("minEvictableIdleTimeMillis")));
        //((DruidDataSource)slaveDatasource).setMaxEvictableIdleTimeMillis(Long.parseLong("" + masterDataSourceProperties.get("maxEvictableIdleTimeMillis")));
        DynamicDataSourceContextHolder.DATA_SOURCE_IDS.add(SLAVE);
        customDataSources.put(SLAVE.getKey(), slaveDatasource);
        LOG.info("注册第二数据源成功");
        // bean定义类
        GenericBeanDefinition define = new GenericBeanDefinition();
        // 设置bean的类型，此处DynamicRoutingDataSource是继承AbstractRoutingDataSource的实现类
        define.setBeanClass(DynamicDataSource.class);
        // 需要注入的参数
        MutablePropertyValues mpv = define.getPropertyValues();
        // 添加默认数据源，避免key不存在的情况没有数据源可用
        mpv.add("defaultTargetDataSource", masterDatasource);
        // 添加其他数据源
        mpv.add("targetDataSources", customDataSources);
        // 将该bean注册为datasource，不使用springboot自动生成的datasource
        beanDefinitionRegistry.registerBeanDefinition("dataSource", define);
        LOG.info("注册数据源成功，一共注册{}个数据源", customDataSources.keySet().size() + 1);
    }
    

    /**
     * 通过字符串获取数据源class对象
     *
     * @param typeStr
     * @return
     */
    private Class<? extends DataSource> getDataSourceType(String typeStr) {
        Class<? extends DataSource> type;
        try {
            if (StringUtils.hasLength(typeStr)) {
                // 字符串不为空则通过反射获取class对象
                type = (Class<? extends DataSource>) Class.forName(typeStr);
            } else {
                // 默认为hikariCP数据源，与springboot默认数据源保持一致
                LOG.error("failed to find valid data source type!");
                throw new IllegalArgumentException("can not resolve this datasource type:" + typeStr);
            }
            return type;
        } catch (ClassNotFoundException | IllegalArgumentException e) {
            throw new IllegalArgumentException("can not resolve class with type: " + typeStr); //无法通过反射获取class对象的情况则抛出异常，该情况一般是写错了，所以此次抛出一个runtimeexception
        }
    }

    /**
     * 绑定参数，以下三个方法都是参考DataSourceBuilder的bind方法实现的，目的是尽量保证我们自己添加的数据源构造过程与springboot保持一致
     *
     * @param result
     * @param properties
     */
    private void bind(DataSource result, Map properties) {
        ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
        Binder binders = new Binder(new ConfigurationPropertySource[]{source.withAliases(aliases)});
        // 将参数绑定到对象
        binders.bind(ConfigurationPropertyName.EMPTY, Bindable.ofInstance(result));
    }

    private <T extends DataSource> T bind(Class<T> clazz, Map properties) {
        ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
        ConfigurationPropertySource[] sources = new ConfigurationPropertySource[]{source.withAliases(aliases)};
        LOG.info("sources:{}", sources.length);
        if(sources.length > 0){
            for(ConfigurationPropertySource src : sources){
                LOG.info("src:{}", src.toString());
            }
        }
        Binder binders = new Binder(sources);
        // 通过类型绑定参数并获得实例对象
        return binders.bind(ConfigurationPropertyName.EMPTY, Bindable.of(clazz)).get();
    }
    

    /**
     * @param clazz
     * @param sourcePath 参数路径，对应配置文件中的值，如: spring.datasource
     * @param <T>
     * @return
     */
    private <T extends DataSource> T bind(Class<T> clazz, String sourcePath) {
        Map properties = binder.bind(sourcePath, Map.class).get();
        return bind(clazz, properties);
    }

    /**
     * EnvironmentAware接口的实现方法，通过aware的方式注入，此处是environment对象
     *
     * @param environment
     */
    @Override
    public void setEnvironment(Environment environment) {
        LOG.info("开始注册数据源");
        this.evn = environment;
        // 绑定配置器
        binder = Binder.get(evn);
    }
}
