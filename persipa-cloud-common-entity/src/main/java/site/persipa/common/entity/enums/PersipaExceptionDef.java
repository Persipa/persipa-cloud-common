package site.persipa.common.entity.enums;

/**
 * @author persipa
 */
public interface PersipaExceptionDef {

    int getCode();

    String getMsg();

    ExceptionLevelEnum getLevel();
}
