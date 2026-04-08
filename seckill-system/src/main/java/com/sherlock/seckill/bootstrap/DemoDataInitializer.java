package com.sherlock.seckill.bootstrap;

import com.sherlock.seckill.entity.Product;
import com.sherlock.seckill.repository.ProductRepository;
import com.sherlock.seckill.service.StockDeductionService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DemoDataInitializer implements ApplicationRunner {

    private final ProductRepository productRepository;
    private final StockDeductionService stockDeductionService;

    public DemoDataInitializer(ProductRepository productRepository,
                               StockDeductionService stockDeductionService) {
        this.productRepository = productRepository;
        this.stockDeductionService = stockDeductionService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (productRepository.count() > 0) {
            return;
        }
        Product p1 = new Product();
        p1.setName("演示商品 A（限量 50）");
        p1.setPrice(new BigDecimal("99.00"));
        p1.setStock(50);
        Product p2 = new Product();
        p2.setName("演示商品 B（限量 20）");
        p2.setPrice(new BigDecimal("199.00"));
        p2.setStock(20);
        productRepository.save(p1);
        productRepository.save(p2);

        stockDeductionService.initStock(p1.getId(), p1.getStock());
        stockDeductionService.initStock(p2.getId(), p2.getStock());
    }
}
