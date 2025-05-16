package de.yelr.ai.furina.service.impl;

import de.yelr.ai.furina.dto.ai.SearchFilter;
import de.yelr.ai.furina.dto.ai.SearchResultResponse;
import de.yelr.ai.furina.service.FoodSearchService;
import de.yelr.ai.furina.util.FilterExpressionParser;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class FoodSearchServiceImpl implements FoodSearchService {

    private static final int DEFAULT_TOP_K = 5;
    private static final int MAX_TOP_K = 20;

    private final VectorStore vectorStore;
    private final FilterExpressionParser filterParser;

    public FoodSearchServiceImpl(VectorStore vectorStore, FilterExpressionParser filterParser) {
        this.vectorStore = vectorStore;
        this.filterParser = filterParser;
    }

    @Override
    public List<SearchResultResponse> search(String query, int topK, List<SearchFilter> filters) {
        if (!StringUtils.hasText(query)) {
            return List.of();
        }
        int safeTopK = topK <= 0 ? DEFAULT_TOP_K : Math.min(topK, MAX_TOP_K);

        SearchRequest.Builder request = SearchRequest.builder()
                .query(query.trim())
                .topK(safeTopK);
        Filter.Expression expression = filterParser.combine(filters);
        if (expression != null) {
            request.filterExpression(expression);
        }

        List<Document> documents = vectorStore.similaritySearch(request.build());
        if (documents == null) {
            return List.of();
        }
        return documents.stream()
                .map(document -> new SearchResultResponse(
                        document.getId(),
                        document.getText(),
                        document.getMetadata(),
                        document.getScore()))
                .toList();
    }
}
