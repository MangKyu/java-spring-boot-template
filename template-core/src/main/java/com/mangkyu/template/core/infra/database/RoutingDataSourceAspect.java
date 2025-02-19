package com.mangkyu.template.core.infra.database;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
class RoutingDataSourceAspect {

    @Around("@annotation(routingDataSource)")
    public Object annotationRoutingDataSourceAnnotation(
            ProceedingJoinPoint joinPoint,
            RoutingDataSource routingDataSource
    ) throws Throwable {
        // 메서드의 애노테이션을 우선 적용
        DataSourceRouteThreadLocal.setDataSourceRoute(routingDataSource.value());
        return joinPoint.proceed();
    }

    @Around("@within(routingDataSource) && !@annotation(RoutingDataSource)")
    public Object around(
            ProceedingJoinPoint joinPoint,
            RoutingDataSource routingDataSource
    ) throws Throwable {
        // 클래스 레벨 애노테이션은 메서드에 없을 때만 적용
        DataSourceRouteThreadLocal.setDataSourceRoute(routingDataSource.value());
        return joinPoint.proceed();
    }

}
