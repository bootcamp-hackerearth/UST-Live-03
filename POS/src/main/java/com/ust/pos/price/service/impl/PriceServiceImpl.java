package com.ust.pos.price.service.impl;

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
public class PriceServiceImpl implements PriceService {

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PriceDto findByIdentifier(String identifier) {
        return modelMapper.map(
                priceRepository.findByIdentifier(identifier),
                PriceDto.class
        );
    }

    @Override
    public PriceDto save(PriceDto priceDto) {

        Price existing = priceRepository.findByIdentifier(priceDto.getIdentifier());
        if (existing != null) {
            priceDto.setSuccess(false);
            priceDto.setMessage("Price already exists : " + priceDto.getIdentifier());
            return priceDto;
        }

        Price price = modelMapper.map(priceDto, Price.class);
        priceRepository.save(price);
        return priceDto;
    }

    @Override
    public PriceDto update(PriceDto priceDto) {

        Price existing = priceRepository.findByIdentifier(priceDto.getIdentifier());
        if (existing == null) {
            priceDto.setSuccess(false);
            priceDto.setMessage("Price not found : " + priceDto.getIdentifier());
            return priceDto;
        }

        modelMapper.map(priceDto, existing);
        priceRepository.save(existing);
        return priceDto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {

        priceRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<PriceDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();
        Page<Price> customerPage = priceRepository.findAll(pageable);
        return modelMapper.map(customerPage.getContent(), listType);
    }

    @Override
    public PriceDto toggleStatus(String identifier) {
        Price price = priceRepository.findByIdentifier(identifier);
        price.setStatus(!price.isStatus());
        priceRepository.save(price);
        return modelMapper.map(price, PriceDto.class);
    }

    @Override
    public List<PriceDto> findIfTrue() {
        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();
        return modelMapper.map(priceRepository.findByStatusIsTrue(), listType);
    }
}