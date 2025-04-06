package com.example.petinnoflightandairlines.service;

import com.example.petinnoflightandairlines.dto.AirportDTO;
import com.example.petinnoflightandairlines.mapper.AirportMapper;
import com.example.petinnoflightandairlines.model.Airport;
import com.example.petinnoflightandairlines.repository.AirportRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        checkAirportIata(airportDTO);
        Airport airport = airportMapper.convertAirportDTOToAirport(airportDTO);

        Airport savedAirport = saveAirport(airport);
        return airportMapper.convertAirportToAirportDTO(savedAirport);
    }

    @Override
    public AirportDTO getAirportDTO(Long airportId) {

        return airportMapper.convertAirportToAirportDTO(getAirportById(airportId));
    }

    @Override
    public AirportDTO updateAirportDTO(Long airportId,
                                       @Valid AirportDTO airportDTO) {

        checkAirportIata(airportDTO);
        Airport foundAirport = getAirportById(airportId);
        log.info("started update of airport {}", airportId);

        foundAirport.setName(airportDTO.getName());
        foundAirport.setAirportIcao(airportDTO.getAirportIcao());
        foundAirport.setAirportIata(airportDTO.getAirportIata());
        foundAirport.setLocation(airportDTO.getLocation());
        foundAirport.setMaxCountOfSyncFlights(airportDTO.getMaxCountOfSyncFlights());

        Airport savedAirport = saveAirport(foundAirport);
        return airportMapper.convertAirportToAirportDTO(savedAirport);
    }

    @Override
    public void deleteAirport(Long airportId) {

        airportRepository.deleteById(airportId);
    }

    @Override
    public Integer getAllFlightsConnectedWithAirport(Long airportId) {

        Airport airport = getAirportById(airportId);
        return airport.getDepartureFlights().size()
                + airport.getArrivalFlights().size();

    }

    @Override
    public List<AirportDTO> getAirportsByLocation(String location) {
        if (StringUtils.isEmpty(location) || location.length() > 50) {
            throw new RuntimeException("Location is empty or its length is larger than 50");
        }

        return airportRepository.getAirportsByLocation(location)
                .stream()
                .map(airportMapper::convertAirportToAirportDTO)
                .toList();
    }

    @Override
    public List<Airport> getAllAirports() {

        return airportRepository.findAll();
    }

    private Airport saveAirport(Airport airport) {

        return airportRepository.save(airport);
    }

    private Airport getAirportById(Long airportId) {

        return airportRepository.findById(airportId)
                .orElseThrow(() -> new RuntimeException("Airport not found"));
    }

    private void checkAirportIata(AirportDTO airportDTO){

        Optional<Airport> airportOptional = airportRepository.getAirportByAirportIata(airportDTO.getAirportIata());
        if (airportOptional.isPresent()) {
            throw new RuntimeException("Airport iata should be unique");
        }
    }

}
