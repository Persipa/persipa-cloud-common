package site.persipa.cloud.pojo.rest.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.persipa.cloud.enums.ResultLevelEnum;

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

    private T data;

    private final long timestamp = System.currentTimeMillis();

    public static Result<Void> success() {
        return Result.result(ResultLevelEnum.INFO, null, null);
    }

    public static <T> Result<T> success(T data) {
        return Result.result(ResultLevelEnum.INFO, null, data);
    }

    public static Result<Void> warn() {
        return Result.result(ResultLevelEnum.WARNING, null, null);
    }

    public static <T> Result<T> warn(String message, T data) {
        return Result.result(ResultLevelEnum.WARNING, message, data);
    }

    public static Result<Void> fail() {
        return Result.result(ResultLevelEnum.EXCEPTION, null, null);
    }

    public static <T> Result<T> fail(String message, T data) {
        return Result.result(ResultLevelEnum.EXCEPTION, message, data);
    }

    public static <T> Result<T> error() {
        return Result.result(ResultLevelEnum.ERROR, null, null);
    }

    private static <T> Result<T> result(ResultLevelEnum resultLevelEnum, String message, T data) {
        if (message == null || message.isBlank()) {
            message = resultLevelEnum.getDefaultMsg();
        }
        return new Result<>(resultLevelEnum.getCode(), resultLevelEnum.getLevel(), message, data);
    }

}
