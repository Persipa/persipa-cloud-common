package site.persipa.cloud.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author persipa
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class PersipaBaseException extends Exception{

    protected int code;

    protected String msg;

    protected String description;

}
