package de.yelr.ai.furina.common;

import java.util.List;

/** Pagination envelope. Page numbers are one-based. */
public record PageResponse<T>(List<T> items, long total, long page, long size, long pages) {

    public static <T> PageResponse<T> of(List<T> items, long total, long page, long size) {
        long pages = size == 0 ? 0 : (total + size - 1) / size;
        return new PageResponse<>(items, total, page, size, pages);
    }
}
