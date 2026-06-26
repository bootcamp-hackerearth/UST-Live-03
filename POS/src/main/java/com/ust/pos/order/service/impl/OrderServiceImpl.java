package com.ust.pos.order.service.impl;

import com.ust.pos.dto.OrdersDto;
import com.ust.pos.model.OrdersRepository;
import com.ust.pos.order.service.OrderService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrdersRepository ordersRepository;
    private final ModelMapper modelMapper;

    public OrderServiceImpl(OrdersRepository ordersRepository, ModelMapper modelMapper) {
        this.ordersRepository = ordersRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<OrdersDto> findAll() {
        Type listType = new TypeToken<List<OrdersDto>>() {
        }.getType();
        return modelMapper.map(ordersRepository.findAll(),listType);
    }

    @Override
    public OrdersDto findByIdentifier(String identifier) {
        return modelMapper.map(ordersRepository.findByIdentifier(identifier),OrdersDto.class);
    }
}
