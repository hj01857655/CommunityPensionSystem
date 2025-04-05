package com.communitypension.communitypensionadmin.config;

import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 服务类型配置
 * 用于定义简单服务和复杂服务的分类
 */
@Configuration
public class ServiceTypeConfig {

    /**
     * 简单服务类型集合
     * 这些服务类型可以自动审核通过
     */
    private static final Set<String> SIMPLE_SERVICE_TYPES = new HashSet<>(Arrays.asList(
            "cleaning"  // 清洁服务
            // 可以根据需要添加更多简单服务类型
    ));

    /**
     * 复杂服务类型集合
     * 这些服务类型需要人工审核
     */
    private static final Set<String> COMPLEX_SERVICE_TYPES = new HashSet<>(Arrays.asList(
            "medical",  // 医疗服务
            "repair"    // 维修服务
            // 可以根据需要添加更多复杂服务类型
    ));

    /**
     * 判断服务类型是否为简单服务
     *
     * @param serviceType 服务类型
     * @return 是否为简单服务
     */
    public static boolean isSimpleService(String serviceType) {
        return SIMPLE_SERVICE_TYPES.contains(serviceType);
    }

    /**
     * 判断服务类型是否为复杂服务
     *
     * @param serviceType 服务类型
     * @return 是否为复杂服务
     */
    public static boolean isComplexService(String serviceType) {
        return COMPLEX_SERVICE_TYPES.contains(serviceType);
    }
}
