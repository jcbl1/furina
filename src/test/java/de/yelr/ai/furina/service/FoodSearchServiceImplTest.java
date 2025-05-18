package de.yelr.ai.furina.service;

import de.yelr.ai.furina.dto.ai.SearchFilter;
import de.yelr.ai.furina.dto.ai.SearchResultResponse;
import de.yelr.ai.furina.service.impl.FoodSearchServiceImpl;
import de.yelr.ai.furina.util.FilterExpressionParser;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FoodSearchServiceImplTest {

    @Test
    void mapsDocumentsAndAppliesFilters() {
        VectorStore vectorStore = mock(VectorStore.class);
        Document document = Document.builder()
                .id("10")
                .text("{\"title\":\"Noodle shop\"}")
                .metadata(Map.of("rating", 4.8))
                .score(0.91)
                .build();
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(document));

        FoodSearchServiceImpl service = new FoodSearchServiceImpl(vectorStore, new FilterExpressionParser());
        List<SearchResultResponse> results = service.search("noodle", 5,
                List.of(new SearchFilter("rating", "GTE", "4.5")));

        assertThat(results).hasSize(1);
        assertThat(results.get(0).id()).isEqualTo("10");
        assertThat(results.get(0).score()).isEqualTo(0.91);
        assertThat(results.get(0).metadata()).containsEntry("rating", 4.8);

        ArgumentCaptor<SearchRequest> request = ArgumentCaptor.forClass(SearchRequest.class);
        verify(vectorStore).similaritySearch(request.capture());
        Filter.Expression expression = request.getValue().getFilterExpression();
        assertThat(expression).isNotNull();
        assertThat(expression.type()).isEqualTo(Filter.ExpressionType.GTE);
    }

    @Test
    void clampsTopKAndHandlesBlankQuery() {
        VectorStore vectorStore = mock(VectorStore.class);
        FoodSearchServiceImpl service = new FoodSearchServiceImpl(vectorStore, new FilterExpressionParser());

        assertThat(service.search("  ", 5, null)).isEmpty();
        assertThat(service.search("noodle", 999, null)).isEmpty();
        verify(vectorStore).similaritySearch(any(SearchRequest.class));
    }
}
