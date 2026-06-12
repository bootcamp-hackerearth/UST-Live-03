package com.ust.pos.price.service.impl;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
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
        priceDto.setIdentifier(priceDto.getProduct() + "_" + priceDto.getType());
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
    public WsDto<PriceDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();
        if (pageable == null) {
            List<PriceDto> priceDtoList = modelMapper.map(priceRepository.findAll(), listType);
            WsDto<PriceDto> response = new WsDto<>();
            response.setDtoList(priceDtoList);
            response.setTotalRecords(priceDtoList.size());
            return response;
        }
        Page<Price> pricePage = priceRepository.findAll(pageable);
        List<PriceDto> priceDtoList = modelMapper.map(pricePage.getContent(), listType);
        WsDto<PriceDto> wsDto = new WsDto<>();
        wsDto.setDtoList(priceDtoList);
        wsDto.setPage(pricePage.getNumber());
        wsDto.setSizePerPage(pricePage.getSize());
        wsDto.setTotalPages(pricePage.getTotalPages());
        wsDto.setTotalRecords(pricePage.getTotalElements());
        return wsDto;
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
        priceRepository.save(existingPrice);
        return priceDto;
    }

    @Override
    public PriceDto findByIdentifier(String identifier) {
        return modelMapper.map(priceRepository.findByIdentifier(identifier), PriceDto.class);
    }

    @Override
    public void deleteByIdentifier(String identifier) {
        priceRepository.deleteByIdentifier(identifier);
    }

}