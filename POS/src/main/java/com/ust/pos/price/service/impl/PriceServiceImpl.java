package com.ust.pos.price.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.PriceService;
import jakarta.persistence.EntityNotFoundException;
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

    public static final String PRICE_WITH_IDENTIFIER = "Price with identifier - ";
    private final PriceRepository priceRepository;
    private final ModelMapper modelMapper;

    public PriceServiceImpl(
            PriceRepository priceRepository,
            ModelMapper modelMapper
    ) {
        this.priceRepository = priceRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PriceDto save(PriceDto priceDto) {
        priceDto.setIdentifier(priceDto.getProduct() +"_"+ priceDto.getType());
        String identifier = priceDto.getIdentifier();
        Price existingPrice = priceRepository.findByIdentifier(identifier);
        if (existingPrice != null) {
            if (existingPrice.isDeleted()) {
                priceDto.setMessage(PRICE_WITH_IDENTIFIER + identifier + " has been soft deleted. (Rollback by changing status)");
                priceDto.setSuccess(false);
                return priceDto;
            }
            priceDto.setMessage(PRICE_WITH_IDENTIFIER + identifier + " already exists");
            priceDto.setSuccess(false);
            return priceDto;
        }
        Price price = modelMapper.map(priceDto, Price.class);
        setCreatedDetails(price);
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
            response.setTotalRecords(prices.size());
            response.setTotalPages(1);
            response.setSizePerPage(prices.size());
            response.setPage(0);
        } else {
            Page<Price> pricePage = priceRepository.findByDeletedFalse(pageable);
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
            priceDto.setMessage(PRICE_WITH_IDENTIFIER + priceDto.getId() + "not found");
            priceDto.setSuccess(false);
            return priceDto;
        }
        modelMapper.map(priceDto, existingPrice);
        setModifiedDetails(existingPrice);
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
        Price price = priceRepository.findByIdentifier(identifier);
        if (price == null) {
            throw new EntityNotFoundException("Price not found");
        }
        softDelete(price);
        setModifiedDetails(price);
        priceRepository.save(price);
    }
}