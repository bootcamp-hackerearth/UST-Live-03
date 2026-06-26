package com.ust.pos.orderentry.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartEntry.service.CartEntryService;
import com.ust.pos.dto.*;
import com.ust.pos.model.*;
import com.ust.pos.orderentry.service.OrderEntryService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
public class OrderEntryServiceImpl extends BaseService implements OrderEntryService {

    private final CartEntryService cartEntryService;
    private final CartService cartService;
    private final ModelMapper modelMapper;
    private final OrderEntryReopsitory orderEntryReopsitory;
    private final OrdersRepository ordersRepository;
    private final ProductRepository productRepository;

    public OrderEntryServiceImpl(CartEntryService cartEntryService, CartService cartService, ModelMapper modelMapper, OrderEntryReopsitory orderEntryReopsitory, OrdersRepository ordersRepository, ProductRepository productRepository) {
        this.cartEntryService = cartEntryService;
        this.cartService = cartService;
        this.modelMapper = modelMapper;
        this.orderEntryReopsitory = orderEntryReopsitory;
        this.ordersRepository = ordersRepository;
        this.productRepository = productRepository;
    }

    @Override
    public OrdersDto save(OrdersDto ordersDto) {
        String cartId = ordersDto.getCustomerId();
        List<CartEntryDto> cartEntryList = cartEntryService.findByCartId(cartId);

        String orderID = generateOrderId(cartId);

        for (CartEntryDto cartEntry : cartEntryList){
            OrderEntry orderEntry = modelMapper.map(cartEntry, OrderEntry.class);
            orderEntry.setId(null);
            orderEntry.setOrderId(orderID);
            orderEntry.setProductName(productRepository.findByIdentifier(cartEntry.getProduct()).getName());
            setCreatedDetails(orderEntry);
            orderEntryReopsitory.save(orderEntry);
        }

        CartDto cartDto = cartService.findByIdentifier(cartId);
        modelMapper.map(cartDto, ordersDto);
        ordersDto.setIdentifier(orderID);
        ordersDto.setId(null);
        Orders order = modelMapper.map(ordersDto,Orders.class);
        ordersRepository.save(order);
        setCreatedDetails(order);
        cartEntryService.deleteAllByCartId(cartId);

        return ordersDto;
    }

    @Override
    public List<OrderEntryDto> findByOrderId(String orderId) {
        Type listType = new TypeToken<List<OrderEntryDto>>() {
        }.getType();
        return modelMapper.map(orderEntryReopsitory.findAllByOrderId(orderId), listType);
    }

    private String generateOrderId(String cartId) {
        String date = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String shortCartId = cartId.substring(0, Math.min(5, cartId.length()));
        return "ORD-" + date + "-" + shortCartId;
    }
}
