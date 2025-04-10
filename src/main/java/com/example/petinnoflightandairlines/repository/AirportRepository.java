package com.example.petinnoflightandairlines.repository;

import com.example.petinnoflightandairlines.model.Airport;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long>, JpaSpecificationExecutor<Airport> {

    Optional<Airport> getAirportByAirportIata(String iata);

    Optional<Airport> getAirportByAirportIcao(String icao);

    @EntityGraph(attributePaths = {"arrivalFlights", "departureFlights"})
    Airport findAirportWithFlightsById(Long airportId);
}
