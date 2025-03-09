package site.persipa.common.core.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常捕获
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseWebException.class)
    public ProblemDetail handleBaseWebException(BaseWebException exception) {
        return exception.toProblemDetail();
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail exception(Exception exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

}
