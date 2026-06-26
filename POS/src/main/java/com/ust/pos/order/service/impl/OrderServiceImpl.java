package com.ust.pos.order.service.impl;

import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderItemDto;
import com.ust.pos.model.*;
import com.ust.pos.order.service.OrderService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private final CartRepository cartRepository;
    private final CartEntryService cartEntryService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ModelMapper modelMapper;

    public OrderServiceImpl(CartRepository cartRepository, CartEntryService cartEntryService,
                            OrderRepository orderRepository, OrderItemRepository orderItemRepository, ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.cartEntryService = cartEntryService;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
    }

    @Override
    public OrderDto createOrder(String cartId, String paymentMethod) {

        Cart cart = cartRepository.findByIdentifier(cartId);

        List<CartEntryDto> cartEntries =
                cartEntryService.findByCartId(cartId);

        OrderDto response = new OrderDto();

        if (cart == null || cartEntries == null || cartEntries.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("Cart is empty or not found");
            return response;
        }

        String orderNo =
                "ORD-" + System.currentTimeMillis();

        Order order = new Order();

        order.setIdentifier(orderNo);

        order.setCustomerId(cartId);
        order.setCustomer(cart.getCustomer());

        order.setSubtotal(
                cart.getTotalPrice().add(
                        cart.getDiscount() != null
                                ? cart.getDiscount()
                                : BigDecimal.ZERO
                )
        );

        order.setDiscount(
                cart.getDiscount() != null
                        ? cart.getDiscount()
                        : BigDecimal.ZERO
        );

        order.setTotalPrice(
                cart.getTotalPrice() != null
                        ? cart.getTotalPrice()
                        : BigDecimal.ZERO
        );

        order.setCouponCode(cart.getCoupon());

        order.setPaymentMethod(paymentMethod);

        order.setPaymentCompleted(true);

        order.setOrderStatus(OrderStatus.CONFIRMED);

        order.setCreatedAt(LocalDateTime.now());

        orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartEntryDto entry : cartEntries) {

            OrderItem item = new OrderItem();

            item.setIdentifier(
                    orderNo + "_" + entry.getProduct()
            );

            item.setOrderIdentifier(orderNo);

            item.setProduct(entry.getProduct());

            item.setQuantity(entry.getQuantity());

            item.setUnitPrice(entry.getUnitPrice());

            item.setTotalPrice(entry.getTotalPrice());

            orderItems.add(item);
        }

        orderItemRepository.saveAll(orderItems);

        cartEntryService.deleteAll(cartId);

        cartRepository.deleteByIdentifier(cartId);

        OrderDto dto =
                modelMapper.map(order, OrderDto.class);

        Type listType =
                new TypeToken<List<OrderItemDto>>() {
                }.getType();

        dto.setItems(
                modelMapper.map(orderItems, listType)
        );

        dto.setSuccess(true);
        dto.setMessage("Order created successfully");

        return dto;
    }

    @Override
    public OrderDto updateStatus(String orderId, String status) {

        Order order = orderRepository.findByIdentifierAndDeletedFalse(orderId);

        OrderDto dto = new OrderDto();

        if (order == null) {
            dto.setSuccess(false);
            dto.setMessage("Order not found - " + orderId);
            return dto;
        }

        order.setOrderStatus(OrderStatus.valueOf(status.toUpperCase()));
        orderRepository.save(order);

        dto = modelMapper.map(order, OrderDto.class);
        dto.setSuccess(true);
        dto.setMessage("Order status updated");

        return dto;
    }

    @Override
    public OrderDto findByIdentifier(String identifier) {

        Order order = orderRepository.findByIdentifierAndDeletedFalse(identifier);

        OrderDto dto = new OrderDto();

        if (order == null) {
            dto.setSuccess(false);
            dto.setMessage("Order not found");
            return dto;
        }

        dto = modelMapper.map(order, OrderDto.class);

        List<OrderItem> items = orderItemRepository.findByOrderIdentifierAndDeletedFalse(identifier);

        Type listType = new TypeToken<List<OrderItemDto>>() {
        }.getType();
        dto.setItems(modelMapper.map(items, listType));

        return dto;
    }

    @Override
    public List<OrderDto> findAll() {

        Type listType = new TypeToken<List<OrderDto>>() {
        }.getType();

        return modelMapper.map(orderRepository.findAll(), listType);
    }

    @Override
    public Page<OrderDto> findAll(String search, Pageable pageable) {

        Page<Order> page;

        if (search != null && !search.trim().isEmpty()) {
            page = orderRepository.findByIdentifierContainingIgnoreCaseAndDeletedFalse(search, pageable);
        } else {
            page = orderRepository.findAll(pageable);
        }
        return page.map(order -> modelMapper.map(order, OrderDto.class));
    }

    @Override
    public void delete(String identifier) {

        List<OrderItem> items = orderItemRepository.findByOrderIdentifierAndDeletedFalse(identifier);
        orderItemRepository.deleteAll(items);

        Order order = orderRepository.findByIdentifierAndDeletedFalse(identifier);
        if (order != null) {
            order.setDeleted(true);
            orderRepository.save(order);
        }
    }
}