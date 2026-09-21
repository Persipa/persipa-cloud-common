package site.persipa.common.web.cors;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.webmvc.autoconfigure.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author persipa
 */
class PersipaWebCorsAutoConfigurationTests {

    private final WebApplicationContextRunner webContextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    WebMvcAutoConfiguration.class,
                    PersipaWebCorsAutoConfiguration.class))
            .withUserConfiguration(TestControllerConfiguration.class);

    @Test
    void shouldNotRegisterCorsConfigurerByDefault() {
        webContextRunner.run(context ->
                assertThat(context).doesNotHaveBean(PersipaWebCorsConfigurer.class));
    }

    @Test
    void shouldApplyConfiguredCorsMapping() {
        webContextRunner.withPropertyValues(
                        "persipa.cloud.web.cors.enabled=true",
                        "persipa.cloud.web.cors.path-pattern=/api/**",
                        "persipa.cloud.web.cors.allowed-origins=https://allowed.example.com",
                        "persipa.cloud.web.cors.allowed-origin-patterns=https://*.pattern.example.com",
                        "persipa.cloud.web.cors.allowed-methods=GET,POST",
                        "persipa.cloud.web.cors.allowed-headers=X-Request-Id",
                        "persipa.cloud.web.cors.exposed-headers=X-Trace-Id",
                        "persipa.cloud.web.cors.allow-credentials=true",
                        "persipa.cloud.web.cors.max-age=1h")
                .run(context -> {
                    performGet(context, "/api/orders", "https://allowed.example.com")
                            .andExpect(status().isOk())
                            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                                    "https://allowed.example.com"))
                            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"))
                            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "X-Trace-Id"));

                    performGet(context, "/api/orders", "https://shop.pattern.example.com")
                            .andExpect(status().isOk())
                            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                                    "https://shop.pattern.example.com"));

                    performPreflight(context, "/api/orders", "https://allowed.example.com")
                            .andExpect(status().isOk())
                            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                                    "https://allowed.example.com"))
                            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"))
                            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST"))
                            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "X-Request-Id"))
                            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600"));
                });
    }

    @Test
    void shouldRejectDisallowedOrigin() {
        webContextRunner.withPropertyValues(
                        "persipa.cloud.web.cors.enabled=true",
                        "persipa.cloud.web.cors.allowed-origins=https://allowed.example.com")
                .run(context -> performGet(context, "/api/orders", "https://rejected.example.com")
                        .andExpect(status().isForbidden()));
    }

    @Test
    void shouldRejectRequestsOutsideConfiguredPath() {
        webContextRunner.withPropertyValues(
                        "persipa.cloud.web.cors.enabled=true",
                        "persipa.cloud.web.cors.path-pattern=/api/**",
                        "persipa.cloud.web.cors.allowed-origins=https://allowed.example.com")
                .run(context -> performGet(context, "/internal/orders", "https://allowed.example.com")
                        .andExpect(status().isOk())
                        .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)));
    }

    @Test
    void shouldFailWhenEnabledWithoutAnEffectiveOrigin() {
        webContextRunner.withPropertyValues("persipa.cloud.web.cors.enabled=true")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure()).hasMessageContaining("allowed-origins");
                });
    }

    @Test
    void shouldFailWhenCredentialsAreAllowedForWildcardOrigin() {
        webContextRunner.withPropertyValues(
                        "persipa.cloud.web.cors.enabled=true",
                        "persipa.cloud.web.cors.allowed-origins=*",
                        "persipa.cloud.web.cors.allow-credentials=true")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure()).hasMessageContaining("allow-credentials");
                });
    }

    @Test
    void shouldNotRegisterCorsConfigurerOutsideServletWebApplication() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(PersipaWebCorsAutoConfiguration.class))
                .withPropertyValues(
                        "persipa.cloud.web.cors.enabled=true",
                        "persipa.cloud.web.cors.allowed-origins=https://allowed.example.com")
                .run(context -> assertThat(context).doesNotHaveBean(PersipaWebCorsConfigurer.class));
    }

    @Test
    void shouldNotRegisterCorsConfigurerWhenMvcIsMissing() {
        webContextRunner.withClassLoader(new FilteredClassLoader(WebMvcConfigurer.class))
                .withPropertyValues(
                        "persipa.cloud.web.cors.enabled=true",
                        "persipa.cloud.web.cors.allowed-origins=https://allowed.example.com")
                .run(context -> assertThat(context).doesNotHaveBean(PersipaWebCorsConfigurer.class));
    }

    @Test
    void shouldBackOffWhenDedicatedCorsConfigurerExists() {
        webContextRunner.withPropertyValues(
                        "persipa.cloud.web.cors.enabled=true",
                        "persipa.cloud.web.cors.allowed-origins=https://allowed.example.com")
                .withUserConfiguration(CustomCorsConfigurerConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(PersipaWebCorsConfigurer.class);
                    assertThat(context.getBean(PersipaWebCorsConfigurer.class))
                            .isSameAs(context.getBean("customCorsConfigurer"));
                    performGet(context, "/api/orders", "https://custom.example.com")
                            .andExpect(status().isOk())
                            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                                    "https://custom.example.com"));
                    performGet(context, "/api/orders", "https://allowed.example.com")
                            .andExpect(status().isForbidden());
                });
    }

    @Test
    void shouldNotBackOffForOrdinaryBusinessWebMvcConfigurer() {
        webContextRunner.withPropertyValues(
                        "persipa.cloud.web.cors.enabled=true",
                        "persipa.cloud.web.cors.allowed-origins=https://allowed.example.com")
                .withUserConfiguration(OrdinaryBusinessWebMvcConfigurerConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(PersipaWebCorsConfigurer.class);
                    performGet(context, "/api/orders", "https://allowed.example.com")
                            .andExpect(status().isOk())
                            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                                    "https://allowed.example.com"));
                });
    }

    @Test
    void shouldAllowBusinessConfigurerToOverrideSamePathCorsMapping() {
        webContextRunner.withPropertyValues(
                        "persipa.cloud.web.cors.enabled=true",
                        "persipa.cloud.web.cors.path-pattern=/api/**",
                        "persipa.cloud.web.cors.allowed-origins=https://default.example.com")
                .withUserConfiguration(OverridingBusinessWebMvcConfigurerConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(PersipaWebCorsConfigurer.class);
                    performGet(context, "/api/orders", "https://business.example.com")
                            .andExpect(status().isOk())
                            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                                    "https://business.example.com"));
                    performGet(context, "/api/orders", "https://default.example.com")
                            .andExpect(status().isForbidden());
                });
    }

    private static ResultActions performGet(WebApplicationContext context, String path, String origin) throws Exception {
        return mockMvc(context).perform(get(path).header(HttpHeaders.ORIGIN, origin));
    }

    private static ResultActions performPreflight(WebApplicationContext context, String path, String origin)
            throws Exception {
        return mockMvc(context).perform(options(path)
                .header(HttpHeaders.ORIGIN, origin)
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "X-Request-Id"));
    }

    private static MockMvc mockMvc(WebApplicationContext context) {
        return MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Configuration(proxyBeanMethods = false)
    static class TestControllerConfiguration {

        @Bean
        TestController testController() {
            return new TestController();
        }
    }

    @RestController
    static class TestController {

        @GetMapping({"/api/orders", "/internal/orders"})
        String orders() {
            return "ok";
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomCorsConfigurerConfiguration {

        @Bean
        PersipaWebCorsConfigurer customCorsConfigurer() {
            PersipaWebCorsProperties properties = new PersipaWebCorsProperties();
            properties.setAllowedOrigins(List.of("https://custom.example.com"));
            return new PersipaWebCorsConfigurer(properties);
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class OrdinaryBusinessWebMvcConfigurerConfiguration {

        @Bean
        WebMvcConfigurer ordinaryBusinessWebMvcConfigurer() {
            return new WebMvcConfigurer() {
                @Override
                public void addCorsMappings(CorsRegistry registry) {
                }
            };
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class OverridingBusinessWebMvcConfigurerConfiguration {

        @Bean
        WebMvcConfigurer overridingBusinessWebMvcConfigurer() {
            return new WebMvcConfigurer() {
                @Override
                public void addCorsMappings(CorsRegistry registry) {
                    registry.addMapping("/api/**").allowedOrigins("https://business.example.com");
                }
            };
        }
    }
}
