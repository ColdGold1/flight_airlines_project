package com.example.petinnoflightandairlines.model;

import com.example.petinnoflightandairlines.enums.SubscriptionType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "subscription")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "count_of_bookings")
    private int countOfBookings;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_of_subscription")
    private SubscriptionType subscriptionType;

    @OneToMany(mappedBy = "subscription", fetch = FetchType.EAGER,
                orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<UserSub> userSubs = new ArrayList<>();
}
