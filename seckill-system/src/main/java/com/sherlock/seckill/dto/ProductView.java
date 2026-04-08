package com.sherlock.seckill.dto;

import java.math.BigDecimal;

public class ProductView {
    private Long id;
    private String name;
    private BigDecimal price;
    /** 缓存中的实时剩余库存；未预热时为 -1 */
    private int remainingStock;

    public ProductView() {
    }

    public ProductView(Long id, String name, BigDecimal price, int remainingStock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.remainingStock = remainingStock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getRemainingStock() {
        return remainingStock;
    }

    public void setRemainingStock(int remainingStock) {
        this.remainingStock = remainingStock;
    }
}
