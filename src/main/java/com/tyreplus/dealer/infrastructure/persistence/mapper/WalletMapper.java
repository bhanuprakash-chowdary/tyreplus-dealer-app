package com.tyreplus.dealer.infrastructure.persistence.mapper;

import com.tyreplus.dealer.domain.entity.Wallet;
import com.tyreplus.dealer.infrastructure.persistence.entity.WalletJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper between domain Wallet entity and JPA entity.
 */
@Component
public class WalletMapper {

    public WalletJpaEntity toJpaEntity(Wallet wallet) {
        if (wallet == null) {
            return null;
        }

        return WalletJpaEntity.builder()
                .id(wallet.getId())
                .dealerId(wallet.getDealerId())
                .balance(wallet.getBalance())
                .version(wallet.getVersion())
                .build();
    }

    public Wallet toDomainEntity(WalletJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        return Wallet.builder()
                .id(jpaEntity.getId())
                .dealerId(jpaEntity.getDealerId())
                .balance(jpaEntity.getBalance())
                .version(jpaEntity.getVersion())
                .build();
    }
}

