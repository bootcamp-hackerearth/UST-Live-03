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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class OrderEntryServiceImplementation
        implements OrderEntryService {

    private final OrderEntryRepository orderEntryRepository;

    private final ModelMapper modelMapper;

    public OrderEntryServiceImplementation(OrderEntryRepository orderEntryRepository, ModelMapper modelMapper) {
        this.orderEntryRepository = orderEntryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public OrderEntryDto save(OrderEntryDto orderEntryDto) {

        OrderEntry orderEntry =
                modelMapper.map(
                        orderEntryDto,
                        OrderEntry.class
                );

        orderEntryRepository.save(orderEntry);

        return orderEntryDto;
    }

    @Override
    public OrderEntryDto update(OrderEntryDto orderEntryDto) {

        String identifier =
                orderEntryDto.getIdentifier();

        OrderEntry existingOrderEntry =
                orderEntryRepository.findByIdentifier(
                        identifier
                );

        if(existingOrderEntry == null)
        {
            orderEntryDto.setSuccess(false);
            orderEntryDto.setMessage(
                    "OrderEntry with identifier - "
                            + identifier
                            + " not found"
            );
            return orderEntryDto;
        }

        modelMapper.map(
                orderEntryDto,
                existingOrderEntry
        );

        orderEntryRepository.save(
                existingOrderEntry
        );

        return orderEntryDto;
    }

    @Override
    public void delete(String identifier) {

        orderEntryRepository
                .deleteByIdentifier(
                        identifier
                );
    }

    @Override
    public List<OrderEntryDto> findAll() {

        Type listType =
                new TypeToken<
                        List<OrderEntryDto>>() {
                }.getType();

        return modelMapper.map(
                orderEntryRepository.findAll(),
                listType
        );
    }

    @Override
    public WsDto<OrderEntryDto> findAll(
            Pageable pageable
    ) {

        Type listType =
                new TypeToken<
                        List<OrderEntryDto>>() {
                }.getType();

        Page<OrderEntry> orderEntryPage =
                orderEntryRepository.findAll(
                        pageable
                );

        WsDto<OrderEntryDto> wsDto =
                new WsDto<>();

        wsDto.setDtoList(
                modelMapper.map(
                        orderEntryPage.getContent(),
                        listType
                )
        );

        wsDto.setTotalRecords(
                orderEntryPage.getTotalElements()
        );

        wsDto.setTotalPage(
                orderEntryPage.getTotalPages()
        );

        wsDto.setSizePerPage(
                pageable.getPageSize()
        );

        wsDto.setPage(
                pageable.getPageNumber()
        );

        return wsDto;
    }

    @Override
    public OrderEntryDto findByIdentifier(
            String identifier
    ) {

        return modelMapper.map(
                orderEntryRepository.findByIdentifier(
                        identifier
                ),
                OrderEntryDto.class
        );
    }

    @Override
    public List<OrderEntryDto> findByOrderId(
            String orderId
    ) {

        Type listType =
                new TypeToken<
                        List<OrderEntryDto>>() {
                }.getType();

        return modelMapper.map(
                orderEntryRepository.findByOrderId(
                        orderId
                ),
                listType
        );
    }
}