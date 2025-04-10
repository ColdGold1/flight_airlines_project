package com.example.petinnoflightandairlines.service;

import com.example.petinnoflightandairlines.dto.AirportDTO;
import com.example.petinnoflightandairlines.dto.AirportSearchCriteria;
import com.example.petinnoflightandairlines.mapper.AirportMapper;
import com.example.petinnoflightandairlines.model.Airport;
import com.example.petinnoflightandairlines.repository.AirportRepository;
import com.example.petinnoflightandairlines.specifications.AirportSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AirportServiceImpl implements AirportService {

    private final AirportRepository airportRepository;

    private final AirportMapper airportMapper;

    @Override
    public AirportDTO save(@Valid AirportDTO airportDTO) {

        checkAirportCodes(airportDTO);
        Airport airport = airportMapper.convertAirportDTOToAirport(airportDTO);

        Airport savedAirport = saveAirport(airport);
        return airportMapper.convertAirportToAirportDTO(savedAirport);
    }

    @Override
    public Page<AirportDTO> getAirports(AirportSearchCriteria criteria,
                                        Pageable pageable) {
        Page<Airport> airportPage;

        if (criteria.isEmpty()) {
            airportPage = airportRepository.findAll(pageable);
        } else {
            Specification<Airport> spec = AirportSpecification.build(criteria);
            airportPage = airportRepository.findAll(spec, pageable);
        }

        return airportPage.map(airportMapper::convertAirportToAirportDTO);
    }

    @Override
    public AirportDTO updateAirport(Long airportId,
                                    @Valid AirportDTO airportDTO) {

        checkAirportCodes(airportDTO);
        AirportSearchCriteria criteria = AirportSearchCriteria
                .builder()
                .id(airportId)
                .build();

        Pageable pageable = PageRequest.of(0, 1);

        Page<AirportDTO> airportPage = getAirports(criteria, pageable);
        log.info("started update of airport {}", airportId);

        AirportDTO foundAirportDTO = airportPage.getContent().get(0);

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
