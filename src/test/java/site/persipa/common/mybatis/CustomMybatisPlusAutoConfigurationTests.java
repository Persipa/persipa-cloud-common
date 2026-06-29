package site.persipa.common.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author persipa
 */
class CustomMybatisPlusAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CustomMybatisPlusAutoConfiguration.class));

    @Test
    void shouldNotRegisterPaginationInterceptorByDefault() {
        contextRunner.run(context -> assertThat(context).doesNotHaveBean(MybatisPlusInterceptor.class));
    }

    @Test
    void shouldNotRegisterPaginationInterceptorWhenPaginationModuleIsMissing() {
        contextRunner.withClassLoader(new FilteredClassLoader(PaginationInnerInterceptor.class))
                .withPropertyValues("persipa.cloud.orm.mybatis.pagination.enabled=true")
                .run(context -> assertThat(context).doesNotHaveBean(MybatisPlusInterceptor.class));
    }

    @Test
    void shouldRegisterPaginationInterceptorWhenEnabled() {
        contextRunner.withPropertyValues("persipa.cloud.orm.mybatis.pagination.enabled=true")
                .run(context -> {
                    assertThat(context).hasSingleBean(MybatisPlusInterceptor.class);

                    PaginationInnerInterceptor pagination = getPaginationInterceptor(context
                            .getBean(MybatisPlusInterceptor.class));
                    assertThat(pagination.getDbType()).isNull();
                    assertThat(pagination.isOverflow()).isFalse();
                    assertThat(pagination.getMaxLimit()).isNull();
                });
    }

    @Test
    void shouldApplyPaginationProperties() {
        contextRunner.withPropertyValues(
                        "persipa.cloud.orm.mybatis.pagination.enabled=true",
                        "persipa.cloud.orm.mybatis.pagination.db-type=mysql",
                        "persipa.cloud.orm.mybatis.pagination.overflow=true",
                        "persipa.cloud.orm.mybatis.pagination.max-limit=200")
                .run(context -> {
                    PaginationInnerInterceptor pagination = getPaginationInterceptor(context
                            .getBean(MybatisPlusInterceptor.class));
                    assertThat(pagination.getDbType()).isEqualTo(DbType.MYSQL);
                    assertThat(pagination.isOverflow()).isTrue();
                    assertThat(pagination.getMaxLimit()).isEqualTo(200L);
                });
    }

    @Test
    void shouldBackOffWhenCustomInterceptorExists() {
        contextRunner.withPropertyValues("persipa.cloud.orm.mybatis.pagination.enabled=true")
                .withUserConfiguration(CustomInterceptorConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(MybatisPlusInterceptor.class);
                    assertThat(context.getBean(MybatisPlusInterceptor.class))
                            .isSameAs(context.getBean("customMybatisPlusInterceptor"));
                    assertThat(context.getBean(MybatisPlusInterceptor.class).getInterceptors()).isEmpty();
                });
    }

    private static PaginationInnerInterceptor getPaginationInterceptor(MybatisPlusInterceptor interceptor) {
        assertThat(interceptor.getInterceptors()).hasSize(1);
        assertThat(interceptor.getInterceptors().get(0)).isInstanceOf(PaginationInnerInterceptor.class);
        return (PaginationInnerInterceptor) interceptor.getInterceptors().get(0);
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomInterceptorConfiguration {

        @Bean
        MybatisPlusInterceptor customMybatisPlusInterceptor() {
            return new MybatisPlusInterceptor();
        }
    }
}
