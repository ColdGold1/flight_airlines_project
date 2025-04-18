package com.example.petinnoflightandairlines.model;

import com.example.petinnoflightandairlines.model.enumtype.BookingStatusType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "booking")
@Data
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "flight_id")
    private Flight flight;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status")
    private BookingStatusType bookingStatusType;

    @NotNull
    @Column(name = "booking_datetime")
    private ZonedDateTime bookingDatetime;

    @OneToMany(mappedBy = "booking",
            cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE},
            orphanRemoval = true
    )
    private List<Ticket> tickets = new ArrayList<>();

}
