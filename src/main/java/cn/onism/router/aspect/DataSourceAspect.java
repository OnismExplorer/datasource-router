package cn.onism.router.aspect;

import cn.onism.router.annotation.DataSource;
import cn.onism.router.config.DataSourceContextHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 数据源路由切面
 *
 * @author HeXin
 * @date 2024/06/02
 */
@Aspect
@Order(1)
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

    /**
     * 获取数据源注解
     *
     * @param joinPoint 加入点
     * @return {@link Optional }<{@link DataSource }>
     */
    private Optional<DataSource> getDataSourceAnnotation(JoinPoint joinPoint) {
        // 优先获取方法上的注解
        DataSource methodDataSource = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(DataSource.class);
        if (methodDataSource != null) {
            return Optional.of(methodDataSource);
        }
        // 再获取类上的注解
        DataSource classDataSource = joinPoint.getTarget().getClass().getAnnotation(DataSource.class);
        return Optional.ofNullable(classDataSource);
    }

}
