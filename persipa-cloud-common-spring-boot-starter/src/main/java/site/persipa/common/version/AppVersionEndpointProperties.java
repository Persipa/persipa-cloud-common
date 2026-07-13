package site.persipa.common.version;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 应用版本 HTTP endpoint 配置。
 *
 * @author persipa
 */
@Data
@ConfigurationProperties(prefix = "persipa.cloud.app-version.endpoint")
public class AppVersionEndpointProperties {

    /**
     * 是否启用应用版本 HTTP endpoint。
     */
    private boolean enabled = false;

    /**
     * 应用版本 HTTP endpoint 访问路径。
     */
    private String path = "/_version";

}
