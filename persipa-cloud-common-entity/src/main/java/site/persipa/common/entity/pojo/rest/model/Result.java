package site.persipa.common.entity.pojo.rest.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.persipa.common.entity.exception.BaseException;
import site.persipa.common.entity.exception.DefaultSimpleException;

/**
 * @author persipa
 */
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Result<T> {

    private int code;

    private String message;

    private T payload;

    private final long timestamp = System.currentTimeMillis();

    public static Result<Void> success() {
        return Result.success(null);
    }

    public static <T> Result<T> success(T payload) {
        return new Result<>(0, "Success", payload);
    }

    public static Result<Void> fail() {
        return Result.fail(DefaultSimpleException.create());
    }

    public static Result<Void> fail(BaseException exception) {
        return Result.fail(exception, null);
    }

    public static <T> Result<T> fail(BaseException exception, T payload) {
        return new Result<>(exception.getCode(), exception.getMessage(), payload);
    }

}
