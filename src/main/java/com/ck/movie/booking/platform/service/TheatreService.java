package com.ck.movie.booking.platform.service;

import com.ck.movie.booking.platform.dto.response.TheatreDetails;
import com.ck.movie.booking.platform.entity.Theatre;
import com.ck.movie.booking.platform.exception.ResourceNotFoundException;
import com.ck.movie.booking.platform.exception.ServiceException;
import com.ck.movie.booking.platform.repository.TheatreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TheatreService {

    private final TheatreRepository theatreRepository;

    public TheatreDetails findById(String id) {
        try {
            return theatreRepository.findById(id)
                    .map(this::toDetails)
                    .orElseThrow(() -> new ResourceNotFoundException("Theatre not found: " + id));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Unexpected error retrieving theatre with id: " + id, e);
        }
    }

    public TheatreDetails getEntityById(String id) {
        try {
            return theatreRepository.findById(id)
                    .map(this::toDetails)
                    .orElseThrow(() -> new ResourceNotFoundException("Theatre not found: " + id));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Unexpected error retrieving theatre with id: " + id, e);
        }
    }

    private TheatreDetails toDetails(Theatre theatre) {
        return TheatreDetails.builder()
                .name(theatre.getName())
                .address(theatre.getAddress())
                .build();
    }
}
