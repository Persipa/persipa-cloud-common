package site.persipa.common.openapi;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author persipa
 */
@Data
@ConfigurationProperties(prefix = "persipa.cloud.openapi")
public class PersipaOpenApiProperties {

    /**
     * 文档标题。
     */
    private String title = "API Documentation";

    /**
     * 文档描述。
     */
    private String description = "";

    /**
     * 文档版本。
     */
    private String version = "v1";

    /**
     * 服务条款地址。
     */
    private String termsOfService = "";

}
