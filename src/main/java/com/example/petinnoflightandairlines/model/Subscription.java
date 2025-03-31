package com.example.petinnoflightandairlines.model;

import com.example.petinnoflightandairlines.model.enumtype.SubscriptionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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

    @NotNull
    @Size(min = 1, max = 20)
    private String name;

    @NotNull
    @Column(name = "count_of_bookings")
    @Positive
    private Integer countOfBookings;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type_of_subscription")
    private SubscriptionType subscriptionType;

    @OneToMany(mappedBy = "subscription",
            orphanRemoval = true,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private List<UserSub> userSubs = new ArrayList<>();
}
