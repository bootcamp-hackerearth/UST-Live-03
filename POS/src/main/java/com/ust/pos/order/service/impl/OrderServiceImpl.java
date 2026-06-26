package com.ust.pos.order.service.impl;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.model.*;
import com.ust.pos.order.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartEntryRepository cartEntryRepository;
    private final OrderEntryRepository orderEntryRepository;
    private final ModelMapper modelMapper;

    @Override
    public OrderDto placeOrder(String customerId, String paymentMethod) {

        Cart cart = cartRepository.findByIdentifier(customerId);

        if (cart == null) {
            OrderDto errorDto = new OrderDto();
            errorDto.setSuccess(false);
            errorDto.setMessage("Cart not found for customer: " + customerId);
            return errorDto;
        }

        Order order = new Order();
        order.setIdentifier(customerId);
        order.setTotalPrice(cart.getTotalPrice());
        order.setOriginalPrice(cart.getOriginalPrice());
        order.setDiscount(cart.getDiscount());
        order.setPaymentMethod(paymentMethod);
        order.setOrderedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        List<CartEntry> cartEntries = cartEntryRepository.findByCartId(customerId);
        for (CartEntry cartEntry : cartEntries) {
            OrderEntry orderEntry = new OrderEntry();
            orderEntry.setIdentifier(savedOrder.getId() + "_" + cartEntry.getProduct());
            orderEntry.setOrderId(String.valueOf(savedOrder.getId()));
            orderEntry.setProduct(cartEntry.getProduct());
            orderEntry.setQuantity(cartEntry.getQuantity());
            orderEntry.setUnitPrice(cartEntry.getUnitPrice());
            orderEntry.setOriginalPrice(cartEntry.getOriginalPrice());
            orderEntry.setDiscount(cartEntry.getDiscount());
            orderEntry.setTotalPrice(cartEntry.getTotalPrice());
            orderEntryRepository.save(orderEntry);
        }

        cartEntryRepository.deleteByCartId(customerId);
        cartRepository.deleteByIdentifier(customerId);

        OrderDto orderDto = modelMapper.map(savedOrder, OrderDto.class);
        orderDto.setSuccess(true);
        return orderDto;
    }

    @Override
    public PaginatedResponseDto<OrderDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<OrderDto>>() {
        }.getType();
        Page<Order> orderPage = orderRepository.findAll(pageable);
        List<OrderDto> items = modelMapper.map(orderPage.getContent(), listType);

        PaginatedResponseDto<OrderDto> response = new PaginatedResponseDto<>();
        response.setItems(items);
        response.setTotalRecords(orderPage.getTotalElements());
        response.setTotalPages(orderPage.getTotalPages());
        response.setSizePerPage(pageable.getPageSize());
        response.setPage(pageable.getPageNumber());

        return response;
    }

    @Override
    public OrderDto findByIdentifier(String identifier) {

        Order order = orderRepository.findByIdentifier(identifier);
        if (order == null) {
            OrderDto errorDto = new OrderDto();
            errorDto.setSuccess(false);
            errorDto.setMessage("Order not found: " + identifier);
            return errorDto;
        }
        return modelMapper.map(order, OrderDto.class);
    }

    @Override
    public OrderDto findById(Long id) {

        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            OrderDto errorDto = new OrderDto();
            errorDto.setSuccess(false);
            errorDto.setMessage("Order not found: " + id);
            return errorDto;
        }

        OrderDto orderDto = modelMapper.map(order, OrderDto.class);

        List<OrderEntry> entries = orderEntryRepository.findByOrderId(String.valueOf(id));
        Type listType = new TypeToken<List<OrderEntryDto>>() {
        }.getType();
        orderDto.setEntries(modelMapper.map(entries, listType));
        orderDto.setSuccess(true);
        return orderDto;
    }
}