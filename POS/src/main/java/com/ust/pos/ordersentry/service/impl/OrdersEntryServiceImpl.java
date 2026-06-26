package com.ust.pos.ordersentry.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.OrdersEntryDto;
import com.ust.pos.model.OrdersEntry;
import com.ust.pos.model.OrdersEntryRepository;
import com.ust.pos.ordersentry.service.OrdersEntryService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class OrdersEntryServiceImpl extends BaseService implements OrdersEntryService {

    private final OrdersEntryRepository ordersEntryRepository;

    private final ModelMapper modelMapper;

    public OrdersEntryServiceImpl(OrdersEntryRepository ordersEntryRepository, ModelMapper modelMapper) {
        this.ordersEntryRepository = ordersEntryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public OrdersEntryDto save(
            OrdersEntryDto ordersEntryDto) {

        OrdersEntry ordersEntry =
                modelMapper.map(
                        ordersEntryDto,
                        OrdersEntry.class
                );
        setCreatedDetails(ordersEntry);
        ordersEntryRepository.save(ordersEntry
        );

        return modelMapper.map(
                ordersEntry,
                OrdersEntryDto.class
        );
    }

    @Override
    public OrdersEntryDto get(
            String identifier) {

        OrdersEntry ordersEntry =
                ordersEntryRepository.findByIdentifier(
                        identifier
                );

        if (ordersEntry == null) {
            return null;
        }

        return modelMapper.map(
                ordersEntry,
                OrdersEntryDto.class
        );
    }

    @Override
    @Transactional
    public void delete(
            String identifier) {

        ordersEntryRepository.deleteByIdentifier(
                identifier
        );
    }

    @Override
    public List<OrdersEntryDto> findByOrdersId(
            String ordersId) {

        Type listType =
                new TypeToken<List<OrdersEntryDto>>() {
                }.getType();

        return modelMapper.map(
                ordersEntryRepository.findByOrdersId(
                        ordersId
                ),
                listType
        );
    }

    @Override
    @Transactional
    public void deleteByOrdersId(
            String ordersId) {

        ordersEntryRepository.deleteByOrdersId(
                ordersId
        );
    }
}