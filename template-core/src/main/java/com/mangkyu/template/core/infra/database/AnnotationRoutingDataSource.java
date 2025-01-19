package com.mangkyu.template.core.infra.database;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

class AnnotationRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected DataSourceRoute determineCurrentLookupKey() {
        DataSourceRoute dataSourceRoute = DataSourceRouteThreadLocal.getDataSourceRoute();
        if (dataSourceRoute != null) {
            return dataSourceRoute;
        }

        if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
            return DataSourceRoute.RO;
        }

        return DataSourceRoute.RW;
    }
}
