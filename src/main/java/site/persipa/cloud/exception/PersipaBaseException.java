package site.persipa.cloud.exception;

import site.persipa.cloud.enums.ExceptionLevelEnum;

public interface PersipaBaseException {

    int getCode();

    String getMsg();

    ExceptionLevelEnum getLevel();
}
