package site.persipa.cloud.pojo.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.persipa.cloud.enums.ResultCodeEnum;

/**
 * @author persipa
 */
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Result<T> {

    private int code;

    private String message;

    private T data;

    public static Result<Void> success() {
        return Result.resultWithoutData(ResultCodeEnum.SUCCESS);
    }

    public static <T> Result<T> success(T data) {
        return Result.resultWithData(ResultCodeEnum.SUCCESS, data);
    }

    public static Result<Void> fail(ResultCodeEnum resultCodeEnum) {
        return Result.resultWithoutData(resultCodeEnum);
    }

    private static Result<Void> resultWithoutData(ResultCodeEnum resultCodeEnum) {
        return new Result<>(resultCodeEnum.getCode(), resultCodeEnum.getMessage(), null);
    }

    private static <T> Result<T> resultWithData(ResultCodeEnum resultCodeEnum, T data) {
        return new Result<>(resultCodeEnum.getCode(), resultCodeEnum.getMessage(), data);
    }
}
