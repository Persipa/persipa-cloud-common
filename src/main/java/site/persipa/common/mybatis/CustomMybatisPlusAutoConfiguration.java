package site.persipa.common.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

/**
 * @author persipa
 */
@AutoConfiguration
public class CustomMybatisPlusAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "persipa.cloud.orm.mybatis", name = "auto-fill-time",
            havingValue = "true", matchIfMissing = true)
    public MetaObjectHandler modifyTimeMetaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class);

            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
            }
        };
    }


}
