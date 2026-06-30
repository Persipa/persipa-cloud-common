package site.persipa.common.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                this.strictInsertFill(metaObject, properties.getUpdateTimeField(), Instant::now, Instant.class);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, properties.getUpdateTimeField(), Instant::now, Instant.class);
            }
        };
    }

    /**
     * MyBatis-Plus 分页插件自动配置。
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(PaginationInnerInterceptor.class)
    @ConditionalOnProperty(prefix = "persipa.cloud.orm.mybatis.pagination", name = "enabled",
            havingValue = "true")
    static class PaginationConfiguration {

        /**
         * 注册默认的 MyBatis-Plus 拦截器，并将分页插件加入插件链。
         */
        @Bean
        @ConditionalOnMissingBean(MybatisPlusInterceptor.class)
        MybatisPlusInterceptor mybatisPlusInterceptor(MybatisProperties properties) {
            MybatisProperties.Pagination pagination = properties.getPagination();
            PaginationInnerInterceptor paginationInterceptor = pagination.getDbType() == null
                    ? new PaginationInnerInterceptor()
                    : new PaginationInnerInterceptor(pagination.getDbType());
            paginationInterceptor.setOverflow(pagination.isOverflow());
            paginationInterceptor.setMaxLimit(pagination.getMaxLimit());

            MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
            interceptor.addInnerInterceptor(paginationInterceptor);
            return interceptor;
        }
    }

}
