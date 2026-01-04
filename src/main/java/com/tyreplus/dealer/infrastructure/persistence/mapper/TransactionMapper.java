package com.tyreplus.dealer.infrastructure.persistence.mapper;

import com.tyreplus.dealer.domain.entity.Transaction;
import com.tyreplus.dealer.infrastructure.persistence.entity.TransactionJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper between domain Transaction entity and JPA entity.
 */
@Component
public class TransactionMapper {

    public TransactionJpaEntity toJpaEntity(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        return TransactionJpaEntity.builder()
                .id(transaction.getId())
                .walletId(transaction.getWalletId())
                .dealerId(transaction.getDealerId())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .timestamp(transaction.getTimestamp())
                .build();
    }

    public Transaction toDomainEntity(TransactionJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        return Transaction.builder()
                .id(jpaEntity.getId())
                .walletId(jpaEntity.getWalletId())
                .dealerId(jpaEntity.getDealerId())
                .type(jpaEntity.getType())
                .amount(jpaEntity.getAmount())
                .description(jpaEntity.getDescription())
                .timestamp(jpaEntity.getTimestamp())
                .build();
    }
}

