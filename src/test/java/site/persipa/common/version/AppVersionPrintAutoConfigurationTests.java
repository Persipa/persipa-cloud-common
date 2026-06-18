package site.persipa.common.version;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    void shouldNotRegisterRunnerByDefault() {
        contextRunner.run(context -> assertThat(context).doesNotHaveBean(AppVersionPrintRunner.class));
    }

    @Test
    void shouldRegisterRunnerWhenPrintEnabled() {
        contextRunner.withPropertyValues("persipa.cloud.app-version.print=true")
                .run(context -> assertThat(context).hasSingleBean(AppVersionPrintRunner.class));
    }

    @Test
    void shouldPrintBuildPropertiesWhenAvailable(CapturedOutput output) {
        contextRunner.withPropertyValues("persipa.cloud.app-version.print=true")
                .withUserConfiguration(BuildPropertiesConfiguration.class)
                .run(context -> {
                    context.getBean(AppVersionPrintRunner.class).run(null);

                    assertThat(output).contains(SEPARATOR)
                            .contains("demo-service Started.")
                            .contains("Version: 1.2.3");
                });
    }

    @Test
    void shouldPrintFallbackMessageWhenBuildPropertiesMissing(CapturedOutput output) {
        contextRunner.withPropertyValues("persipa.cloud.app-version.print=true")
                .run(context -> {
                    context.getBean(AppVersionPrintRunner.class).run(null);

                    assertThat(output).contains(SEPARATOR)
                            .contains("Spring Application Started.");
                });
    }

    @Test
    void shouldBackOffWhenCustomRunnerExists() {
        contextRunner.withPropertyValues("persipa.cloud.app-version.print=true")
                .withUserConfiguration(CustomRunnerConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(AppVersionPrintRunner.class);
                    assertThat(context.getBean(AppVersionPrintRunner.class))
                            .isSameAs(context.getBean("customAppVersionPrintRunner"));
                });
    }

    @Test
    void shouldBackOffWhenCustomRunnerBeanNameExists() {
        contextRunner.withPropertyValues("persipa.cloud.app-version.print=true")
                .withUserConfiguration(CustomRunnerBeanNameConfiguration.class)
                .run(context -> {
                    assertThat(context).hasBean("appVersionPrintRunner");
                    assertThat(context).doesNotHaveBean(AppVersionPrintRunner.class);
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
    static class CustomRunnerConfiguration {

        @Bean
        AppVersionPrintRunner customAppVersionPrintRunner() {
            return new AppVersionPrintRunner(null) {
                @Override
                public void run(org.springframework.boot.ApplicationArguments args) {
                }
            };
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomRunnerBeanNameConfiguration {

        @Bean
        ApplicationRunner appVersionPrintRunner() {
            return args -> {
            };
        }
    }
}
