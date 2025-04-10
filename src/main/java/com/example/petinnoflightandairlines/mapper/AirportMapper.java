package com.example.petinnoflightandairlines.mapper;

import com.example.petinnoflightandairlines.dto.AirportDTO;
import com.example.petinnoflightandairlines.model.Airport;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AirportMapper {

    AirportDTO convertAirportToAirportDTO(Airport airport);

    Airport convertAirportDTOToAirport(AirportDTO airportDTO);
}
