package com.ust.pos.orderentry.service.impl;

import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.OrderEntry;
import com.ust.pos.model.OrderEntryRepository;
import com.ust.pos.orderentry.service.OrderentryService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class OrderentryServiceImpl implements OrderentryService {

    private final OrderEntryRepository orderEntryRepository;

    private final ModelMapper modelMapper;

    public OrderentryServiceImpl(OrderEntryRepository orderEntryRepository, ModelMapper modelMapper) {
        this.orderEntryRepository = orderEntryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public WsDto<OrderEntryDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<OrderEntryDto>>() {
        }.getType();

        Page<OrderEntry> page = orderEntryRepository.findAll(pageable);

        WsDto<OrderEntryDto> dto = new WsDto<>();

        dto.setContent(modelMapper.map(page.getContent(), listType));
        dto.setTotalRecords(page.getTotalElements());
        dto.setTotalPages(page.getTotalPages());
        dto.setSizePerPage(pageable.getPageSize());
        dto.setPage(pageable.getPageNumber());

        return dto;
    }

    @Override
    public OrderEntryDto findByIdentifier(String identifier) {

        OrderEntry entity = orderEntryRepository.findByIdentifier(identifier);

        if (entity == null) return null;

        return modelMapper.map(entity, OrderEntryDto.class);
    }

    @Override
    public List<OrderEntryDto> findByOrderId(String orderId) {

        List<OrderEntry> list = orderEntryRepository.findByOrderId(orderId);

        Type listType = new TypeToken<List<OrderEntryDto>>() {
        }.getType();

        return modelMapper.map(list, listType);
    }
}