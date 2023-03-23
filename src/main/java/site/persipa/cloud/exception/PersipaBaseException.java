package site.persipa.cloud.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import site.persipa.cloud.enums.ExceptionLevelEnum;

/**
 * @author persipa
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class PersipaBaseException extends Exception{

    protected int code;

    protected String msg;

    protected String description;

    protected ExceptionLevelEnum level;

}
