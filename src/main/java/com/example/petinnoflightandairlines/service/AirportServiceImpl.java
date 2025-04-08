package com.example.petinnoflightandairlines.service;

import com.example.petinnoflightandairlines.dto.AirportDTO;
import com.example.petinnoflightandairlines.dto.AirportSearchCriteria;
import com.example.petinnoflightandairlines.mapper.AirportMapper;
import com.example.petinnoflightandairlines.model.Airport;
import com.example.petinnoflightandairlines.repository.AirportRepository;
import com.example.petinnoflightandairlines.specifications.AirportSpecification;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AirportServiceImpl implements AirportService {

    private final AirportRepository airportRepository;

    private final AirportMapper airportMapper;

    @Override
    public AirportDTO addAirport(@Valid AirportDTO airportDTO) {

        checkAirportCodes(airportDTO);
        Airport airport = airportMapper.convertAirportDTOToAirport(airportDTO);

        Airport savedAirport = saveAirport(airport);
        return airportMapper.convertAirportToAirportDTO(savedAirport);
    }

    @Override
    public List<AirportDTO> getAirports(AirportSearchCriteria criteria) {

        List<Airport> airports;

        if (criteria.isEmpty()) {
            airports = airportRepository.findAll();
        } else {
            airports = getAirportsBy(criteria);
        }

        if (airports.isEmpty()) {
            throw new RuntimeException("There are not airports with such fields");
        }

        return airports.stream()
                .map(airportMapper::convertAirportToAirportDTO)
                .toList();
    }

    @Override
    public AirportDTO updateAirportDTO(Long airportId,
                                       @Valid AirportDTO airportDTO) {

        checkAirportCodes(airportDTO);
        AirportSearchCriteria criteria = AirportSearchCriteria
                .builder()
                .id(airportId)
                .build();
        AirportDTO foundAirportDTO = getAirports(criteria).get(0);
        log.info("started update of airport {}", airportId);

        foundAirportDTO.setName(airportDTO.getName());
        foundAirportDTO.setAirportIcao(airportDTO.getAirportIcao());
        foundAirportDTO.setAirportIata(airportDTO.getAirportIata());
        foundAirportDTO.setLocation(airportDTO.getLocation());
        foundAirportDTO.setMaxCountOfSyncFlights(airportDTO.getMaxCountOfSyncFlights());

        Airport savedAirport = saveAirport(foundAirportDTO);
        return airportMapper.convertAirportToAirportDTO(savedAirport);
    }

    @Override
    public void deleteAirport(Long airportId) {

        airportRepository.deleteById(airportId);
    }

    @Override
    public Integer getAllFlightsConnectedWithAirport(Long airportId) {

        Airport foundAirportDTO = airportRepository.findAirportWithFlightsById(airportId);

        return foundAirportDTO.getDepartureFlights().size()
                + foundAirportDTO.getArrivalFlights().size();
    }

    private List<Airport> getAirportsBy(AirportSearchCriteria criteria) {
        Specification<Airport> spec = AirportSpecification.build(criteria);

        return airportRepository.findAll(spec);
    }

    private Airport saveAirport(Airport airport) {

        return airportRepository.save(airport);
    }

    private Airport saveAirport(AirportDTO airportDTO) {

        return airportRepository.save(airportMapper.convertAirportDTOToAirport(airportDTO));
    }

    private void checkAirportCodes(AirportDTO airportDTO) {

        Optional<Airport> airportIataOptional = airportRepository.getAirportByAirportIata(airportDTO.getAirportIata());
        Optional<Airport> airportIcaoOptional = airportRepository.getAirportByAirportIcao(airportDTO.getAirportIcao());

        if (airportIataOptional.isPresent()) {
            throw new RuntimeException("Airport iata should be unique");
        }
        if (airportIcaoOptional.isPresent()) {
            throw new RuntimeException("Airport icao should be unique");
        }
    }

}
