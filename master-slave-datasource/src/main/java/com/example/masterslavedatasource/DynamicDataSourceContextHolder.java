package com.example.masterslavedatasource;

// 1 创建数据源上下文管理类：用于管理在运行时，数据源k-v映射列表的key
public class DynamicDataSourceContextHolder {
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    // 切面中会调用，在执行正式方法前 为线程设置key
    public static void setDataSourceType(String dsType) {
        System.out.println("Data source type set to: " + dsType);
        CONTEXT_HOLDER.set(dsType);
    }

    // 动态数据源调用 使用key对应的具体数据源
    public static String getDataSourceType() {
        return CONTEXT_HOLDER.get();
    }

    // 切面中会调用，在执行正式方法后 清除线程对应的key
    public static void clearDataSourceType() {
        System.out.println("Clearing data source type");
        CONTEXT_HOLDER.remove();
    }
}