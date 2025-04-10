package com.example.petinnoflightandairlines.specifications;

import com.example.petinnoflightandairlines.dto.AirportSearchCriteria;
import com.example.petinnoflightandairlines.model.Airport;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AirportSpecification {

    public static Specification<Airport> build(AirportSearchCriteria criteria) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), criteria.getId()));
            }

            if (criteria.getName() != null) {
                predicates.add(criteriaBuilder.equal(root.get("name"), criteria.getName()));
            }

            if (criteria.getAirportIata() != null) {
                predicates.add(criteriaBuilder.equal(root.get("airportIata"), criteria.getAirportIata()));

            }

            if (criteria.getAirportIcao() != null) {
                predicates.add(criteriaBuilder.equal(root.get("airportIcao"), criteria.getAirportIcao()));

            }

            if (criteria.getLocation() != null) {
                predicates.add(criteriaBuilder.equal(root.get("location"), criteria.getLocation()));
            }

            if (criteria.getMaxCountOfSyncFlights() != null) {
                predicates.add(criteriaBuilder.equal(root.get("maxCountOfSyncFlights"),
                        criteria.getMaxCountOfSyncFlights()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
