package com.tyreplus.dealer.infrastructure.persistence.adapter;

import com.tyreplus.dealer.application.dto.LeadDetailsResponse;
import com.tyreplus.dealer.domain.entity.Lead;
import com.tyreplus.dealer.domain.entity.LeadStatus;
import com.tyreplus.dealer.domain.repository.LeadRepository;
import com.tyreplus.dealer.infrastructure.persistence.entity.LeadJpaEntity;
import com.tyreplus.dealer.infrastructure.persistence.mapper.LeadMapper;
import com.tyreplus.dealer.infrastructure.persistence.repository.LeadJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter implementing LeadRepository using JPA.
 */
@Component
public class LeadRepositoryAdapter implements LeadRepository {

    private final LeadJpaRepository jpaRepository;
    private final LeadMapper mapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY_PREFIX = "LEAD_";

    public LeadRepositoryAdapter(LeadJpaRepository jpaRepository, LeadMapper mapper,RedisTemplate<String, Object> redisTemplate) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Lead save(Lead lead) {
        LeadJpaEntity jpaEntity = mapper.toJpaEntity(lead);
        LeadJpaEntity saved = jpaRepository.save(jpaEntity);
        Lead savedLead = mapper.toDomainEntity(saved);
        // 4. Cache Invalidation: Remove old data so next read gets fresh DB values
        redisTemplate.delete(CACHE_KEY_PREFIX + savedLead.getId());
        return savedLead;
    }

    @Override
    public Optional<Lead> findById(UUID id) {

        String key = CACHE_KEY_PREFIX + id.toString();

        // 1. Try to fetch from Redis Cache
        Lead cachedLead = (Lead) redisTemplate.opsForValue().get(key);
        if (cachedLead != null) {
            return Optional.of(cachedLead);
        }

        // 2. Cache Miss - Fetch from PostgreSQL
        return jpaRepository.findById(id).map(entity -> {
            Lead domainLead = mapper.toDomainEntity(entity);

            // 3. Prime the Cache for 10 minutes
            redisTemplate.opsForValue().set(key, domainLead, Duration.ofMinutes(10));
            return domainLead;
        });
    }

    @Override
    public List<Lead> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Lead> findByStatus(String status) {
        LeadStatus leadStatus = LeadStatus.valueOf(status.toUpperCase());
        return jpaRepository.findByStatus(leadStatus).stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public long countByPurchasedByDealerIdAndCreatedAtAfter(UUID dealerId, LocalDateTime startOfDay) {
        return jpaRepository.countByPurchasedByDealerIdAndCreatedAtAfter(dealerId,startOfDay);
    }

    @Override
    public List<Lead> findRecentPurchases(UUID dealerId, int limit) {
        // Uses PageRequest to handle the LIMIT at the SQL level
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        return jpaRepository.findByPurchasedByDealerId(dealerId, pageable)
                .stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public long countByPurchasedByDealerId(UUID dealerId) {
        return jpaRepository.countByPurchasedByDealerId(dealerId);
    }

    @Override
    public long countByPurchasedByDealerIdAndStatus(UUID dealerId, LeadStatus status) {
        return jpaRepository.countByPurchasedByDealerIdAndStatus(dealerId,status);
    }

    @Override
    public Optional<Lead> findByIdWithLock(UUID id) {
        return jpaRepository.findByIdWithLock(id)
                .map(mapper::toDomainEntity);
    }

    @Override
    public void saveAll(List<Lead> leads) {
        List<LeadJpaEntity> entities = leads.stream()
                .map(mapper::toJpaEntity)
                .toList();
        jpaRepository.saveAll(entities);
    }


    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public void flush() {
        jpaRepository.flush();
    }

    @Override
    public Page<Lead> findLeadsWithFilters(LeadStatus status, UUID dealerId, String sort, Pageable pageable) {
        return jpaRepository.findLeadsWithFilters(status,dealerId,sort,pageable).map(mapper::toDomainEntity);
    }
}

