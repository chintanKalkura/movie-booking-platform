package com.ck.movie.booking.platform.service;

import com.ck.movie.booking.platform.constants.enums.MovieRating;
import com.ck.movie.booking.platform.constants.enums.ScreenType;
import com.ck.movie.booking.platform.constants.enums.ShowStatus;
import com.ck.movie.booking.platform.dto.request.BookingRequest;
import com.ck.movie.booking.platform.dto.response.BookingResponse;
import com.ck.movie.booking.platform.dto.response.MovieDetails;
import com.ck.movie.booking.platform.dto.response.PriceDetails;
import com.ck.movie.booking.platform.dto.response.ShowDetails;
import com.ck.movie.booking.platform.dto.response.TheatreDetails;
import com.ck.movie.booking.platform.entity.Booking;
import com.ck.movie.booking.platform.exception.BadRequestException;
import com.ck.movie.booking.platform.exception.ResourceNotFoundException;
import com.ck.movie.booking.platform.exception.ServiceException;
import com.ck.movie.booking.platform.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private ShowService showService;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private BookingService bookingService;

    private static final String SHOW_ID = "dddddddd-0000-0000-0000-000000000001";
    private static final LocalDate TEST_DATE = LocalDate.of(2026, 4, 10);

    // ── HAPPY PATH ───────────────────────────────────────────────────────────

    @Test
    void bookShow_validRequest_returnsCorrectBookingResponse() {
        ShowDetails show = buildShowDetails();
        BookingRequest request = new BookingRequest(List.of("A1", "A2"), "user@example.com", "9876543210");

        when(showService.getShowById(SHOW_ID)).thenReturn(show);
        doNothing().when(showService).bookSeats(2, SHOW_ID);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setId("booking-id-123");
            return b;
        });

        BookingResponse response = bookingService.bookShow(SHOW_ID, request);

        assertThat(response.bookingId()).isEqualTo("booking-id-123");
        assertThat(response.seats()).isEqualTo(List.of("A1", "A2"));
        assertThat(response.userEmail()).isEqualTo("user@example.com");
        assertThat(response.userPhoneNumber()).isEqualTo("9876543210");
        assertThat(response.totalCost()).isEqualByComparingTo(BigDecimal.valueOf(1100)); // 550 × 2
        assertThat(response.show().movie()).isEqualTo(show.movie());
        assertThat(response.show().showTime()).isEqualTo(show.showTime());
        assertThat(response.show().showDate()).isEqualTo(show.showDate());
        assertThat(response.show().screenType()).isEqualTo(show.screenType());
        assertThat(response.show().theatre()).isEqualTo(show.theatre());
    }

    @Test
    void bookShow_validRequest_savesBookingWithCorrectProperties() {
        BookingRequest request = new BookingRequest(List.of("A1", "A2"), "user@example.com", "9876543210");

        when(showService.getShowById(SHOW_ID)).thenReturn(buildShowDetails());
        doNothing().when(showService).bookSeats(2, SHOW_ID);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        bookingService.bookShow(SHOW_ID, request);

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());
        Booking saved = captor.getValue();

        assertThat(saved.getShowId()).isEqualTo(SHOW_ID);
        assertThat(saved.getSeats()).isEqualTo("A1,A2");
        assertThat(saved.getTotalCost()).isEqualByComparingTo(BigDecimal.valueOf(1100));
        assertThat(saved.getUserEmail()).isEqualTo("user@example.com");
        assertThat(saved.getUserPhoneNumber()).isEqualTo("9876543210");
    }

    @Test
    void bookShow_singleSeat_calculatesTotalCostCorrectly() {
        BookingRequest request = new BookingRequest(List.of("B5"), "user@example.com", "9876543210");

        when(showService.getShowById(SHOW_ID)).thenReturn(buildShowDetails());
        doNothing().when(showService).bookSeats(1, SHOW_ID);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        BookingResponse response = bookingService.bookShow(SHOW_ID, request);

        assertThat(response.totalCost()).isEqualByComparingTo(BigDecimal.valueOf(550));
    }

    @Test
    void bookShow_showNotFound_throwsResourceNotFoundException() {
        when(showService.getShowById(SHOW_ID))
                .thenThrow(new ResourceNotFoundException("Show not found: " + SHOW_ID));

        assertThatThrownBy(() -> bookingService.bookShow(SHOW_ID, validRequest()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(SHOW_ID);
    }

    @Test
    void bookShow_notEnoughSeats_throwsBadRequestException() {
        when(showService.getShowById(SHOW_ID)).thenReturn(buildShowDetails());
        doThrow(new BadRequestException("Not enough seats available"))
                .when(showService).bookSeats(anyInt(), eq(SHOW_ID));

        assertThatThrownBy(() -> bookingService.bookShow(SHOW_ID, validRequest()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Not enough seats");
    }

    @Test
    void bookShow_unexpectedException_throwsServiceException() {
        when(showService.getShowById(SHOW_ID))
                .thenThrow(new RuntimeException("Unexpected DB failure"));

        assertThatThrownBy(() -> bookingService.bookShow(SHOW_ID, validRequest()))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(SHOW_ID);
    }

    private BookingRequest validRequest() {
        return new BookingRequest(List.of("A1"), "user@example.com", "9876543210");
    }

    private ShowDetails buildShowDetails() {
        return ShowDetails.builder()
                .movie(MovieDetails.builder()
                        .name("Inception").category("Sci-Fi Thriller")
                        .language("English").rating(MovieRating.UA).build())
                .showTime(LocalTime.of(10, 30))
                .showDate(TEST_DATE)
                .screenType(ScreenType.IMAX)
                .showStatus(ShowStatus.FILLING_FAST)
                .theatre(TheatreDetails.builder().name("PVR Cinemas").address("123 MG Road").build())
                .price(PriceDetails.builder().cost(BigDecimal.valueOf(550)).offers(List.of("IMAX Weekend Special")).build())
                .totalSeats(200)
                .seatsAvailable(100)
                .build();
    }
}
