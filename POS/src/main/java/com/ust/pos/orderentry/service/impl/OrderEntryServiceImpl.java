package com.ust.pos.orderentry.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.OrderEntry;
import com.ust.pos.model.OrderEntryRepository;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.orderentry.service.OrderEntryService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderEntryServiceImpl extends CommonService implements OrderEntryService {
    private final OrderEntryRepository orderEntryRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public OrderEntryServiceImpl(OrderEntryRepository orderEntryRepository, ProductRepository productRepository,
                                 ModelMapper modelMapper) {
        this.orderEntryRepository = orderEntryRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public OrderEntryDto save(OrderEntryDto orderEntryDto) {
        OrderEntry orderEntry = new OrderEntry();
        orderEntry.setIdentifier(UUID.randomUUID().toString());
        orderEntry.setOrderIdentifier(orderEntryDto.getOrderIdentifier());
        orderEntry.setProductIdentifier(orderEntryDto.getProductIdentifier());
        orderEntry.setQuantity(orderEntryDto.getQuantity());
        orderEntry.setSellingPrice(orderEntryDto.getSellingPrice());
        orderEntry.setMrpPrice(orderEntryDto.getMrpPrice());
        orderEntry.setTotalPrice(orderEntryDto.getTotalPrice());
        orderEntry.setDiscount(orderEntryDto.getDiscount());
        orderEntry.setStatus(true);
        orderEntryRepository.save(orderEntry);
        return modelMapper.map(orderEntry, OrderEntryDto.class);
    }

    @Override
    public List<OrderEntryDto> findAllByOrderIdentifier(String orderIdentifier) {
        Type listType = new TypeToken<List<OrderEntryDto>>() {
        }.getType();
        List<OrderEntry> entries = orderEntryRepository.findAllByOrderIdentifier(orderIdentifier);
        List<OrderEntryDto> dtos = modelMapper.map(entries, listType);

        for (OrderEntryDto dto : dtos) {
            if (dto.getProductIdentifier() == null) {
                continue;
            }
            Product product = productRepository.findByIdentifier(dto.getProductIdentifier());
            if (product != null) {
                ProductDto productDto = modelMapper.map(product, ProductDto.class);
                dto.setProduct(productDto);
            }
        }

        return dtos;
    }

    @Override
    public void deleteByOrderIdentifier(String orderIdentifier) {
        orderEntryRepository.deleteByOrderIdentifier(orderIdentifier);
    }
}