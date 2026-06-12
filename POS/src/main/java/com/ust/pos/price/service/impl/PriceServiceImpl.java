package com.ust.pos.price.service.impl;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.PriceService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class PriceServiceImpl implements PriceService {

    @Autowired
    PriceRepository priceRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public PriceDto save(PriceDto priceDto) {
        priceDto.setIdentifier(priceDto.getProduct() +"_"+ priceDto.getType());
        String identifier = priceDto.getIdentifier();
        Price existingPrice = priceRepository.findByIdentifier(identifier);
        if (existingPrice != null) {
            priceDto.setMessage("Price with identifier - " + identifier + " already exists");
            priceDto.setSuccess(false);
            return priceDto;
        }
        Price price = modelMapper.map(priceDto, Price.class);
        priceRepository.save(price);
        return priceDto;
    }

    @Override
    public PaginationResponseDto<PriceDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<PriceDto>>() {}.getType();
        PaginationResponseDto<PriceDto> response = new PaginationResponseDto<>();
        if (pageable == null) {
            List<Price> prices = priceRepository.findAll();
            response.setDtoList(modelMapper.map(prices, listType));
            response.setTotalRecords((long) prices.size());
            response.setTotalPages(1);
            response.setSizePerPage(prices.size());
            response.setPage(0);
        } else {
            Page<Price> pricePage = priceRepository.findAll(pageable);
            response.setDtoList(modelMapper.map(pricePage.getContent(), listType));
            response.setTotalRecords(pricePage.getTotalElements());
            response.setTotalPages(pricePage.getTotalPages());
            response.setSizePerPage(pageable.getPageSize());
            response.setPage(pageable.getPageNumber());
        }
        return response;
    }

    @Override
    public PriceDto update(PriceDto priceDto) {
        String identifier = priceDto.getIdentifier();
        Price existingPrice = priceRepository.findByIdentifier(identifier);
        if (existingPrice == null) {
            priceDto.setMessage("Price with identifier - " + priceDto.getId() + "not found");
            priceDto.setSuccess(false);
            return priceDto;
        }
        modelMapper.map(priceDto, existingPrice);
        priceRepository.save(existingPrice);
        return priceDto;
    }

    @Override
    public PriceDto findByIdentifier(String identifier) {
        Price price=priceRepository.findByIdentifier(identifier);
        if(price==null){
            return null;
        }
        return modelMapper.map(price, PriceDto.class);
    }

    @Override
    public void deleteByIdentifier(String identifier) {
        priceRepository.deleteByIdentifier(identifier);
    }
}