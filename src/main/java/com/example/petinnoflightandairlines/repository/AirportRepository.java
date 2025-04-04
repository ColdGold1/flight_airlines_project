package com.example.petinnoflightandairlines.repository;

import com.example.petinnoflightandairlines.model.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {

    List<Airport> getAirportsByLocation(String location);

    Optional<Airport> getAirportByAirportIata(String iata);

}
