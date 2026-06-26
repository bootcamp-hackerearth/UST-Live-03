package com.ust.pos.price.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.PriceService;
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
    private final ModelMapper modelMapper;

    public PriceServiceImpl(PriceRepository priceRepository, ModelMapper modelMapper) {
        this.priceRepository = priceRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PriceDto save(PriceDto priceDto) {
        priceDto.setIdentifier(priceDto.getProduct() + "_" + priceDto.getType());
        String identifier = priceDto.getIdentifier();
        Price existingPrice = priceRepository.findByIdentifier(identifier);
        if (existingPrice != null) {
            if (existingPrice.isDeleted()) {
                priceDto.setMessage("Price with identifier " + identifier + " has been soft deleted. (Rollback by changing status)");
                priceDto.setSuccess(false);
                return priceDto;
            }
            priceDto.setMessage("Price with identifier - " + identifier + " already exists");
            priceDto.setSuccess(false);
            return priceDto;
        }
        Price price = modelMapper.map(priceDto, Price.class);
        setCreatedDetails(price);
        priceRepository.save(price);
        priceDto.setSuccess(true);
        priceDto.setMessage("Price created successfully");
        return priceDto;
    }

    @Override
    public WsDto<PriceDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();
        WsDto<PriceDto> wsDto = new WsDto<>();
        if (pageable == null) {
            List<PriceDto> priceDtoList = modelMapper.map(priceRepository.findByDeletedFalse(pageable), listType);
            wsDto.setDtoList(priceDtoList);
            wsDto.setTotalRecords(priceDtoList.size());
            return wsDto;
        }
        Page<Price> pricePage = priceRepository.findByDeletedFalse(pageable);
        wsDto.setDtoList(modelMapper.map(pricePage.getContent(), listType));
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setTotalPages(pricePage.getTotalPages());
        wsDto.setTotalRecords(pricePage.getTotalElements());
        return wsDto;
    }

    @Override
    public PriceDto update(PriceDto priceDto) {
        Price existingPrice = priceRepository.findByIdentifier(priceDto.getIdentifier());
        if (existingPrice == null) {
            priceDto.setMessage("Price with identifier - " + priceDto.getIdentifier() + " not found");
            priceDto.setSuccess(false);
            return priceDto;
        }
        modelMapper.map(priceDto, existingPrice);
        setModifiedDetails(existingPrice);
        priceRepository.save(existingPrice);
        priceDto.setSuccess(true);
        priceDto.setMessage("Price updated successfully");
        return priceDto;
    }

    @Override
    public PriceDto findByIdentifier(String identifier) {
        return modelMapper.map(priceRepository.findByIdentifier(identifier), PriceDto.class);
    }

    @Override
    public void deleteByIdentifier(String identifier) {
        Price price = priceRepository.findByIdentifier(identifier);
        softDelete(price);
        setModifiedDetails(price);
        priceRepository.save(price);
    }

}