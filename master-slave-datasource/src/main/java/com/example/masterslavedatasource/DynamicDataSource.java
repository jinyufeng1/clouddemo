package com.example.masterslavedatasource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

// 2 创建动态数据源类 主从切换的关键枢纽
@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource {
    //设置默认默认数据源 和 数据源k-v映射列表
    public DynamicDataSource(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources) {
        super.setDefaultTargetDataSource(defaultTargetDataSource); // 如果没有注解就不会经过切面设置key，使用这个默认的数据源
        super.setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }

    // 程序每次调用 mapper 访问数据库前，都会调用AbstractRoutingDataSource.determineTargetDataSource，这个方法会调用determineCurrentLookupKey
    // 如果key为null，就还是用默认数据源，不为null并且get也不为null，就切换成目标数据源，这样能保证 下一次访问确定数据源 不受上一次影响
    @Override
    protected Object determineCurrentLookupKey() {
        String dataSourceType = DynamicDataSourceContextHolder.getDataSourceType();
        log.info("读写分离数据源标识【{}】", dataSourceType);
        return dataSourceType;
    }
}