package com.ust.pos.order.service.impl;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Order;
import com.ust.pos.model.OrderEntry;
import com.ust.pos.model.OrderEntryRepository;
import com.ust.pos.model.OrderRepository;
import com.ust.pos.order.service.OrderService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEntryRepository orderEntryRepository;
    private final com.ust.pos.model.CartRepository cartRepository;
    private final ModelMapper modelMapper;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderEntryRepository orderEntryRepository,
            com.ust.pos.model.CartRepository cartRepository,
            ModelMapper modelMapper
    ) {
        this.orderRepository = orderRepository;
        this.orderEntryRepository = orderEntryRepository;
        this.cartRepository = cartRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public OrderDto processCheckout(OrderDto orderDto) {

        Order order = modelMapper.map(orderDto, Order.class);
        String orderUUID = UUID.randomUUID().toString();

        order.setIdentifier(orderUUID);
        order.setCreatedOn(LocalDateTime.now());
        order.setModifiedOn(LocalDateTime.now());
        order.setStatus(true);
        order.setCreatedBy("SYSTEM");

        Order savedOrder = orderRepository.saveAndFlush(order);

        List<OrderEntryDto> savedEntryDtos = new ArrayList<>();

        if (orderDto.getEntryList() != null) {

            for (OrderEntryDto entryDto : orderDto.getEntryList()) {

                OrderEntry entry = modelMapper.map(entryDto, OrderEntry.class);

                entry.setIdentifier(UUID.randomUUID().toString());
                entry.setOrderIdentifier(orderUUID);
                entry.setCreatedOn(LocalDateTime.now());
                entry.setModifiedOn(LocalDateTime.now());
                entry.setStatus(true);
                entry.setCreatedBy("SYSTEM");

                OrderEntry savedEntry = orderEntryRepository.saveAndFlush(entry);

                savedEntryDtos.add(modelMapper.map(savedEntry, OrderEntryDto.class));
            }
        }

        cartRepository.deleteByIdentifier(orderDto.getCustomerIdentifier());

        OrderDto responseDto = modelMapper.map(savedOrder, OrderDto.class);

        Type listType = new TypeToken<List<OrderEntryDto>>() {
        }.getType();
        responseDto.setEntryList(savedEntryDtos.isEmpty()
                ? Collections.emptyList()
                : modelMapper.map(savedEntryDtos, listType));

        return responseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderDetails(String identifier) {

        Order order = orderRepository.findByIdentifier(identifier);

        if (order == null) {
            throw new ResourceNotFoundException("Requested invoice does not exist.");
        }

        List<OrderEntry> entries = orderEntryRepository.findByOrderIdentifier(identifier);

        OrderDto dto = modelMapper.map(order, OrderDto.class);

        Type listType = new TypeToken<List<OrderEntryDto>>() {
        }.getType();
        dto.setEntryList(modelMapper.map(entries, listType));

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrdersList() {

        Type listType = new TypeToken<List<OrderDto>>() {
        }.getType();

        return modelMapper.map(orderRepository.findAll(), listType);
    }
}