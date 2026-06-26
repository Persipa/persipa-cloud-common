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
                            .contains("demo-service Started.")
                            .contains("Version: 1.2.3");
                });
    }

    @Test
    void shouldPrintFallbackMessageWhenBuildPropertiesMissing(CapturedOutput output) {
        contextRunner.withPropertyValues("persipa.cloud.app-version.print=true")
                .run(context -> {
                    context.getBean(AppVersionPrintReadyListener.class).onApplicationEvent(null);

                    assertThat(output).contains(SEPARATOR)
                            .contains("Spring Application Started.");
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
            Properties properties = new Properties();
            properties.setProperty("name", "demo-service");
            properties.setProperty("version", "1.2.3");
            return new BuildProperties(properties);
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
}
