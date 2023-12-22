package site.persipa.cloud.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author persipa
 */
@Getter
@AllArgsConstructor
public enum ResultLevelEnum {

    INFO(0, "info", "success"),

    WARNING(3, "warning", "warning"),

    EXCEPTION(6, "exception", "request error"),

    ERROR(9, "error", "server error"),

    ;

    private final int code;

    private final String level;

    private final String defaultMsg;
}
