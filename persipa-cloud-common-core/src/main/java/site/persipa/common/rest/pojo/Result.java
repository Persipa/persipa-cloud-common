package site.persipa.common.rest.pojo;

import java.time.Instant;

/**
 * 统一 REST 响应体。
 *
 * @param code 状态码，成功为 0，失败为负数
 * @param message 响应消息，保证非空
 * @param payload 响应数据
 * @param timestamp 响应时间
 * @author persipa
 */
public record Result<T>(int code, String message, T payload, Instant timestamp) {

    /**
     * 规范化空值，保证响应字段可直接序列化输出。
     */
    public Result {
        message = message == null ? "" : message;
        timestamp = timestamp == null ? Instant.now() : timestamp;
    }

    /**
     * 返回无负载的成功响应。
     */
    public static Result<Void> success() {
        return Result.success(null);
    }

    /**
     * 返回带负载的成功响应。
     */
    public static <T> Result<T> success(T payload) {
        return Result.result(0, "", payload);
    }

    /**
     * 返回失败响应。
     */
    public static Result<Void> fail(String message) {
        return Result.result(-1, message, null);
    }

    private static <T> Result<T> result(int code, String message, T payload) {
        return new Result<>(code, message, payload, Instant.now());
    }
}
