package com.tyreplus.dealer.infrastructure.persistence.repository;

import com.tyreplus.dealer.domain.entity.LeadStatus;
import com.tyreplus.dealer.infrastructure.persistence.entity.LeadJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Spring Data JPA repository for LeadJpaEntity.
 */
@Repository
public interface LeadJpaRepository extends JpaRepository<LeadJpaEntity, UUID>, JpaSpecificationExecutor<LeadJpaEntity> {
    List<LeadJpaEntity> findByStatus(LeadStatus status);
    // This method counts leads for a specific dealer created after a certain time
    long countByPurchasedByDealerIdAndCreatedAtAfter(UUID dealerId, LocalDateTime startOfDay);

    // Spring Data needs Pageable to handle "Top 10" or "Limit"
    List<LeadJpaEntity> findByPurchasedByDealerId(UUID dealerId, Pageable pageable);

    long countByPurchasedByDealerId(UUID dealerId);

    long countByPurchasedByDealerIdAndStatus(UUID dealerId, LeadStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM LeadJpaEntity l WHERE l.id = :id")
    Optional<LeadJpaEntity> findByIdWithLock(@Param("id") UUID id);

    @Query("SELECT l FROM LeadJpaEntity l " +
            "WHERE (:status IS NULL OR l.status = :status) " +
            "AND (:dealerId NOT MEMBER OF l.skippedByDealerIds) " +
            "AND (l.purchasedByDealerId IS NULL) " +
            "ORDER BY " +
            "CASE WHEN :sort = 'date_asc' THEN l.createdAt END ASC, " +
            "CASE WHEN :sort = 'date_desc' THEN l.createdAt END DESC")
    Page<LeadJpaEntity> findLeadsWithFilters(
            @Param("status") LeadStatus status,
            @Param("dealerId") UUID dealerId,
            @Param("sort") String sort,
            Pageable pageable);
}

