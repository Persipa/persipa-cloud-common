package site.persipa.cloud.pojo.page.dto;

import lombok.Data;

/**
 * @author persipa
 */
@Data
public class PageDto<T> {

    private int current;

    private int size;

    private T payload;
}
