package com.ust.pos.order.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.*;
import com.ust.pos.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends BaseService implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEntryRepository orderEntryRepository;
    private final CartRepository cartRepository;
    private final CartEntryRepository cartEntryRepository;
    private final ModelMapper modelMapper;

    @Override
    public OrderDto checkout(OrderDto orderDto) {
        Cart cart = cartRepository.findByIdentifier(orderDto.getCustomerIdentifier());
        if (cart == null) {
            orderDto.setSuccess(false);
            orderDto.setMessage("Cart not found");
            return orderDto;
        }
        List<CartEntry> cartEntryList = cartEntryRepository.findAllByCartId(cart.getIdentifier());
        if (cartEntryList == null || cartEntryList.isEmpty()) {
            orderDto.setSuccess(false);
            orderDto.setMessage("Cart is empty");
            return orderDto;
        }
        String orderIdentifier = "ORD-" + System.currentTimeMillis();
        Order order = new Order();
        order.setIdentifier(orderIdentifier);
        order.setCustomerIdentifier(cart.getIdentifier());
        order.setOriginalPrice(cart.getOriginalPrice());
        order.setDiscount(cart.getDiscount());
        order.setTotalPrice(cart.getTotalPrice());
        order.setPaymentMethod(orderDto.getPaymentMethod());

        if ("CASH".equalsIgnoreCase(orderDto.getPaymentMethod())) {
            order.setReceivedAmount(orderDto.getReceivedAmount());
            BigDecimal changeAmount = orderDto.getReceivedAmount().subtract(cart.getTotalPrice());
            order.setChangeAmount(changeAmount);
        } else {
            order.setReceivedAmount(cart.getTotalPrice());
            order.setChangeAmount(BigDecimal.ZERO);
        }
        setCreatedDetails(order);
        orderRepository.save(order);
        for (CartEntry cartEntry : cartEntryList) {
            OrderEntry orderEntry = new OrderEntry();
            orderEntry.setIdentifier(orderIdentifier + "-" + cartEntry.getProduct());
            orderEntry.setOrderIdentifier(orderIdentifier);
            orderEntry.setProductIdentifier(cartEntry.getProduct());
            orderEntry.setMrp(cartEntry.getOriginalPrice());
            orderEntry.setUnitDiscount(cartEntry.getDiscount());
            orderEntry.setUnitPrice(cartEntry.getUnitPrice());
            orderEntry.setQuantity(cartEntry.getQuantity());
            orderEntry.setTotalPrice(cartEntry.getTotalPrice());
            setCreatedDetails(orderEntry);
            orderEntryRepository.save(orderEntry);
        }
        cartEntryRepository.deleteAll(cartEntryList);
        cart.setOriginalPrice(BigDecimal.ZERO);
        cart.setDiscount(BigDecimal.ZERO);
        cart.setTotalPrice(BigDecimal.ZERO);
        setModifiedDetails(cart);
        cartRepository.save(cart);
        OrderDto responseDto = modelMapper.map(order, OrderDto.class);
        Type entryListType = new TypeToken<List<OrderEntryDto>>() {
        }.getType();
        List<OrderEntry> orderEntryList = orderEntryRepository.findByOrderIdentifier(orderIdentifier);
        responseDto.setEntryList(modelMapper.map(orderEntryList, entryListType));
        responseDto.setSuccess(true);
        responseDto.setMessage("Order placed successfully");

        return responseDto;
    }

    @Override
    public OrderDto get(String identifier) {
        Order order = orderRepository.findByIdentifier(identifier);
        if (order == null) {
            OrderDto orderDto = new OrderDto();
            orderDto.setSuccess(false);
            orderDto.setMessage("Order not found");
            return orderDto;
        }
        OrderDto orderDto = modelMapper.map(order, OrderDto.class);
        List<OrderEntry> orderEntryList = orderEntryRepository.findByOrderIdentifier(identifier);
        Type entryListType = new TypeToken<List<OrderEntryDto>>() {
        }.getType();
        orderDto.setEntryList(modelMapper.map(orderEntryList, entryListType));
        return orderDto;
    }

    @Override
    public WsDto<OrderDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<OrderDto>>() {
        }.getType();
        Page<Order> orderPage = orderRepository.findAll(pageable);
        WsDto<OrderDto> orderWsDto = new WsDto<>();
        orderWsDto.setDtoList(modelMapper.map(orderPage.getContent(), listType));
        orderWsDto.setTotalRecords(orderPage.getTotalElements());
        orderWsDto.setTotalPages(orderPage.getTotalPages());
        orderWsDto.setSizePerPage(pageable.getPageSize());
        orderWsDto.setPage(pageable.getPageNumber());
        return orderWsDto;
    }

    @Override
    public boolean delete(String identifier) {
        orderEntryRepository.deleteByOrderIdentifier(identifier);
        orderRepository.deleteByIdentifier(identifier);
        return true;
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
