package site.persipa.common.enums;

/**
 * @author persipa
 */
public interface PersipaExceptionDef {

    int getCode();

    String getMsg();

    ExceptionLevelEnum getLevel();
}
