package com.ust.pos.order.service.impl;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.*;
import com.ust.pos.order.service.OrderService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartEntryRepository cartEntryRepository;
    private final ModelMapper modelMapper;
    private final OrderEntryRepository orderEntryRepository;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            CartRepository cartRepository,
            CartEntryRepository cartEntryRepository,
            ModelMapper modelMapper,
            OrderEntryRepository orderEntryRepository
    ) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.cartEntryRepository = cartEntryRepository;
        this.modelMapper = modelMapper;
        this.orderEntryRepository = orderEntryRepository;
    }

    @Override
    public OrderDto placeOrder(String cartIdentifier,
                               String customerIdentifier,
                               String paymentMethod) {
        OrderDto dto = new OrderDto();
        Cart cart = cartRepository.findByIdentifier(cartIdentifier);
        if (cart == null) {
            dto.setSuccess(false);
            dto.setMessage("Cart not found");
            return dto;
        }
        List<CartEntry> cartEntries =
                cartEntryRepository.findByCartIdentifier(cartIdentifier);
        Order order = new Order();
        order.setIdentifier("ORD-" + System.currentTimeMillis());
        order.setCustomerIdentifier(customerIdentifier);
        order.setTotalPrice(cart.getTotalPrice());
        order.setOriginalPrice(cart.getOriginalPrice());
        order.setDiscount(cart.getDiscount());
        order.setPaymentMethod(paymentMethod);
        order.setOrderPlacedTime(LocalDateTime.now());
        orderRepository.save(order);
        for (CartEntry cartEntry : cartEntries) {
            OrderEntry orderEntry = new OrderEntry();
            orderEntry.setOrderIdentifier(order.getIdentifier());
            orderEntry.setProductIdentifier(cartEntry.getProductIdentifier());
            orderEntry.setQuantity(cartEntry.getQuantity());
            orderEntry.setUnitPrice(cartEntry.getUnitPrice());
            orderEntry.setOriginalPrice(cartEntry.getOriginalPrice());
            orderEntry.setDiscount(cartEntry.getDiscount());
            orderEntry.setTotalPrice(cartEntry.getTotalPrice());
            orderEntryRepository.save(orderEntry);
        }
        cartEntryRepository.deleteByIdentifier(cartIdentifier);
        cartRepository.deleteByIdentifier(cartIdentifier);
        dto = modelMapper.map(order, OrderDto.class);
        dto.setSuccess(true);
        dto.setMessage("Order placed successfully");
        return dto;
    }

    @Override
    public WsDto<OrderDto> findAll(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAll(pageable);
        Type listType = new TypeToken<List<OrderDto>>() {
        }.getType();
        List<OrderDto> orderDtos =
                modelMapper.map(orderPage.getContent(), listType);
        WsDto<OrderDto> response = new WsDto<>();
        response.setDtoList(orderDtos);
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
            OrderDto orderDto = new OrderDto();
            orderDto.setSuccess(false);
            orderDto.setMessage("Order not found");
            return orderDto;
        }
        OrderDto orderDto = modelMapper.map(order, OrderDto.class);
        List<OrderEntry> orderEntryList =
                orderEntryRepository.findByOrderIdentifier(identifier);
        Type entryListType = new TypeToken<List<OrderEntryDto>>() {
        }.getType();
        orderDto.setEntryList(
                modelMapper.map(orderEntryList, entryListType)
        );
        return orderDto;
    }
    @Override
    public WsDto<OrderDto> findAll(Specification<Order> example, Pageable pageable) {
        Type listType = new TypeToken<List<OrderDto>>() {
        }.getType();
        Page<Order> page = orderRepository.findAll(example, pageable);
        WsDto<OrderDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }
}