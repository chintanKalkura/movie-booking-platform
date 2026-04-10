package com.ck.movie.booking.platform.service;

import com.ck.movie.booking.platform.cache.CachedShowDetails;
import com.ck.movie.booking.platform.cache.CachedShowPage;
import com.ck.movie.booking.platform.cache.ShowCacheService;
import com.ck.movie.booking.platform.constants.enums.ShowStatus;
import com.ck.movie.booking.platform.dto.request.ShowCreateRequest;
import com.ck.movie.booking.platform.dto.response.ShowDetails;
import com.ck.movie.booking.platform.entity.Screen;
import com.ck.movie.booking.platform.entity.Show;
import com.ck.movie.booking.platform.exception.ResourceNotFoundException;
import com.ck.movie.booking.platform.exception.BadRequestException;
import com.ck.movie.booking.platform.exception.ServiceException;
import com.ck.movie.booking.platform.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowService {

    private final ShowRepository showRepository;
    private final MovieService movieService;
    private final PriceService priceService;
    private final TheatreService theatreService;
    private final ShowCacheService showCacheService;

    public Page<ShowDetails> getShowsByMovieName(String movieName, LocalDate date, Pageable pageable) {
        try {
            CachedShowPage cachedPage = showCacheService.getStableShowPage(movieName, date, pageable);

            List<String> showIds = cachedPage.content().stream()
                    .map(CachedShowDetails::showId)
                    .toList();

            Map<String, Show> liveShowMap = showRepository.findAllById(showIds).stream()
                    .collect(Collectors.toMap(Show::getId, Function.identity()));

            List<ShowDetails> fullDetails = cachedPage.content().stream()
                    .map(cached -> mergeWithLiveData(cached, liveShowMap.get(cached.showId())))
                    .toList();

            return new PageImpl<>(fullDetails, pageable, cachedPage.totalElements());
        } catch (ServiceException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Unexpected error retrieving shows for movie: " + movieName, e);
        }
    }

    public void createShow(ShowCreateRequest request) {
        try {
            var theatre = theatreService.getEntityById(request.theatreId());

            Screen screen = theatre.screens().stream()
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

    public ShowDetails getShowById(String showId) {
        try {
            return showRepository.findById(showId)
                    .map(this::toDetails)
                    .orElseThrow(() -> new ResourceNotFoundException("Show not found: " + showId));
        } catch (ServiceException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Unexpected error retrieving shows for id: " + showId, e);
        }
    }

    public void bookSeats(int requestedSeats, String showId) {
        try {
            Show show = showRepository.findById(showId)
                    .orElseThrow(() -> new ResourceNotFoundException("Show not found: " + showId));
            if (requestedSeats > show.getSeatsAvailable()) {
                throw new BadRequestException(
                        "Not enough seats available. Requested: " + requestedSeats +
                                ", Available: " + show.getSeatsAvailable() + ", for show: " + showId);
            }
            show.setSeatsAvailable(show.getSeatsAvailable() - requestedSeats);
            show.setShowStatus(calculateShowStatus(show.getSeatsAvailable(), show.getTotalSeats()));
            showRepository.save(show);
        } catch (ServiceException | ResourceNotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Unexpected error booking seats for show: " + showId, e);
        }
    }

    private ShowDetails mergeWithLiveData(CachedShowDetails cached, Show liveShow) {
        return ShowDetails.builder()
                .movie(cached.movie())
                .showTime(cached.showTime())
                .showDate(cached.showDate())
                .screenType(cached.screenType())
                .theatre(cached.theatre())
                .price(cached.price())
                .totalSeats(cached.totalSeats())
                .showStatus(liveShow != null ? liveShow.getShowStatus() : null)
                .seatsAvailable(liveShow != null ? liveShow.getSeatsAvailable() : 0)
                .build();
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
