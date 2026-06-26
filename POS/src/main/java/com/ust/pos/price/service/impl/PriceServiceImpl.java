package com.ust.pos.price.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.PriceService;
import com.ust.pos.product.service.ProductService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class PriceServiceImpl extends BaseService implements PriceService {

    private final PriceRepository priceRepository;
    private final ProductService productService;
    private final ModelMapper modelMapper;

    public PriceServiceImpl(PriceRepository priceRepository,ProductService productService,
                            ModelMapper modelMapper) {
        this.priceRepository = priceRepository;
        this.productService = productService;
        this.modelMapper = modelMapper;
    }

    @Override
    public PriceDto save(PriceDto priceDto) {

        String identifier =priceDto.getProductId() + "_" +priceDto.getPriceType().replace(" ", "_");
        Price existing = priceRepository.findByIdentifier(identifier);

        if (existing != null && !Boolean.TRUE.equals(existing.getDeleted())) {
            priceDto.setSuccess(false);
            priceDto.setMessage("Price already exists");
            return priceDto;
        }

        priceDto.setIdentifier(identifier);
        priceDto.setProductName(productService.findByIdentifier(priceDto.getProductId()).getProductName());
        Price price = modelMapper.map(priceDto, Price.class);
        setCreatedDetails(price);
        priceRepository.save(price);
        priceDto.setSuccess(true);
        priceDto.setMessage("Price saved successfully");
        return priceDto;
    }

    @Override
    public PriceDto update(PriceDto priceDto) {

        Price existing = priceRepository.findByIdentifier(priceDto.getIdentifier());

        if (existing == null || Boolean.TRUE.equals(existing.getDeleted())) {
            priceDto.setSuccess(false);
            priceDto.setMessage("Price not found");
            return priceDto;
        }

        priceDto.setProductName(productService.findByIdentifier(priceDto.getProductId()).getProductName());
        existing.setProductId(priceDto.getProductId());
        existing.setProductName(priceDto.getProductName());
        existing.setPriceType(priceDto.getPriceType());
        existing.setValue(priceDto.getValue());
        setModifiedDetails(existing);
        priceRepository.save(existing);
        priceDto.setSuccess(true);
        priceDto.setMessage("Price updated successfully");
        return priceDto;
    }

    @Override
    public PriceDto findByIdentifier(String identifier) {

        Price price = priceRepository.findByIdentifier(identifier);

        if (price == null || Boolean.TRUE.equals(price.getDeleted())) {
            PriceDto dto = new PriceDto();
            dto.setSuccess(false);
            dto.setMessage("Price not found");
            return dto;
        }

        PriceDto dto = modelMapper.map(price, PriceDto.class);
        dto.setSuccess(true);
        return dto;
    }

    @Override
    public WsDto<PriceDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<PriceDto>>() {}.getType();
        Page<Price> page = priceRepository.findByDeletedFalse(pageable);
        WsDto<PriceDto> ws = new WsDto<>();
        ws.setDtoList(modelMapper.map(page.getContent(), listType));
        ws.setTotalRecords(page.getTotalElements());
        ws.setTotalPages(page.getTotalPages());
        ws.setSizePerPage(pageable.getPageSize());
        ws.setPage(pageable.getPageNumber());
        return ws;
    }

    @Override
    public void delete(String identifier) {

        Price price = priceRepository.findByIdentifier(identifier);

        if (price == null) return;

        price.setDeleted(true);
        setModifiedDetails(price);
        priceRepository.save(price);
    }

    @Override
    public List<PriceDto> findActivePrices() {

        List<Price> list = priceRepository.findByStatusTrueAndDeletedFalse();
        Type type = new TypeToken<List<PriceDto>>() {}.getType();
        return modelMapper.map(list, type);
    }

    @Override
    public List<String> getPriceTypes() {
        return List.of("Selling Price", "Cost Price", "MRP");
    }
}