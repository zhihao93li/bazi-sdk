package com.tafu.bazi.sdk.spring.autoconfigure;

import com.tafu.bazi.sdk.BaziCalculator;
import com.tafu.bazi.sdk.BaziCalculatorImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 八字 SDK 自动配置类
 * 
 * <p>自动配置条件:
 * <ul>
 *   <li>Classpath 中存在 BaziCalculator 类</li>
 *   <li>用户未自定义 BaziCalculator Bean</li>
 * </ul>
 * 
 * <p>配置项: 详见 {@link BaziProperties}
 * 
 * @author Tafu Team
 * @version 1.0.0
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(BaziCalculator.class)
@EnableConfigurationProperties(BaziProperties.class)
public class BaziAutoConfiguration {

    /**
     * 创建 BaziCalculator Bean
     * 
     * @param properties 配置属性
     * @return BaziCalculator 实例
     */
    @Bean
    @ConditionalOnMissingBean(BaziCalculator.class)
    public BaziCalculator baziCalculator(BaziProperties properties) {
        log.info("Auto-configuring BaziCalculator with properties: {}", properties);
        
        // 如果未来实现了缓存功能,可以在这里根据 properties.getCache().isEnabled() 创建不同的实现
        return new BaziCalculatorImpl();
    }
}
