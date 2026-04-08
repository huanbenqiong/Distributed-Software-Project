package com.sherlock.seckill.service;

import com.sherlock.seckill.dto.ProductView;
import com.sherlock.seckill.dto.SeckillResult;
import com.sherlock.seckill.entity.SeckillOrder;
import com.sherlock.seckill.repository.ProductRepository;
import com.sherlock.seckill.repository.SeckillOrderRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeckillService {

    private final ProductRepository productRepository;
    private final SeckillOrderRepository orderRepository;
    private final StockDeductionService stockDeductionService;

    public SeckillService(
            ProductRepository productRepository,
            SeckillOrderRepository orderRepository,
            StockDeductionService stockDeductionService) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.stockDeductionService = stockDeductionService;
    }

    public List<ProductView> listProducts() {
        return productRepository.findAll().stream()
                .map(p -> new ProductView(
                        p.getId(),
                        p.getName(),
                        p.getPrice(),
                        stockDeductionService.getRemainingStock(p.getId())
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public SeckillResult executeSeckill(Long userId, Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("商品不存在");
        }

        if (orderRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new IllegalStateException("每位用户限购一件该商品");
        }

        StockDeductionResult decr = stockDeductionService.tryDecrementOne(productId);
        if (decr == StockDeductionResult.NOT_READY) {
            throw new IllegalStateException("秒杀未就绪，请稍后重试");
        }
        if (decr == StockDeductionResult.OUT_OF_STOCK) {
            throw new IllegalStateException("已售罄");
        }

        try {
            SeckillOrder order = new SeckillOrder();
            order.setUserId(userId);
            order.setProductId(productId);
            orderRepository.save(order);

            int rows = productRepository.decrementStockIfPositive(productId);
            if (rows == 0) {
                stockDeductionService.compensateIncrementOne(productId);
                throw new IllegalStateException("库存不足");
            }

            return new SeckillResult(order.getId(), userId, productId);
        } catch (DataIntegrityViolationException e) {
            stockDeductionService.compensateIncrementOne(productId);
            throw new IllegalStateException("每位用户限购一件该商品");
        } catch (RuntimeException e) {
            stockDeductionService.compensateIncrementOne(productId);
            throw e;
        }
    }
}
