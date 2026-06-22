package com.ust.pos.order.service.impl;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.*;
import com.ust.pos.order.service.OrderService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final CartRepository cartRepository;

    private final CartEntryRepository cartEntryRepository;

    private final OrderRepository orderRepository;

    private final OrderEntryRepository orderEntryRepository;

    private final CartEntryService cartEntryService;

    private final CartService cartService;

    private final ModelMapper modelMapper;

    public OrderServiceImpl(CartRepository cartRepository, CartEntryRepository cartEntryRepository, OrderRepository orderRepository, OrderEntryRepository orderEntryRepository, CartEntryService cartEntryService, CartService cartService, ModelMapper modelMapper) {
        this.cartRepository = cartRepository;
        this.cartEntryRepository = cartEntryRepository;
        this.orderRepository = orderRepository;
        this.orderEntryRepository = orderEntryRepository;
        this.cartEntryService = cartEntryService;
        this.cartService = cartService;
        this.modelMapper = modelMapper;
    }

    private String generateOrderId(String cartIdentifier) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "ORD-" + cartIdentifier + "-" + timestamp + "-" + random;
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
        orderDto.setEntryDtoList(modelMapper.map(orderEntries, listType));
        return orderDto;
    }

    @Override
    public OrderDto findByOrderId(String orderId) {
        List<Order> orders = orderRepository.findByOrderId(orderId);
        if (orders.isEmpty()) {
            OrderDto notFound = new OrderDto();
            notFound.setSuccess(false);
            notFound.setMessage("Order not found");
            return notFound;
        }
        Order order = orders.get(0);
        OrderDto orderDto = modelMapper.map(order, OrderDto.class);
        List<OrderEntry> entries = orderEntryRepository.findByOrderId(orderId);
        Type listType = new TypeToken<List<OrderEntryDto>>() {}.getType();
        orderDto.setEntryDtoList(modelMapper.map(entries, listType));
        return orderDto;
    }

    @Override
    public WsDto<OrderDto> findAll(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAll(pageable);
        Type listType = new TypeToken<List<OrderDto>>() {}.getType();
        List<OrderDto> orderDtoList = modelMapper.map(orderPage.getContent(), listType);
        for (OrderDto orderDto : orderDtoList) {
            List<OrderEntry> entries = orderEntryRepository.findByOrderId(orderDto.getOrderId());
            Type entryListType = new TypeToken<List<OrderEntryDto>>() {}.getType();
            orderDto.setEntryDtoList(modelMapper.map(entries, entryListType));
        }
        WsDto<OrderDto> orderWsDto = new WsDto<>();
        orderWsDto.setDtoList(orderDtoList);
        orderWsDto.setTotalRecords(orderPage.getTotalElements());
        orderWsDto.setTotalPages(orderPage.getTotalPages());
        orderWsDto.setSizePerPage(pageable.getPageSize());
        orderWsDto.setPage(pageable.getPageNumber());
        return orderWsDto;
    }
}