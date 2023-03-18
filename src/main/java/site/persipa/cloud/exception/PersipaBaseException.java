package site.persipa.cloud.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author persipa
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class PersipaBaseException extends RuntimeException{

    private int code;

    private String msg;

    private String description;

}
