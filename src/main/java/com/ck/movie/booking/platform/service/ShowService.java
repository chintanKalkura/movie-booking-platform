package com.ck.movie.booking.platform.service;

import com.ck.movie.booking.platform.constants.enums.ShowStatus;
import com.ck.movie.booking.platform.dto.request.ShowCreateRequest;
import com.ck.movie.booking.platform.dto.response.ShowDetails;
import com.ck.movie.booking.platform.entity.Screen;
import com.ck.movie.booking.platform.entity.Show;
import com.ck.movie.booking.platform.entity.Theatre;
import com.ck.movie.booking.platform.exception.ResourceNotFoundException;
import com.ck.movie.booking.platform.exception.ServiceException;
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
        try {
            return showRepository.findByMovieNameAndShowDate(movieName, date, pageable)
                    .map(this::toDetails);
        } catch (ServiceException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Unexpected error retrieving shows for movie: " + movieName, e);
        }
    }

    public void createShow(ShowCreateRequest request) {
        try {
            movieService.findById(request.movieId());
            priceService.findById(request.priceId());
            Theatre theatre = theatreService.getEntityById(request.theatreId());

            Screen screen = theatre.getScreens().stream()
                    .filter(s -> s.getNumber() == request.screenNumber())
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Screen " + request.screenNumber() + " not found in theatre: " + request.theatreId()));

            int totalSeats = screen.getTotalSeats();
            int seatsAvailable = totalSeats;

            Show show = new Show();
            show.setMovieName(request.movieName());
            show.setMovieId(request.movieId());
            show.setTheatreId(request.theatreId());
            show.setPriceId(request.priceId());
            show.setScreenType(screen.getScreenType());
            show.setShowTime(request.showTime());
            show.setShowDate(request.showDate());
            show.setTotalSeats(totalSeats);
            show.setSeatsAvailable(seatsAvailable);
            show.setShowStatus(calculateShowStatus(seatsAvailable, totalSeats));

            showRepository.save(show);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Unexpected error creating show for movie: " + request.movieName(), e);
        }
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
                .totalSeats(show.getTotalSeats())
                .seatsAvailable(show.getSeatsAvailable())
                .build();
    }

    private ShowStatus calculateShowStatus(int seatsAvailable, int totalSeats) {
        if (totalSeats == 0) return ShowStatus.EMPTY;
        double availablePercent = (double) seatsAvailable / totalSeats * 100;
        if (availablePercent >= 75) return ShowStatus.EMPTY;
        if (availablePercent >= 25) return ShowStatus.FILLING_FAST;
        return ShowStatus.FEW_SEATS_REMAINING;
    }
}
