package com.ust.pos.orderentry.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.model.OrderEntry;
import com.ust.pos.model.OrderEntryRepository;
import com.ust.pos.orderentry.service.OrderEntryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderEntryServiceImpl extends BaseService implements OrderEntryService {

    private final OrderEntryRepository orderEntryRepository;

    private final ModelMapper modelMapper;

    public OrderEntryServiceImpl(ModelMapper modelMapper, OrderEntryRepository orderEntryRepository) {
        this.modelMapper = modelMapper;
        this.orderEntryRepository = orderEntryRepository;
    }

    @Override
    public OrderEntryDto save(OrderEntryDto dto) {
        OrderEntry orderEntry = modelMapper.map(dto, OrderEntry.class);
        setCreatedDetails(orderEntry);
        orderEntryRepository.save(orderEntry);
        return dto;
    }

    @Override
    public List<OrderEntryDto> findByOrderId(String orderId) {
        return orderEntryRepository.findByOrderId(orderId).stream()
                .map(orderEntry -> modelMapper.map(orderEntry, OrderEntryDto.class))
                .toList();
    }

    @Override
    public void deleteByOrderId(String orderId) {
        orderEntryRepository.deleteByOrderId(orderId);
    }
}