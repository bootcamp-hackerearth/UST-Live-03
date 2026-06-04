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
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class PriceServiceImpl implements PriceService {

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PriceDto save(PriceDto priceDto) {
        String identifier = priceDto.getIdentifier();
        Price existingPrice = priceRepository.findByIdentifier(identifier);
        if (existingPrice != null) {
            priceDto.setMessage("Price with identifier - " + identifier + " already exists");
            priceDto.setSuccess(false);
            return priceDto;
        }
        priceDto.setDifference(priceDto.getSellingPrice() - priceDto.getCostPrice());
        Price price = modelMapper.map(priceDto, Price.class);
        priceRepository.save(price);
        return priceDto;
    }

    @Override
    public PriceDto update(PriceDto priceDto) {
        String identifier = priceDto.getIdentifier();
        Price existingPrice = priceRepository.findByIdentifier(identifier);
        if (existingPrice == null) {
            priceDto.setMessage("Price with identifier - " + identifier + " is not found");
            priceDto.setSuccess(false);
            return priceDto;
        }
        priceDto.setDifference(priceDto.getSellingPrice() - priceDto.getCostPrice());
        Price price = modelMapper.map(priceDto, Price.class);
        priceRepository.save(price);
        return priceDto;
    }

    @Override
    public void delete(String identifier) {
        priceRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<PriceDto> findAll() {
        Type listOfType = new TypeToken<List<PriceDto>>() {
        }.getType();
        return modelMapper.map(priceRepository.findAll(), listOfType);
    }

    @Override
    public PriceDto findByIdentifier(String identifier) {
        return modelMapper.map(priceRepository.findByIdentifier(identifier), PriceDto.class);
    }

    @Override
    public List<PriceDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();
        Page<Price> customerPage = priceRepository.findAll(pageable);
        return modelMapper.map(customerPage.getContent(), listType);
    }
}
