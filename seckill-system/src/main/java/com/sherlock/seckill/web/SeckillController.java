package com.sherlock.seckill.web;

import com.sherlock.seckill.dto.ApiResponse;
import com.sherlock.seckill.dto.ProductView;
import com.sherlock.seckill.dto.SeckillRequest;
import com.sherlock.seckill.dto.SeckillResult;
import com.sherlock.seckill.service.SeckillService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SeckillController {

    private final SeckillService seckillService;

    public SeckillController(SeckillService seckillService) {
        this.seckillService = seckillService;
    }

    @GetMapping("/products")
    public ApiResponse<List<ProductView>> products() {
        return ApiResponse.ok(seckillService.listProducts());
    }

    @PostMapping("/seckill")
    public ApiResponse<SeckillResult> seckill(@Valid @RequestBody SeckillRequest request) {
        SeckillResult result = seckillService.executeSeckill(request.getUserId(), request.getProductId());
        return ApiResponse.ok(result);
    }
}
