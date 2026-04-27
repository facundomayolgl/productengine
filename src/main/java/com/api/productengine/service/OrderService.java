package com.api.productengine.service;

import com.api.productengine.dto.OrderRequestDTO;
import com.api.productengine.dto.OrderResponseDTO;
import com.api.productengine.exception.*;
import com.api.productengine.model.Order;
import com.api.productengine.model.Product;
import com.api.productengine.repository.OrderRepository;
import com.api.productengine.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderResponseDTO create(OrderRequestDTO request) {
        if (request.productId() == null) {
            throw new ProductNotFoundException(-1L);
        }

        if (request.quantity() == null || request.quantity() <= 0) {
            throw new InvalidProductQuantityException("La orden no puede tener 0 productos.");
        }

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ProductNotFoundException(request.productId()));

        if (product.getStock() < request.quantity()) {
            throw new NotEnoughStockException("El producto no cuenta con existencias suficientes.");
        }

        BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(request.quantity()));

        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOrderAmountException("La orden debe tener un saldo mayor a $0.");
        }

        product.setStock(product.getStock() - request.quantity());
        productRepository.save(product);

        Order order = new Order(product, request.quantity(), totalAmount);
        Order savedOrder = orderRepository.save(order);

        return mapToDTO(savedOrder);
    }

    public List<OrderResponseDTO> findAll() {
        return orderRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public OrderResponseDTO findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return mapToDTO(order);
    }

    @Transactional
    public OrderResponseDTO update(Long id, OrderRequestDTO request) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        Product oldProduct = existingOrder.getProduct();
        oldProduct.setStock(oldProduct.getStock() + existingOrder.getQuantity());
        productRepository.save(oldProduct);

        if (request.productId() == null) {
            throw new ProductNotFoundException(-1L);
        }

        if (request.quantity() == null || request.quantity() <= 0) {
            throw new InvalidProductQuantityException("La orden no puede tener 0 productos.");
        }

        Product newProduct = productRepository.findById(request.productId())
                .orElseThrow(() -> new ProductNotFoundException(request.productId()));

        if (newProduct.getStock() < request.quantity()) {
            throw new NotEnoughStockException("El producto no cuenta con existencias suficientes.");
        }

        BigDecimal newTotalAmount = newProduct.getPrice().multiply(BigDecimal.valueOf(request.quantity()));

        if (newTotalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOrderAmountException("La orden debe tener un saldo mayor a $0.");
        }

        newProduct.setStock(newProduct.getStock() - request.quantity());
        productRepository.save(newProduct);

        existingOrder.setProduct(newProduct);
        existingOrder.setQuantity(request.quantity());
        existingOrder.setTotalAmount(newTotalAmount);

        Order updatedOrder = orderRepository.save(existingOrder);
        return mapToDTO(updatedOrder);
    }

    @Transactional
    public void delete(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        Product product = order.getProduct();
        product.setStock(product.getStock() + order.getQuantity());
        productRepository.save(product);

        orderRepository.deleteById(id);
    }

    private OrderResponseDTO mapToDTO(Order order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getOrderNumber(),
                order.getProduct().getId(),
                order.getProduct().getName(),
                order.getQuantity(),
                order.getTotalAmount()
        );
    }
}