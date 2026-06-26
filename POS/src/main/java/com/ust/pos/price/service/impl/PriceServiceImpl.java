package com.ust.pos.price.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.PriceService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class PriceServiceImpl extends BaseService implements PriceService {
    private final PriceRepository priceRepository;
    private final ModelMapper modelMapper;

    public PriceServiceImpl(PriceRepository priceRepository,
                            ModelMapper modelMapper) {
        this.priceRepository = priceRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PriceDto findByIdentifier(String identifier) {
        Price price = priceRepository.findByIdentifierAndDeletedFalse(identifier);
        if (price == null) {
            return null;
        }
        return modelMapper.map(price, PriceDto.class);
    }

    @Override
    public PriceDto save(PriceDto priceDto) {
        priceDto.setIdentifier(
                priceDto.getProduct() + "_" + priceDto.getPriceType()
        );
        String identifier = priceDto.getIdentifier();
        Price existingPrice = priceRepository.findByIdentifier(identifier);
        if (existingPrice != null) {
            if (Boolean.TRUE.equals(existingPrice.getDeleted())) {
                priceDto.setMessage("Price - " + identifier + " was deleted and cannot be recreated");
            } else {
                priceDto.setMessage("Price with identifier - " + identifier + " already exists");
            }
            priceDto.setSuccess(false);
            return priceDto;
        }
        Price price = modelMapper.map(priceDto, Price.class);
        setCreatedDetails(price);
        priceRepository.save(price);
        priceDto.setSuccess(true);
        return priceDto;
    }

    @Override
    public PriceDto update(PriceDto priceDto) {
        String identifier = priceDto.getIdentifier();
        Price existingPrice = priceRepository.findByIdentifierAndDeletedFalse(identifier);
        if (existingPrice == null) {
            priceDto.setMessage("Price with identifier - " + identifier + " not found");
            priceDto.setSuccess(false);
            return priceDto;
        }
        modelMapper.map(priceDto, existingPrice);
        setModifiedDetails(existingPrice);
        priceRepository.save(existingPrice);
        priceDto.setSuccess(true);
        return priceDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Price price = priceRepository.findByIdentifierAndDeletedFalse(identifier);
        if (price != null) {
            softDelete(price);
            setModifiedDetails(price);
            priceRepository.save(price);
        }
    }

    @Override
    public WsDto<PriceDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();
        Page<Price> pricePage = priceRepository.findByDeletedFalse(pageable);
        WsDto<PriceDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(pricePage.getContent(), listType));
        wsDto.setTotalRecords(pricePage.getTotalElements());
        wsDto.setTotalPages(pricePage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }
}