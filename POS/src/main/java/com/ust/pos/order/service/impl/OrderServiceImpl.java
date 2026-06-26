package com.ust.pos.order.service.impl;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.model.*;
import com.ust.pos.order.service.OrderService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;


    private final CartRepository cartRepository;


    private final CartService cartService;


    private final CartEntryService cartEntryService;


    private final ModelMapper modelMapper;


    private final CartEntryRepository cartEntryRepository;


    private final OrderEntryRepository orderEntryRepository;

    public OrderServiceImpl(OrderRepository orderRepository,CartRepository cartRepository,CartService cartService,CartEntryService cartEntryService,ModelMapper modelMapper, CartEntryRepository cartEntryRepository,OrderEntryRepository orderEntryRepository){
        this.orderRepository=orderRepository;
        this.cartRepository=cartRepository;
        this.cartService=cartService;
        this.cartEntryService=cartEntryService;
        this.modelMapper=modelMapper;
        this.cartEntryRepository=cartEntryRepository;
        this.orderEntryRepository=orderEntryRepository;
    }

    @Override
    public String generateOrderId(String cartIdentifier) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String cleanIdentifier = (cartIdentifier != null && !cartIdentifier.trim().isEmpty())
                ? cartIdentifier.replaceAll("\\D", "")
                : "WALKIN";
        return "ORD-" + cleanIdentifier + "-" + timestamp;
    }

    @Override
    public OrderDto placeOrder(String cartIdentifier, String paymentMode) {
        Cart cart = cartRepository.findByIdentifier(cartIdentifier);
        List<CartEntry> cartEntries = cartEntryRepository.findByCart(cartIdentifier);
        String orderId = generateOrderId(cartIdentifier);
        LocalDateTime orderDate = LocalDateTime.now();
        Order order = modelMapper.map(cart, Order.class);
        order.setId(null);
        order.setOrderId(orderId);
        order.setPaymentMode(paymentMode);
        order.setOrderDate(orderDate);
        orderRepository.save(order);
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
        OrderDto orderDto = modelMapper.map(order, OrderDto.class);
        Type listType = new TypeToken<List<OrderEntryDto>>() {}.getType();
        orderDto.setOrderEntryDtoList(modelMapper.map(orderEntries, listType));
        return orderDto;
    }

    @Override
    public List<OrderDto> findAll() {
        List<Order> orders = orderRepository.findAllByOrderByOrderDateDesc();
        List<OrderDto> result = new ArrayList<>();
        for (Order order : orders) {
            OrderDto orderDto = modelMapper.map(order, OrderDto.class);
            List<OrderEntry> entries = orderEntryRepository.findByOrderId(order.getOrderId());
            Type listType = new TypeToken<List<OrderEntryDto>>() {}.getType();
            orderDto.setOrderEntryDtoList(modelMapper.map(entries, listType));
            result.add(orderDto);
        }
        return result;
    }
}