package de.yelr.ai.furina.service;

public interface FoodIndexService {

    /**
     * Rebuilds the vector index from the relational catalog.
     *
     * @return number of indexed restaurants
     */
    int reindex();
}
