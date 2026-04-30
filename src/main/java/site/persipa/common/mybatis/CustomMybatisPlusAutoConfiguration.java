package site.persipa.common.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.time.Instant;

/**
 * @author persipa
 */
@AutoConfiguration
@ConditionalOnClass(MetaObjectHandler.class)
@EnableConfigurationProperties(MybatisProperties.class)
public class CustomMybatisPlusAutoConfiguration {

    /**
     * 创建默认字段填充处理器，自动维护 createTime 和 updateTime。
     */
    @Bean
    @ConditionalOnMissingBean(MetaObjectHandler.class)
    @ConditionalOnProperty(prefix = "persipa.cloud.orm.mybatis", name = "auto-fill-time",
            havingValue = "true")
    public MetaObjectHandler modifyTimeMetaObjectHandler(MybatisProperties properties) {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, properties.getCreateTimeField(), Instant::now, Instant.class);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, properties.getUpdateTimeField(), Instant::now, Instant.class);
            }
        };
    }


}
