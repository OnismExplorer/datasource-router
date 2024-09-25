package cn.onism.router.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 路由数据源
 *
 * @author HeXin
 * @date 2024/06/02
 */
public class RoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getDataSource();
    }
}
