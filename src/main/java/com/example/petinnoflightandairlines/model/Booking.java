package com.example.petinnoflightandairlines.model;

import com.example.petinnoflightandairlines.enums.BookingStatusType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "booking")
@Data
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "flight_id")
    private Flight flight;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status")
    private BookingStatusType bookingStatusType;

    @Column(name = "booking_datetime")
    private LocalDateTime bookingDatetime;

    @OneToMany(mappedBy = "booking",
            cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE},
            orphanRemoval = true
    )
    private List<Ticket> tickets = new ArrayList<>();

}
