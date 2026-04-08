package com.sherlock.seckill.service.impl;

import com.sherlock.seckill.service.StockDeductionResult;
import com.sherlock.seckill.service.StockDeductionService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@ConditionalOnProperty(prefix = "seckill.redis", name = "enabled", havingValue = "false", matchIfMissing = true)
public class LocalStockDeductionService implements StockDeductionService {

    private final ConcurrentHashMap<Long, AtomicInteger> stocks = new ConcurrentHashMap<>();

    @Override
    public void initStock(Long productId, int quantity) {
        stocks.put(productId, new AtomicInteger(quantity));
    }

    @Override
    public StockDeductionResult tryDecrementOne(Long productId) {
        AtomicInteger atomic = stocks.get(productId);
        if (atomic == null) {
            return StockDeductionResult.NOT_READY;
        }
        for (;;) {
            int current = atomic.get();
            if (current < 1) {
                return StockDeductionResult.OUT_OF_STOCK;
            }
            if (atomic.compareAndSet(current, current - 1)) {
                return StockDeductionResult.OK;
            }
        }
    }

    @Override
    public void compensateIncrementOne(Long productId) {
        AtomicInteger atomic = stocks.get(productId);
        if (atomic != null) {
            atomic.incrementAndGet();
        }
    }

    @Override
    public int getRemainingStock(Long productId) {
        AtomicInteger atomic = stocks.get(productId);
        if (atomic == null) {
            return -1;
        }
        return atomic.get();
    }
}
