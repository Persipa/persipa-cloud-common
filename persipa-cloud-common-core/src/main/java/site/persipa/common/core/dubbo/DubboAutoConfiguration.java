package site.persipa.common.core.dubbo;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * @author persipa
 */
@AutoConfiguration
@ConditionalOnClass(name = "org.apache.dubbo.config.spring.ReferenceBean")
@ConditionalOnProperty(prefix = "persipa.cloud.dubbo", name = "enabled", matchIfMissing = true)
@EnableDubbo
public class DubboAutoConfiguration {
}
