package site.persipa.common.core.nacos;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * @author persipa
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "persipa.cloud.nacos.discovery", name = "enabled", matchIfMissing = true)
@EnableDiscoveryClient
@RefreshScope
public class NacosDiscoveryAutoConfiguration {
}


