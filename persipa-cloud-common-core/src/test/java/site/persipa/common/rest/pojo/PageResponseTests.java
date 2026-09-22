package site.persipa.common.rest.pojo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author persipa
 */
class PageResponseTests {

    @Test
    void shouldNormalizeNullListToEmptyList() {
        PageResponse<String> response = new PageResponse<>(null, 0, 10, 1, 0);

        assertThat(response.list()).isNotNull().isEmpty();
    }
}
