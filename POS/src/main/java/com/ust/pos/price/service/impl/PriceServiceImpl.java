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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.lang.reflect.Type;
import java.util.List;

@Service
public class PriceServiceImpl extends BaseService implements PriceService {
    private final PriceRepository priceRepository;
    private final ModelMapper modelMapper;

    public PriceServiceImpl(PriceRepository priceRepository, ModelMapper modelMapper) {
        this.priceRepository = priceRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PriceDto findByIdentifier(String identifier) {
        return modelMapper.map(priceRepository.findByIdentifier(identifier), PriceDto.class);
    }

    @Override
    public PriceDto save(PriceDto priceDto) {
        priceDto.setIdentifier(priceDto.getProduct() + "_" + priceDto.getPriceType());
        String identifier = priceDto.getIdentifier();
        Price existingPrice = priceRepository.findByIdentifier(identifier);
        if (existingPrice != null) {
            if (existingPrice.isDeleted()) {
                priceDto.setMessage("Price with identifier" + identifier + "has been soft deleted.(Rollback by changing status)");
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
        return priceDto;
    }

    @Override
    public PriceDto update(PriceDto priceDto) {
        String identifier = priceDto.getIdentifier();
        Price existingPrice = priceRepository.findByIdentifier(identifier);
        if (existingPrice == null) {
            priceDto.setMessage("Price with identifier - " + identifier + " not found");
            priceDto.setSuccess(false);
            return priceDto;
        }
        modelMapper.map(priceDto, existingPrice);
        setModifiedDetails(existingPrice);
        priceRepository.save(existingPrice);
        return priceDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Price price = priceRepository.findByIdentifier(identifier);
        softDelete(price);
        setModifiedDetails(price);
        priceRepository.save(price);
    }

    public WsDto<PriceDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();
        Page<Price> pricePage = priceRepository.findByDeletedFalse(pageable);

        WsDto<PriceDto> priceWsDto = new WsDto<>();
        priceWsDto.setDtoList(modelMapper.map(pricePage.getContent(), listType));
        priceWsDto.setTotalRecords(pricePage.getTotalElements());
        priceWsDto.setTotalPages(pricePage.getTotalPages());
        priceWsDto.setSizePerPage(pageable.getPageSize());
        priceWsDto.setPage(pageable.getPageNumber());

        return priceWsDto;
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