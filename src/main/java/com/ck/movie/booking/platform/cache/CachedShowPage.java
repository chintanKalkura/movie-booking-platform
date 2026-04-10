package com.ck.movie.booking.platform.cache;

import java.util.List;

/**
 * Cacheable wrapper for a page of stable show details.
 * Captures both the content and total element count so pagination metadata
 * can be reconstructed on a cache hit without an additional DB query.
 */
public record CachedShowPage(
        List<CachedShowDetails> content,
        long totalElements
) {}
