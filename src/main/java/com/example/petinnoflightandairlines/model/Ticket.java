package com.example.petinnoflightandairlines.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(name = "seat_number")
    private int seatNumber;

}
