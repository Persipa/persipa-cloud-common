package site.persipa.common.entity.pojo.page.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author persipa
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PageDto<T> extends PageBaseDto{

    private T payload;

}
