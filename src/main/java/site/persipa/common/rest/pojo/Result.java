package site.persipa.common.rest.pojo;

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
        return Result.result(0, null, payload);
    }

    public static Result<Void> fail(String message) {
        return Result.result(-1, message, null);
    }

    private static <T> Result<T> result(int code, String message, T payload) {
        return new Result<>(code, message, payload);
    }

}
