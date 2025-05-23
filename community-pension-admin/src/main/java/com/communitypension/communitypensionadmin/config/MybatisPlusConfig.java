package com.communitypension.communitypensionadmin.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;
import java.util.Map;
/**
 * MybatisPlus配置
 */
@EnableTransactionManagement
@Configuration
public class MybatisPlusConfig {
    /**
     * 新增分页插件，并设置数据库类型为mysql
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 添加分页插件
        PaginationInnerInterceptor pageInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        pageInterceptor.setMaxLimit(1000L);
        // 添加溢出处理（默认false，建议设为true）
        pageInterceptor.setOverflow(true);
        interceptor.addInnerInterceptor(pageInterceptor);
        
        // 添加防止全表更新与删除插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        
        // 添加动态表名插件，用于处理MySQL关键字
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        Map<String, TableNameHandler> tableNameHandlerMap = new HashMap<>();
        
        // 为message表添加特殊处理，将read字段用反引号包裹
        tableNameHandlerMap.put("message", (sql, tableName) -> tableName);
        
        dynamicTableNameInnerInterceptor.setTableNameHandler((sql, tableName) -> {
            // 这里可以对SQL进行处理，替换关键字
            if ("message".equals(tableName)) {
                // 将SQL中的read替换为`read`
                sql = sql.replaceAll("([^`])read([^`])", "$1`read`$2");
            }
            return tableName;
        });
        
        interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
        
        return interceptor;
    }
}
