package com.sherlock.seckill.repository;

import com.sherlock.seckill.entity.SeckillOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeckillOrderRepository extends JpaRepository<SeckillOrder, Long> {

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    Optional<SeckillOrder> findByUserIdAndProductId(Long userId, Long productId);
}
