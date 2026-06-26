package com.ust.pos.order.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.*;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.Order;
import com.ust.pos.model.OrderRepository;
import com.ust.pos.order.service.OrderService;
import com.ust.pos.orderentry.service.OrderEntryService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl extends BaseService implements OrderService {


    private final OrderRepository orderRepository;

    private final ModelMapper modelMapper;

    private final OrderEntryService orderEntryService;

    private final CartService cartService;

    private final CartEntryRepository cartEntryRepository;

    public OrderServiceImpl(CartEntryRepository cartEntryRepository, CartService cartService, ModelMapper modelMapper,
                            OrderEntryService orderEntryService, OrderRepository orderRepository) {
        this.cartEntryRepository = cartEntryRepository;
        this.cartService = cartService;
        this.modelMapper = modelMapper;
        this.orderEntryService = orderEntryService;
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderDto save(OrderDto orderDto) {
        String identifier = orderDto.getIdentifier();
        Order existingOrder = orderRepository.findByIdentifier(identifier);
        if (existingOrder != null) {
            orderDto.setMessage("Order already exists");
            orderDto.setSuccess(false);
            return orderDto;
        }
        Order order = modelMapper.map(orderDto, Order.class);
        setCreatedDetails(order);
        orderRepository.save(order);
        return orderDto;
    }

    @Override
    public OrderDto get(String identifier) {
        Order order = orderRepository.findByIdentifier(identifier);
        if (order == null) {
            return null;
        }
        OrderDto dto = modelMapper.map(order, OrderDto.class);
        dto.setOrderEntryDtoList(orderEntryService.findByOrderId(identifier));
        return dto;
    }

    @Override
    @Transactional
    public OrderDto checkout(String cartId) {
        CartDto cartDto = cartService.findByIdentifier(cartId);

        if (cartDto == null) {
            return null;
        }

        String orderId = "ORD_" + System.currentTimeMillis();

        save(buildOrderDto(cartDto, orderId));
        saveOrderEntries(cartDto, orderId);

        cartEntryRepository.deleteAllByCartId(cartId);

        return get(orderId);
    }

    private OrderDto buildOrderDto(CartDto cartDto, String orderId) {
        OrderDto orderDto = new OrderDto();

        orderDto.setIdentifier(orderId);
        orderDto.setCustomer(cartDto.getCustomer());
        orderDto.setCustomerName(cartDto.getCustomerName());

        if (cartDto.getCustomerPhone() != null) {
            orderDto.setCustomerPhone(Long.valueOf(cartDto.getCustomerPhone()));
        }

        orderDto.setDiscount(toDouble(cartDto.getDiscount()));
        orderDto.setTotalPrice(toDouble(cartDto.getTotalPrice()));
        orderDto.setTotalOriginalPrice(toDouble(cartDto.getTotalOriginalPrice()));
        orderDto.setOrderDate(LocalDateTime.now());
        orderDto.setStatus("PLACED");

        return orderDto;
    }

    private void saveOrderEntries(CartDto cartDto, String orderId) {
        if (cartDto.getCartEntryDtoList() == null) {
            return;
        }

        for (CartEntryDto cartEntry : cartDto.getCartEntryDtoList()) {
            orderEntryService.save(buildOrderEntry(cartEntry, orderId));
        }
    }

    private OrderEntryDto buildOrderEntry(CartEntryDto cartEntry, String orderId) {
        OrderEntryDto orderEntryDto = new OrderEntryDto();

        orderEntryDto.setIdentifier("OE_" + System.nanoTime());
        orderEntryDto.setOrderId(orderId);
        orderEntryDto.setProduct(cartEntry.getProduct());
        orderEntryDto.setQuantity(
                cartEntry.getQuantity() == null ? 0 : cartEntry.getQuantity().intValue());
        orderEntryDto.setUnitPrice(toDouble(cartEntry.getUnitPrice()));
        orderEntryDto.setDiscount(toDouble(cartEntry.getDiscount()));
        orderEntryDto.setTotalPrice(toDouble(cartEntry.getTotalPrice()));

        return orderEntryDto;
    }

    private double toDouble(Number value) {
        return value == null ? 0.0 : value.doubleValue();
    }

    @Override
    public WsDto<OrderDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<OrderDto>>() {
        }.getType();
        Page<Order> orderPage = orderRepository.findAll(pageable);
        WsDto<OrderDto> wsDto = new WsDto<>();

        wsDto.setDtoList(modelMapper.map(orderPage.getContent(), listType));
        wsDto.setTotalRecords(orderPage.getTotalElements());
        wsDto.setTotalPages(orderPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        orderRepository.deleteByIdentifier(identifier);
        orderEntryService.deleteByOrderId(identifier);
    }
}