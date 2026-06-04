package com.ust.pos.price.service.impl;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.PriceService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class PriceServiceImpl implements PriceService {
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    PriceRepository priceRepository;

    @Override
    public PriceDto save(PriceDto dto) {
        String identifier = dto.getIdentifier();
        if (priceRepository.existsByIdentifier(identifier)) {
            dto.setMessage("Already Exist!");
            dto.setSuccess(false);
            return dto;
        }
        Price price = modelMapper.map(dto, Price.class);
        priceRepository.save(price);
        dto.setSuccess(true);
        dto.setMessage("Price Saved ");
        return dto;
    }

    @Override
    public List<PriceDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();
        Page<Price> pricePage = priceRepository.findAll(pageable);
        return modelMapper.map(pricePage.getContent(), listType);
    }

    @Override
    public PriceDto findById(Long id) {

        return priceRepository.findById(id)
                .map(p -> modelMapper.map(p, PriceDto.class))
                .orElseThrow(() -> new RuntimeException("Price not found"));

    }

    @Override
    public void delete(String identifier) {
        priceRepository.deleteByIdentifier(identifier);
    }

    @Override
    public PriceDto update(PriceDto priceDto) {
        Price price = priceRepository.findByIdentifier(priceDto.getIdentifier());

        if (price == null) {
            priceDto.setSuccess(false);
            priceDto.setMessage("Price not found!");
            return priceDto;
        }

        if (!priceDto.getIdentifier().equalsIgnoreCase(price.getIdentifier()) && priceRepository.existsByIdentifier(priceDto.getIdentifier())) {
            priceDto.setMessage("This price already exist!");
            priceDto.setSuccess(false);
            return priceDto;
        }
        modelMapper.map(priceDto, price);
        priceRepository.save(price);
        priceDto.setMessage("Product Updated Successfully");
        return priceDto;
    }

    @Override
    public PriceDto findByIdentifier(String identifier) {

        Price price = priceRepository.findByIdentifier(identifier);
        return modelMapper.map(price, PriceDto.class);
    }
}