package site.persipa.common.web.cors;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVC CORS 自动配置。
 *
 * @author persipa
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({WebMvcConfigurer.class, CorsRegistry.class})
@ConditionalOnProperty(prefix = "persipa.cloud.web.cors", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(PersipaWebCorsProperties.class)
public class PersipaWebCorsAutoConfiguration {

    /**
     * 创建独立的 CORS MVC 配置器，不因应用的其他 WebMvcConfigurer 而退让。
     */
    @Bean
    @ConditionalOnMissingBean(PersipaWebCorsConfigurer.class)
    public PersipaWebCorsConfigurer persipaWebCorsConfigurer(PersipaWebCorsProperties properties) {
        properties.validate();
        return new PersipaWebCorsConfigurer(properties);
    }
}
