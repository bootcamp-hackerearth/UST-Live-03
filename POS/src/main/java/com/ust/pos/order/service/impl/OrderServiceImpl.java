package com.ust.pos.order.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.*;
import com.ust.pos.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends BaseService implements OrderService {

    public static final RuntimeException PRODUCT_NOT_FOUND = new RuntimeException("Product not found");

    private final OrderRepository orderRepository;
    private final OrderEntryRepository orderEntryRepository;
    private final CartRepository cartRepository;
    private final CartEntryRepository cartEntryRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDto checkout(OrderDto orderDto) {
        Cart cart = cartRepository.findByIdentifierAndDeletedFalse(orderDto.getCustomerIdentifier());

        if (cart == null) {
            orderDto.setSuccess(false);
            orderDto.setMessage("Cart not found matching customer details");
            return orderDto;
        }
        List<CartEntry> cartEntryList = cartEntryRepository.findByCartIdentifierAndDeletedFalse(cart.getIdentifier());
        if (cartEntryList == null || cartEntryList.isEmpty()) {
            orderDto.setSuccess(false);
            orderDto.setMessage("Cart is currently empty");
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
            order.setChangeAmount(changeAmount.compareTo(BigDecimal.ZERO) > 0 ? changeAmount : BigDecimal.ZERO);
        } else {
            order.setReceivedAmount(cart.getTotalPrice());
            order.setChangeAmount(BigDecimal.ZERO);
        }

        if (order.getStatus() == null) {
            order.setStatus(true);
        }
        setCreatedDetails(order);
        orderRepository.save(order);
        for (CartEntry cartEntry : cartEntryList) {
            OrderEntry orderEntry = new OrderEntry();
            orderEntry.setIdentifier(orderIdentifier + "-" + cartEntry.getProductIdentifier());
            orderEntry.setOrderIdentifier(orderIdentifier);
            orderEntry.setProductIdentifier(cartEntry.getProductIdentifier());
            orderEntry.setQuantity(cartEntry.getQuantity());
            orderEntry.setUnitPrice(cartEntry.getUnitPrice());
            orderEntry.setOriginalPrice(cartEntry.getOriginalPrice());
            orderEntry.setDiscount(cartEntry.getDiscount());
            orderEntry.setTotalPrice(cartEntry.getTotalPrice());

            if (orderEntry.getStatus() == null) {
                orderEntry.setStatus(true);
            }
            setCreatedDetails(orderEntry);
            orderEntryRepository.save(orderEntry);
            softDelete(cartEntry);
            setModifiedDetails(cartEntry);
            cartEntryRepository.save(cartEntry);
        }

        cart.setOriginalPrice(BigDecimal.ZERO);
        cart.setDiscount(BigDecimal.ZERO);
        cart.setTotalPrice(BigDecimal.ZERO);
        setModifiedDetails(cart);
        cartRepository.save(cart);
        OrderDto responseDto = modelMapper.map(order, OrderDto.class);
        Type entryListType = new TypeToken<List<OrderEntryDto>>() {
        }.getType();
        List<OrderEntry> orderEntryList = orderEntryRepository.findByOrderIdentifierAndDeletedFalse(orderIdentifier);
        responseDto.setEntryList(modelMapper.map(orderEntryList, entryListType));
        responseDto.setSuccess(true);
        responseDto.setMessage("Order placed successfully");
        return responseDto;
    }

    @Override
    public OrderDto get(String identifier) {
        Order order = orderRepository.findByIdentifierAndDeletedFalse(identifier);
        if (order == null) {
            OrderDto orderDto = new OrderDto();
            orderDto.setSuccess(false);
            orderDto.setMessage("Order registry trace not found");
            return orderDto;
        }
        OrderDto orderDto = modelMapper.map(order, OrderDto.class);
        List<OrderEntry> orderEntryList = orderEntryRepository.findByOrderIdentifierAndDeletedFalse(identifier);
        Type entryListType = new TypeToken<List<OrderEntryDto>>() {
        }.getType();

        orderDto.setEntryList(modelMapper.map(orderEntryList, entryListType));
        return orderDto;
    }

    @Override
    public WsDto<OrderDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<OrderDto>>() {
        }.getType();

        Page<Order> orderPage = orderRepository.findAllByDeletedFalse(pageable);
        WsDto<OrderDto> orderWsDto = new WsDto<>();
        orderWsDto.setDtoList(modelMapper.map(orderPage.getContent(), listType));
        orderWsDto.setTotalRecords(orderPage.getTotalElements());
        orderWsDto.setTotalPage(orderPage.getTotalPages());
        orderWsDto.setSizePerPage(pageable.getPageSize());
        orderWsDto.setPage(pageable.getPageNumber());
        return orderWsDto;
    }

    @Override
    @Transactional
    public boolean delete(String identifier) {
        Order order = orderRepository.findByIdentifierAndDeletedFalse(identifier);

        if (order != null) {
            softDelete(order);
            setModifiedDetails(order);
            orderRepository.save(order);
            List<OrderEntry> entries = orderEntryRepository.findByOrderIdentifierAndDeletedFalse(identifier);

            for (OrderEntry entry : entries) {
                softDelete(entry);
                setModifiedDetails(entry);
                orderEntryRepository.save(entry);
            }
            return true;
        }
        return false;
    }

    @Override
    public WsDto<OrderDto> findAll(Specification<Order> example, Pageable pageable) {

        Type listType = new TypeToken<List<OrderDto>>() {
        }.getType();
        Page<Order> page = orderRepository.findAll(example, pageable);

        WsDto<OrderDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPage(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}