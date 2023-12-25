package site.persipa.common.core.nacos;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * @author persipa
 */
@Configuration
@ConditionalOnProperty(prefix = "persipa.cloud.auto-project-name",name = "enabled",matchIfMissing = true)
public class ProjectNacosNameConfiguration implements EnvironmentAware {

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public void setEnvironment(Environment environment) {
        if (!StringUtils.hasText(System.getProperty("project.name"))) {
            System.setProperty("project.name", applicationName);
        }
    }
}
