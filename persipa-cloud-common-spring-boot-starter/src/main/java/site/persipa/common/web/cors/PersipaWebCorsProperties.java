package site.persipa.common.web.cors;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.List;

/**
 * MVC CORS 配置属性。
 *
 * @author persipa
 */
@ConfigurationProperties(prefix = "persipa.cloud.web.cors")
@Data
public class PersipaWebCorsProperties {

    private boolean enabled;

    private String pathPattern = "/**";

    private List<String> allowedOrigins = List.of();

    private List<String> allowedOriginPatterns = List.of();

    private List<String> allowedMethods = List.of("GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");

    private List<String> allowedHeaders = List.of("*");

    private List<String> exposedHeaders = List.of();

    private boolean allowCredentials;

    private Duration maxAge = Duration.ofHours(1);

    public void setPathPattern(String pathPattern) {
        this.pathPattern = requireText(pathPattern, "path-pattern");
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = normalize(allowedOrigins, "allowed-origins");
    }

    public void setAllowedOriginPatterns(List<String> allowedOriginPatterns) {
        this.allowedOriginPatterns = normalize(allowedOriginPatterns, "allowed-origin-patterns");
    }

    public void setAllowedMethods(List<String> allowedMethods) {
        this.allowedMethods = normalize(allowedMethods, "allowed-methods");
    }

    public void setAllowedHeaders(List<String> allowedHeaders) {
        this.allowedHeaders = normalize(allowedHeaders, "allowed-headers");
    }

    public void setExposedHeaders(List<String> exposedHeaders) {
        this.exposedHeaders = normalize(exposedHeaders, "exposed-headers");
    }

    public void setMaxAge(Duration maxAge) {
        if (maxAge == null || maxAge.isZero() || maxAge.isNegative()) {
            throw new IllegalArgumentException("max-age 必须是正 Duration");
        }
        this.maxAge = maxAge;
    }

    /**
     * 校验启用 CORS 时的安全约束。
     */
    public void validate() {
        if (allowedOrigins.isEmpty() && allowedOriginPatterns.isEmpty()) {
            throw new IllegalStateException("启用 persipa.cloud.web.cors 时必须配置 allowed-origins 或 allowed-origin-patterns");
        }
        if (allowCredentials && allowedOrigins.contains("*")) {
            throw new IllegalStateException("allow-credentials=true 时 allowed-origins 不能包含 '*'");
        }
    }

    private static List<String> normalize(List<String> values, String propertyName) {
        if (values == null) {
            return List.of();
        }
        return values.stream()
                .map(value -> requireText(value, propertyName))
                .toList();
    }

    private static String requireText(String value, String propertyName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(propertyName + " 不能包含空字符串");
        }
        return value.trim();
    }
}
