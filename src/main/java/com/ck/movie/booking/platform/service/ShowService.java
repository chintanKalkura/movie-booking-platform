package com.ck.movie.booking.platform.service;

import com.ck.movie.booking.platform.dto.response.ShowDetails;
import com.ck.movie.booking.platform.entity.Show;
import com.ck.movie.booking.platform.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ShowService {

    private final ShowRepository showRepository;
    private final MovieService movieService;
    private final PriceService priceService;
    private final TheatreService theatreService;

    public Page<ShowDetails> getShowsByMovieName(String movieName, LocalDate date, Pageable pageable) {
        return showRepository.findByMovieNameAndShowDate(movieName, date, pageable)
                .map(this::toDetails);
    }

    private ShowDetails toDetails(Show show) {
        return ShowDetails.builder()
                .movie(movieService.findById(show.getMovieId()))
                .showTime(show.getShowTime())
                .showDate(show.getShowDate())
                .screenType(show.getScreenType())
                .showStatus(show.getShowStatus())
                .theatre(theatreService.findById(show.getTheatreId()))
                .price(priceService.findById(show.getPriceId()))
                .build();
    }
}
