package site.persipa.cloud.enums;

/**
 * @author persipa
 */
public interface PersipaExceptionDef {

    int getCode();

    String getMsg();

    ExceptionLevelEnum getLevel();
}
