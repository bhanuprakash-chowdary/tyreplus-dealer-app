package com.tyreplus.dealer.infrastructure.persistence.mapper;

import com.tyreplus.dealer.domain.entity.Lead;
import com.tyreplus.dealer.infrastructure.persistence.entity.LeadJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper between domain Lead entity and JPA entity.
 */
@Component
public class LeadMapper {

    public LeadJpaEntity toJpaEntity(Lead lead) {
        if (lead == null) {
            return null;
        }

        return LeadJpaEntity.builder()
                .id(lead.getId())
                .customerName(lead.getCustomerName())
                .customerPhone(lead.getCustomerPhone())
                .customerEmail(lead.getCustomerEmail())
                .vehicleModel(lead.getVehicleModel())
                .vehicleYear(lead.getVehicleYear())
                .status(lead.getStatus())
                .leadCost(lead.getLeadCost())
                .purchasedByDealerId(lead.getPurchasedByDealerId())
                .createdAt(lead.getCreatedAt())
                .purchasedAt(lead.getPurchasedAt())
                .build();
    }

    public Lead toDomainEntity(LeadJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        return Lead.builder()
                .id(jpaEntity.getId())
                .customerName(jpaEntity.getCustomerName())
                .customerPhone(jpaEntity.getCustomerPhone())
                .customerEmail(jpaEntity.getCustomerEmail())
                .vehicleModel(jpaEntity.getVehicleModel())
                .vehicleYear(jpaEntity.getVehicleYear())
                .status(jpaEntity.getStatus())
                .leadCost(jpaEntity.getLeadCost())
                .purchasedByDealerId(jpaEntity.getPurchasedByDealerId())
                .createdAt(jpaEntity.getCreatedAt())
                .purchasedAt(jpaEntity.getPurchasedAt())
                .build();
    }
}

