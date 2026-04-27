package com.api.productengine.controller;

import com.api.productengine.dto.ProductStockUpdateDTO;
import com.api.productengine.model.Product;
import com.api.productengine.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        Product created = service.create(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        List<Product> products = service.findAll();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        Product product = service.findById(id);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product) {
        Product updated = service.update(id, product);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(
            @RequestParam String keyword,
            @RequestParam Double maxPrice) {
        return ResponseEntity.ok(service.searchProducts(keyword, maxPrice));
    }

    @GetMapping("/stock-value")
    public ResponseEntity<Double> getTotalStockValue() {
        return ResponseEntity.ok(service.findTotalStockValue());
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<Void> updateStock(
            @PathVariable Long id,
            @RequestBody ProductStockUpdateDTO stockUpdate) {
        service.updateProductStock(id, stockUpdate.newStock());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/average-price")
    public ResponseEntity<BigDecimal> getAveragePrice() {
        return ResponseEntity.ok(service.findAveragePrice());
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<Product>> getByPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        return ResponseEntity.ok(service.findByPriceRange(min, max));
    }

    @GetMapping("/out-of-stock")
    public ResponseEntity<List<Product>> getOutOfStock() {
        return ResponseEntity.ok(service.findOutOfStockProducts());
    }

    @GetMapping("/search-name/{name}")
    public ResponseEntity<List<Product>> getByNameCaseInsensitive(@PathVariable String name) {
        return ResponseEntity.ok(service.findByNameCaseInsensitive(name));
    }
}