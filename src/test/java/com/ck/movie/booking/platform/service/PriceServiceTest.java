package com.ck.movie.booking.platform.service;

import com.ck.movie.booking.platform.dto.response.PriceDetails;
import com.ck.movie.booking.platform.entity.Price;
import com.ck.movie.booking.platform.exception.ResourceNotFoundException;
import com.ck.movie.booking.platform.repository.PriceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock private PriceRepository priceRepository;
    @InjectMocks private PriceService priceService;

    @Test
    void findById_mapsEntityToDetailsCorrectly() {
        Price price = new Price();
        price.setId("price-1");
        price.setCost(BigDecimal.valueOf(350));
        price.setOffers("10% off HDFC Cards,Buy 2 Get 1 Free on Tuesdays");

        when(priceRepository.findById("price-1")).thenReturn(Optional.of(price));

        PriceDetails result = priceService.findById("price-1");

        assertThat(result.cost()).isEqualByComparingTo(BigDecimal.valueOf(350));
        assertThat(result.offers()).containsExactly("10% off HDFC Cards", "Buy 2 Get 1 Free on Tuesdays");
    }

    @Test
    void findById_offersWithWhitespace_trimsEntries() {
        Price price = new Price();
        price.setId("price-1");
        price.setCost(BigDecimal.valueOf(550));
        price.setOffers(" IMAX Weekend Special , Complimentary Popcorn ");

        when(priceRepository.findById("price-1")).thenReturn(Optional.of(price));

        PriceDetails result = priceService.findById("price-1");

        assertThat(result.offers()).containsExactly("IMAX Weekend Special", "Complimentary Popcorn");
    }

    @Test
    void findById_nullOffers_returnsEmptyList() {
        Price price = new Price();
        price.setId("price-1");
        price.setCost(BigDecimal.valueOf(250));
        price.setOffers(null);

        when(priceRepository.findById("price-1")).thenReturn(Optional.of(price));

        PriceDetails result = priceService.findById("price-1");

        assertThat(result.offers()).isEmpty();
    }

    @Test
    void findById_blankOffers_returnsEmptyList() {
        Price price = new Price();
        price.setId("price-1");
        price.setCost(BigDecimal.valueOf(250));
        price.setOffers("   ");

        when(priceRepository.findById("price-1")).thenReturn(Optional.of(price));

        PriceDetails result = priceService.findById("price-1");

        assertThat(result.offers()).isEmpty();
    }

    @Test
    void findById_notFound_throwsRuntimeException() {
        when(priceRepository.findById("missing-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> priceService.findById("missing-id"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("missing-id");
    }
}
