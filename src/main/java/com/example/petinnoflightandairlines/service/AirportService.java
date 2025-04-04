package com.example.petinnoflightandairlines.service;

import com.example.petinnoflightandairlines.dto.AirportDTO;
import com.example.petinnoflightandairlines.model.Airport;

import java.util.List;

public interface AirportService {

    AirportDTO addAirport(AirportDTO airportDTO);

    AirportDTO getAirportDTO(Long airportId);

    AirportDTO updateAirportDTO(Long airportId, AirportDTO airportDTO);

    void deleteAirport(Long airportId);

    Integer getAllFlightsConnectedWithAirport(Long airportId);

    List<Airport> getAirportsByLocation(String location);

    List<Airport> getAllAirports();
}
