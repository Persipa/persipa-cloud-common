package site.persipa.common.rest.pojo;

import java.util.List;

/**
 * 与持久层分页实现无关的分页响应。
 *
 * @param list 当前页数据
 * @param total 总记录数
 * @param pageSize 每页记录数
 * @param pageNumber 当前页码
 * @param totalPages 总页数
 * @param <T> 数据类型
 * @author persipa
 */
public record PageResponse<T>(
        List<T> list,
        long total,
        long pageSize,
        long pageNumber,
        long totalPages
) {

    /**
     * 规范化空列表，保证序列化时始终输出数组。
     */
    public PageResponse {
        list = list == null ? List.of() : list;
    }
}
