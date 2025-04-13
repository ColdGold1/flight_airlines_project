package com.example.petinnoflightandairlines.service;

import com.example.petinnoflightandairlines.dto.AirportDTO;
import com.example.petinnoflightandairlines.dto.AirportSearchCriteria;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class AirportServiceImplTest {

    private final Airport airport = Airport
            .builder()
            .id(ID)
            .airportIata(AIRPORT_IATA)
            .airportIcao(AIRPORT_ICAO)
            .name(NAME)
            .location(LOCATION)
            .build();

    private AirportDTO airportDTO;

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

        airportDTO = airportMapper.convertAirportToAirportDTO(airport);

    }

    @Test
    void test_save_shouldSaveAirport() {
        //given
        when(airportRepository.getAirportByAirportIata(anyString())).thenReturn(Optional.empty());
        when(airportRepository.getAirportByAirportIcao(anyString())).thenReturn(Optional.empty());
        when(airportRepository.save(any(Airport.class))).thenReturn(airport);
        //when
        AirportDTO savedAirportDTO = airportService.save(airportDTO);
        //then
        assertNotNull(savedAirportDTO);
        assertEquals(AIRPORT_IATA, savedAirportDTO.getAirportIata());
        assertEquals(AIRPORT_ICAO, savedAirportDTO.getAirportIcao());
        assertEquals(NAME, savedAirportDTO.getName());
        assertEquals(LOCATION, savedAirportDTO.getLocation());

        verify(airportRepository, times(1)).getAirportByAirportIata(anyString());
        verify(airportRepository, times(1)).getAirportByAirportIcao(anyString());
        verify(airportRepository, times(1)).save(any(Airport.class));
    }

    @Test
    void test_save_shouldThrowExceptionCuzOfIata() {
        //given
        when(airportRepository.getAirportByAirportIata(anyString())).thenReturn(Optional.of(new Airport()));
        when(airportRepository.getAirportByAirportIcao(anyString())).thenReturn(Optional.empty());
        //when
        RuntimeException re = assertThrows(RuntimeException.class, () -> airportService.save(airportDTO));
        //then
        assertEquals("Airport iata should be unique", re.getMessage());
        verify(airportRepository, times(1)).getAirportByAirportIata(anyString());
        verify(airportRepository, times(1)).getAirportByAirportIcao(anyString());
    }

    @Test
    void test_save_shouldThrowExceptionCuzOfIcao() {
        //given
        when(airportRepository.getAirportByAirportIata(anyString())).thenReturn(Optional.empty());
        when(airportRepository.getAirportByAirportIcao(anyString())).thenReturn(Optional.of(new Airport()));
        //when
        RuntimeException re = assertThrows(RuntimeException.class, () -> airportService.save(airportDTO));
        //then
        assertEquals("Airport icao should be unique", re.getMessage());
        verify(airportRepository, times(1)).getAirportByAirportIata(anyString());
        verify(airportRepository, times(1)).getAirportByAirportIcao(anyString());
    }

    @Test
    void test_getAirports_shouldGetEmptyList() {
        //given
        when(airportRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(new ArrayList<>()));

        //when
        AirportSearchCriteria airportSearchCriteria = AirportSearchCriteria
                .builder()
                .build();

        Pageable pageable = PageRequest.of(0, 1);
        Page<AirportDTO> airportPage = airportService.getAirports(airportSearchCriteria, pageable);
        List<AirportDTO> airports = airportPage.getContent();
        //then
        assertEquals(0, airports.size());
        verify(airportRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void test_getAirports_shouldGetAirport() {
        //given
        when(airportRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(airport)));

        //when
        AirportSearchCriteria criteria = AirportSearchCriteria.builder()
                .id(1L)
                .build();
        Pageable pageable = PageRequest.of(0, 1);
        Page<AirportDTO> airportPage = airportService.getAirports(criteria, pageable);
        AirportDTO foundAirportDTO = airportPage.getContent().get(0);
        //then
        assertEquals(NAME, foundAirportDTO.getName());
        assertEquals(AIRPORT_IATA, foundAirportDTO.getAirportIata());
        assertEquals(AIRPORT_ICAO, foundAirportDTO.getAirportIcao());
        assertEquals(LOCATION, foundAirportDTO.getLocation());

        verify(airportRepository, times(1))
                .findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void test_updateAirport_shouldUpdateAirport() {
        //given
        AirportDTO airportDTO1 = AirportDTO.builder()
                .name("random name")
                .maxCountOfSyncFlights(7)
                .airportIata("321")
                .airportIcao("4123")
                .location("random location")
                .build();

        Airport airport1 = airportMapper.convertAirportDTOToAirport(airportDTO1);

        when(airportRepository.getAirportByAirportIata(anyString())).thenReturn(Optional.empty());
        when(airportRepository.getAirportByAirportIcao(anyString())).thenReturn(Optional.empty());
        when(airportRepository.findById(anyLong())).thenReturn(Optional.of(airport1));
        when(airportRepository.save(any(Airport.class))).thenReturn(airport1);

        //when
        AirportDTO updateAirportDTO = airportService.updateAirport(1L, airportDTO1);

        //then
        assertNotNull(updateAirportDTO);
        assertEquals(airportDTO1.getAirportIata(), updateAirportDTO.getAirportIata());
        assertEquals(airportDTO1.getAirportIcao(), updateAirportDTO.getAirportIcao());
        assertEquals(airportDTO1.getName(), updateAirportDTO.getName());
        assertEquals(airportDTO1.getLocation(), updateAirportDTO.getLocation());

        verify(airportRepository, times(1)).getAirportByAirportIata(anyString());
        verify(airportRepository, times(1)).getAirportByAirportIcao(anyString());
        verify(airportRepository, times(1)).findById(anyLong());
        verify(airportRepository, times(1)).save(any(Airport.class));
    }

    @Test
    void test_deleteAirport_shouldDeleteAirport() {
        //given
        doNothing().when(airportRepository).deleteById(anyLong());
        //when
        airportService.deleteAirport(anyLong());
        //then
        verify(airportRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void test_getAllFlightsConnectedWithAirport_shouldGetCount() {

        //given
        airport.getArrivalFlights().add(Flight.builder().flightNumber("123").build());
        airport.getArrivalFlights().add(Flight.builder().flightNumber("123").build());
        airport.getArrivalFlights().add(Flight.builder().flightNumber("123").build());

        airport.getDepartureFlights().add(Flight.builder().flightNumber("123").build());
        airport.getDepartureFlights().add(Flight.builder().flightNumber("123").build());
        airport.getDepartureFlights().add(Flight.builder().flightNumber("123").build());

        when(airportRepository.findAirportWithFlightsById(anyLong())).thenReturn(airport);

        Integer count = airportService.getAllFlightsConnectedWithAirport(1L);

        assertEquals(6, count);
        verify(airportRepository, times(1)).findAirportWithFlightsById(anyLong());
    }

}