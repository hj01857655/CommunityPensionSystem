package com.communitypension.communitypensionadmin.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
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

        PaginationInnerInterceptor pageInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        pageInterceptor.setMaxLimit(1000L);
        // 添加溢出处理（默认false，建议设为true）
        pageInterceptor.setOverflow(true);
        // 添加数据库类型配置（已设置MySQL）
        interceptor.addInnerInterceptor(pageInterceptor);
        return interceptor;
    }
}
