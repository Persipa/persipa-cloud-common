package site.persipa.cloud.exception;

import site.persipa.cloud.enums.ExceptionLevelEnum;

/**
 * @author persipa
 */
public interface PersipaException {

    int getCode();

    String getMsg();

    ExceptionLevelEnum getLevel();
}
