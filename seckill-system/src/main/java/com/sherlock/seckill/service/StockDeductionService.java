package com.sherlock.seckill.service;

/**
 * 库存扣减抽象：分布式场景下由 Redis Lua 实现原子性；本地演示可用内存原子变量。
 */
public interface StockDeductionService {

    void initStock(Long productId, int quantity);

    StockDeductionResult tryDecrementOne(Long productId);

    /** 订单落库失败时回滚缓存库存 */
    void compensateIncrementOne(Long productId);

    /** 当前缓存中的剩余库存，未初始化返回 -1 */
    int getRemainingStock(Long productId);
}
