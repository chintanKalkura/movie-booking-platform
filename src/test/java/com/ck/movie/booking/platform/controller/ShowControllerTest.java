package com.ck.movie.booking.platform.controller;

import com.ck.movie.booking.platform.constants.enums.MovieRating;
import com.ck.movie.booking.platform.constants.enums.ScreenType;
import com.ck.movie.booking.platform.constants.enums.ShowStatus;
import com.ck.movie.booking.platform.dto.request.ShowCreateRequest;
import com.ck.movie.booking.platform.dto.response.MovieDetails;
import com.ck.movie.booking.platform.dto.response.PriceDetails;
import com.ck.movie.booking.platform.dto.response.ShowDetails;
import com.ck.movie.booking.platform.dto.response.TheatreDetails;
import com.ck.movie.booking.platform.service.ShowService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShowController.class)
class ShowControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private ShowService showService;

    private static final String MOVIE = "Inception";
    private static final String DATE  = "10-04-2026";

    @Test
    void getShows_validParams_returns200WithPagedContent() throws Exception {
        Page<ShowDetails> page = new PageImpl<>(List.of(buildShowDetails()), PageRequest.of(0, 10), 1);
        when(showService.getShowsByMovieName(eq(MOVIE), any(LocalDate.class), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/shows").param("movie", MOVIE).param("date", DATE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].movie.name").value(MOVIE))
                .andExpect(jsonPath("$.content[0].screenType").value("IMAX"))
                .andExpect(jsonPath("$.content[0].totalSeats").value(200))
                .andExpect(jsonPath("$.content[0].seatsAvailable").value(100));
    }

    @Test
    void getShows_emptyResult_returns200WithEmptyPage() throws Exception {
        when(showService.getShowsByMovieName(any(), any(LocalDate.class), any(Pageable.class)))
                .thenReturn(Page.empty());

        mockMvc.perform(get("/shows").param("movie", "Unknown").param("date", DATE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void getShows_missingMovieParam_returns400() throws Exception {
        mockMvc.perform(get("/shows").param("date", DATE))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getShows_missingDateParam_returns400() throws Exception {
        mockMvc.perform(get("/shows").param("movie", MOVIE))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getShows_customPagination_returns200() throws Exception {
        Page<ShowDetails> page = new PageImpl<>(List.of(buildShowDetails()), PageRequest.of(1, 5), 6);
        when(showService.getShowsByMovieName(any(), any(LocalDate.class), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/shows").param("movie", MOVIE).param("date", DATE)
                        .param("page", "1").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(6))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(1));
    }

    @Test
    void createShow_validRequest_returns201() throws Exception {
        ShowCreateRequest request = new ShowCreateRequest(
                MOVIE, "movie-id-1", "theatre-id-1", 1, "price-id-1",
                LocalTime.of(10, 30), LocalDate.now().plusDays(1));
        doNothing().when(showService).createShow(any(ShowCreateRequest.class));

        mockMvc.perform(post("/shows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void createShow_missingBody_returns400() throws Exception {
        mockMvc.perform(post("/shows").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShow_blankMovieName_returns400() throws Exception {
        ShowCreateRequest request = new ShowCreateRequest(
                "", "movie-id-1", "theatre-id-1", 1, "price-id-1",
                LocalTime.of(10, 30), LocalDate.now().plusDays(1));

        mockMvc.perform(post("/shows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShow_negativeScreenId_returns400() throws Exception {
        ShowCreateRequest request = new ShowCreateRequest(
                MOVIE, "movie-id-1", "theatre-id-1", -1, "price-id-1",
                LocalTime.of(10, 30), LocalDate.now().plusDays(1));

        mockMvc.perform(post("/shows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    private ShowDetails buildShowDetails() {
        return ShowDetails.builder()
                .movie(MovieDetails.builder().name(MOVIE).category("Sci-Fi Thriller").language("English").rating(MovieRating.UA).build())
                .showTime(LocalTime.of(10, 30))
                .showDate(LocalDate.of(2026, 4, 10))
                .screenType(ScreenType.IMAX)
                .showStatus(ShowStatus.FILLING_FAST)
                .theatre(TheatreDetails.builder().name("PVR Cinemas").address("123 MG Road").build())
                .price(PriceDetails.builder().cost(BigDecimal.valueOf(550)).offers(List.of("IMAX Weekend Special")).build())
                .totalSeats(200)
                .seatsAvailable(100)
                .build();
    }
}
