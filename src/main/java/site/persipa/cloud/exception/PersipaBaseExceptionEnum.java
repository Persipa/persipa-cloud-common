package site.persipa.cloud.exception;

import site.persipa.cloud.enums.ExceptionLevelEnum;

/**
 * @author persipa
 */
public interface PersipaBaseExceptionEnum {

    int getCode();

    String getMsg();

    ExceptionLevelEnum getLevel();
}
