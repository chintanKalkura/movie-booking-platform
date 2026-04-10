package com.ck.movie.booking.platform.cache;

import com.ck.movie.booking.platform.entity.Show;
import com.ck.movie.booking.platform.repository.ShowRepository;
import com.ck.movie.booking.platform.service.MovieService;
import com.ck.movie.booking.platform.service.PriceService;
import com.ck.movie.booking.platform.service.TheatreService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ShowCacheService {

    private final ShowRepository showRepository;
    private final MovieService movieService;
    private final PriceService priceService;
    private final TheatreService theatreService;

    @Cacheable(
            value = "shows",
            key = "#movieName + '::' + #date + '::' + #pageable.pageNumber + '::' + #pageable.pageSize + '::' + #pageable.sort"
    )
    public CachedShowPage getStableShowPage(String movieName, LocalDate date, Pageable pageable) {
        Page<CachedShowDetails> page = showRepository
                .findByMovieNameAndShowDate(movieName, date, pageable)
                .map(this::toCachedDetails);
        return new CachedShowPage(page.getContent(), page.getTotalElements());
    }

    private CachedShowDetails toCachedDetails(Show show) {
        return new CachedShowDetails(
                show.getId(),
                movieService.findById(show.getMovieId()),
                show.getShowTime(),
                show.getShowDate(),
                show.getScreenType(),
                theatreService.findById(show.getTheatreId()),
                priceService.findById(show.getPriceId()),
                show.getTotalSeats()
        );
    }
}
