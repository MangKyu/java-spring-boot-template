package com.mangkyu.template.core.infra.database;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
class RoutingDataSourceAspect {

    @Around("@annotation(routingDataSource)")
    public Object around(ProceedingJoinPoint joinPoint, RoutingDataSource routingDataSource) throws Throwable {
        DataSourceRouteThreadLocal.setDataSourceRoute(routingDataSource.value());
        return joinPoint.proceed();
    }

}
