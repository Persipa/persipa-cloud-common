package site.persipa.common.entity.pojo.rest.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.persipa.common.entity.enums.ExceptionLevelEnum;
import site.persipa.common.entity.enums.ResultLevelEnum;
import site.persipa.common.entity.exception.PersipaBaseException;

/**
 * @author persipa
 */
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Result<T> {

    private int code;

    private String level;

    private String message;

    private T payload;

    private final long timestamp = System.currentTimeMillis();

    public static Result<Void> success() {
        return Result.success(null);
    }

    public static <T> Result<T> success(T payload) {
        return Result.result(ResultLevelEnum.INFO, null, payload);
    }

    public static <T> Result<T> exception(PersipaBaseException exception) {
        return Result.exception(exception, null, null);
    }

    public static <T> Result<T> exception(PersipaBaseException exception, String message, T payload) {
        ExceptionLevelEnum exceptionLevel = exception.getLevel();
        ResultLevelEnum resultLevel = exceptionLevel.getResultLevel();
        if (resultLevel == null) {
            resultLevel = ResultLevelEnum.ERROR;
        }
        return Result.result(resultLevel, message, payload);
    }

    public static Result<Void> fail() {
        return Result.result(ResultLevelEnum.ERROR, null, null);
    }

    private static <T> Result<T> result(ResultLevelEnum resultLevelEnum, String message, T payload) {
        if (message == null || message.isBlank()) {
            message = resultLevelEnum.getDefaultMsg();
        }
        return new Result<>(resultLevelEnum.getCode(), resultLevelEnum.getLevel(), message, payload);
    }

}
