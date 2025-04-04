package com.example.petinnoflightandairlines.mapper;

import com.example.petinnoflightandairlines.dto.AirportDTO;
import com.example.petinnoflightandairlines.model.Airport;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AirportMapper {

    AirportMapper INSTANCE = Mappers.getMapper(AirportMapper.class);

    AirportDTO convertAirportToAirportDTO(Airport airport);

    Airport convertAirportDTOToAirport(AirportDTO airportDTO);
}
