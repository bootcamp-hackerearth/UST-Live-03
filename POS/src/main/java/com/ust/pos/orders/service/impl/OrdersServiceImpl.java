package com.ust.pos.orders.service.impl;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.OrdersDto;
import com.ust.pos.model.*;
import com.ust.pos.orders.service.OrdersService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrdersServiceImpl implements OrdersService {

    private final OrdersRepository ordersRepository;

    private final CartRepository cartRepository;

    private final CartService cartService;

    private final CartEntryService cartEntryService;

    private final ModelMapper modelMapper;

    private final CartEntryRepository cartEntryRepository;

    private final OrderEntryRepository orderEntryRepository;

    public OrdersServiceImpl(OrdersRepository ordersRepository, CartRepository cartRepository, CartService cartService, CartEntryService cartEntryService, ModelMapper modelMapper, CartEntryRepository cartEntryRepository, OrderEntryRepository orderEntryRepository) {
        this.ordersRepository = ordersRepository;
        this.cartRepository = cartRepository;
        this.cartService = cartService;
        this.cartEntryService = cartEntryService;
        this.modelMapper = modelMapper;
        this.cartEntryRepository = cartEntryRepository;
        this.orderEntryRepository = orderEntryRepository;
    }

    @Override
    public String generateOrderId(String cartIdentifier) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        String cleanIdentifier = "WALKIN";
        if (cartIdentifier != null && !cartIdentifier.trim().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (char c : cartIdentifier.toCharArray()) {
                if (Character.isDigit(c)) {
                    sb.append(c);
                }
            }
            cleanIdentifier = sb.toString();
        }

        return "ORD-" + cleanIdentifier + "-" + timestamp;
    }

    @Override
    public OrdersDto placeOrder(String cartIdentifier, String paymentMode) {
        Cart cart = cartRepository.findByIdentifier(cartIdentifier);
        List<CartEntry> cartEntries = cartEntryRepository.findByCart(cartIdentifier);
        String orderId = generateOrderId(cartIdentifier);
        LocalDateTime orderDate = LocalDateTime.now();
        Orders order = modelMapper.map(cart, Orders.class);
        order.setId(null);
        order.setOrderId(orderId);
        order.setPaymentMode(paymentMode);
        order.setOrderDate(orderDate);
        ordersRepository.save(order);
        List<OrderEntry> orderEntries = new ArrayList<>();
        for (CartEntry cartEntry : cartEntries) {
            OrderEntry orderEntry = modelMapper.map(cartEntry, OrderEntry.class);
            orderEntry.setId(null);
            orderEntry.setOrderId(orderId);
            orderEntries.add(orderEntry);
        }

        orderEntryRepository.saveAll(orderEntries);
        cartEntryService.deleteAllByCart(cartIdentifier);
        cartService.recalculate(cartIdentifier);
        OrdersDto orderDto = modelMapper.map(order, OrdersDto.class);
        Type listType = new TypeToken<List<OrderEntryDto>>() {
        }.getType();
        orderDto.setOrderEntryDtoList(modelMapper.map(orderEntries, listType));
        return orderDto;
    }

    @Override
    public List<OrdersDto> findAll() {
        List<Orders> orders = ordersRepository.findAllByOrderByOrderDateDesc();
        List<OrdersDto> result = new ArrayList<>();
        for (Orders order : orders) {
            OrdersDto orderDto = modelMapper.map(order, OrdersDto.class);
            List<OrderEntry> entries = orderEntryRepository.findByOrderId(order.getOrderId());
            Type listType = new TypeToken<List<OrderEntryDto>>() {
            }.getType();
            orderDto.setOrderEntryDtoList(modelMapper.map(entries, listType));
            result.add(orderDto);
        }
        return result;
    }
}