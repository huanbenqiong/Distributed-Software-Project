package com.sherlock.seckill.service;

public enum StockDeductionResult {
    /** 扣减成功 */
    OK,
    /** 库存不足或已售罄 */
    OUT_OF_STOCK,
    /** 缓存未预热（Redis key 不存在等） */
    NOT_READY
}
