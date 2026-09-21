package site.persipa.common.web.cors;

import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 仅注册 Persipa Cloud Common 管理的 MVC CORS 规则。
 *
 * @author persipa
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
@RequiredArgsConstructor
public class PersipaWebCorsConfigurer implements WebMvcConfigurer {

    private final PersipaWebCorsProperties properties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(properties.getPathPattern())
                .allowedOrigins(properties.getAllowedOrigins().toArray(String[]::new))
                .allowedOriginPatterns(properties.getAllowedOriginPatterns().toArray(String[]::new))
                .allowedMethods(properties.getAllowedMethods().toArray(String[]::new))
                .allowedHeaders(properties.getAllowedHeaders().toArray(String[]::new))
                .exposedHeaders(properties.getExposedHeaders().toArray(String[]::new))
                .allowCredentials(properties.isAllowCredentials())
                .maxAge(properties.getMaxAge().toSeconds());
    }
}
