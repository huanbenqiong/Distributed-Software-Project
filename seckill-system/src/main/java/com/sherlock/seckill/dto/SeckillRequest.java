package com.sherlock.seckill.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class SeckillRequest {

    @NotNull
    @Positive
    private Long userId;

    @NotNull
    @Positive
    private Long productId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
