package com.ruoyi;

import com.ruoyi.framework.datasource.EnableDynamicDataSource;
import static org.springframework.boot.Banner.Mode.OFF;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动程序
 *
 * @author ruoyi
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class})
@EnableDynamicDataSource
@EnableScheduling
public class RuoYiApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(RuoYiApplication.class);
        application.setBannerMode(OFF);
        application.run(args);
    }
}
