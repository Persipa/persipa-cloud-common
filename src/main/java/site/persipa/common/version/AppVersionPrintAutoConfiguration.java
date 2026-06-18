package site.persipa.common.version;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author persipa
 */
@AutoConfiguration
@EnableConfigurationProperties(AppVersionPrintProperties.class)
@ConditionalOnProperty(prefix = "persipa.cloud.app-version", name = "print", havingValue = "true")
public class AppVersionPrintAutoConfiguration {

    /**
     * 创建应用版本信息打印 Runner。
     */
    @Bean
    @ConditionalOnMissingBean(value = AppVersionPrintRunner.class, name = "appVersionPrintRunner")
    public AppVersionPrintRunner appVersionPrintRunner(ObjectProvider<BuildProperties> buildPropertiesProvider) {
        return new AppVersionPrintRunner(buildPropertiesProvider);
    }
}
