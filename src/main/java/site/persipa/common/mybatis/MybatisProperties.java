package site.persipa.common.mybatis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author persipa
 */
@Data
@ConfigurationProperties(prefix = "persipa.cloud.orm.mybatis")
public class MybatisProperties {

    /**
     * 是否启用 createTime 和 updateTime 自动填充。
     */
    private boolean autoFillTime = false;

    private String createTimeField = "createTime";

    private String updateTimeField = "updateTime";

}
