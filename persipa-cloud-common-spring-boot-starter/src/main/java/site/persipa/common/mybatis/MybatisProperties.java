package site.persipa.common.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
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

    /**
     * 分页插件配置。
     */
    private Pagination pagination = new Pagination();

    /**
     * MyBatis-Plus 分页插件配置。
     */
    @Data
    public static class Pagination {

        /**
         * 是否自动注册分页插件。
         */
        private boolean enabled = false;

        /**
         * 数据库类型，为空时由 MyBatis-Plus 自动识别。
         */
        private DbType dbType;

        /**
         * 查询页码超过总页数时是否回到第一页。
         */
        private boolean overflow = false;

        /**
         * 单页最大记录数，为空时不限制。
         */
        private Long maxLimit;
    }

}
