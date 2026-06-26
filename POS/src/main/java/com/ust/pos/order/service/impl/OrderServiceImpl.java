package com.ust.pos.order.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartentryService;
import com.ust.pos.dto.*;
import com.ust.pos.model.OrderEntry;
import com.ust.pos.model.OrderEntryRepository;
import com.ust.pos.model.OrderRepository;
import com.ust.pos.model.Orders;
import com.ust.pos.order.service.OrderService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class OrderServiceImpl extends BaseService implements OrderService {

    private final OrderRepository ordersRepository;

    private final OrderEntryRepository orderEntryRepository;

    private final CartentryService cartEntryService;

    private final CartService cartService;

    private final ModelMapper modelMapper;

    public OrderServiceImpl(OrderRepository ordersRepository, OrderEntryRepository orderEntryRepository, CartentryService cartEntryService, CartService cartService, ModelMapper modelMapper) {
        this.ordersRepository = ordersRepository;
        this.orderEntryRepository = orderEntryRepository;
        this.cartEntryService = cartEntryService;
        this.cartService = cartService;
        this.modelMapper = modelMapper;
    }

    @Override
    public OrdersDto save(OrdersDto ordersDto) {

        String cartId = ordersDto.getCustomerId();
        String orderID = generateOrderId(cartId);

        Orders existingOrder = ordersRepository.findByIdentifier(orderID);

        if (existingOrder != null) {
            ordersDto.setMessage(
                    existingOrder.isDeleted()
                            ? "Order - " + orderID + " already exists but was deleted, Please contact Administrator"
                            : "Order - " + orderID + " already exists"
            );
            ordersDto.setSuccess(false);
            return ordersDto;
        }

        List<CartEntryDto> cartEntryList = cartEntryService.findByCartId(cartId);

        for (CartEntryDto cartEntry : cartEntryList) {

            OrderEntry orderEntry = new OrderEntry();

            orderEntry.setIdentifier(cartEntry.getProduct() + "_" + orderID);
            orderEntry.setProduct(cartEntry.getProduct());
            orderEntry.setQuantity(cartEntry.getQuantity());
            orderEntry.setUnitPrice(cartEntry.getUnitPrice());
            orderEntry.setTotalPrice(cartEntry.getTotalPrice());
            orderEntry.setDiscount(cartEntry.getDiscount());
            orderEntry.setTotalOriginalPrice(cartEntry.getOriginalPrice());

            orderEntry.setOrderId(orderID);
            orderEntryRepository.save(orderEntry);
        }

        CartDto cartDto = cartService.findByIdentifier(cartId);

        ordersDto.setIdentifier(orderID);
        ordersDto.setTotalPrice(cartDto.getTotalPrice());
        ordersDto.setDiscount(cartDto.getDiscount());
        ordersDto.setTotalOriginalPrice(cartDto.getOriginalPrice());
        ordersDto.setCoupon(cartDto.getCoupon());

        Orders order = modelMapper.map(ordersDto, Orders.class);
        setCreatedDetails(order);
        ordersRepository.save(order);

        cartEntryService.deleteAllByCartId(cartId);

        ordersDto.setSuccess(true);
        return ordersDto;
    }

    @Override
    public WsDto<OrdersDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<OrdersDto>>() {
        }.getType();

        Page<Orders> page = ordersRepository.findAll(pageable);

        WsDto<OrdersDto> dto = new WsDto<>();

        dto.setContent(modelMapper.map(page.getContent(), listType));
        dto.setTotalRecords(page.getTotalElements());
        dto.setTotalPages(page.getTotalPages());
        dto.setSizePerPage(pageable.getPageSize());
        dto.setPage(pageable.getPageNumber());

        return dto;
    }

    @Override
    public OrdersDto findByIdentifier(String identifier) {

        Orders order = ordersRepository.findByIdentifier(identifier);

        if (order == null) return null;

        OrdersDto dto = modelMapper.map(order, OrdersDto.class);

        dto.setOrderEntryDtoList(getOrderEntries(identifier));

        return dto;
    }

    @Override
    public List<OrderEntryDto> getOrderEntries(String orderId) {

        List<OrderEntry> list = orderEntryRepository.findByOrderId(orderId);

        Type listType = new TypeToken<List<OrderEntryDto>>() {
        }.getType();

        return modelMapper.map(list, listType);
    }

    @Override
    public String generateOrderId(String cartId) {

        String date = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        String shortCartId = cartId.substring(0, Math.min(5, cartId.length()));

        return "ORD-" + date + "-" + shortCartId;
    }

    @Override
    public void delete(String identifier) {

        ordersRepository.deleteByIdentifier(identifier);
    }
}
