package com.example.petinnoflightandairlines.service;

import com.example.petinnoflightandairlines.dto.AirportDTO;
import com.example.petinnoflightandairlines.dto.AirportSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AirportService {

    AirportDTO save(AirportDTO airportDTO);

    Page<AirportDTO> getAirports(AirportSearchCriteria airportSearchCriteria, Pageable pageable);

    AirportDTO updateAirport(Long airportId, AirportDTO airportDTO);

    void deleteAirport(Long airportId);

    Integer getAllFlightsConnectedWithAirport(Long airportId);

}
