package site.persipa.common.core.web;

import site.persipa.common.entity.exception.BaseException;
import site.persipa.common.entity.exception.DefaultSimpleException;
import site.persipa.common.entity.pojo.rest.model.Result;

public class BaseController {

    protected <T> Result<T> success(T data) {
        return Result.success(data);
    }

    protected <T> Result<T> fail(T data) {
        return fail(DefaultSimpleException.create(), data);
    }

    protected <T> Result<T> fail(BaseException exception, T data) {
        return Result.fail(exception, data);
    }


}
