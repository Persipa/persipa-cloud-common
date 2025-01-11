package site.persipa.common.core.dubbo;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * @author persipa
 */
@Configuration
@EnableDubbo
@ConditionalOnProperty(prefix = "persipa.cloud.dubbo", name = "enabled", matchIfMissing = true)
public class DubboConfiguration {
}
