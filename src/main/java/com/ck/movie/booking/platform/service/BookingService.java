package com.ck.movie.booking.platform.service;

import com.ck.movie.booking.platform.constants.enums.ShowStatus;
import com.ck.movie.booking.platform.dto.request.BookingRequest;
import com.ck.movie.booking.platform.dto.response.BookedShowDetails;
import com.ck.movie.booking.platform.dto.response.BookingResponse;
import com.ck.movie.booking.platform.dto.response.PriceDetails;
import com.ck.movie.booking.platform.entity.Booking;
import com.ck.movie.booking.platform.entity.Show;
import com.ck.movie.booking.platform.exception.BadRequestException;
import com.ck.movie.booking.platform.exception.ResourceNotFoundException;
import com.ck.movie.booking.platform.exception.ServiceException;
import com.ck.movie.booking.platform.repository.BookingRepository;
import com.ck.movie.booking.platform.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final ShowService showService;
    private final BookingRepository bookingRepository;
    private final PriceService priceService;
    private final MovieService movieService;
    private final TheatreService theatreService;

    @Transactional
    public BookingResponse bookShow(String showId, BookingRequest request) {
        try {
            Show show = showService.getShowById(showId);

            int requestedSeats = request.seats().size();

            showService.bookSeats(requestedSeats, showId);

            PriceDetails price = priceService.findById(show.getPriceId());
            BigDecimal totalCost = price.cost().multiply(BigDecimal.valueOf(requestedSeats));

            Booking booking = new Booking();
            booking.setShowId(showId);
            booking.setSeats(String.join(",", request.seats()));
            booking.setTotalCost(totalCost);
            booking.setUserEmail(request.userEmail());
            booking.setUserPhoneNumber(request.userPhoneNumber());
            bookingRepository.save(booking);

            return BookingResponse.builder()
                    .bookingId(booking.getId())
                    .seats(request.seats())
                    .show(buildBookedShowDetails(show))
                    .totalCost(totalCost)
                    .userEmail(request.userEmail())
                    .userPhoneNumber(request.userPhoneNumber())
                    .build();

        } catch (BadRequestException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Unexpected error booking show: " + showId, e);
        }
    }

    private BookedShowDetails buildBookedShowDetails(Show show) {
        return BookedShowDetails.builder()
                .movie(movieService.findById(show.getMovieId()))
                .showTime(show.getShowTime())
                .showDate(show.getShowDate())
                .screenType(show.getScreenType())
                .theatre(theatreService.findById(show.getTheatreId()))
                .build();
    }
}
