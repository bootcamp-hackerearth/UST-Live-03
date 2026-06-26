package com.ust.pos.price.service.impl;

import com.ust.pos.CommonService;
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

public class PriceServiceImpl extends CommonService implements PriceService {

    private final PriceRepository priceRepository;
    private final ModelMapper modelMapper;

    public PriceServiceImpl(PriceRepository priceRepository, ModelMapper modelMapper) {
        this.priceRepository = priceRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PriceDto save(PriceDto priceDto) {
        Price existing = priceRepository.findByIdentifier(priceDto.getIdentifier());
        if (existing != null) {
            if(existing.isDeleted()){
                priceDto.setMessage("Price identifier - " + priceDto.getIdentifier() + " not available");
                priceDto.setSuccess(false);
                return priceDto;
            }
            priceDto.setSuccess(false);
            priceDto.setMessage("Price already exists for identifier: " + priceDto.getIdentifier());
            return priceDto;
        }
        Price price = modelMapper.map(priceDto, Price.class);
        setAuditFields(price,true);
        priceRepository.save(price);
        return priceDto;
    }

    @Override
    public PriceDto update(PriceDto priceDto) {
        Price existing = priceRepository.findByIdentifier(priceDto.getIdentifier());
        if (existing == null) {
            priceDto.setSuccess(false);
            priceDto.setMessage("Price not found for identifier: " + priceDto.getIdentifier());
            return priceDto;
        }
        modelMapper.map(priceDto, existing);
        setAuditFields(existing,false);
        priceRepository.save(existing);
        return priceDto;
    }

    @Override
    public PriceDto findByIdentifier(String identifier) {
        Price price = priceRepository.findByIdentifier(identifier);
        return modelMapper.map(price, PriceDto.class);
    }

    @Override
    public void delete(String identifier) {
        Price price=priceRepository.findByIdentifier(identifier);
        softDelete(price);
        setAuditFields(price,false);
    }

    @Override
    public WsDto<PriceDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<PriceDto>>() {}.getType();
        Page<Price> pricePage = priceRepository.findByIsDeletedFalse(pageable);
        WsDto<PriceDto> priceDtoWsDto = new WsDto<>();
        priceDtoWsDto.setDtoList(modelMapper.map(pricePage.getContent(), listType));
        priceDtoWsDto.setTotalRecords(pricePage.getTotalElements());
        priceDtoWsDto.setTotalPages(pricePage.getTotalPages());
        priceDtoWsDto.setSizePerPage(pageable.getPageSize());
        priceDtoWsDto.setPage(pageable.getPageNumber());
        return priceDtoWsDto;
    }
}
