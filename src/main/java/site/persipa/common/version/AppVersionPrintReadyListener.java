package site.persipa.common.version;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

/**
 * 在 Spring 应用 Ready 后打印版本信息。
 *
 * @author persipa
 */
public class AppVersionPrintReadyListener implements ApplicationListener<ApplicationReadyEvent>, Ordered {

    private static final String SEPARATOR = "------------------------------------------------------------";
    private static final String GREEN = "\033[32m";
    private static final String RESET = "\033[0m";

    private final ObjectProvider<BuildProperties> buildPropertiesProvider;

    public AppVersionPrintReadyListener(ObjectProvider<BuildProperties> buildPropertiesProvider) {
        this.buildPropertiesProvider = buildPropertiesProvider;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        StringBuilder message = new StringBuilder()
                .append(SEPARATOR).append(System.lineSeparator())
                .append("Spring Application Started.").append(System.lineSeparator());

        BuildProperties buildProperties = buildPropertiesProvider.getIfAvailable();
        if (buildProperties != null) {
            if (StringUtils.hasText(buildProperties.getName())) {
                message.append("AppName: ").append(GREEN).append(buildProperties.getName()).append(RESET)
                        .append(System.lineSeparator());
            }
            if (StringUtils.hasText(buildProperties.getVersion())) {
                message.append("Version: ").append(GREEN).append(buildProperties.getVersion()).append(RESET)
                        .append(System.lineSeparator());
            }
        }

        message.append(SEPARATOR).append(System.lineSeparator());
        System.out.print(message);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
