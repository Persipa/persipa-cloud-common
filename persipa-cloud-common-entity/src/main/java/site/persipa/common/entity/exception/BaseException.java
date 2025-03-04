package site.persipa.common.entity.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BaseException extends Exception {

    protected int code;

    protected String problem;

    protected String reasonTemplate;

    protected Object[] reasonVariables;

    public BaseException(BaseExceptionEnum baseExceptionEnum, Object... variables) {
        if (variables == null || variables.length == 0) {
            variables = baseExceptionEnum.getReasonVariables();
        }
        this.code = baseExceptionEnum.getCode();
        this.problem = baseExceptionEnum.getProblem();
        this.reasonTemplate = baseExceptionEnum.getReasonTemplate();
        this.reasonVariables = variables;
    }

    protected BaseException(int code, String problem, String reasonTemplate, Object... reasonVariables) {
        this.code = code;
        this.problem = problem;
        this.reasonTemplate = reasonTemplate;
        this.reasonVariables = reasonVariables;
    }

    public String getReason() {
        if (reasonTemplate == null) {
            return null;
        } else {
            return String.format(reasonTemplate, reasonVariables);
        }
    }

    @Override
    public String getMessage() {
        StringBuilder stringBuilder = new StringBuilder(problem);
        String reason = getReason();
        if (reason != null && !reason.isEmpty()) {
            stringBuilder.append(": ")
                    .append(reason);
        }
        return stringBuilder.toString();
    }

}
