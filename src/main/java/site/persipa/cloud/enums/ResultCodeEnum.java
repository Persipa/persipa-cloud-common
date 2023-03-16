package site.persipa.cloud.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCodeEnum {

    SUCCESS(200, "success"),

    REFUSE(403, "refuse"),

    ERROR(500, "error"),

    ;

    private final int code;

    private final String message;


}
