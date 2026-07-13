package site.persipa.common.version;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;

/**
 * 应用版本 HTTP endpoint 自动配置。
 *
 * @author persipa
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(name = {
        "org.springframework.web.bind.annotation.RestController",
        "org.springframework.web.servlet.DispatcherServlet"
})
@ConditionalOnProperty(prefix = "persipa.cloud.app-version.endpoint", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(AppVersionEndpointProperties.class)
public class AppVersionEndpointAutoConfiguration {

    /**
     * 创建应用版本 HTTP endpoint Controller。
     */
    @Bean
    @ConditionalOnMissingBean(AppVersionEndpointController.class)
    public AppVersionEndpointController appVersionEndpointController(
            ObjectProvider<BuildProperties> buildPropertiesProvider) {
        return new AppVersionEndpointController(buildPropertiesProvider);
    }
}
