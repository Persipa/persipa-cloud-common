package site.persipa.common.core.orm.mybatis;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * @author persipa
 */
@AutoConfiguration
public class CustomMybatisPlusAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "persipa.cloud.orm.mybatis", name = "auto-fill-time",
            havingValue = "true", matchIfMissing = true)
    public ModifyTimeMetaObjectHandler modifyTimeMetaObjectHandler() {
        return new ModifyTimeMetaObjectHandler();
    }


}
