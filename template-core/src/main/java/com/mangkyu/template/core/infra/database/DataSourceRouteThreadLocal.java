package com.mangkyu.template.core.infra.database;

class DataSourceRouteThreadLocal {

    private static final ThreadLocal<DataSourceRoute> dataSourceRoute = new ThreadLocal<>();

    public static DataSourceRoute getDataSourceRoute() {
        return dataSourceRoute.get();
    }

    public static void setDataSourceRoute(DataSourceRoute route) {
        dataSourceRoute.set(route);
    }

    public static void removeDataSourceRoute() {
        dataSourceRoute.remove();
    }
}
