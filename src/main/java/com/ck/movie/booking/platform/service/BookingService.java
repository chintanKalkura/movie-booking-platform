package com.ck.movie.booking.platform.service;

import com.ck.movie.booking.platform.dto.request.BookingRequest;
import com.ck.movie.booking.platform.dto.response.BookedShowDetails;
import com.ck.movie.booking.platform.dto.response.BookingResponse;
import com.ck.movie.booking.platform.dto.response.PriceDetails;
import com.ck.movie.booking.platform.dto.response.ShowDetails;
import com.ck.movie.booking.platform.entity.Booking;
import com.ck.movie.booking.platform.exception.BadRequestException;
import com.ck.movie.booking.platform.exception.ResourceNotFoundException;
import com.ck.movie.booking.platform.exception.ServiceException;
import com.ck.movie.booking.platform.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final ShowService showService;
    private final BookingRepository bookingRepository;

    @Transactional
    public BookingResponse bookShow(String showId, BookingRequest request) {
        try {
            ShowDetails show = showService.getShowById(showId);

            int requestedSeats = request.seats().size();

            showService.bookSeats(requestedSeats, showId);

            PriceDetails price = show.price();
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

    private BookedShowDetails buildBookedShowDetails(ShowDetails show) {
        return BookedShowDetails.builder()
                .movie(show.movie())
                .showTime(show.showTime())
                .showDate(show.showDate())
                .screenType(show.screenType())
                .theatre(show.theatre())
                .build();
    }
}
