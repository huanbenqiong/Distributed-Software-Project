package com.sherlock.seckill.service.impl;

import com.sherlock.seckill.service.StockDeductionResult;
import com.sherlock.seckill.service.StockDeductionService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@ConditionalOnProperty(prefix = "seckill.redis", name = "enabled", havingValue = "true")
public class RedisStockDeductionService implements StockDeductionService {

    private static final String KEY_PREFIX = "seckill:stock:";

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> stockDecrScript;

    public RedisStockDeductionService(StringRedisTemplate redisTemplate,
                                      RedisScript<Long> stockDecrScript) {
        this.redisTemplate = redisTemplate;
        this.stockDecrScript = stockDecrScript;
    }

    @Override
    public void initStock(Long productId, int quantity) {
        redisTemplate.opsForValue().set(key(productId), String.valueOf(quantity));
    }

    @Override
    public StockDeductionResult tryDecrementOne(Long productId) {
        Long result = redisTemplate.execute(
                stockDecrScript,
                Collections.singletonList(key(productId))
        );
        if (result == null) {
            return StockDeductionResult.NOT_READY;
        }
        long code = result;
        if (code == 1L) {
            return StockDeductionResult.OK;
        }
        if (code == 0L) {
            return StockDeductionResult.OUT_OF_STOCK;
        }
        return StockDeductionResult.NOT_READY;
    }

    @Override
    public void compensateIncrementOne(Long productId) {
        redisTemplate.opsForValue().increment(key(productId), 1);
    }

    @Override
    public int getRemainingStock(Long productId) {
        String v = redisTemplate.opsForValue().get(key(productId));
        if (v == null) {
            return -1;
        }
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static String key(Long productId) {
        return KEY_PREFIX + productId;
    }
}
