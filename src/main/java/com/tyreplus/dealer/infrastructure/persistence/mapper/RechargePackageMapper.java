package com.tyreplus.dealer.infrastructure.persistence.mapper;

import com.tyreplus.dealer.domain.entity.RechargePackage;
import com.tyreplus.dealer.infrastructure.persistence.entity.RechargePackageJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper between domain RechargePackage and JPA entity.
 */
@Component
public class RechargePackageMapper {

    public RechargePackage toDomainEntity(RechargePackageJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return RechargePackage.builder()
                .id(entity.getId())
                .name(entity.getName())
                .priceInInr(entity.getPriceInInr())
                .credits(entity.getCredits())
                .popular(entity.isPopular())
                .active(entity.isActive())
                .build();
    }

    public RechargePackageJpaEntity toJpaEntity(RechargePackage domain) {
        if (domain == null) {
            return null;
        }

        return RechargePackageJpaEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .priceInInr(domain.getPriceInInr())
                .credits(domain.getCredits())
                .popular(domain.isPopular())
                .active(domain.isActive())
                .build();
    }
}

