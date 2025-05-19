package de.yelr.ai.furina.util;

import de.yelr.ai.furina.dto.ai.SearchFilter;
import de.yelr.ai.furina.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.springframework.ai.vectorstore.filter.Filter;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FilterExpressionParserTest {

    private final FilterExpressionParser parser = new FilterExpressionParser();

    @Test
    void parsesEqualityFilter() {
        Filter.Expression expression = parser.parse(new SearchFilter("rating", "GTE", "4.5"));

        assertThat(expression.type()).isEqualTo(Filter.ExpressionType.GTE);
        assertThat(((Filter.Key) expression.left()).key()).isEqualTo("rating");
        assertThat(((Filter.Value) expression.right()).value()).isEqualTo("4.5");
    }

    @Test
    void combinesFiltersWithAnd() {
        Filter.Expression expression = parser.combine(List.of(
                new SearchFilter("rating", "GTE", "4.0"),
                new SearchFilter("deliveryTime", "LTE", "40")
        ));

        assertThat(expression.type()).isEqualTo(Filter.ExpressionType.AND);
        assertThat(expression.left()).isInstanceOf(Filter.Expression.class);
        assertThat(expression.right()).isInstanceOf(Filter.Expression.class);
    }

    @Test
    void returnsNullForEmptyFilters() {
        assertThat(parser.combine(List.of())).isNull();
        assertThat(parser.combine(null)).isNull();
    }

    @Test
    void rejectsUnsupportedOperator() {
        assertThatThrownBy(() -> parser.parse(new SearchFilter("rating", "BETWEEN", "4")))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Unsupported filter operator");
    }
}
