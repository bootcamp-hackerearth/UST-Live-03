package com.ust.pos.orderitem.service.impl;

import com.ust.pos.dto.OrderItemDto;
import com.ust.pos.model.OrderItem;
import com.ust.pos.model.OrderItemRepository;
import com.ust.pos.orderitem.service.OrderItemService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final ModelMapper modelMapper;

    public OrderItemServiceImpl(OrderItemRepository orderItemRepository, ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public OrderItemDto save(OrderItemDto dto) {

        dto.setIdentifier(dto.getOrderIdentifier() + "_" + dto.getProduct());

        OrderItem existing = orderItemRepository.findByIdentifierAndDeletedFalse(dto.getIdentifier());

        if (existing == null) {
            existing = new OrderItem();
        }

        modelMapper.map(dto, existing);

        orderItemRepository.save(existing);

        return findByIdentifier(dto.getIdentifier());
    }

    @Override
    public OrderItemDto update(OrderItemDto dto) {

        OrderItem existing = orderItemRepository.findByIdentifierAndDeletedFalse(dto.getIdentifier());

        if (existing == null) {
            dto.setSuccess(false);
            dto.setMessage("OrderItem not found - " + dto.getIdentifier());
            return dto;
        }

        modelMapper.map(dto, existing);

        orderItemRepository.save(existing);

        return findByIdentifier(dto.getIdentifier());
    }

    @Override
    public void delete(String identifier) {
        OrderItem orderItem = orderItemRepository.findByIdentifierAndDeletedFalse(identifier);
        if (orderItem != null) {
            orderItem.setDeleted(true);
            orderItemRepository.save(orderItem);
        }
    }

    @Override
    public OrderItemDto findByIdentifier(String identifier) {
        OrderItem item = orderItemRepository.findByIdentifierAndDeletedFalse(identifier);

        if (item == null) {
            OrderItemDto dto = new OrderItemDto();
            dto.setSuccess(false);
            dto.setMessage("OrderItem not found");
            return dto;
        }

        return modelMapper.map(item, OrderItemDto.class);
    }

    @Override
    public List<OrderItemDto> findAll() {
        Type listType = new TypeToken<List<OrderItemDto>>() {
        }.getType();
        return modelMapper.map(orderItemRepository.findByDeletedFalse(), listType);
    }

    @Override
    public List<OrderItemDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<OrderItemDto>>() {
        }.getType();

        Page<OrderItem> page = orderItemRepository.findAll(pageable);

        return modelMapper.map(page.getContent(), listType);
    }

    @Override
    public List<OrderItemDto> findByOrderIdentifier(String orderIdentifier) {
        Type listType = new TypeToken<List<OrderItemDto>>() {
        }.getType();

        List<OrderItem> items = orderItemRepository.findByOrderIdentifierAndDeletedFalse(orderIdentifier);

        return modelMapper.map(items, listType);
    }
}