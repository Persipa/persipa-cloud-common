package site.persipa.common.version;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;

/**
 * 在 Spring 应用 Ready 后打印版本信息。
 *
 * @author persipa
 */
@Slf4j
public class AppVersionPrintReadyListener implements ApplicationListener<ApplicationReadyEvent>, Ordered {

    private static final String SEPARATOR = "------------------------------------------------------------";

    private final ObjectProvider<BuildProperties> buildPropertiesProvider;

    public AppVersionPrintReadyListener(ObjectProvider<BuildProperties> buildPropertiesProvider) {
        this.buildPropertiesProvider = buildPropertiesProvider;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        BuildProperties buildProperties = buildPropertiesProvider.getIfAvailable();
        if (buildProperties != null) {
            log.info("""
                    {}
                    Spring Application {} Started.
                    Version: {}
                    {}""", SEPARATOR, buildProperties.getName(), buildProperties.getVersion(), SEPARATOR);
            return;
        }

        log.info("""
                {}
                Spring Application Started.
                {}""", SEPARATOR, SEPARATOR);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
