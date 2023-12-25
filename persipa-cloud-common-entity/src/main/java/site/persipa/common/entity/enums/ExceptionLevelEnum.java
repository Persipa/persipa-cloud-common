package site.persipa.common.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author persipa
 */
@Getter
@AllArgsConstructor
public enum ExceptionLevelEnum {

    WARNING(3, "warning", ResultLevelEnum.WARNING),

    EXCEPTION(6, "exception", ResultLevelEnum.EXCEPTION),

    ERROR(9, "error", ResultLevelEnum.ERROR),

    ;

    private final int code;

    private final String value;

    private final ResultLevelEnum resultLevel;
}
