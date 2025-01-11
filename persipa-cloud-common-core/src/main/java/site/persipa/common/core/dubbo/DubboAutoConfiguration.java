package site.persipa.common.core.dubbo;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * @author persipa
 */
@AutoConfiguration
@ConditionalOnClass(name = "org.apache.dubbo.config.spring.context.annotation.EnableDubbo")
@ConditionalOnProperty(prefix = "persipa.cloud.dubbo", name = "enabled", matchIfMissing = true)
public class DubboAutoConfiguration {

    @Configuration
    @EnableDubbo
    static class DubboConfiguration {
    }
}
