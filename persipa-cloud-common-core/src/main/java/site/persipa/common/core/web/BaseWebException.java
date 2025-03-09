package site.persipa.common.core.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public abstract class BaseWebException extends Exception {

    protected HttpStatus status;

    protected String title;

    protected String detail;

    protected Map<String, Object> properties;

    protected ProblemDetail toProblemDetail() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setProperties(properties);
        return problemDetail;
    }

}
