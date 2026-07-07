package com.ust.pos.orderentry.service.impl;

import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.OrderEntry;
import com.ust.pos.model.OrderEntryRepository;
import com.ust.pos.orderentry.service.OrderEntryService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new ResourceNotFoundException(
                    "OrderEntry with identifier '" + identifier + "' not found");
        }

        return modelMapper.map(orderEntry, OrderEntryDto.class);
    }

    @Override
    public OrderEntryDto save(OrderEntryDto orderEntryDto) {

        String identifier = orderEntryDto.getIdentifier();
        OrderEntry existingOrderEntry = orderEntryRepository.findByIdentifier(identifier);

        if (existingOrderEntry != null) {
            orderEntryDto.setMessage(
                    existingOrderEntry.isDeleted()
                            ? " OrderEntry with identifier - " + identifier
                            + " already exists but was deleted, Please contact Administrator."
                            : " OrderEntry with identifier - " + identifier
                            + " already exists."
            );
            orderEntryDto.setSuccess(false);
            return orderEntryDto;
        }

        OrderEntry orderEntry = modelMapper.map(orderEntryDto, OrderEntry.class);
        orderEntryRepository.save(orderEntry);

        return orderEntryDto;
    }

    @Override
    public OrderEntryDto update(OrderEntryDto orderEntryDto) {

        String identifier = orderEntryDto.getIdentifier();
        OrderEntry existingOrderEntry = orderEntryRepository.findByIdentifier(identifier);

        if (existingOrderEntry == null) {
            orderEntryDto.setMessage("OrderEntry with identifier - " + identifier + " not found");
            orderEntryDto.setSuccess(false);
            return orderEntryDto;
        }

        modelMapper.map(orderEntryDto, existingOrderEntry);
        orderEntryRepository.save(existingOrderEntry);

        return orderEntryDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {

        orderEntryRepository.deleteByIdentifier(identifier);
    }

    @Override
    public WsDto<OrderEntryDto> findAll(Pageable pageable) {

        Page<OrderEntry> orderEntryPage = orderEntryRepository.findByIsDeletedFalse(pageable);

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
}