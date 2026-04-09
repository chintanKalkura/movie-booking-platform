package com.ck.movie.booking.platform.service;

import com.ck.movie.booking.platform.constants.enums.MovieRating;
import com.ck.movie.booking.platform.constants.enums.ScreenType;
import com.ck.movie.booking.platform.constants.enums.ShowStatus;
import com.ck.movie.booking.platform.dto.request.ShowCreateRequest;
import com.ck.movie.booking.platform.dto.response.MovieDetails;
import com.ck.movie.booking.platform.dto.response.PriceDetails;
import com.ck.movie.booking.platform.dto.response.ShowDetails;
import com.ck.movie.booking.platform.dto.response.TheatreDetails;
import com.ck.movie.booking.platform.entity.Screen;
import com.ck.movie.booking.platform.entity.Show;
import com.ck.movie.booking.platform.entity.Theatre;
import com.ck.movie.booking.platform.exception.ResourceNotFoundException;
import com.ck.movie.booking.platform.repository.ShowRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShowServiceTest {

    @Mock private ShowRepository showRepository;
    @Mock private MovieService movieService;
    @Mock private PriceService priceService;
    @Mock private TheatreService theatreService;
    @InjectMocks private ShowService showService;

    private static final LocalDate TEST_DATE = LocalDate.of(2026, 4, 10);
    private static final String MOVIE_NAME = "Inception";
    private static final String MOVIE_ID   = "aaaaaaaa-0000-0000-0000-000000000001";
    private static final String THEATRE_ID = "bbbbbbbb-0000-0000-0000-000000000001";
    private static final String PRICE_ID   = "cccccccc-0000-0000-0000-000000000001";

    // ── GET ──────────────────────────────────────────────────────────────────

    @Test
    void getShowsByMovieName_returnsMappedPageOfShowDetails() {
        Show show = buildShow();
        Pageable pageable = PageRequest.of(0, 10);

        MovieDetails  movieDetails  = buildMovieDetails();
        TheatreDetails theatreDetails = TheatreDetails.builder().name("PVR Cinemas").address("123 MG Road").build();
        PriceDetails  priceDetails  = PriceDetails.builder().cost(BigDecimal.valueOf(550)).offers(List.of("IMAX Weekend Special")).build();

        when(showRepository.findByMovieNameAndShowDate(MOVIE_NAME, TEST_DATE, pageable))
                .thenReturn(new PageImpl<>(List.of(show), pageable, 1));
        when(movieService.findById(MOVIE_ID)).thenReturn(movieDetails);
        when(theatreService.findById(THEATRE_ID)).thenReturn(theatreDetails);
        when(priceService.findById(PRICE_ID)).thenReturn(priceDetails);

        Page<ShowDetails> result = showService.getShowsByMovieName(MOVIE_NAME, TEST_DATE, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        ShowDetails details = result.getContent().get(0);
        assertThat(details.movie()).isEqualTo(movieDetails);
        assertThat(details.theatre()).isEqualTo(theatreDetails);
        assertThat(details.price()).isEqualTo(priceDetails);
        assertThat(details.showTime()).isEqualTo(LocalTime.of(10, 30));
        assertThat(details.showDate()).isEqualTo(TEST_DATE);
        assertThat(details.screenType()).isEqualTo(ScreenType.IMAX);
        assertThat(details.showStatus()).isEqualTo(ShowStatus.FILLING_FAST);
        assertThat(details.totalSeats()).isEqualTo(200);
        assertThat(details.seatsAvailable()).isEqualTo(100);
    }

    @Test
    void getShowsByMovieName_noMatchingShows_returnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(showRepository.findByMovieNameAndShowDate(MOVIE_NAME, TEST_DATE, pageable))
                .thenReturn(Page.empty(pageable));

        Page<ShowDetails> result = showService.getShowsByMovieName(MOVIE_NAME, TEST_DATE, pageable);

        assertThat(result).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    void getShowsByMovieName_multipleShows_returnsAllMapped() {
        Show show1 = buildShow();
        Show show2 = buildShow();
        show2.setId("dddddddd-0000-0000-0000-000000000002");
        show2.setShowTime(LocalTime.of(15, 0));
        show2.setShowStatus(ShowStatus.EMPTY);

        Pageable pageable = PageRequest.of(0, 10);

        when(showRepository.findByMovieNameAndShowDate(MOVIE_NAME, TEST_DATE, pageable))
                .thenReturn(new PageImpl<>(List.of(show1, show2), pageable, 2));
        when(movieService.findById(MOVIE_ID)).thenReturn(buildMovieDetails());
        when(theatreService.findById(THEATRE_ID)).thenReturn(TheatreDetails.builder().name("PVR").address("123").build());
        when(priceService.findById(PRICE_ID)).thenReturn(PriceDetails.builder().cost(BigDecimal.valueOf(350)).offers(List.of()).build());

        Page<ShowDetails> result = showService.getShowsByMovieName(MOVIE_NAME, TEST_DATE, pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);
    }

    // ── CREATE ───────────────────────────────────────────────────────────────

    @Test
    void createShow_savesShowWithCorrectProperties() {
        ShowCreateRequest request = new ShowCreateRequest(
                MOVIE_NAME, MOVIE_ID, THEATRE_ID, 1, PRICE_ID,
                LocalTime.of(10, 30), TEST_DATE);

        when(theatreService.getEntityById(THEATRE_ID)).thenReturn(buildTheatreWithScreen(1, 200, ScreenType.IMAX));
        when(showRepository.save(any(Show.class))).thenAnswer(inv -> inv.getArgument(0));

        showService.createShow(request);

        ArgumentCaptor<Show> captor = ArgumentCaptor.forClass(Show.class);
        verify(showRepository).save(captor.capture());
        Show saved = captor.getValue();

        assertThat(saved.getMovieName()).isEqualTo(MOVIE_NAME);
        assertThat(saved.getMovieId()).isEqualTo(MOVIE_ID);
        assertThat(saved.getTheatreId()).isEqualTo(THEATRE_ID);
        assertThat(saved.getPriceId()).isEqualTo(PRICE_ID);
        assertThat(saved.getScreenType()).isEqualTo(ScreenType.IMAX);
        assertThat(saved.getTotalSeats()).isEqualTo(200);
        assertThat(saved.getSeatsAvailable()).isEqualTo(200);
        assertThat(saved.getShowStatus()).isEqualTo(ShowStatus.EMPTY); // 100% available → EMPTY
        assertThat(saved.getShowTime()).isEqualTo(LocalTime.of(10, 30));
        assertThat(saved.getShowDate()).isEqualTo(TEST_DATE);
    }

    @Test
    void createShow_screenNotFound_throwsResourceNotFoundException() {
        ShowCreateRequest request = new ShowCreateRequest(
                MOVIE_NAME, MOVIE_ID, THEATRE_ID, 99, PRICE_ID,
                LocalTime.of(10, 30), TEST_DATE);

        when(theatreService.getEntityById(THEATRE_ID)).thenReturn(buildTheatreWithScreen(1, 200, ScreenType.IMAX));

        assertThatThrownBy(() -> showService.createShow(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    private Show buildShow() {
        Show show = new Show();
        show.setId("dddddddd-0000-0000-0000-000000000001");
        show.setMovieName(MOVIE_NAME);
        show.setMovieId(MOVIE_ID);
        show.setTheatreId(THEATRE_ID);
        show.setPriceId(PRICE_ID);
        show.setScreenType(ScreenType.IMAX);
        show.setShowTime(LocalTime.of(10, 30));
        show.setShowDate(TEST_DATE);
        show.setShowStatus(ShowStatus.FILLING_FAST);
        show.setTotalSeats(200);
        show.setSeatsAvailable(100);
        return show;
    }

    private MovieDetails buildMovieDetails() {
        return MovieDetails.builder()
                .name(MOVIE_NAME).category("Sci-Fi Thriller").language("English").rating(MovieRating.UA).build();
    }

    private TheatreDetails buildTheatreWithScreen(int screenId, int totalSeats, ScreenType screenType) {
        Screen screen = new Screen();
        screen.setNumber(screenId);
        screen.setTotalSeats(totalSeats);
        screen.setScreenType(screenType);

        return new TheatreDetails("Inox", "123, MG Road, Bengaluru", List.of(screen));
    }
}
