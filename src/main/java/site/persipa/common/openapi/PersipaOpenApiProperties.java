package site.persipa.common.openapi;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author persipa
 */
@Data
@ConfigurationProperties(prefix = "persipa.cloud.openapi")
public class PersipaOpenApiProperties {

    /**
     * 是否启用 OpenAPI 自动配置。
     */
    private boolean enabled = false;

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

    /**
     * OpenAPI server 列表。
     */
    private List<ServerProperties> servers = new ArrayList<>();

    @Data
    public static class ServerProperties {

        /**
         * server 地址。
         */
        private String url;

        /**
         * server 描述。
         */
        private String description = "";

    }

}
