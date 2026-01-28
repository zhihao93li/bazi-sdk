package com.tafu.bazi.sdk.spring.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 八字 SDK 配置属性
 * 
 * <p>配置前缀: bazi
 * 
 * <p>可配置项:
 * <ul>
 *   <li>cache.enabled: 是否启用缓存 (预留,当前版本未实现)</li>
 * </ul>
 * 
 * @author Tafu Team
 * @version 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "bazi")
public class BaziProperties {

    /**
     * 缓存配置
     */
    private Cache cache = new Cache();

    @Data
    public static class Cache {
        /**
         * 是否启用缓存
         * 默认: false (当前版本未实现缓存功能)
         */
        private boolean enabled = false;
    }
}
