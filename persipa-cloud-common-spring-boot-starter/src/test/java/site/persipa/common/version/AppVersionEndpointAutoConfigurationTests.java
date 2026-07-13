package site.persipa.common.version;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.webmvc.autoconfigure.WebMvcAutoConfiguration;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author persipa
 */
class AppVersionEndpointAutoConfigurationTests {

    private final WebApplicationContextRunner webContextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    WebMvcAutoConfiguration.class,
                    AppVersionEndpointAutoConfiguration.class));

    @Test
    void shouldNotRegisterEndpointByDefault() {
        webContextRunner.run(context ->
                assertThat(context).doesNotHaveBean(AppVersionEndpointController.class));
    }

    @Test
    void shouldNotRegisterEndpointWhenDisabled() {
        webContextRunner.withPropertyValues("persipa.cloud.app-version.endpoint.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(AppVersionEndpointController.class));
    }

    @Test
    void shouldRegisterEndpointWhenEnabled() {
        webContextRunner.withPropertyValues("persipa.cloud.app-version.endpoint.enabled=true")
                .run(context -> assertThat(context).hasSingleBean(AppVersionEndpointController.class));
    }

    @Test
    void shouldReturnBuildVersionFromDefaultPath() {
        webContextRunner.withPropertyValues("persipa.cloud.app-version.endpoint.enabled=true")
                .withUserConfiguration(BuildPropertiesConfiguration.class)
                .run(context -> performGet(context, "/_version")
                        .andExpect(status().isOk())
                        .andExpect(content().json("{\"version\":\"1.2.3\"}")));
    }

    @Test
    void shouldUseCustomEndpointPath() {
        webContextRunner.withPropertyValues(
                        "persipa.cloud.app-version.endpoint.enabled=true",
                        "persipa.cloud.app-version.endpoint.path=/internal/version")
                .withUserConfiguration(BuildPropertiesConfiguration.class)
                .run(context -> performGet(context, "/internal/version")
                        .andExpect(status().isOk())
                        .andExpect(content().json("{\"version\":\"1.2.3\"}")));
    }

    @Test
    void shouldReturnServiceUnavailableWhenBuildPropertiesMissing() {
        webContextRunner.withPropertyValues("persipa.cloud.app-version.endpoint.enabled=true")
                .run(context -> {
                    AppVersionEndpointController controller = context.getBean(AppVersionEndpointController.class);

                    assertThatThrownBy(controller::getVersion)
                            .isInstanceOf(ResponseStatusException.class)
                            .extracting(exception -> ((ResponseStatusException) exception).getStatusCode())
                            .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
                });
    }

    @Test
    void shouldReturnServiceUnavailableWhenBuildVersionMissing() {
        webContextRunner.withPropertyValues("persipa.cloud.app-version.endpoint.enabled=true")
                .withUserConfiguration(BuildPropertiesWithoutVersionConfiguration.class)
                .run(context -> {
                    AppVersionEndpointController controller = context.getBean(AppVersionEndpointController.class);

                    assertThatThrownBy(controller::getVersion)
                            .isInstanceOf(ResponseStatusException.class)
                            .extracting(exception -> ((ResponseStatusException) exception).getStatusCode())
                            .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
                });
    }

    @Test
    void shouldNotRegisterEndpointOutsideServletWebApplication() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(AppVersionEndpointAutoConfiguration.class))
                .withPropertyValues("persipa.cloud.app-version.endpoint.enabled=true")
                .run(context -> assertThat(context).doesNotHaveBean(AppVersionEndpointController.class));
    }

    @Test
    void shouldNotRegisterEndpointWhenDispatcherServletIsMissing() {
        webContextRunner.withClassLoader(new FilteredClassLoader("org.springframework.web.servlet.DispatcherServlet"))
                .withPropertyValues("persipa.cloud.app-version.endpoint.enabled=true")
                .run(context -> assertThat(context).doesNotHaveBean(AppVersionEndpointController.class));
    }

    @Test
    void shouldBackOffWhenCustomEndpointExists() {
        webContextRunner.withPropertyValues("persipa.cloud.app-version.endpoint.enabled=true")
                .withUserConfiguration(CustomEndpointConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(AppVersionEndpointController.class);
                    assertThat(context.getBean(AppVersionEndpointController.class))
                            .isSameAs(context.getBean("customAppVersionEndpointController"));
                });
    }

    private static ResultActions performGet(WebApplicationContext context, String path) throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        return mockMvc.perform(get(path));
    }

    @Configuration(proxyBeanMethods = false)
    static class BuildPropertiesConfiguration {

        @Bean
        BuildProperties buildProperties() {
            return createBuildProperties("1.2.3");
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class BuildPropertiesWithoutVersionConfiguration {

        @Bean
        BuildProperties buildProperties() {
            return createBuildProperties(null);
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomEndpointConfiguration {

        @Bean
        AppVersionEndpointController customAppVersionEndpointController(
                ObjectProvider<BuildProperties> buildPropertiesProvider) {
            return new AppVersionEndpointController(buildPropertiesProvider);
        }
    }

    private static BuildProperties createBuildProperties(String version) {
        Properties properties = new Properties();
        if (version != null) {
            properties.setProperty("version", version);
        }
        return new BuildProperties(properties);
    }
}
