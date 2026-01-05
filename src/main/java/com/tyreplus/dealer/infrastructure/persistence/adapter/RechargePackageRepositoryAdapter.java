package com.tyreplus.dealer.infrastructure.persistence.adapter;

import com.tyreplus.dealer.domain.entity.RechargePackage;
import com.tyreplus.dealer.domain.repository.RechargePackageRepository;
import com.tyreplus.dealer.infrastructure.persistence.mapper.RechargePackageMapper;
import com.tyreplus.dealer.infrastructure.persistence.repository.RechargePackageJpaRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RechargePackageRepositoryAdapter implements RechargePackageRepository {

    private static final String PACKAGE_KEY_PREFIX = "package:";
    private static final String ACTIVE_PACKAGES_KEY = "packages:active";

    private final RechargePackageJpaRepository jpaRepository;
    private final RechargePackageMapper mapper;
    private final RedisTemplate<String, RechargePackage> redisTemplate;
    private final RedisTemplate<String, List<RechargePackage>> packageListRedisTemplate;

    public RechargePackageRepositoryAdapter(
            RechargePackageJpaRepository jpaRepository,
            RechargePackageMapper mapper, RedisTemplate<String, RechargePackage> redisTemplate, RedisTemplate<String, List<RechargePackage>> packageListRedisTemplate) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
        this.redisTemplate = redisTemplate;
        this.packageListRedisTemplate = packageListRedisTemplate;
    }

    @Override
    public Optional<RechargePackage> findById(UUID id) {
        String cacheKey = PACKAGE_KEY_PREFIX + id;

        // 1. Try Redis
        RechargePackage cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached!=null) {
            return Optional.of(cached);
        }

        // 2. Fallback to DB
        Optional<RechargePackage> fromDb = jpaRepository.findById(id)
                .map(mapper::toDomainEntity);

        // 3. Populate cache
        fromDb.ifPresent(pkg ->
                redisTemplate.opsForValue().set(
                        cacheKey,
                        pkg,
                        Duration.ofHours(1) // TTL
                )
        );

        return fromDb;
    }

    @Override
    public List<RechargePackage> findActivePackages() {
        // 1. Try Redis
        List<RechargePackage> cached = packageListRedisTemplate.opsForValue().get(ACTIVE_PACKAGES_KEY);
        if (cached != null && !cached.isEmpty()) {
            return cached;
        }

        // 2. Load from DB
        List<RechargePackage> packages = jpaRepository
                .findByActiveTrueOrderByPriceInInrAsc()
                .stream()
                .map(mapper::toDomainEntity)
                .toList();

        // 3. Cache list
        packageListRedisTemplate.opsForValue().set(
                ACTIVE_PACKAGES_KEY,
                packages,
                Duration.ofMinutes(30)
        );

        return packages;
    }

    public void updatePackage(RechargePackage pkg) {
        jpaRepository.save(mapper.toJpaEntity(pkg));

        redisTemplate.delete("package:" + pkg.getId());
        packageListRedisTemplate.delete("packages:active");
    }

    @Override
    public void save(RechargePackage pkg){
        jpaRepository.save(mapper.toJpaEntity(pkg));

        redisTemplate.delete("package:" + pkg.getId());
        packageListRedisTemplate.delete("packages:active");
    }
}
