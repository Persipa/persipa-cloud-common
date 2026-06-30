package site.persipa.common.rest.pojo;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author persipa
 */
class PageResponseTests {

    @Test
    void shouldRetainPaginationValues() {
        PageResponse<String> response = new PageResponse<>(List.of("first", "second"), 12, 2, 3, 6);

        assertThat(response.list()).containsExactly("first", "second");
        assertThat(response.total()).isEqualTo(12);
        assertThat(response.pageSize()).isEqualTo(2);
        assertThat(response.pageNumber()).isEqualTo(3);
        assertThat(response.totalPages()).isEqualTo(6);
    }

    @Test
    void shouldNormalizeNullListToEmptyList() {
        PageResponse<String> response = new PageResponse<>(null, 0, 10, 1, 0);

        assertThat(response.list()).isNotNull().isEmpty();
    }
}
