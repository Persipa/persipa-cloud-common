package site.persipa.common.mybatis;

import com.baomidou.mybatisplus.core.metadata.IPage;
import site.persipa.common.rest.pojo.PageResponse;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * MyBatis-Plus 分页结果转换器。
 *
 * @author persipa
 */
public final class MybatisPageResponseConverter {

    private MybatisPageResponseConverter() {
    }

    /**
     * 将 MyBatis-Plus 分页结果转换为公共分页响应。
     *
     * @param page MyBatis-Plus 分页结果
     * @param <T> 数据类型
     * @return 公共分页响应
     */
    public static <T> PageResponse<T> from(IPage<T> page) {
        Objects.requireNonNull(page, "page must not be null");
        return new PageResponse<>(
                page.getRecords(),
                page.getTotal(),
                page.getSize(),
                page.getCurrent(),
                page.getPages()
        );
    }

    /**
     * 转换 MyBatis-Plus 分页结果，并映射当前页记录。
     *
     * @param page MyBatis-Plus 分页结果
     * @param mapper 记录转换函数
     * @param <S> 源数据类型
     * @param <T> 目标数据类型
     * @return 映射后的公共分页响应
     */
    public static <S, T> PageResponse<T> from(
            IPage<S> page,
            Function<? super S, ? extends T> mapper
    ) {
        Objects.requireNonNull(page, "page must not be null");
        Objects.requireNonNull(mapper, "mapper must not be null");

        List<S> records = page.getRecords();
        List<T> mappedRecords = records == null
                ? List.of()
                : records.stream().<T>map(mapper).toList();
        return new PageResponse<>(
                mappedRecords,
                page.getTotal(),
                page.getSize(),
                page.getCurrent(),
                page.getPages()
        );
    }
}
