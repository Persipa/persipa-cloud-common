package site.persipa.common.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author persipa
 */
@AutoConfiguration
@ConditionalOnClass(OpenAPI.class)
@EnableConfigurationProperties(PersipaOpenApiProperties.class)
@ConditionalOnProperty(prefix = "persipa.cloud.openapi", name = "enabled", havingValue = "true",
        matchIfMissing = false)
public class SpringDocAutoConfiguration {

    /**
     * 创建默认 OpenAPI 文档信息，便于下游服务统一接入。
     */
    @Bean
    @ConditionalOnMissingBean(OpenAPI.class)
    public OpenAPI persipaCommonOpenAPI(PersipaOpenApiProperties properties) {
        Info info = new Info()
                .title(properties.getTitle())
                .version(properties.getVersion());

        if (StringUtils.hasText(properties.getDescription())) {
            info.setDescription(properties.getDescription());
        }

        if (StringUtils.hasText(properties.getTermsOfService())) {
            info.setTermsOfService(properties.getTermsOfService());
        }

        OpenAPI openAPI = new OpenAPI().info(info);
        if (!CollectionUtils.isEmpty(properties.getServers())) {
            List<Server> servers = properties.getServers().stream()
                    .filter(serverProperties -> StringUtils.hasText(serverProperties.getUrl()))
                    .map(serverProperties -> {
                        Server server = new Server().url(serverProperties.getUrl());
                        if (StringUtils.hasText(serverProperties.getDescription())) {
                            server.setDescription(serverProperties.getDescription());
                        }
                        return server;
                    })
                    .toList();

            if (!servers.isEmpty()) {
                openAPI.setServers(servers);
            }
        }

        return openAPI;
    }
}
