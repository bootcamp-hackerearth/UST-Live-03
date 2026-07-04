package com.ust.pos.orderentry.impl;

import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.OrderEntry;
import com.ust.pos.model.OrderEntryRepository;
import com.ust.pos.orderentry.OrderEntryService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderEntryServiceImpl implements OrderEntryService {

    private final OrderEntryRepository orderEntryRepository;

    private final ModelMapper modelMapper;

    public OrderEntryServiceImpl(OrderEntryRepository orderEntryRepository, ModelMapper modelMapper) {
        this.orderEntryRepository = orderEntryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public OrderEntryDto findByIdentifier(String identifier) {

        OrderEntry orderEntry = orderEntryRepository.findByIdentifier(identifier);

        if (orderEntry == null) {
            return null;
        }

        return modelMapper.map(orderEntry, OrderEntryDto.class);
    }

    @Override
    public List<OrderEntryDto> findOrderEntryByOrderId(String orderId) {

        List<OrderEntry> orderEntries = orderEntryRepository.findByOrderId(orderId);

        if (orderEntries == null) {
            return new ArrayList<>();
        }

        return orderEntries.stream().map(orderEntry ->
                modelMapper.map(orderEntry, OrderEntryDto.class)).toList();
    }

    @Override
    public WsDto<OrderEntryDto> findAll(Pageable pageable) {

        Page<OrderEntry> orderEntryPage = orderEntryRepository.findAll(pageable);

        WsDto<OrderEntryDto> orderEntryDto = new WsDto<>();

        List<OrderEntryDto> orderEntryDtos = orderEntryPage.getContent()
                .stream()
                .map(product -> modelMapper.map(product, OrderEntryDto.class))
                .toList();

        orderEntryDto.setContent(orderEntryDtos);
        orderEntryDto.setPage(orderEntryPage.getNumber());
        orderEntryDto.setSizePerPage(orderEntryPage.getSize());
        orderEntryDto.setTotalPages(orderEntryPage.getTotalPages());
        orderEntryDto.setTotalRecords(orderEntryPage.getTotalElements());

        return orderEntryDto;
    }

    @Override
    public WsDto<OrderEntryDto> findAll(Specification<OrderEntry> example, Pageable pageable) {

        Type listType = new TypeToken<List<OrderEntryDto>>() {
        }.getType();
        Page<OrderEntry> page = orderEntryRepository.findAll(example, pageable);

        WsDto<OrderEntryDto> wsDto = new WsDto<>();
        wsDto.setContent(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}
