package com.ust.pos.price.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.PriceService;
import com.ust.pos.product.service.ProductService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class PriceServiceImpl extends BaseService implements PriceService {

    public static final String PRICE_NOT_FOUND = "Price not found";

    private final PriceRepository priceRepository;
    private final ProductService productService;
    private final ModelMapper modelMapper;

    public PriceServiceImpl(PriceRepository priceRepository, ProductService productService,
                            ModelMapper modelMapper) {
        this.priceRepository = priceRepository;
        this.productService = productService;
        this.modelMapper = modelMapper;
    }

    @Override
    public PriceDto save(PriceDto priceDto) {

        String identifier = priceDto.getProductId() + "_" + priceDto.getPriceType().replace(" ", "_");

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
            throw new ResourceNotFoundException(
                    "Price with identifier '" + priceDto.getIdentifier() + "' not found");
        }

        priceDto.setProductName(productService.findByIdentifier(priceDto.getProductId()).getProductName());

        existing.setProductId(priceDto.getProductId());
        existing.setProductName(priceDto.getProductName());
        existing.setPriceType(priceDto.getPriceType());
        existing.setValue(priceDto.getValue());

        setModifiedDetails(existing);

        Price saved = priceRepository.save(existing);

        PriceDto result = modelMapper.map(saved, PriceDto.class);
        result.setSuccess(true);
        result.setMessage("Price updated successfully");

        return result;
    }

    @Override
    public PriceDto findByIdentifier(String identifier) {

        Price price = priceRepository.findByIdentifier(identifier);

        if (price == null || Boolean.TRUE.equals(price.getDeleted())) {
            throw new ResourceNotFoundException(
                    "Price with identifier '" + identifier + "' not found");
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

        if (price == null || Boolean.TRUE.equals(price.getDeleted())) {
            throw new ResourceNotFoundException(
                    "Price with identifier '" + identifier + "' not found");
        }

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

    @Override
    public WsDto<PriceDto> findAll(Specification<Price> example, Pageable pageable) {

        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();
        Page<Price> page = priceRepository.findAll(example, pageable);

        WsDto<PriceDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}