package cn.onism.router.config;

/**
 * 数据源上下文持有者
 *
 * @author HeXin
 * @date 2024/06/02
 */
public class DataSourceContextHolder {

    private DataSourceContextHolder(){

    }

    /**
     * 上下文持有者(开一个线程)
     */
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    public static void setDataSource(String dataSourceType) {
        CONTEXT_HOLDER.set(dataSourceType);
    }

    public static String getDataSource() {
        return CONTEXT_HOLDER.get();
    }

    public static void clearDataSource() {
        CONTEXT_HOLDER.remove();
    }
}
