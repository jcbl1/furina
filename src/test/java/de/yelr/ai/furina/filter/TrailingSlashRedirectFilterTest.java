package de.yelr.ai.furina.filter;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TrailingSlashRedirectFilterTest {

    private final TrailingSlashRedirectFilter filter = new TrailingSlashRedirectFilter();

    @Test
    void redirectsTrailingSlashWithQueryString() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/foods/");
        request.setQueryString("page=2&size=5");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(308);
        assertThat(response.getHeader("Location")).isEqualTo("/api/v1/foods?page=2&size=5");
        verify(chain, org.mockito.Mockito.never()).doFilter(request, response);
    }

    @Test
    void collapsesRepeatedSlashes() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/foods///");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, mock(FilterChain.class));

        assertThat(response.getHeader("Location")).isEqualTo("/api/v1/foods");
    }

    @Test
    void passesThroughCanonicalPath() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/foods");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(200);
    }
}
