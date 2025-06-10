package com.example.masterslavedatasource;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

// 4 创建一个 AOP 切面类 用于在方法执行时切换数据源
@Aspect
@Order(1)
@Component
public class DataSourceAspect {
    @Pointcut("@annotation(com.example.masterslavedatasource.DataSource) || @within(com.example.masterslavedatasource.DataSource)")
    public void dsPointCut() {}

    @Around("dsPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        DataSource dataSource = getDataSource(joinPoint);
        if (dataSource != null) {
            DynamicDataSourceContextHolder.setDataSourceType(dataSource.value());
        }
        try {
            // 在调用正式方法前如果设置了key， 动态数据源会切换key
            return joinPoint.proceed();
        } finally {
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

    // 拿到自定义的DataSource注解，从而拿到key进行后续工作
    private DataSource getDataSource(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        DataSource dataSource = AnnotationUtils.findAnnotation(signature.getMethod(), DataSource.class);
        if (dataSource != null) {
            return dataSource;
        }
        return AnnotationUtils.findAnnotation(signature.getDeclaringType(), DataSource.class);
    }
}