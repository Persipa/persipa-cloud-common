package site.persipa.common.entity.exception;

import site.persipa.common.entity.enums.ExceptionLevelEnum;

public interface PersipaBaseException {

    int getCode();

    String getMsg();

    ExceptionLevelEnum getLevel();
}
