package site.persipa.common.entity.exception;

public class DefaultSimpleException extends BaseException {

    private DefaultSimpleException() {
        super(-1, "Server Error", null);
    }

    private DefaultSimpleException(String problem) {
        super(-1, problem, null);
    }

    public static DefaultSimpleException create() {
        return new DefaultSimpleException();
    }

    public static DefaultSimpleException create(String problem) {
        return new DefaultSimpleException(problem);
    }
}
