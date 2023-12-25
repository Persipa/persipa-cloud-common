package site.persipa.common.core.nacos;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author persipa
 */
@Configuration
@ConditionalOnProperty(prefix = "persipa.cloud.nacos.descovery", name = "enabled", matchIfMissing = true)
@EnableDiscoveryClient
@RefreshScope
public class NacosConfiguration {
}
