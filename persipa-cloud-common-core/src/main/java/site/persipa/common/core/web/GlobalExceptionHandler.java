package site.persipa.common.core.web;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.persipa.common.entity.exception.BaseException;
import site.persipa.common.entity.exception.DefaultSimpleException;
import site.persipa.common.entity.pojo.rest.model.Result;

/**
 * 全局异常捕获
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    protected <T> Result<T> exception(BaseException exception, T data) {
        return Result.fail(exception, data);
    }

    @ExceptionHandler(Exception.class)
    public <T> Result<T> exception(Exception exception, T data) {
        return Result.fail(DefaultSimpleException.create(exception.getMessage()), data);
    }

}
