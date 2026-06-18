package site.persipa.common.version;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.info.BuildProperties;

/**
 * 在 Spring 应用启动后打印版本信息。
 *
 * @author persipa
 */
@Slf4j
public class AppVersionPrintRunner implements ApplicationRunner {

    private static final String SEPARATOR = "------------------------------------------------------------";

    private final ObjectProvider<BuildProperties> buildPropertiesProvider;

    public AppVersionPrintRunner(ObjectProvider<BuildProperties> buildPropertiesProvider) {
        this.buildPropertiesProvider = buildPropertiesProvider;
    }

    @Override
    public void run(ApplicationArguments args) {
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
}
