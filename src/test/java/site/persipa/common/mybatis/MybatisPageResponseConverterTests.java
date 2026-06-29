package site.persipa.common.mybatis;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import site.persipa.common.rest.pojo.PageResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

/**
 * @author persipa
 */
class MybatisPageResponseConverterTests {

    @Test
    void shouldConvertPageToPageResponse() {
        Page<String> page = new Page<>(2, 10, 25);
        page.setRecords(List.of("first", "second"));

        PageResponse<String> response = MybatisPageResponseConverter.from(page);

        assertThat(response.list()).containsExactly("first", "second");
        assertThat(response.total()).isEqualTo(25);
        assertThat(response.pageSize()).isEqualTo(10);
        assertThat(response.pageNumber()).isEqualTo(2);
        assertThat(response.totalPages()).isEqualTo(3);
    }

    @Test
    void shouldConvertIPageToPageResponse() {
        IPage<String> page = new Page<String>(1, 5, 1)
                .setRecords(List.of("record"));

        PageResponse<String> response = MybatisPageResponseConverter.from(page);

        assertThat(response.list()).containsExactly("record");
        assertThat(response.total()).isEqualTo(1);
        assertThat(response.pageSize()).isEqualTo(5);
        assertThat(response.pageNumber()).isEqualTo(1);
        assertThat(response.totalPages()).isEqualTo(1);
    }

    @Test
    void shouldMapRecordsWithoutModifyingSourcePage() {
        Page<String> page = new Page<>(1, 10, 2);
        page.setRecords(List.of("first", "second"));

        PageResponse<Integer> response = MybatisPageResponseConverter.from(page, String::length);

        assertThat(response.list()).containsExactly(5, 6);
        assertThat(response.total()).isEqualTo(2);
        assertThat(page.getRecords()).containsExactly("first", "second");
    }

    @Test
    void shouldNormalizeNullRecordsToEmptyList() {
        Page<String> page = new Page<>(1, 10, 0);
        page.setRecords(null);

        assertThat(MybatisPageResponseConverter.from(page).list()).isEmpty();
        assertThat(MybatisPageResponseConverter.from(page, String::length).list()).isEmpty();
    }

    @Test
    void shouldRejectNullPage() {
        assertThatNullPointerException()
                .isThrownBy(() -> MybatisPageResponseConverter.from((IPage<Object>) null))
                .withMessage("page must not be null");
    }

    @Test
    void shouldRejectNullMapper() {
        IPage<String> page = new Page<>(1, 10);

        assertThatNullPointerException()
                .isThrownBy(() -> MybatisPageResponseConverter.from(page, null))
                .withMessage("mapper must not be null");
    }
}
