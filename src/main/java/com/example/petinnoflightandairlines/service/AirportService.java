package com.example.petinnoflightandairlines.service;

import com.example.petinnoflightandairlines.dto.AirportDTO;
import com.example.petinnoflightandairlines.dto.AirportSearchCriteria;

import java.util.List;

public interface AirportService {

    AirportDTO addAirport(AirportDTO airportDTO);

    List<AirportDTO> getAirports(AirportSearchCriteria airportSearchCriteria);

    AirportDTO updateAirportDTO(Long airportId, AirportDTO airportDTO);

    void deleteAirport(Long airportId);

    Integer getAllFlightsConnectedWithAirport(Long airportId);

}
