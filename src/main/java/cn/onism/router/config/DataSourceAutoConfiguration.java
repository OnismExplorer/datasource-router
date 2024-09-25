package cn.onism.router.config;

import cn.onism.router.aspect.DataSourceAspect;
import cn.onism.router.property.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 数据源自动配置
 *
 * @author HeXin
 * @date 2024/06/02
 */
@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
public class DataSourceAutoConfiguration {

    /**
     * 路由数据源
     *
     * @param dataSourceProperties 数据源属性
     * @return {@link DataSource }
     */
    @Bean
    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
        Map<Object, Object> dataSourceMap = new HashMap<>();
        DataSource defaultDataSource = null;

        // 遍历配置的数据源属性
        for (DataSourceProperties.DataSourceProperty property : dataSourceProperties.getDatasource()) {
            DataSource dataSource = DataSourceBuilder.create()
                    .url(property.getUrl())
                    .username(property.getUsername())
                    .password(property.getPassword())
                    .driverClassName(property.getDriverClassName())
                    .build();

            // 如果配置为默认数据源
            if ("default".equals(property.getName())) {
                defaultDataSource = dataSource;
            }

            dataSourceMap.put(property.getName(), dataSource);
        }

        // 配置路由数据源
        RoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setDefaultTargetDataSource(Objects.requireNonNull(defaultDataSource, "Default DataSource must not be null"));
        routingDataSource.setTargetDataSources(dataSourceMap);
        return routingDataSource;
    }

    /**
     * 数据源切换的切面
     *
     * @return {@link DataSourceAspect }
     */
    @Bean
    public DataSourceAspect dataSourceAspect() {
        return new DataSourceAspect();
    }

    /**
     * 事务管理器
     *
     * @param dataSource 路由数据源
     * @return {@link PlatformTransactionManager }
     */
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}

