package site.persipa.common.entity.pojo.rest.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
        return Result.fail(null);
    }

    public static <T> Result<T> fail(T payload) {
        return new Result<>(-1, "Fail", payload);
    }

}
