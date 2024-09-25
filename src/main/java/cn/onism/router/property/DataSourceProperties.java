package cn.onism.router.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 数据源属性
 *
 * @author HeXin
 * @date 2024/06/02
 */
@ConfigurationProperties(prefix = "datasources", ignoreInvalidFields = true)
public class DataSourceProperties {
    private List<DataSourceProperty> datasource;

    public List<DataSourceProperty> getDatasource() {
        return datasource;
    }

    public void setDatasource(List<DataSourceProperty> datasource) {
        this.datasource = datasource;
    }

    public static class DataSourceProperty {
        private String name;
        private String url;
        private String username;
        private String password;
        private String driverClassName;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName(String driverClassName) {
            this.driverClassName = driverClassName;
        }
    }
}
