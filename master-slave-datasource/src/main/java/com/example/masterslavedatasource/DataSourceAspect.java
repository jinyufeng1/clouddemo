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
//    @annotation 匹配所有加了注解的方法
//    @within 匹配所有加了注解的类中所有的方法
    @Pointcut("@annotation(com.example.masterslavedatasource.DataSource) || @within(com.example.masterslavedatasource.DataSource)")
    public void dsPointCut() {}

    @Around("dsPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        DataSource dataSource = getDataSource(joinPoint);
        // 为了代码的健壮性和容错性
        if (dataSource == null) {
            throw new RuntimeException("未在方法或类上找到 DataSource 注解");
        }

        DynamicDataSourceContextHolder.setDataSourceType(dataSource.value());
        try {
            // 在调用正式方法前如果设置了key， 动态数据源会切换key
            return joinPoint.proceed();
        }
        catch (Throwable throwable)
        {
            throwable.printStackTrace();
            throw throwable;
        }
        finally {
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