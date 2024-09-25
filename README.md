# 数据源路由组件

&emsp;&emsp;该项目提供了一种动态数据源路由解决方案，它支持在多个数据源之间进行路由，允许根据运行时条件无缝切换。系统旨在与基于 JDBC 的操作高效工作，包括 MyBatis，用于需要多个数据库的场景。

## 目录

- [背景](#背景)
- [功能](#功能)
- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [使用方法](#使用方法)
- [配置](#配置)
- [数据源切换](#数据源切换)

## 背景

&emsp;&emsp;许多现代应用程序需要与多个数据库进行交互，以实现不同的目的，例如分离读写操作、管理多个环境或处理各自数据库的不同微服务。该项目提供了一种灵活、易于配置的解决方案，用于在运行时在多个数据源之间路由。

&emsp;&emsp;该解决方案专注于原始 JDBC 进行数据访问。这种轻量级的方法非常适合那些不需要完整 ORM 但需要动态数据源管理的应用程序。

&emsp;&emsp;思路来源于小傅哥的一篇技术博客：[https://bugstack.cn/md/road-map/db-router.html](https://bugstack.cn/md/road-map/db-router.html)

&emsp;&emsp;其代码仓库：[https://gitcode.net/KnowledgePlanet/road-map/xfg-dev-tech-db-router](https://gitcode.net/KnowledgePlanet/road-map/xfg-dev-tech-db-router)

&emsp;&emsp;目前只实现了自定义切换数据源/库的功能，尚未实现“数据库表寻址”操作，后续可以添加上...

## 当前功能

- **动态数据源路由**：轻松在多个数据源之间切换。
- **可定制的数据源切换**：使用注解或自定义逻辑在运行时切换数据源。
- **轻量快速**：专注于 JDBC 连接数据源，没有其他额外的开销。
- **事务管理**：全面支持跨数据源的事务管理。
- **可扩展的配置**：可以在配置文件中定义任意数量的数据源。

## 技术栈

- **Spring Boot**：用于构建核心应用程序。即该组件适合用于由 `SpringBoot`**`、`SpringCloud` 框架开发的系统使用
- **Spring Data JDBC**：通过 JDBC 进行数据库交互。
- **HikariCP**：高效的连接池。
- **Spring AOP**：用于创建动态数据源路由切换的切面。
- **Tomcat**：嵌入式服务器，用于运行应用程序。

## 项目结构

```
src/
├── main/
│   ├── java/
│   │   ├── cn.onism.router/
│   │   │   ├── annotation/              # 包含数据源切换需使用的注解
│   │   │   ├── aspect/              # 包含数据源切换的切面
│   │   │   ├── config/              # 配置类
│   │   │   └── property/          # 路由数据源所需配置的必要参数
│   ├── resources/
│   │   ├── META-INF          # 存放元数据和配置
│   │         └── spring.factories/     # 自动配置元数据
└── /
```

## 使用方法

### 克隆仓库

```bash
git https://github.com/OnismExplorer/datasource-router.git
cd datasource-router
```
由于本组件并未上线 Maven 中央仓库，所以需要执行 `mvn clean install` 安装到本地进行使用

### 引入依赖

在需要使用组件的项目中的 pom.xml 文件中引入本地安装好的依赖
```xml
<dependency>
    <groupId>cn.onism</groupId>
    <artifactId>datasource-router</artifactId>
    <version>1.0</version>
</dependency>
```

### 配置不同数据源

在项目的 application.yml 文件中添加以下配置

``` yaml
datasource:
    hikari:
      maximumPoolSize: 20
      minimumIdle: 5
      connectionTimeout: 5000
      idleTimeout: 300000
      maxLifetime: 1800000
      validationTimeout: 3000
      leakDetectionThreshold: 2000
  jpa:
    hibernate:
      ddl-auto: update # 设置数据库模式更新策略
    open-in-view: false
```

下面则是配置数据源的示例，注意，`default`默认数据源一定要有，否则会报错。当没有其他数据源时模式使用的是`default`数据源

```yaml
datasources:
  datasource:
    - name: default
      username: root
      password: 123456
      url: jdbc:mysql://localhost:3306/system?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf-8&rewriteBatchedStatements=true
      driver-class-name: com.mysql.cj.jdbc.Driver
    - name: user
      username: root
      password: 123456
      url: jdbc:mysql://localhost:3307/user?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf-8&rewriteBatchedStatements=true
      driver-class-name: com.mysql.cj.jdbc.Driver
    - name: order
      username: root
      password: 111111
      url: jdbc:sqlserver://localhost:1433;database=order;encrypt=false;trustServerCertificate=true;
      driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
```

### 使用/切换数据源

&emsp;&emsp;如同上面所说，默认情况下会从`default`配置的数据源中进行操作，所以使用/切换回 default 时，可以添加`@DataSource("default")`注解，也可以不添加注解(操作执行完成后会销毁当前线程，所以会自动切换回 default 数据源)。

```java
@DataSource("default")
public List<User> getList() {
    return userDao.getList();
}
```

&emsp;&emsp;如果是使用/切换到其他数据源，则是使用注解`@DataSource()`，其中的 value 为配置的`datasource.name`的值

```java
@DataSource("order")
public List<Order> getList() {
    return orderDao.getList();
}
```
&emsp;&emsp;当然，该注解不仅可以使用到需要切换数据源的方法上，还可以放在类上(表明该类中所有方法都使用该数据源)。

```java
@RestController
@RequestMapping("/user")
public class UserController {
    // 业务代码...
}
```

&emsp;&emsp;不仅可以在 Controller 层使用，也可以在 Service 层与 DAO 层使用，可以根据需求粒度自行选择使用时机

```java
// 在 Controller 使用
@RestController
@RequestMapping("/user")
@DataSource("system")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    @DataSource("user")
    public List<User> list() {
        return userService.page(new Page<>(1,10)).getRecords();
    }
}

// 在 Service 层使用，注意：只能在 Service 实现类中使用
@Service
@DataSource("order")
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
        implements OrderService{

    @Autowired
    private UserService userService;
    
    @Override
    @DataSource("user")
    public User getUserInfo(String uid) {
        return userService.getUserInfo(uid);
    }
}

// 在 DAO 层使用
public interface OrderMapper extends BaseMapper<Order> {

    @DataSource("order")
    List<Order> getOrder(String orderId);
}
```

&emsp;&emsp;同时，从上述代码可以看出，该组件可以和 MyBatis 与 MyBatis-Plus 结合使用

## 代码细节

&emsp;&emsp;该应用程序使用 Spring 的 `@Configuration` 和 `@EnableConfigurationProperties` 来动态加载多个数据源。以下是 `DataSourceAutoConfiguration.java` 中配置的简化版本：

```java
@Bean
public DataSource dataSource(DataSourceProperties dataSourceProperties) {
    // 加载和设置多个数据源的逻辑
}
```

&emsp;&emsp;使用 ThreadLocal 实现用于存储当前线程的数据源信息的`DataSourceContextHolder`上下文。

```java
/**
 * 数据源上下文持有者
 */
public class DataSourceContextHolder {
    
}
```

&emsp;&emsp;该配置定义了默认数据源，并将其他数据源注册到 `RoutingDataSource` 中。

### 数据源切换

&emsp;&emsp;使用自定义切面（`DataSourceAspect`）来动态切换数据源，并且使用`Optional`类解决可能出现的空指针问题。以下是如何使用 `@DataSource` 注解的示例：

```java
@Aspect
@Component
public class DataSourceAspect {

    @Pointcut("execution(* *(..)) && (@annotation(cn.onism.router.annotation.DataSource) || @within(cn.onism.router.annotation.DataSource))")
    public void dataSourcePointcut() {
    }

    /**
     * 切换数据源
     *
     * @param joinPoint 加入点
     */
    @Before("dataSourcePointcut()")
    public void switchDataSource(JoinPoint joinPoint) {
        Optional<DataSource> dataSource = getDataSourceAnnotation(joinPoint);
        dataSource.ifPresent(ds -> DataSourceContextHolder.setDataSource(ds.value()));
    }

    /**
     * 清除数据源
     *
     * @param joinPoint 加入点
     */
    @After("dataSourcePointcut()")
    public void clearDataSource(JoinPoint joinPoint) {
        DataSourceContextHolder.clearDataSource();
    }
}
```
