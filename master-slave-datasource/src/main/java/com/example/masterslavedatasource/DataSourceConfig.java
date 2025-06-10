package com.example.masterslavedatasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

// 3 创建一个配置类 用于配置主从数据源 和 管理这是数据源的动态数据源
@Configuration
public class DataSourceConfig {
    @Value("${master.url}")
    private String masterUrl;
    @Value("${master.username1}")
    private String masterUsername;
    @Value("${master.password}")
    private String masterPassword;
    @Value("${master.decrypt.key}")
    private String masterDecryptKey;

    @Value("${slave.url}")
    private String slaveUrl;
    @Value("${slave.username1}")
    private String slaveUsername;
    @Value("${slave.password}")
    private String slavePassword;
    @Value("${slave.decrypt.key}")
    private String slaveDecryptKey;
    @Bean
    public DataSource masterDataSource() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(masterUrl);
        dataSource.setUsername(masterUsername);
        dataSource.setPassword(masterPassword);
        // spring.datasource.druid.connect-properties.config.decrypt=true
        // spring.datasource.druid.connect-properties.config.decrypt.key=${decrypt.key}
        dataSource.setConnectionProperties("config.decrypt=true;config.decrypt.key=" + masterDecryptKey);
        // spring.datasource.druid.filter.config.enabled=true
        dataSource.setFilters("config");

        return dataSource;
    }

    @Bean
    public DataSource slaveDataSource() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(slaveUrl);
        dataSource.setUsername(slaveUsername);
        dataSource.setPassword(slavePassword);
        // 创建 Properties 对象来设置连接属性
        Properties properties = new Properties();
        properties.setProperty("config.decrypt", "true");
        properties.setProperty("config.decrypt.key", slaveDecryptKey);
        dataSource.setConnectProperties(properties);
        dataSource.setFilters("config");

        return dataSource;
    }

    // 动态数据源源管理主从数据源 自定义动态数据源后 默认的数据源将被覆盖
    @Bean(name = "dynamicDataSource")
    @Primary
    public DynamicDataSource dynamicDataSource() throws SQLException {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("master", masterDataSource());
        targetDataSources.put("slave", slaveDataSource());

        return new DynamicDataSource(masterDataSource(), targetDataSources);
    }
}
