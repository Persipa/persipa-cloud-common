package site.persipa.common.version;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author persipa
 */
@ExtendWith(OutputCaptureExtension.class)
class AppVersionPrintAutoConfigurationTests {

    private static final String SEPARATOR = "------------------------------------------------------------";

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AppVersionPrintAutoConfiguration.class));

    @Test
    void shouldNotRegisterReadyListenerByDefault() {
        contextRunner.run(context -> assertThat(context).doesNotHaveBean(AppVersionPrintReadyListener.class));
    }

    @Test
    void shouldRegisterReadyListenerWhenPrintEnabled() {
        contextRunner.withPropertyValues("persipa.cloud.app-version.print=true")
                .run(context -> assertThat(context).hasSingleBean(AppVersionPrintReadyListener.class));
    }

    @Test
    void shouldRunWithLowestPrecedenceOrder() {
        contextRunner.withPropertyValues("persipa.cloud.app-version.print=true")
                .run(context -> assertThat(context.getBean(AppVersionPrintReadyListener.class).getOrder())
                        .isEqualTo(Ordered.LOWEST_PRECEDENCE));
    }

    @Test
    void shouldPrintBuildPropertiesWhenAvailable(CapturedOutput output) {
        contextRunner.withPropertyValues("persipa.cloud.app-version.print=true")
                .withUserConfiguration(BuildPropertiesConfiguration.class)
                .run(context -> {
                    context.getBean(AppVersionPrintReadyListener.class).onApplicationEvent(null);

                    assertThat(output).contains(SEPARATOR)
                            .contains("Spring Application Started.")
                            .contains("AppName: \033[32mdemo-service\033[0m")
                            .contains("Version: \033[32m1.2.3\033[0m");
                });
    }

    @Test
    void shouldPrintFallbackMessageWhenBuildPropertiesMissing(CapturedOutput output) {
        contextRunner.withPropertyValues("persipa.cloud.app-version.print=true")
                .run(context -> {
                    context.getBean(AppVersionPrintReadyListener.class).onApplicationEvent(null);

                    assertThat(output).contains(SEPARATOR)
                            .contains("Spring Application Started.")
                            .doesNotContain("AppName:")
                            .doesNotContain("Version:");
                });
    }

    @Test
    void shouldSkipAppNameWhenBuildNameMissing(CapturedOutput output) {
        contextRunner.withPropertyValues("persipa.cloud.app-version.print=true")
                .withUserConfiguration(BuildPropertiesWithoutNameConfiguration.class)
                .run(context -> {
                    context.getBean(AppVersionPrintReadyListener.class).onApplicationEvent(null);

                    assertThat(output).contains("Spring Application Started.")
                            .doesNotContain("AppName:")
                            .contains("Version: \033[32m1.2.3\033[0m");
                });
    }

    @Test
    void shouldSkipVersionWhenBuildVersionMissing(CapturedOutput output) {
        contextRunner.withPropertyValues("persipa.cloud.app-version.print=true")
                .withUserConfiguration(BuildPropertiesWithoutVersionConfiguration.class)
                .run(context -> {
                    context.getBean(AppVersionPrintReadyListener.class).onApplicationEvent(null);

                    assertThat(output).contains("Spring Application Started.")
                            .contains("AppName: \033[32mdemo-service\033[0m")
                            .doesNotContain("Version:");
                });
    }

    @Test
    void shouldBackOffWhenCustomReadyListenerExists() {
        contextRunner.withPropertyValues("persipa.cloud.app-version.print=true")
                .withUserConfiguration(CustomReadyListenerConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(AppVersionPrintReadyListener.class);
                    assertThat(context.getBean(AppVersionPrintReadyListener.class))
                            .isSameAs(context.getBean("customAppVersionPrintReadyListener"));
                });
    }

    @Test
    void shouldBackOffWhenCustomReadyListenerBeanNameExists() {
        contextRunner.withPropertyValues("persipa.cloud.app-version.print=true")
                .withUserConfiguration(CustomReadyListenerBeanNameConfiguration.class)
                .run(context -> {
                    assertThat(context).hasBean("appVersionPrintReadyListener");
                    assertThat(context).doesNotHaveBean(AppVersionPrintReadyListener.class);
                });
    }

    @Configuration(proxyBeanMethods = false)
    static class BuildPropertiesConfiguration {

        @Bean
        BuildProperties buildProperties() {
            return createBuildProperties("demo-service", "1.2.3");
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class BuildPropertiesWithoutNameConfiguration {

        @Bean
        BuildProperties buildProperties() {
            return createBuildProperties(null, "1.2.3");
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class BuildPropertiesWithoutVersionConfiguration {

        @Bean
        BuildProperties buildProperties() {
            return createBuildProperties("demo-service", null);
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomReadyListenerConfiguration {

        @Bean
        AppVersionPrintReadyListener customAppVersionPrintReadyListener() {
            return new AppVersionPrintReadyListener(null) {
                @Override
                public void onApplicationEvent(ApplicationReadyEvent event) {
                }
            };
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomReadyListenerBeanNameConfiguration {

        @Bean
        ApplicationListener<ApplicationReadyEvent> appVersionPrintReadyListener() {
            return event -> {
            };
        }
    }

    private static BuildProperties createBuildProperties(String name, String version) {
        Properties properties = new Properties();
        if (name != null) {
            properties.setProperty("name", name);
        }
        if (version != null) {
            properties.setProperty("version", version);
        }
        return new BuildProperties(properties);
    }
}
