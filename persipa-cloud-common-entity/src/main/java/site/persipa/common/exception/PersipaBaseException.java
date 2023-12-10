package site.persipa.common.exception;

import site.persipa.common.enums.ExceptionLevelEnum;

public interface PersipaBaseException {

    int getCode();

    String getMsg();

    ExceptionLevelEnum getLevel();
}
