package com.ust.pos.orders.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.OrdersDto;
import com.ust.pos.dto.OrdersEntryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Orders;
import com.ust.pos.model.OrdersRepository;
import com.ust.pos.orders.service.OrdersService;
import com.ust.pos.ordersentry.service.OrdersEntryService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class OrdersServiceImpl extends BaseService implements OrdersService {

    private final OrdersRepository ordersRepository;

    private final ModelMapper modelMapper;

    private final OrdersEntryService ordersEntryService;

    public OrdersServiceImpl(OrdersRepository ordersRepository, ModelMapper modelMapper, OrdersEntryService ordersEntryService) {
        this.ordersRepository = ordersRepository;
        this.modelMapper = modelMapper;
        this.ordersEntryService = ordersEntryService;
    }

    @Override
    @Transactional
    public OrdersDto save(OrdersDto ordersDto) {
        Orders existing = ordersRepository.findByIdentifier(ordersDto.getIdentifier());
        if (existing != null) {
            ordersDto.setSuccess(false);
            ordersDto.setMessage("Orders already exists");
            return ordersDto;
        }
        Orders orders = modelMapper.map(ordersDto, Orders.class);
        setCreatedDetails(orders);
        ordersRepository.save(orders);

        if (ordersDto.getOrdersEntryDtoList() != null) {
            for (OrdersEntryDto entry : ordersDto.getOrdersEntryDtoList()) {
                entry.setOrdersId(ordersDto.getIdentifier());
                ordersEntryService.save(entry);
            }
        }

        return get(
                ordersDto.getIdentifier()
        );
    }

    @Override
    public OrdersDto get(String identifier) {

        Orders orders =
                ordersRepository.findByIdentifier(
                        identifier
                );

        if (orders == null) {
            return null;
        }

        OrdersDto dto =
                modelMapper.map(
                        orders,
                        OrdersDto.class
                );

        dto.setOrdersEntryDtoList(
                ordersEntryService.findByOrdersId(
                        identifier
                )
        );

        return dto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        ordersRepository.deleteByIdentifier(
                identifier
        );
    }

    @Override
    public WsDto<OrdersDto> findAll(Pageable pageable) {
        Page<Orders> ordersPage = ordersRepository.findAll(pageable);
        Type listType = new TypeToken<List<OrdersDto>>() {
        }.getType();
        WsDto<OrdersDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(ordersPage.getContent(), listType));
        wsDto.setTotalRecords(ordersPage.getTotalElements());
        wsDto.setTotalPages(ordersPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }
}