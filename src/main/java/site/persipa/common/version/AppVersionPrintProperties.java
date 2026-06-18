package site.persipa.common.version;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author persipa
 */
@Data
@ConfigurationProperties(prefix = "persipa.cloud.app-version")
public class AppVersionPrintProperties {

    /**
     * 是否在应用启动完成后打印应用版本信息。
     */
    private boolean print = false;

}
