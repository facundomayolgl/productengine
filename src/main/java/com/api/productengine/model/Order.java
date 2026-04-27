package com.api.productengine.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderNumber = UUID.randomUUID().toString();

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    public Order() {}

    public Order(Product product, Integer quantity, BigDecimal totalAmount) {
        this.product = product;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
    }

    public Long getId() { return id; }

    public String getOrderNumber() { return orderNumber; }

    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public Product getProduct() { return product; }

    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }

    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getTotalAmount() { return totalAmount; }

    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}