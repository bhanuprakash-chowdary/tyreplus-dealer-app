package com.tyreplus.dealer.domain.entity;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RechargePackage {

    private UUID id;
    private String name;
    private int priceInInr;
    private int credits;
    private boolean popular;
    private boolean active;
}
