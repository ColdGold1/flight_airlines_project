package com.example.petinnoflightandairlines.service;

import com.example.petinnoflightandairlines.dto.AirportDTO;
import com.example.petinnoflightandairlines.mapper.AirportMapper;
import com.example.petinnoflightandairlines.model.Airport;
import com.example.petinnoflightandairlines.model.Flight;
import com.example.petinnoflightandairlines.repository.AirportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class AirportServiceImplTest {

    private Airport airport;

    private static final Long ID = 1L;
    private static final String NAME = "Some name";
    private static final String LOCATION = "Some location";
    private static final String AIRPORT_IATA = "123";
    private static final String AIRPORT_ICAO = "1234";

    AirportService airportService;

    @Mock
    AirportRepository airportRepository;

    AirportMapper airportMapper = Mappers.getMapper(AirportMapper.class);

    @BeforeEach
    void setUp() {
        airportService = new AirportServiceImpl(airportRepository, airportMapper);
        airport = Airport
                .builder()
                .id(ID)
                .airportIata(AIRPORT_IATA)
                .airportIcao(AIRPORT_ICAO)
                .name(NAME)
                .location(LOCATION)
                .build();
    }

    @Test
    void ifAirportWithSuchIataNotExists_thenAddAirport() {
        //given
        AirportDTO airportDTO = airportMapper.convertAirportToAirportDTO(airport);

        when(airportRepository.getAirportByAirportIata(anyString())).thenReturn(Optional.empty());
        when(airportRepository.save(any(Airport.class))).thenReturn(airport);
        //when
        AirportDTO savedAirportDTO = airportService.addAirport(airportDTO);
        //then
        assertNotNull(savedAirportDTO);
        assertEquals(AIRPORT_IATA, savedAirportDTO.getAirportIata());
        assertEquals(AIRPORT_ICAO, savedAirportDTO.getAirportIcao());
        assertEquals(NAME, savedAirportDTO.getName());
        assertEquals(LOCATION, savedAirportDTO.getLocation());

        verify(airportRepository, times(1)).getAirportByAirportIata(anyString());
        verify(airportRepository, times(1)).save(any(Airport.class));
    }

    @Test
    void ifAirportWithSuchIataExists_thenThrowException() {
        //given
        AirportDTO airportDTO = airportMapper.convertAirportToAirportDTO(airport);

        when(airportRepository.getAirportByAirportIata(anyString())).thenReturn(Optional.of(new Airport()));
        //when
        RuntimeException re = assertThrows(RuntimeException.class, () -> airportService.addAirport(airportDTO));
        //then
        assertEquals("Airport iata should be unique", re.getMessage());

        verify(airportRepository, times(1)).getAirportByAirportIata(anyString());
    }

    @Test
    void ifAirportDoesntExist_thenThrowException() {
        //given
        when(airportRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when
        RuntimeException re = assertThrows(RuntimeException.class, () -> airportService.getAirportDTO(1L));

        //then
        assertEquals("Airport not found", re.getMessage());

        verify(airportRepository, times(1)).findById(anyLong());
    }

    @Test
    void ifAirportExists_thenGetAirport() {
        //given
        when(airportRepository.findById(anyLong())).thenReturn(Optional.of(airport));

        //when
        AirportDTO foundAirportDTO = airportService.getAirportDTO(1L);

        //then
        assertEquals(NAME, foundAirportDTO.getName());
        assertEquals(AIRPORT_IATA, foundAirportDTO.getAirportIata());
        assertEquals(AIRPORT_ICAO, foundAirportDTO.getAirportIcao());
        assertEquals(LOCATION, foundAirportDTO.getLocation());

        verify(airportRepository, times(1)).findById(anyLong());
    }

    @Test
    void ifAirportExists_thenUpdateAirport() {
        //given

        AirportDTO airportDTO = AirportDTO.builder()
                .name("random name")
                .maxCountOfSyncFlights(7)
                .airportIata("123")
                .airportIcao("1234")
                .location("random location")
                .build();

        when(airportRepository.getAirportByAirportIata(anyString())).thenReturn(Optional.empty());
        when(airportRepository.findById(anyLong())).thenReturn(Optional.of(airport));
        when(airportRepository.save(any(Airport.class))).thenReturn(airport);

        //when
        AirportDTO updateAirportDTO = airportService.updateAirportDTO(1L, airportDTO);

        //then
        assertNotNull(updateAirportDTO);
        assertEquals(airportDTO.getAirportIata(), updateAirportDTO.getAirportIata());
        assertEquals(airportDTO.getAirportIcao(), updateAirportDTO.getAirportIcao());
        assertEquals(airportDTO.getName(), updateAirportDTO.getName());
        assertEquals(airportDTO.getLocation(), updateAirportDTO.getLocation());

        verify(airportRepository, times(1)).getAirportByAirportIata(anyString());
        verify(airportRepository, times(1)).findById(anyLong());
        verify(airportRepository, times(1)).save(any(Airport.class));
    }

    @Test
    void deleteAirport() {
        //given
        doNothing().when(airportRepository).deleteById(anyLong());
        //when
        airportService.deleteAirport(anyLong());
        //then
        verify(airportRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void ifAirportExists_thenCheckCountOfConnectedFlights() {
        //given

        airport.getArrivalFlights().add(new Flight());
        airport.getArrivalFlights().add(new Flight());
        airport.getArrivalFlights().add(new Flight());

        airport.getDepartureFlights().add(new Flight());
        airport.getDepartureFlights().add(new Flight());
        airport.getDepartureFlights().add(new Flight());

        when(airportRepository.findById(anyLong())).thenReturn(Optional.of(airport));

        Integer count = airportService.getAllFlightsConnectedWithAirport(1L);

        assertEquals(6, count);
        verify(airportRepository, times(1)).findById(anyLong());
    }


    @Test
    void ifLocationIsBad_thenThrowException() {
        //given
        //when
        RuntimeException re = assertThrows(RuntimeException.class, () -> airportService.getAirportsByLocation(""));

        //then
        assertEquals("Location is empty or its length is larger than 50", re.getMessage());
    }

    @Test
    void ifAllGood_thenGet() {
        //given
        when(airportRepository.getAirportsByLocation(anyString())).thenReturn(List.of(new Airport(), new Airport()));

        //when
        List<AirportDTO> airports = airportService.getAirportsByLocation(airport.getLocation());

        //then
        assertEquals(2, airports.size());

        verify(airportRepository, times(1)).getAirportsByLocation(anyString());
    }
}