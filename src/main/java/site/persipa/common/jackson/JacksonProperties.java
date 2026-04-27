package site.persipa.common.jackson;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author persipa
 */
@Data
@ConfigurationProperties(prefix = "persipa.cloud.json.jackson")
public class JacksonProperties {

    /**
     * 是否注册 JavaTimeModule。
     */
    private boolean javaTimeModule = false;

}
