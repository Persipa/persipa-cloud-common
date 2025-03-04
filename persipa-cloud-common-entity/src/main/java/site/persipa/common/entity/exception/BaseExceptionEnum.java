package site.persipa.common.entity.exception;

public interface BaseExceptionEnum {

    int getCode();

    String getProblem();

    String getReasonTemplate();

    Object[] getReasonVariables();

}
