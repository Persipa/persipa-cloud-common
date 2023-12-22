package site.persipa.cloud.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import site.persipa.cloud.enums.ExceptionLevelEnum;
import site.persipa.cloud.enums.PersipaExceptionDef;

/**
 * @author persipa
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PersipaRuntimeException extends RuntimeException implements PersipaBaseException {

    protected int code;

    protected String msg;

    protected String description;

    protected ExceptionLevelEnum level;

    public PersipaRuntimeException(PersipaExceptionDef exception) {
        this.code = exception.getCode();
        this.msg = exception.getMsg();
        this.level = exception.getLevel();
    }

    public PersipaRuntimeException(PersipaExceptionDef exception, String description) {
        this(exception);
        this.description = description;
    }
}
