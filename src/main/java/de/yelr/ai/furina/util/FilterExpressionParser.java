package de.yelr.ai.furina.util;

import de.yelr.ai.furina.dto.ai.SearchFilter;
import de.yelr.ai.furina.exception.ApiException;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class FilterExpressionParser {

    public Filter.Expression combine(List<SearchFilter> filters) {
        if (filters == null || filters.isEmpty()) {
            return null;
        }
        Filter.Expression expression = null;
        for (SearchFilter filter : filters) {
            Filter.Expression current = parse(filter);
            expression = expression == null
                    ? current
                    : new Filter.Expression(Filter.ExpressionType.AND, expression, current);
        }
        return expression;
    }

    public Filter.Expression parse(SearchFilter filter) {
        if (filter == null || !StringUtils.hasText(filter.field())) {
            throw ApiException.badRequest("Filter field is required");
        }
        Filter.ExpressionType type = parseType(filter.operator());
        return new Filter.Expression(type, new Filter.Key(filter.field()), new Filter.Value(filter.value()));
    }

    private static Filter.ExpressionType parseType(String operator) {
        if (!StringUtils.hasText(operator)) {
            throw ApiException.badRequest("Filter operator is required");
        }
        try {
            return switch (operator.trim().toUpperCase()) {
                case "EQ" -> Filter.ExpressionType.EQ;
                case "NE" -> Filter.ExpressionType.NE;
                case "GT" -> Filter.ExpressionType.GT;
                case "GTE" -> Filter.ExpressionType.GTE;
                case "LT" -> Filter.ExpressionType.LT;
                case "LTE" -> Filter.ExpressionType.LTE;
                case "IN" -> Filter.ExpressionType.IN;
                case "NIN" -> Filter.ExpressionType.NIN;
                default -> throw ApiException.badRequest("Unsupported filter operator: " + operator);
            };
        } catch (IllegalArgumentException exception) {
            throw ApiException.badRequest("Unsupported filter operator: " + operator);
        }
    }
}
