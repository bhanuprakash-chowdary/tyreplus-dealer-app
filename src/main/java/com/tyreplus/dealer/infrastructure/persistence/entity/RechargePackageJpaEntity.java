package com.tyreplus.dealer.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "recharge_packages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RechargePackageJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "price_in_inr", nullable = false)
    private int priceInInr;

    @Column(nullable = false)
    private int credits;

    @Column(nullable = false)
    private boolean popular;

    @Column(nullable = false)
    private boolean active = true;
}
