package com.ck.movie.booking.platform.cache;

import java.util.List;

public record CachedShowPage(
        List<CachedShowDetails> content,
        long totalElements
) {}
