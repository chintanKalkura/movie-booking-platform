package com.ck.movie.booking.platform.service;

import com.ck.movie.booking.platform.dto.response.TheatreDetails;
import com.ck.movie.booking.platform.entity.Theatre;
import com.ck.movie.booking.platform.exception.ResourceNotFoundException;
import com.ck.movie.booking.platform.repository.TheatreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TheatreServiceTest {

    @Mock private TheatreRepository theatreRepository;
    @InjectMocks private TheatreService theatreService;

    @Test
    void findById_mapsEntityToDetailsCorrectly() {
        Theatre theatre = new Theatre();
        theatre.setId("theatre-1");
        theatre.setName("PVR Cinemas");
        theatre.setAddress("123 MG Road, Bengaluru, Karnataka");

        when(theatreRepository.findById("theatre-1")).thenReturn(Optional.of(theatre));

        TheatreDetails result = theatreService.findById("theatre-1");

        assertThat(result.name()).isEqualTo("PVR Cinemas");
        assertThat(result.address()).isEqualTo("123 MG Road, Bengaluru, Karnataka");
    }

    @Test
    void findById_notFound_throwsRuntimeException() {
        when(theatreRepository.findById("missing-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> theatreService.findById("missing-id"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("missing-id");
    }
}
