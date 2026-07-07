package com.ust.pos.order.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.models.*;
import com.ust.pos.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends BaseService implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEntryRepository orderEntryRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDto checkout(OrderDto orderDto) {

        if (orderDto.getEntryList() == null || orderDto.getEntryList().isEmpty()) {
            orderDto.setSuccess(false);
            orderDto.setMessage("Cannot process checkout: Transaction item list is empty");
            return orderDto;
        }

        String orderIdentifier = "ORD-" + System.currentTimeMillis();
        Order order = new Order();
        order.setIdentifier(orderIdentifier);
        order.setCustomerIdentifier(orderDto.getCustomerIdentifier());
        order.setOriginalPrice(orderDto.getOriginalPrice());
        order.setDiscount(orderDto.getDiscount());
        order.setTotalPrice(orderDto.getTotalPrice());
        order.setPaymentMethod(orderDto.getPaymentMethod());

        if ("CASH".equalsIgnoreCase(orderDto.getPaymentMethod())) {
            order.setReceivedAmount(orderDto.getReceivedAmount());
            BigDecimal changeAmount = orderDto.getReceivedAmount().subtract(orderDto.getTotalPrice());
            order.setChangeAmount(changeAmount.compareTo(BigDecimal.ZERO) > 0 ? changeAmount : BigDecimal.ZERO);
        } else {
            order.setReceivedAmount(orderDto.getTotalPrice());
            order.setChangeAmount(BigDecimal.ZERO);
        }

        orderRepository.save(order);

        for (OrderEntryDto entryDto : orderDto.getEntryList()) {
            OrderEntry orderEntry = new OrderEntry();
            orderEntry.setIdentifier(orderIdentifier + "-" + entryDto.getProductIdentifier());
            orderEntry.setOrderIdentifier(orderIdentifier);
            orderEntry.setProductIdentifier(entryDto.getProductIdentifier());
            orderEntry.setQuantity(entryDto.getQuantity());
            orderEntry.setUnitPrice(entryDto.getUnitPrice());
            orderEntry.setOriginalPrice(entryDto.getOriginalPrice() != null ? entryDto.getOriginalPrice() : entryDto.getUnitPrice());
            orderEntry.setDiscount(entryDto.getDiscount() != null ? entryDto.getDiscount() : BigDecimal.ZERO);
            orderEntry.setTotalPrice(entryDto.getTotalPrice());
            orderEntryRepository.save(orderEntry);
        }

        OrderDto responseDto = modelMapper.map(order, OrderDto.class);
        Type entryListType = new TypeToken<List<OrderEntryDto>>() {}.getType();
        List<OrderEntry> orderEntryList = orderEntryRepository.findByOrderIdentifier(orderIdentifier);
        responseDto.setEntryList(modelMapper.map(orderEntryList, entryListType));
        responseDto.setSuccess(true);
        responseDto.setMessage("Order processed and saved directly to ledger successfully!");
        return responseDto;
    }

    @Override
    public OrderDto get(String identifier) {
        Order order = orderRepository.findByIdentifier(identifier);

        if (order == null) {
            OrderDto orderDto = new OrderDto();
            orderDto.setSuccess(false);
            orderDto.setMessage("Order registry trace not found");
            return orderDto;
        }

        OrderDto orderDto = modelMapper.map(order, OrderDto.class);
        List<OrderEntry> orderEntryList = orderEntryRepository.findByOrderIdentifier(identifier);
        Type entryListType = new TypeToken<List<OrderEntryDto>>() {}.getType();
        orderDto.setEntryList(modelMapper.map(orderEntryList, entryListType));
        return orderDto;
    }

    @Override
    public WsDto<OrderDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<OrderDto>>() {}.getType();
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
    @Transactional
    public boolean delete(String identifier) {
        orderEntryRepository.deleteByOrderIdentifier(identifier);
        orderRepository.deleteByIdentifier(identifier);
        return true;
    }
}